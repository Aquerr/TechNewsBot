package pl.bartlomiejstepien.technewsbot.converter;

import com.google.inject.Singleton;
import pl.bartlomiejstepien.technewsbot.dto.WatchedRssFeedDto;
import pl.bartlomiejstepien.technewsbot.repository.model.WatchedRssFeed;

@Singleton
public class WatchedRssFeedConverter
{
    public WatchedRssFeed convertToEntity(WatchedRssFeedDto watchedRssFeedDto)
    {
        if (watchedRssFeedDto == null)
            return null;

        WatchedRssFeed watchedRssFeed = new WatchedRssFeed();
        watchedRssFeed.setId(watchedRssFeedDto.getId());
        watchedRssFeed.setUrl(watchedRssFeedDto.getUrl());
        watchedRssFeed.setRelaseDateTime(watchedRssFeedDto.getReleaseDateTime());
        return watchedRssFeed;
    }

    public WatchedRssFeedDto convertToDto(WatchedRssFeed watchedRssFeed)
    {
        if (watchedRssFeed == null)
            return null;

        WatchedRssFeedDto watchedRssFeedDto = new WatchedRssFeedDto();
        watchedRssFeedDto.setId(watchedRssFeed.getId());
        watchedRssFeedDto.setUrl(watchedRssFeed.getUrl());
        watchedRssFeedDto.setReleaseDateTime(watchedRssFeed.getRelaseDateTime());
        return watchedRssFeedDto;
    }
}
