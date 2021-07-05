package pl.bartlomiejstepien.technewsbot;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import pl.bartlomiejstepien.technewsbot.converter.WatchedRssFeedConverter;
import pl.bartlomiejstepien.technewsbot.discord.command.CommandManager;
import pl.bartlomiejstepien.technewsbot.converter.WatchedGithubSiteConverter;
import pl.bartlomiejstepien.technewsbot.repository.WatchedGithubSiteRepository;
import pl.bartlomiejstepien.technewsbot.repository.WatchedGithubSiteRepositoryImpl;
import pl.bartlomiejstepien.technewsbot.repository.WatchedRssFeedRepository;
import pl.bartlomiejstepien.technewsbot.repository.WatchedRssFeedRepositoryImpl;
import pl.bartlomiejstepien.technewsbot.service.WatchedGithubSiteService;
import pl.bartlomiejstepien.technewsbot.service.WatchedGithubSiteServiceImpl;
import pl.bartlomiejstepien.technewsbot.service.WatchedRssFeedService;
import pl.bartlomiejstepien.technewsbot.service.WatchedRssFeedServiceImpl;
import pl.bartlomiejstepien.technewsbot.watching.NewsWatchManager;

public class BasicModule extends AbstractModule
{
    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("bot-id")).to(System.getProperty("BOT_ID"));
        bindConstant().annotatedWith(Names.named("news-channel-id")).to(System.getProperty("NEWS_CHANNEL_ID"));

        bind(WatchedGithubSiteConverter.class).asEagerSingleton();
        bind(WatchedRssFeedConverter.class).asEagerSingleton();
        bind(NewsWatchManager.class).asEagerSingleton();
        bind(CommandManager.class).asEagerSingleton();
        bind(WatchedGithubSiteRepository.class).to(WatchedGithubSiteRepositoryImpl.class).asEagerSingleton();
        bind(WatchedGithubSiteService.class).to(WatchedGithubSiteServiceImpl.class).asEagerSingleton();
        bind(WatchedRssFeedService.class).to(WatchedRssFeedServiceImpl.class);
        bind(WatchedRssFeedRepository.class).to(WatchedRssFeedRepositoryImpl.class);
    }
}