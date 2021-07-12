package pl.bartlomiejstepien.technewsbot.github.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.technewsbot.converter.WatchedGithubSiteConverter;
import pl.bartlomiejstepien.technewsbot.dto.WatchedGithubSiteDto;
import pl.bartlomiejstepien.technewsbot.github.model.WatchedGithubSite;
import pl.bartlomiejstepien.technewsbot.github.repository.WatchedGithubSiteRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WatchedGithubSiteServiceImplTest
{
    private static final Long WATCHED_GITHUB_SITE_ID = 1L;

    @Mock
    private WatchedGithubSiteConverter watchedGithubSiteConverter;

    @Mock
    private WatchedGithubSiteRepository watchedGithubSiteRepository;

    @InjectMocks
    private WatchedGithubSiteServiceImpl watchedGithubSiteService;

    @Captor
    private ArgumentCaptor<WatchedGithubSite> watchedGithubSiteArgumentCaptor;

    @Test
    void findShouldUseRepositoryAndConverterAndReturnWatchedGithubSiteDto()
    {
        WatchedGithubSite watchedGithubSite = new WatchedGithubSite();
        watchedGithubSite.setId(WATCHED_GITHUB_SITE_ID);
        WatchedGithubSiteDto watchedGithubSiteDto = new WatchedGithubSiteDto();
        watchedGithubSiteDto.setId(WATCHED_GITHUB_SITE_ID);
        watchedGithubSiteDto.setUrl("dasda");
        given(watchedGithubSiteRepository.find(WATCHED_GITHUB_SITE_ID)).willReturn(watchedGithubSite);
        given(watchedGithubSiteConverter.convertToDto(watchedGithubSite)).willReturn(watchedGithubSiteDto);

        final WatchedGithubSiteDto actual = watchedGithubSiteService.find(WATCHED_GITHUB_SITE_ID);

        verify(watchedGithubSiteRepository, times(1)).find(WATCHED_GITHUB_SITE_ID);
        verify(watchedGithubSiteConverter, times(1)).convertToDto(watchedGithubSiteArgumentCaptor.capture());

        assertThat(actual).usingRecursiveComparison().isEqualTo(watchedGithubSiteDto);
    }

    @Test
    void findShouldReturnNullWhenWatchedGithubSiteHasNotBeenFound()
    {
        given(watchedGithubSiteRepository.find(WATCHED_GITHUB_SITE_ID)).willReturn(null);

        final WatchedGithubSiteDto actual = watchedGithubSiteService.find(WATCHED_GITHUB_SITE_ID);

        assertThat(actual).isNull();
    }
}