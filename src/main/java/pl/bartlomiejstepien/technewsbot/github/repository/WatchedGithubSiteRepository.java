package pl.bartlomiejstepien.technewsbot.github.repository;

import com.google.inject.ImplementedBy;
import pl.bartlomiejstepien.technewsbot.github.model.WatchedGithubSite;

import java.util.List;

@ImplementedBy(WatchedGithubSiteRepositoryImpl.class)
public interface WatchedGithubSiteRepository
{
    WatchedGithubSite find(final Long id);

    WatchedGithubSite find(final String url);

    List<WatchedGithubSite> findAll();

    void saveOrUpdate(final WatchedGithubSite watchedNews);

    void delete(final WatchedGithubSite watchedNews);

    void delete(final String url);
}
