package pl.bartlomiejstepien.technewsbot.watching;

import pl.bartlomiejstepien.technewsbot.watching.exception.URIAlreadyBeingWatchedException;

import java.net.URI;

public interface NewsWatcher
{
    enum WatcherType
    {
        GITHUB("github"),
        RSS("rss");

        private String name;

        WatcherType(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    void startWatching();

    WatcherType watcherType();

    void watch(URI uri) throws URIAlreadyBeingWatchedException;
}
