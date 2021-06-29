package pl.bartlomiejstepien.technewsbot.watching;

import pl.bartlomiejstepien.technewsbot.watching.exception.URIAlreadyBeingWatchedException;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GithubProjectWatcher implements NewsWatcher
{
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private final HttpClient httpClient;

    private final ConcurrentLinkedQueue<ProjectWatchTask> watchTasks = new ConcurrentLinkedQueue<>();

    public GithubProjectWatcher(HttpClient httpClient)
    {
        this.httpClient = httpClient;
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> watchTasks.forEach(ProjectWatchTask::checkForNews), 1000, 1, TimeUnit.HOURS);
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

        ProjectWatchTask projectWatchTask = new ProjectWatchTask(uri);
        this.watchTasks.add(projectWatchTask);
    }

    private boolean isAlreadyWatched(URI uri)
    {
        return watchTasks.stream().anyMatch(projectWatchTask -> projectWatchTask.getUri().equals(uri));
    }

    private static final class ProjectWatchTask
    {
        private final URI uri;

        public ProjectWatchTask(final URI uri)
        {
            this.uri = uri;
        }

        public URI getUri()
        {
            return uri;
        }

        public void checkForNews()
        {
            // TODO: Add logic for getting news from github project.
        }
    }
}
