package pl.bartlomiejstepien.technewsbot.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import pl.bartlomiejstepien.technewsbot.core.NewsWatchManager;
import pl.bartlomiejstepien.technewsbot.core.WatcherType;
import pl.bartlomiejstepien.technewsbot.exception.NoWatcherForGivenSiteTypeException;
import pl.bartlomiejstepien.technewsbot.exception.URIAlreadyBeingWatchedException;
import pl.bartlomiejstepien.technewsbot.exception.UnrecognizedURLException;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
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
        return "watch_github <repo_url>";
    }

    @Override
    public String getDescription()
    {
        return "Adds a watch on the specified github project";
    }

    @Override
    public void execute(@NotNull Member member, @NotNull TextChannel textChannel, @NotNull List<String> args)
    {
        String urlString = args.get(0);
        EmbedBuilder embedBuilder = new EmbedBuilder();

        try
        {
            URL url = new URL(urlString);
            watchManager.watch(WatcherType.GITHUB, url);
            embedBuilder.setDescription("Watch started on " + url);
            embedBuilder.setColor(Color.CYAN);
        }
        catch (URIAlreadyBeingWatchedException e)
        {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("Given URL is already being watched!");
            e.printStackTrace();
        }
        catch (NoWatcherForGivenSiteTypeException e)
        {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("Sorry, there is no watching mechanism for the given site type.");
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (UnrecognizedURLException e)
        {
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("Provided URL has wrong format!");
            e.printStackTrace();
        }

        textChannel.sendMessage(embedBuilder.build()).complete();
    }
}
