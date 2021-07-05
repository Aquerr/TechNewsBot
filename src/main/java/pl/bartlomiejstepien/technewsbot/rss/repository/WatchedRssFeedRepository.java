package pl.bartlomiejstepien.technewsbot.rss.repository;

import com.google.inject.ImplementedBy;
import pl.bartlomiejstepien.technewsbot.rss.model.WatchedRssFeed;

import java.util.List;

@ImplementedBy(WatchedRssFeedRepositoryImpl.class)
public interface WatchedRssFeedRepository
{
    WatchedRssFeed find(final Integer id);

    WatchedRssFeed find(final String url);

    List<WatchedRssFeed> findAll();

    void saveOrUpdate(final WatchedRssFeed watchedNews);

    void delete(final WatchedRssFeed watchedNews);
}
