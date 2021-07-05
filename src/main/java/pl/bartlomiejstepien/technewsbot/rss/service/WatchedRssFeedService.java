package pl.bartlomiejstepien.technewsbot.rss.service;

import com.google.inject.ImplementedBy;
import pl.bartlomiejstepien.technewsbot.dto.WatchedRssFeedDto;

import java.util.List;

@ImplementedBy(WatchedRssFeedServiceImpl.class)
public interface WatchedRssFeedService
{
    WatchedRssFeedDto find(final Integer id);

    WatchedRssFeedDto find(final String url);

    List<WatchedRssFeedDto> findAll();

    void saveOrUpdate(final WatchedRssFeedDto watchedNews);

    void delete(final WatchedRssFeedDto watchedNews);
}
