package pl.bartlomiejstepien.technewsbot.watching;

import com.google.inject.Inject;
import pl.bartlomiejstepien.technewsbot.dto.WatchedSiteDto;
import pl.bartlomiejstepien.technewsbot.service.WatchedGithubSiteService;
import pl.bartlomiejstepien.technewsbot.service.WatchedRssFeedService;
import pl.bartlomiejstepien.technewsbot.watching.exception.NoWatcherRegisteredForGivenSiteTypeException;
import pl.bartlomiejstepien.technewsbot.watching.exception.URIAlreadyBeingWatchedException;
import pl.bartlomiejstepien.technewsbot.watching.github.GithubProjectWatcher;
import pl.bartlomiejstepien.technewsbot.watching.rss.RssWatcher;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewsWatchManagerImpl implements NewsWatchManager
{
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private final EnumMap<NewsWatcher.WatcherType, NewsWatcher> newsWatcherMap;
    private final WatchedGithubSiteService watchedGithubSiteService;
    private final WatchedRssFeedService watchedRssFeedService;

    @Inject
    public NewsWatchManagerImpl(WatchedGithubSiteService watchedGithubSiteService, WatchedRssFeedService watchedRssFeedService)
    {
        this.watchedGithubSiteService = watchedGithubSiteService;
        this.watchedRssFeedService = watchedRssFeedService;
        this.newsWatcherMap = new EnumMap<>(NewsWatcher.WatcherType.class);
        registerPossibleWatchers();
    }

    private void registerPossibleWatchers()
    {
        this.newsWatcherMap.put(NewsWatcher.WatcherType.GITHUB, new GithubProjectWatcher(httpClient, this.watchedGithubSiteService));
        this.newsWatcherMap.put(NewsWatcher.WatcherType.RSS, new RssWatcher(httpClient, this.watchedRssFeedService));
    }

    @Override
    public void load()
    {
        for (final NewsWatcher newsWatcher : this.newsWatcherMap.values())
        {
            newsWatcher.startWatching();
        }
    }

    @Override
    public void watch(URI uri) throws URIAlreadyBeingWatchedException, NoWatcherRegisteredForGivenSiteTypeException
    {
        for (final Map.Entry<NewsWatcher.WatcherType, NewsWatcher> watcherEntry : newsWatcherMap.entrySet())
        {
            //TODO: Make it better :P This check is too simple
            if (uri.toString().contains(watcherEntry.getKey().getName()))
            {
                watcherEntry.getValue().watch(uri);
                return;
            }
            else if (watcherEntry.getKey() == NewsWatcher.WatcherType.RSS
                    && (uri.toString().contains("rss") || uri.toString().contains("atom")))
            {
                watcherEntry.getValue().watch(uri);
                return;
            }
        }

        throw new NoWatcherRegisteredForGivenSiteTypeException("No watcher registered for given site type exists");
    }

    @Override
    public List<WatchedSiteDto> getWatchedSites()
    {
        final LinkedList<WatchedSiteDto> watchedSiteDtos = new LinkedList<>();
        watchedSiteDtos.addAll(this.watchedGithubSiteService.findAll().stream().collect(Collectors.toList()));
        return watchedSiteDtos;
    }
}
