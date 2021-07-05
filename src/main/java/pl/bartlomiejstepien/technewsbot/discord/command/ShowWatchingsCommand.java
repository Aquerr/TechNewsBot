package pl.bartlomiejstepien.technewsbot.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import pl.bartlomiejstepien.technewsbot.dto.WatchedSiteDto;
import pl.bartlomiejstepien.technewsbot.watching.NewsWatchManager;

import java.awt.*;
import java.util.List;

public class ShowWatchingsCommand implements Command
{
    private final NewsWatchManager newsWatchManager;

    public ShowWatchingsCommand(final NewsWatchManager newsWatchManager)
    {
        this.newsWatchManager = newsWatchManager;
    }

    @Override
    public String getUsage()
    {
        return "show_watchings";
    }

    @Override
    public String getDescription()
    {
        return "Shows all currently watched sites";
    }

    @Override
    public void execute(@NotNull Member member, @NotNull TextChannel textChannel, @NotNull List<String> args)
    {
        final List<WatchedSiteDto> watchedSites = this.newsWatchManager.getWatchedSites();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.BLUE);
        embedBuilder.setTitle("List of currently watched sites ");

        StringBuilder descriptionBuilder = new StringBuilder();
        for (final WatchedSiteDto watchedSiteDto : watchedSites)
        {
            descriptionBuilder.append(watchedSiteDto.getUrl()).append("\n");
        }
        embedBuilder.setDescription(descriptionBuilder.toString());

        textChannel.sendMessage(embedBuilder.build()).queue();
    }
}
