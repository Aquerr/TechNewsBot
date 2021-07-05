package pl.bartlomiejstepien.technewsbot.github.watching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.technewsbot.TechNewsBot;
import pl.bartlomiejstepien.technewsbot.core.WatcherType;
import pl.bartlomiejstepien.technewsbot.dto.WatchedGithubSiteDto;
import pl.bartlomiejstepien.technewsbot.github.service.WatchedGithubSiteService;
import pl.bartlomiejstepien.technewsbot.core.NewsWatcher;
import pl.bartlomiejstepien.technewsbot.exception.URIAlreadyBeingWatchedException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GithubProjectWatcher implements NewsWatcher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GithubProjectWatcher.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private final ConcurrentLinkedQueue<ProjectWatchTask> watchTasks = new ConcurrentLinkedQueue<>();
    private final WatchedGithubSiteService watchedGithubSiteService;

    public GithubProjectWatcher(WatchedGithubSiteService watchedGithubSiteService)
    {
        this.watchedGithubSiteService = watchedGithubSiteService;
    }

    @Override
    public void startWatching()
    {
        this.watchedGithubSiteService.findAll().stream()
                .map(watchedGithubSiteDto ->
                {
                    try
                    {
                        return new ProjectWatchTask(watchedGithubSiteService, new URL(watchedGithubSiteDto.getUrl()));
                    }
                    catch (MalformedURLException e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .forEach(watchTasks::add);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this::preformScan, 0, 1, TimeUnit.HOURS);
        LOGGER.info("Started Github watcher");
    }

    @Override
    public WatcherType watcherType()
    {
        return WatcherType.GITHUB;
    }

    @Override
    public void watch(URL url) throws URIAlreadyBeingWatchedException
    {
        if (isAlreadyWatched(url))
            throw new URIAlreadyBeingWatchedException(url.toString() + " is already being watched!");

        ProjectWatchTask projectWatchTask = new ProjectWatchTask(watchedGithubSiteService, url);
        this.watchTasks.add(projectWatchTask);
        WatchedGithubSiteDto watchedGithubSiteDto = new WatchedGithubSiteDto();
        watchedGithubSiteDto.setUrl(url.toString());
        watchedGithubSiteDto.setCurrentReleaseTag("");
        watchedGithubSiteDto.setReleaseDateTime(LocalDateTime.of(1900, 1, 1, 1, 1, 1));
        this.watchedGithubSiteService.saveOrUpdate(watchedGithubSiteDto);
    }

    private void preformScan()
    {
        try
        {
            for (final ProjectWatchTask watchTask : this.watchTasks)
            {
                watchTask.checkForNews();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private boolean isAlreadyWatched(URL url)
    {
        return watchTasks.stream().anyMatch(projectWatchTask -> projectWatchTask.getUri().equals(url));
    }

    private static final class ProjectWatchTask
    {
        private final WatchedGithubSiteService watchedGithubSiteService;
        private final URL url;

        public ProjectWatchTask(final WatchedGithubSiteService watchedGithubSiteService, final URL url)
        {
            this.watchedGithubSiteService = watchedGithubSiteService;
            this.url = url;
        }

        public URL getUri()
        {
            return url;
        }

        public void checkForNews()
        {
            WatchedGithubSiteDto watchedGithubSiteDto = Optional.ofNullable(this.watchedGithubSiteService.find(url.toString()))
                    .orElseGet(this::defaultWatchedGithubSiteDto);

            if (watchedGithubSiteDto.getReleaseDateTime() == null)
                watchedGithubSiteDto.setReleaseDateTime(LocalDateTime.of(1900, 1, 1, 1, 1, 1));

            GithubRelease latestRelease = GithubVersionChecker.getLatestRelease(this.url);

            if (latestRelease == null)
                return;

            if (latestRelease.getReleaseDateTime().isAfter(watchedGithubSiteDto.getReleaseDateTime()))
            {
                showReleaseNotesAndSave(watchedGithubSiteDto.getId(), latestRelease);
            }
        }

        private void showReleaseNotesAndSave(long id, GithubRelease githubRelease)
        {
            LOGGER.debug(githubRelease.toString());
            WatchedGithubSiteDto watchedGithubSiteDto = new WatchedGithubSiteDto();
            watchedGithubSiteDto.setUrl(this.url.toString());
            watchedGithubSiteDto.setId(id);
            watchedGithubSiteDto.setReleaseDateTime(githubRelease.getReleaseDateTime());
            watchedGithubSiteDto.setCurrentReleaseTag(githubRelease.getTag());

            TechNewsBot.getInstance().getDiscordPublisher().publishReleaseNotes(githubRelease);

            this.watchedGithubSiteService.saveOrUpdate(watchedGithubSiteDto);
        }

        private WatchedGithubSiteDto defaultWatchedGithubSiteDto()
        {
            WatchedGithubSiteDto watchedGithubSite = new WatchedGithubSiteDto();
            watchedGithubSite.setCurrentReleaseTag("");
            watchedGithubSite.setUrl(url.toString());
            watchedGithubSite.setReleaseDateTime(LocalDateTime.of(1900, 1, 1, 1, 1, 1));
            return watchedGithubSite;
        }
    }
}
