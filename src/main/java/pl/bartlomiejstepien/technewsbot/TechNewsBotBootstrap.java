package pl.bartlomiejstepien.technewsbot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import pl.bartlomiejstepien.technewsbot.discord.command.CommandManager;
import pl.bartlomiejstepien.technewsbot.inject.BasicModule;
import pl.bartlomiejstepien.technewsbot.github.service.WatchedGithubSiteService;
import pl.bartlomiejstepien.technewsbot.core.NewsWatchManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TechNewsBotBootstrap
{
    public static void main(String[] args)
    {
        Injector injector = Guice.createInjector(new BasicModule());
        WatchedGithubSiteService watchedGithubSiteService = injector.getInstance(WatchedGithubSiteService.class);
        CommandManager commandManager = injector.getInstance(CommandManager.class);
        NewsWatchManager newsWatchManager = injector.getInstance(NewsWatchManager.class);

        new TechNewsBot(loadProperties(), commandManager, newsWatchManager, watchedGithubSiteService).start();
    }

    private static Properties loadProperties()
    {
        Properties properties = System.getProperties();
        InputStream inputStream = TechNewsBotBootstrap.class.getClassLoader().getResourceAsStream("techbot.properties");
        try
        {
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return properties;

    }
}
