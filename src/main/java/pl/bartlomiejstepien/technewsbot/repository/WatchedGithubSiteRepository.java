package pl.bartlomiejstepien.technewsbot.repository;

import com.google.inject.ImplementedBy;
import pl.bartlomiejstepien.technewsbot.repository.model.WatchedGithubSite;
import pl.bartlomiejstepien.technewsbot.service.WatchedGithubSiteServiceImpl;

import java.util.List;

@ImplementedBy(WatchedGithubSiteServiceImpl.class)
public interface WatchedGithubSiteRepository
{
    WatchedGithubSite find(final Integer id);

    WatchedGithubSite find(final String url);

    List<WatchedGithubSite> findAll();

    void saveOrUpdate(final WatchedGithubSite watchedNews);

    void delete(final WatchedGithubSite watchedNews);
}
