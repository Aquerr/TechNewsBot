package pl.bartlomiejstepien.technewsbot.rss.watching;

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
import pl.bartlomiejstepien.technewsbot.core.WatcherType;
import pl.bartlomiejstepien.technewsbot.dto.WatchedRssFeedDto;
import pl.bartlomiejstepien.technewsbot.core.NewsWatcher;
import pl.bartlomiejstepien.technewsbot.exception.UnrecognizedURLException;
import pl.bartlomiejstepien.technewsbot.rss.service.WatchedRssFeedService;
import pl.bartlomiejstepien.technewsbot.exception.URIAlreadyBeingWatchedException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RssWatcher implements NewsWatcher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RssWatcher.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private final ConcurrentLinkedQueue<RssWatcher.RssFeedWatchTask> watchTasks = new ConcurrentLinkedQueue<>();
    private final WatchedRssFeedService watchedRssFeedService;

    public RssWatcher(WatchedRssFeedService watchedRssFeedService)
    {
        this.watchedRssFeedService = watchedRssFeedService;
    }

    @Override
    public void startWatching()
    {
        this.watchedRssFeedService.findAll().stream()
                .map(watchedRssFeedDto ->
                {
                    try
                    {
                        return new RssFeedWatchTask(watchedRssFeedService, new URL(watchedRssFeedDto.getUrl()));
                    }
                    catch (MalformedURLException e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .forEach(watchTasks::add);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this::preformScan, 0, 10, TimeUnit.MINUTES);
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
    public void watch(URL url) throws URIAlreadyBeingWatchedException, UnrecognizedURLException
    {
        if (isAlreadyWatched(url))
            throw new URIAlreadyBeingWatchedException(url.toString() + " is already being watched!");

        if (!isValidUri(url))
            throw new UnrecognizedURLException();

        RssWatcher.RssFeedWatchTask projectWatchTask = new RssWatcher.RssFeedWatchTask(watchedRssFeedService, url);
        this.watchTasks.add(projectWatchTask);
        WatchedRssFeedDto watchedRssFeedDto = new WatchedRssFeedDto();
        watchedRssFeedDto.setUrl(url.toString());
        watchedRssFeedDto.setReleaseDateTime(LocalDateTime.of(1900, 1, 1, 1, 1, 1));
        this.watchedRssFeedService.saveOrUpdate(watchedRssFeedDto);
    }

    private boolean isValidUri(URL url)
    {
        SyndFeedInput syndFeedInput = new SyndFeedInput();
        try
        {
            syndFeedInput.build(new XmlReader(url));
            return true;
        }
        catch (FeedException | IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isAlreadyWatched(URL url)
    {
        return watchTasks.stream().anyMatch(projectWatchTask -> projectWatchTask.getUrl().equals(url));
    }

    @Data
    private class RssFeedWatchTask
    {
        @Getter(value = AccessLevel.PRIVATE)
        private final WatchedRssFeedService watchedRssFeedService;

        private final URL url;

        public void checkForNews()
        {
            WatchedRssFeedDto watchedRssFeedDto = Optional.ofNullable(this.watchedRssFeedService.find(url.toString()))
                    .orElse(null);

            SyndFeedInput syndFeedInput = new SyndFeedInput();
            SyndFeed syndFeed = null;
            try
            {
                syndFeed = syndFeedInput.build(new XmlReader(this.url));
                SyndEntry syndEntry = syndFeed.getEntries().get(0);

                LocalDateTime latestPublishedDateTime = Optional.ofNullable(syndEntry.getPublishedDate())
                        .or(() -> Optional.ofNullable(syndEntry.getUpdatedDate()))
                        .map(date -> LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()))
                        .orElse(LocalDateTime.of(1900, 1, 1, 1, 1, 1));

                String description = syndEntry.getContents().stream()
                        .map(SyndContent::getValue)
                        .collect(Collectors.joining("\n"))
                        .replaceAll("\\<[^>]*>", "");

                if (description.isBlank())
                {
                    description = Optional.ofNullable(syndEntry.getDescription())
                            .map(SyndContent::getValue)
                            .orElse("");
                }

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
            watchedRssFeedDto.setUrl(this.url.toString());
            watchedRssFeedDto.setId(id);
            watchedRssFeedDto.setReleaseDateTime(rssNews.getReleaseDateTime());

            TechNewsBot.getInstance().getDiscordPublisher().publishReleaseNotes(rssNews);

            this.watchedRssFeedService.saveOrUpdate(watchedRssFeedDto);
        }
    }
}
