package pl.bartlomiejstepien.technewsbot.core;

import pl.bartlomiejstepien.technewsbot.exception.URIAlreadyBeingWatchedException;
import pl.bartlomiejstepien.technewsbot.exception.UnrecognizedURLException;

import java.net.URL;

public interface NewsWatcher
{
    void startWatching();

    WatcherType watcherType();

    void watch(URL url) throws URIAlreadyBeingWatchedException, UnrecognizedURLException;

    void unwatch(String url);
}
