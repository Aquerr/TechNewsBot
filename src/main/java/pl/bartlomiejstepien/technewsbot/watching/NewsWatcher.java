package pl.bartlomiejstepien.technewsbot.watching;

import pl.bartlomiejstepien.technewsbot.watching.exception.URIAlreadyBeingWatchedException;

import java.net.URI;

public interface NewsWatcher
{
    enum WatcherType
    {
        GITHUB
    }

    WatcherType watcherType();

    void watch(URI uri) throws URIAlreadyBeingWatchedException;
}
