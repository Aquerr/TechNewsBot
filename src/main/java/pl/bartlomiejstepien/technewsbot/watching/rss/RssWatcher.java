package pl.bartlomiejstepien.technewsbot.watching.rss;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.technewsbot.TechNewsBot;
import pl.bartlomiejstepien.technewsbot.dto.WatchedRssFeedDto;
import pl.bartlomiejstepien.technewsbot.watching.NewsWatcher;
import pl.bartlomiejstepien.technewsbot.service.WatchedRssFeedService;
import pl.bartlomiejstepien.technewsbot.watching.exception.URIAlreadyBeingWatchedException;
import pl.bartlomiejstepien.technewsbot.watching.github.GithubProjectWatcher;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RssWatcher implements NewsWatcher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GithubProjectWatcher.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private final ConcurrentLinkedQueue<RssWatcher.RssFeedWatchTask> watchTasks = new ConcurrentLinkedQueue<>();
    private final WatchedRssFeedService watchedRssFeedService;

    public RssWatcher(HttpClient httpClient, WatchedRssFeedService watchedRssFeedService)
    {
        this.watchedRssFeedService = watchedRssFeedService;
    }

    @Override
    public void startWatching()
    {
        this.watchedRssFeedService.findAll().stream()
                .map(watchedRssFeedDto -> new RssWatcher.RssFeedWatchTask(watchedRssFeedService, URI.create(watchedRssFeedDto.getUrl())))
                .forEach(watchTasks::add);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this::preformScan, 0, 30, TimeUnit.MINUTES);
        LOGGER.info("Started RSS watcher");
    }

    private void preformScan()
    {
        try
        {
            for (final RssFeedWatchTask watchTask : this.watchTasks)
            {
                watchTask.checkForNews();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    @Override
    public WatcherType watcherType()
    {
        return WatcherType.RSS;
    }

    @Override
    public void watch(URI uri) throws URIAlreadyBeingWatchedException
    {
        if (isAlreadyWatched(uri))
            throw new URIAlreadyBeingWatchedException(uri.toString() + " is already being watched!");

        RssWatcher.RssFeedWatchTask projectWatchTask = new RssWatcher.RssFeedWatchTask(watchedRssFeedService, uri);
        this.watchTasks.add(projectWatchTask);
        WatchedRssFeedDto watchedRssFeedDto = new WatchedRssFeedDto();
        watchedRssFeedDto.setUrl(uri.toString());
        watchedRssFeedDto.setReleaseDateTime(LocalDateTime.of(1900, 1, 1, 1, 1, 1));
        this.watchedRssFeedService.saveOrUpdate(watchedRssFeedDto);
    }

    private boolean isAlreadyWatched(URI uri)
    {
        return watchTasks.stream().anyMatch(projectWatchTask -> projectWatchTask.getUri().equals(uri));
    }

    @Data
    private class RssFeedWatchTask
    {
        @Getter(value = AccessLevel.PRIVATE)
        private final WatchedRssFeedService watchedRssFeedService;

        private final URI uri;

        public void checkForNews()
        {
            WatchedRssFeedDto watchedRssFeedDto = Optional.ofNullable(this.watchedRssFeedService.find(uri.toString()))
                    .orElse(null);

            SyndFeedInput syndFeedInput = new SyndFeedInput();
            SyndFeed syndFeed = null;
            try
            {
                syndFeed = syndFeedInput.build(new XmlReader(this.uri.toURL()));
                SyndEntry syndEntry = syndFeed.getEntries().get(0);

                LocalDateTime latestPublishedDateTime = Optional.ofNullable(syndEntry.getPublishedDate())
                        .or(() -> Optional.ofNullable(syndEntry.getUpdatedDate()))
                        .map(date -> LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()))
                        .orElse(LocalDateTime.of(1900, 1, 1, 1, 1, 1));

                String description = syndEntry.getContents().stream()
                        .map(SyndContent::getValue)
                        .collect(Collectors.joining("\n"))
                        .replaceAll("\\<[^>]*>", "");
                String url = syndEntry.getLink();
                String newsTitle = syndEntry.getTitle();

                if (watchedRssFeedDto == null)
                {
                    showReleaseNotesAndSave(0, new RssNews(syndFeed.getTitle(), newsTitle, description, latestPublishedDateTime, url));
                }
                else if (latestPublishedDateTime.isAfter(watchedRssFeedDto.getReleaseDateTime()))
                {
                    showReleaseNotesAndSave(watchedRssFeedDto.getId(), new RssNews(syndFeed.getTitle(), newsTitle, description, latestPublishedDateTime, url));
                }
            }
            catch (FeedException | IOException e)
            {
                e.printStackTrace();
            }
        }

        private void showReleaseNotesAndSave(long id, RssNews rssNews)
        {
            LOGGER.debug(rssNews.toString());
            WatchedRssFeedDto watchedRssFeedDto = new WatchedRssFeedDto();
            watchedRssFeedDto.setUrl(this.uri.toString());
            watchedRssFeedDto.setId(id);
            watchedRssFeedDto.setReleaseDateTime(rssNews.getReleaseDateTime());

            TechNewsBot.getInstance().getDiscordPublisher().publishReleaseNotes(rssNews);

            this.watchedRssFeedService.saveOrUpdate(watchedRssFeedDto);
        }
    }
}
