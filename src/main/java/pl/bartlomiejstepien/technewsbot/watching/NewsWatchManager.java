package pl.bartlomiejstepien.technewsbot.watching;

import pl.bartlomiejstepien.technewsbot.TechNewsBot;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NewsWatchManager
{
    private static HttpClient httpClient = HttpClient.newHttpClient();

    private TechNewsBot techNewsBot;

    private final Map<NewsWatcher.WatcherType, NewsWatcher> newsWatcherList = new HashMap<>();

    public NewsWatchManager(TechNewsBot techNewsBot)
    {
        this.techNewsBot = techNewsBot;

        registerPossibleWatchers();
    }

    private void registerPossibleWatchers()
    {
        this.newsWatcherList.put(NewsWatcher.WatcherType.GITHUB, new GithubProjectWatcher(httpClient));
    }
}
