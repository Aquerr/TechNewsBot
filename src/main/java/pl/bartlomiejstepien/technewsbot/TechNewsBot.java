package pl.bartlomiejstepien.technewsbot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pl.bartlomiejstepien.technewsbot.config.Configuration;
import pl.bartlomiejstepien.technewsbot.core.NewsWatchManager;
import pl.bartlomiejstepien.technewsbot.discord.DiscordMessagePublisher;
import pl.bartlomiejstepien.technewsbot.discord.command.CommandManager;
import pl.bartlomiejstepien.technewsbot.discord.listener.MessageListener;
import pl.bartlomiejstepien.technewsbot.github.service.WatchedGithubSiteService;

import java.util.EnumSet;

@Singleton
public class TechNewsBot
{
    private static TechNewsBot INSTANCE;

    public static TechNewsBot getInstance()
    {
        return INSTANCE;
    }

    private JDA jda;
    private Configuration configuration;

    private CommandManager commandManager;

    private NewsWatchManager newsWatchManager;
    private WatchedGithubSiteService watchedGithubSiteService;

    private DiscordMessagePublisher discordMessagePublisher;

    @Inject
    public TechNewsBot(Configuration configuration, CommandManager commandManager, NewsWatchManager newsWatchManager, WatchedGithubSiteService watchedGithubSiteService)
    {
        INSTANCE = this;
        this.configuration = configuration;
        this.commandManager = commandManager;
        this.watchedGithubSiteService = watchedGithubSiteService;
        this.newsWatchManager = newsWatchManager;
    }

    public void start()
    {
        try
        {
            this.jda = JDABuilder.createLight(this.configuration.getBotToken(), EnumSet.of(GatewayIntent.GUILD_MESSAGES)) // slash commands don't need any intents
                    .setActivity(Activity.playing("Techbot tech!help https://github.com/Aquerr/TechNewsBot"))
                    .addEventListeners(new MessageListener(this.commandManager, getChannelId()))
                    .build().awaitReady();
        }
        catch (InterruptedException e)
        {
            throw new IllegalStateException(e);
        }

        TextChannel textChannel = this.jda.getTextChannelById(getChannelId());

        this.discordMessagePublisher = new DiscordMessagePublisher(textChannel);
        this.newsWatchManager.load();
    }

    public JDA getJda()
    {
        return jda;
    }

    public Long getChannelId()
    {
        return Long.parseLong(this.configuration.getNewsChannelId());
    }

    public DiscordMessagePublisher getDiscordPublisher()
    {
        return this.discordMessagePublisher;
    }
}
