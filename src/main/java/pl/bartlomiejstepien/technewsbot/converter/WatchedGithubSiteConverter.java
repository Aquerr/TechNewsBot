package pl.bartlomiejstepien.technewsbot.converter;

import com.google.inject.Singleton;
import pl.bartlomiejstepien.technewsbot.dto.WatchedGithubSiteDto;
import pl.bartlomiejstepien.technewsbot.repository.model.WatchedGithubSite;

@Singleton
public class WatchedGithubSiteConverter
{
    public WatchedGithubSite convertToEntity(WatchedGithubSiteDto watchedGithubSiteDto)
    {
        if (watchedGithubSiteDto == null)
            return null;

        WatchedGithubSite watchedGithubSite = new WatchedGithubSite();
        watchedGithubSite.setId(watchedGithubSiteDto.getId());
        watchedGithubSite.setUrl(watchedGithubSiteDto.getUrl());
        watchedGithubSite.setCurrentReleaseTag(watchedGithubSiteDto.getCurrentReleaseTag());
        watchedGithubSite.setRelaseDateTime(watchedGithubSiteDto.getReleaseDateTime());
        return watchedGithubSite;
    }

    public WatchedGithubSiteDto convertToDto(WatchedGithubSite watchedGithubSite)
    {
        if (watchedGithubSite == null)
            return null;

        WatchedGithubSiteDto watchedGithubSiteDto = new WatchedGithubSiteDto();
        watchedGithubSiteDto.setId(watchedGithubSite.getId());
        watchedGithubSiteDto.setUrl(watchedGithubSite.getUrl());
        watchedGithubSiteDto.setCurrentReleaseTag(watchedGithubSite.getCurrentReleaseTag());
        watchedGithubSiteDto.setReleaseDateTime(watchedGithubSite.getRelaseDateTime());
        return watchedGithubSiteDto;
    }
}
