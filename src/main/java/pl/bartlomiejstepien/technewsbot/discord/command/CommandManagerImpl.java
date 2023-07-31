package pl.bartlomiejstepien.technewsbot.discord.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.technewsbot.core.NewsWatchManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Singleton
public class CommandManagerImpl implements CommandManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManagerImpl.class);

    private final Map<String, Command> commands = new HashMap<>();

    private final NewsWatchManager newsWatchManager;

    @Inject
    public CommandManagerImpl(NewsWatchManager newsWatchManager)
    {
        this.newsWatchManager = newsWatchManager;
        registerCommands();
    }

    @Override
    public Map<String, Command> getCommands()
    {
        return this.commands;
    }

    private void registerCommands()
    {
        this.commands.put("help", new HelpCommand(this));
        this.commands.put("watch_github", new WatchGithubCommand(this.newsWatchManager));
        this.commands.put("list", new ShowWatchingsCommand(this.newsWatchManager));
        this.commands.put("watch_rss", new WatchRssCommand(this.newsWatchManager));
        this.commands.put("unwatch", new UnwatchCommand(this.newsWatchManager));
    }

    @Override
    public void processCommand(Member member, TextChannel textChannel, String message)
    {
        for (Map.Entry<String, Command> commandMapping : commands.entrySet())
        {
            if (message.startsWith(commandMapping.getKey()))
            {
                List<String> parsedStringArguments = new LinkedList<>();
                if (commandMapping.getKey().length() + 1 <= message.length())
                {
                    parsedStringArguments = parseArguments(message.substring(commandMapping.getKey().length() + 1));
                }
                LOGGER.info("User '" + member.getUser().getName() + "' used '" + commandMapping.getValue().getUsage() + "' command with arguments '" + parsedStringArguments + "'");
                commandMapping.getValue().execute(member, textChannel, parsedStringArguments);
            }
        }
    }

    private List<String> parseArguments(final String message)
    {
        return new LinkedList<>(Arrays.asList(message.split(" ")));
    }
}
