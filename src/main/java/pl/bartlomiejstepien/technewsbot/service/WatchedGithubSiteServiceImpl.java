package pl.bartlomiejstepien.technewsbot.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import pl.bartlomiejstepien.technewsbot.converter.WatchedGithubSiteConverter;
import pl.bartlomiejstepien.technewsbot.dto.WatchedGithubSiteDto;
import pl.bartlomiejstepien.technewsbot.repository.WatchedGithubSiteRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class WatchedGithubSiteServiceImpl implements WatchedGithubSiteService
{
    private final WatchedGithubSiteConverter watchedGithubSiteConverter;
    private final WatchedGithubSiteRepository watchedGithubSiteRepository;

    @Inject
    public WatchedGithubSiteServiceImpl(WatchedGithubSiteConverter watchedGithubSiteConverter, WatchedGithubSiteRepository watchedGithubSiteRepository)
    {
        this.watchedGithubSiteConverter = watchedGithubSiteConverter;
        this.watchedGithubSiteRepository = watchedGithubSiteRepository;
    }

    @Override
    public WatchedGithubSiteDto find(Integer id)
    {
        return Optional.ofNullable(this.watchedGithubSiteRepository.find(id))
                .map(this.watchedGithubSiteConverter::convertToDto)
                .orElse(null);
    }

    @Override
    public WatchedGithubSiteDto find(String url)
    {
        return Optional.ofNullable(this.watchedGithubSiteRepository.find(url))
                .map(this.watchedGithubSiteConverter::convertToDto)
                .orElse(null);
    }

    @Override
    public List<WatchedGithubSiteDto> findAll()
    {
        return this.watchedGithubSiteRepository.findAll().stream()
                .map(this.watchedGithubSiteConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveOrUpdate(WatchedGithubSiteDto watchedNews)
    {
        Optional.ofNullable(watchedNews)
                .map(this.watchedGithubSiteConverter::convertToEntity)
                .ifPresent(this.watchedGithubSiteRepository::saveOrUpdate);
    }

    @Override
    public void delete(WatchedGithubSiteDto watchedNews)
    {
        Optional.ofNullable(watchedNews)
                .map(this.watchedGithubSiteConverter::convertToEntity)
                .ifPresent(this.watchedGithubSiteRepository::delete);
    }
}
