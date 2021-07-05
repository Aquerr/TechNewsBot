package pl.bartlomiejstepien.technewsbot.watching.github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.technewsbot.TechNewsBot;
import pl.bartlomiejstepien.technewsbot.dto.WatchedGithubSiteDto;
import pl.bartlomiejstepien.technewsbot.service.WatchedGithubSiteService;
import pl.bartlomiejstepien.technewsbot.watching.NewsWatcher;
import pl.bartlomiejstepien.technewsbot.watching.exception.URIAlreadyBeingWatchedException;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GithubProjectWatcher implements NewsWatcher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GithubProjectWatcher.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private final HttpClient httpClient;
    private final ConcurrentLinkedQueue<ProjectWatchTask> watchTasks = new ConcurrentLinkedQueue<>();
    private final WatchedGithubSiteService watchedGithubSiteService;

    public GithubProjectWatcher(HttpClient httpClient, WatchedGithubSiteService watchedGithubSiteService)
    {
        this.httpClient = httpClient;
        this.watchedGithubSiteService = watchedGithubSiteService;
    }

    @Override
    public void startWatching()
    {
        this.watchedGithubSiteService.findAll().stream()
                .map(watchedGithubSiteDto -> new ProjectWatchTask(watchedGithubSiteService, URI.create(watchedGithubSiteDto.getUrl())))
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
    public void watch(URI uri) throws URIAlreadyBeingWatchedException
    {
        if (isAlreadyWatched(uri))
            throw new URIAlreadyBeingWatchedException(uri.toString() + " is already being watched!");

        ProjectWatchTask projectWatchTask = new ProjectWatchTask(watchedGithubSiteService, uri);
        this.watchTasks.add(projectWatchTask);
        WatchedGithubSiteDto watchedGithubSiteDto = new WatchedGithubSiteDto();
        watchedGithubSiteDto.setUrl(uri.toString());
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

    private boolean isAlreadyWatched(URI uri)
    {
        return watchTasks.stream().anyMatch(projectWatchTask -> projectWatchTask.getUri().equals(uri));
    }

    private static final class ProjectWatchTask
    {
        private final WatchedGithubSiteService watchedGithubSiteService;
        private final URI uri;

        public ProjectWatchTask(final WatchedGithubSiteService watchedGithubSiteService, final URI uri)
        {
            this.watchedGithubSiteService = watchedGithubSiteService;
            this.uri = uri;
        }

        public URI getUri()
        {
            return uri;
        }

        public void checkForNews()
        {
            WatchedGithubSiteDto watchedGithubSiteDto = Optional.ofNullable(this.watchedGithubSiteService.find(uri.toString()))
                    .orElseGet(this::defaultWatchedGithubSiteDto);

            if (watchedGithubSiteDto.getReleaseDateTime() == null)
                watchedGithubSiteDto.setReleaseDateTime(LocalDateTime.of(1900, 1, 1, 1, 1, 1));

            GithubRelease latestRelease = GithubVersionChecker.getLatestRelease(this.uri);

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
            watchedGithubSiteDto.setUrl(this.uri.toString());
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
            watchedGithubSite.setUrl(uri.toString());
            watchedGithubSite.setReleaseDateTime(LocalDateTime.of(1900, 1, 1, 1, 1, 1));
            return watchedGithubSite;
        }
    }
}
