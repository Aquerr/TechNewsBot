package pl.bartlomiejstepien.technewsbot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pl.bartlomiejstepien.technewsbot.discord.DiscordMessagePublisher;
import pl.bartlomiejstepien.technewsbot.discord.command.CommandManager;
import pl.bartlomiejstepien.technewsbot.discord.listener.MessageListener;
import pl.bartlomiejstepien.technewsbot.service.WatchedGithubSiteService;
import pl.bartlomiejstepien.technewsbot.watching.NewsWatchManager;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Properties;

@Singleton
public class TechNewsBot
{
    private static TechNewsBot INSTANCE;

    public static TechNewsBot getInstance()
    {
        return INSTANCE;
    }

    private JDA jda;
    private Properties properties;
    private String botToken;

    private CommandManager commandManager;

    private NewsWatchManager newsWatchManager;
    private WatchedGithubSiteService watchedGithubSiteService;

    private DiscordMessagePublisher discordMessagePublisher;

    @Inject
    public TechNewsBot(Properties properties, CommandManager commandManager, NewsWatchManager newsWatchManager, WatchedGithubSiteService watchedGithubSiteService)
    {
        INSTANCE = this;
        this.properties = properties;
        this.commandManager = commandManager;
        this.watchedGithubSiteService = watchedGithubSiteService;
        this.newsWatchManager = newsWatchManager;
    }

    public void start()
    {
        try
        {
            this.jda = JDABuilder.createLight(this.properties.getProperty("BOT_TOKEN"), EnumSet.of(GatewayIntent.GUILD_MESSAGES)) // slash commands don't need any intents
                    .addEventListeners(new MessageListener(this.commandManager, getBotId(), getChannelId()))
                    .build().awaitReady();
        }
        catch (LoginException | InterruptedException e)
        {
            e.printStackTrace();
        }

        TextChannel textChannel = this.jda.getTextChannelById(getChannelId());

        this.discordMessagePublisher = new DiscordMessagePublisher(textChannel);
        this.newsWatchManager.load();
    }

    public JDA getJda()
    {
        return jda;
    }

    public long getBotId()
    {
        return Long.parseLong(this.properties.getProperty("BOT_ID"));
    }

    public Long getChannelId()
    {
        return Long.parseLong(this.properties.getProperty("NEWS_CHANNEL_ID"));
    }

    public DiscordMessagePublisher getDiscordPublisher()
    {
        return this.discordMessagePublisher;
    }
}
