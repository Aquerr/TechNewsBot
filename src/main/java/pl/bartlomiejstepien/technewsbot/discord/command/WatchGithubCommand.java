package pl.bartlomiejstepien.technewsbot.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.technewsbot.watching.NewsWatchManager;
import pl.bartlomiejstepien.technewsbot.watching.exception.NoWatcherRegisteredForGivenSiteTypeException;
import pl.bartlomiejstepien.technewsbot.watching.exception.URIAlreadyBeingWatchedException;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class WatchGithubCommand implements Command
{
    private final NewsWatchManager watchManager;

    public WatchGithubCommand(final NewsWatchManager watchManager)
    {
        this.watchManager = watchManager;
    }

    @Override
    public String getUsage()
    {
        return "watch_github <url>";
    }

    @Override
    public String getDescription()
    {
        return "Adds a watch on the specified github project";
    }

    @Override
    public void execute(@NotNull Member member, @NotNull TextChannel textChannel, @NotNull List<String> args)
    {
        String url = args.get(0);
        EmbedBuilder embedBuilder = new EmbedBuilder();

        try
        {
            URI uri = new URI(url);
            watchManager.watch(uri);
            embedBuilder.setDescription("Watch started on " + uri);
            embedBuilder.setColor(Color.CYAN);
        }
        catch (URISyntaxException e)
        {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("Provided URL has wrong format!");
            e.printStackTrace();
        }
        catch (URIAlreadyBeingWatchedException e)
        {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("Given URL is already being watched!");
            e.printStackTrace();
        }
        catch (NoWatcherRegisteredForGivenSiteTypeException e)
        {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("Sorry, there is no watching mechanism for the given site type.");
            e.printStackTrace();
        }

        textChannel.sendMessage(embedBuilder.build()).complete();
    }
}
