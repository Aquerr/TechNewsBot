package pl.bartlomiejstepien.technewsbot.discord.command;

import com.google.inject.ImplementedBy;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Map;

@ImplementedBy(CommandManagerImpl.class)
public interface CommandManager
{
    String COMMAND_PREFIX = "tech!";

    Map<String, Command> getCommands();

    void processCommand(Member member, TextChannel textChannel, String message);
}
