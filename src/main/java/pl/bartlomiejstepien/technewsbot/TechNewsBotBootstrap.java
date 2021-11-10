package pl.bartlomiejstepien.technewsbot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import pl.bartlomiejstepien.technewsbot.config.Configuration;
import pl.bartlomiejstepien.technewsbot.core.NewsWatchManager;
import pl.bartlomiejstepien.technewsbot.discord.command.CommandManager;
import pl.bartlomiejstepien.technewsbot.github.service.WatchedGithubSiteService;
import pl.bartlomiejstepien.technewsbot.inject.BasicModule;

public class TechNewsBotBootstrap
{
    public static void main(String[] args)
    {
        Injector injector = Guice.createInjector(new BasicModule());
        WatchedGithubSiteService watchedGithubSiteService = injector.getInstance(WatchedGithubSiteService.class);
        CommandManager commandManager = injector.getInstance(CommandManager.class);
        NewsWatchManager newsWatchManager = injector.getInstance(NewsWatchManager.class);

        new TechNewsBot(Configuration.loadConfiguration(), commandManager, newsWatchManager, watchedGithubSiteService).start();
    }
}
