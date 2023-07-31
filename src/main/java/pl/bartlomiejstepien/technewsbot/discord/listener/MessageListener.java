package pl.bartlomiejstepien.technewsbot.discord.listener;

import com.google.inject.Inject;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.technewsbot.discord.command.CommandManager;

import static pl.bartlomiejstepien.technewsbot.discord.command.CommandManager.COMMAND_PREFIX;

public class MessageListener extends ListenerAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private final CommandManager commandManager;
    private final long newsChannelId;

    @Inject
    public MessageListener(CommandManager commandManager, final long newsChannelId)
    {
        this.commandManager = commandManager;
        this.newsChannelId = newsChannelId;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        LOGGER.debug("I am ready to use!");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        if (!event.isFromGuild())
            return;

        if (isAuthorTechBot(event))
            return;

        if (event.getChannel().getIdLong() == this.newsChannelId && event.getMessage().getContentRaw().startsWith(COMMAND_PREFIX))
        {
            this.commandManager.processCommand(event.getMember(), event.getChannel().asTextChannel(), event.getMessage().getContentRaw().substring(COMMAND_PREFIX.length()));
        }
    }

    private boolean isAuthorTechBot(MessageReceivedEvent event)
    {
        return event.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong();
    }
}
