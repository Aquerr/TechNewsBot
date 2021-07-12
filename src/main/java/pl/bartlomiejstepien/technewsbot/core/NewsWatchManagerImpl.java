package pl.bartlomiejstepien.technewsbot.core;

import com.google.inject.Inject;
import pl.bartlomiejstepien.technewsbot.dto.WatchedSiteDto;
import pl.bartlomiejstepien.technewsbot.exception.NoWatcherForGivenSiteTypeException;
import pl.bartlomiejstepien.technewsbot.exception.URIAlreadyBeingWatchedException;
import pl.bartlomiejstepien.technewsbot.exception.UnrecognizedURLException;
import pl.bartlomiejstepien.technewsbot.github.service.WatchedGithubSiteService;
import pl.bartlomiejstepien.technewsbot.github.watching.GithubProjectWatcher;
import pl.bartlomiejstepien.technewsbot.rss.service.WatchedRssFeedService;
import pl.bartlomiejstepien.technewsbot.rss.watching.RssWatcher;

import java.net.URL;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class NewsWatchManagerImpl implements NewsWatchManager
{
    private final EnumMap<WatcherType, NewsWatcher> newsWatcherMap;
    private final WatchedGithubSiteService watchedGithubSiteService;
    private final WatchedRssFeedService watchedRssFeedService;

    @Inject
    public NewsWatchManagerImpl(WatchedGithubSiteService watchedGithubSiteService, WatchedRssFeedService watchedRssFeedService)
    {
        this.watchedGithubSiteService = watchedGithubSiteService;
        this.watchedRssFeedService = watchedRssFeedService;
        this.newsWatcherMap = new EnumMap<>(WatcherType.class);
        registerPossibleWatchers();
    }

    private void registerPossibleWatchers()
    {
        this.newsWatcherMap.put(WatcherType.GITHUB, new GithubProjectWatcher(this.watchedGithubSiteService));
        this.newsWatcherMap.put(WatcherType.RSS, new RssWatcher(this.watchedRssFeedService));
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
    public void watch(WatcherType watcherType, URL url) throws URIAlreadyBeingWatchedException, NoWatcherForGivenSiteTypeException, UnrecognizedURLException
    {
        NewsWatcher newsWatcher = this.newsWatcherMap.get(watcherType);
        if (newsWatcher == null)
            throw new NoWatcherForGivenSiteTypeException("No watcher registered for the given URL.");

        newsWatcher.watch(url);
    }

    @Override
    public List<WatchedSiteDto> getWatchedSites()
    {
        final LinkedList<WatchedSiteDto> watchedSiteDtos = new LinkedList<>();
        watchedSiteDtos.addAll(new LinkedList<>(this.watchedGithubSiteService.findAll()));
        watchedSiteDtos.addAll(new LinkedList<>(this.watchedRssFeedService.findAll()));
        return watchedSiteDtos;
    }

    @Override
    public void unwatch(String urlString)
    {
        this.newsWatcherMap.forEach((type, watcher) -> watcher.unwatch(urlString));
    }
}
