package pl.bartlomiejstepien.technewsbot.github.repository;

import com.google.inject.ImplementedBy;
import pl.bartlomiejstepien.technewsbot.github.model.WatchedGithubSite;
import pl.bartlomiejstepien.technewsbot.github.service.WatchedGithubSiteServiceImpl;

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
