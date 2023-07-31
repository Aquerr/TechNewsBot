package pl.bartlomiejstepien.technewsbot.discord.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Command
{
    String getUsage();

    String getDescription();

    void execute(@NotNull final Member member, @NotNull final TextChannel textChannel, @NotNull List<String> args);
}
