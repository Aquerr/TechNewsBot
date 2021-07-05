package pl.bartlomiejstepien.technewsbot.service;

import com.google.inject.ImplementedBy;
import pl.bartlomiejstepien.technewsbot.dto.WatchedGithubSiteDto;
import pl.bartlomiejstepien.technewsbot.repository.WatchedGithubSiteRepositoryImpl;

import java.util.List;

@ImplementedBy(WatchedGithubSiteRepositoryImpl.class)
public interface WatchedGithubSiteService
{
    WatchedGithubSiteDto find(final Integer id);

    WatchedGithubSiteDto find(final String url);

    List<WatchedGithubSiteDto> findAll();

    void saveOrUpdate(final WatchedGithubSiteDto watchedNews);

    void delete(final WatchedGithubSiteDto watchedNews);
}
