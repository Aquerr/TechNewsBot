package pl.bartlomiejstepien.technewsbot.discord.command;

import com.google.inject.Singleton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import pl.bartlomiejstepien.technewsbot.core.NewsWatchManager;

import java.util.List;

@Singleton
public class UnwatchCommand implements Command
{
    private final NewsWatchManager watchManager;

    public UnwatchCommand(final NewsWatchManager watchManager)
    {
        this.watchManager = watchManager;
    }

    @Override
    public String getUsage()
    {
        return "unwatch";
    }

    @Override
    public String getDescription()
    {
        return "Deletes a watch on given url";
    }

    @Override
    public void execute(@NotNull Member member, @NotNull TextChannel textChannel, @NotNull List<String> args)
    {
        String urlString = args.get(0);
        watchManager.unwatch(urlString);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Unwatched url: " + urlString);
        textChannel.sendMessage(embedBuilder.build()).queue();
    }
}
