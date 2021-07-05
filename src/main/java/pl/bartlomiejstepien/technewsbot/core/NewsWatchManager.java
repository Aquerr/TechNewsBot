package pl.bartlomiejstepien.technewsbot.core;

import com.google.inject.ImplementedBy;
import pl.bartlomiejstepien.technewsbot.dto.WatchedSiteDto;
import pl.bartlomiejstepien.technewsbot.exception.NoWatcherForGivenSiteTypeException;
import pl.bartlomiejstepien.technewsbot.exception.URIAlreadyBeingWatchedException;
import pl.bartlomiejstepien.technewsbot.exception.UnrecognizedURLException;

import java.net.URL;
import java.util.List;

@ImplementedBy(NewsWatchManagerImpl.class)
public interface NewsWatchManager
{
    void load();

    void watch(WatcherType watcherType, URL url) throws URIAlreadyBeingWatchedException,
            NoWatcherForGivenSiteTypeException, UnrecognizedURLException;

    List<WatchedSiteDto> getWatchedSites();
}
