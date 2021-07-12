package pl.bartlomiejstepien.technewsbot.rss.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import pl.bartlomiejstepien.technewsbot.converter.WatchedRssFeedConverter;
import pl.bartlomiejstepien.technewsbot.dto.WatchedRssFeedDto;
import pl.bartlomiejstepien.technewsbot.rss.repository.WatchedRssFeedRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class WatchedRssFeedServiceImpl implements WatchedRssFeedService
{
    private final WatchedRssFeedConverter watchedRssFeedConverter;
    private final WatchedRssFeedRepository watchedRssFeedRepository;

    @Inject
    public WatchedRssFeedServiceImpl(WatchedRssFeedConverter watchedRssFeedConverter, WatchedRssFeedRepository watchedRssFeedRepository)
    {
        this.watchedRssFeedConverter = watchedRssFeedConverter;
        this.watchedRssFeedRepository = watchedRssFeedRepository;
    }

    @Override
    public WatchedRssFeedDto find(Integer id)
    {
        return Optional.ofNullable(this.watchedRssFeedRepository.find(id))
                .map(this.watchedRssFeedConverter::convertToDto)
                .orElse(null);
    }

    @Override
    public WatchedRssFeedDto find(String url)
    {
        return Optional.ofNullable(this.watchedRssFeedRepository.find(url))
                .map(this.watchedRssFeedConverter::convertToDto)
                .orElse(null);
    }

    @Override
    public List<WatchedRssFeedDto> findAll()
    {
        return this.watchedRssFeedRepository.findAll().stream()
                .map(this.watchedRssFeedConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveOrUpdate(WatchedRssFeedDto watchedNews)
    {
        Optional.ofNullable(watchedNews)
                .map(this.watchedRssFeedConverter::convertToEntity)
                .ifPresent(this.watchedRssFeedRepository::saveOrUpdate);
    }

    @Override
    public void delete(WatchedRssFeedDto watchedNews)
    {
        Optional.ofNullable(watchedNews)
                .map(this.watchedRssFeedConverter::convertToEntity)
                .ifPresent(this.watchedRssFeedRepository::delete);
    }

    @Override
    public void delete(String url)
    {
        Optional.ofNullable(url)
                .ifPresent(watchedRssFeedRepository::delete);
    }
}
