package pl.bartlomiejstepien.technewsbot.watching;

import com.google.inject.ImplementedBy;
import pl.bartlomiejstepien.technewsbot.dto.WatchedSiteDto;
import pl.bartlomiejstepien.technewsbot.watching.exception.NoWatcherRegisteredForGivenSiteTypeException;
import pl.bartlomiejstepien.technewsbot.watching.exception.URIAlreadyBeingWatchedException;

import java.net.URI;
import java.util.List;

@ImplementedBy(NewsWatchManagerImpl.class)
public interface NewsWatchManager
{
    void load();

    void watch(URI uri) throws URIAlreadyBeingWatchedException, NoWatcherRegisteredForGivenSiteTypeException;

    List<WatchedSiteDto> getWatchedSites();
}
