package pl.bartlomiejstepien.technewsbot.discord.command;

import com.google.inject.Singleton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static pl.bartlomiejstepien.technewsbot.discord.command.CommandManager.COMMAND_PREFIX;

@Singleton
public class HelpCommand implements Command
{
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager)
    {
        this.commandManager = commandManager;
    }

    @Override
    public String getUsage()
    {
        return "help";
    }

    @Override
    public void execute(final Member member, final TextChannel textChannel, final List<String> args)
    {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#00aaff"));
        embedBuilder.setTitle("Available commands:");

        for (final Map.Entry<String, Command> commandEntry : this.commandManager.getCommands().entrySet())
        {
            embedBuilder.addField(new MessageEmbed.Field(COMMAND_PREFIX + commandEntry.getValue().getUsage(), commandEntry.getValue().getDescription(), false));
        }

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public String getDescription()
    {
        return "Shows list of all available commands";
    }
}
