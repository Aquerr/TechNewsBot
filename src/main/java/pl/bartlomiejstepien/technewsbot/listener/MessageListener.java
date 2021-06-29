package pl.bartlomiejstepien.technewsbot.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pl.bartlomiejstepien.technewsbot.TechNewsBot;

public class MessageListener extends ListenerAdapter
{
    private final TechNewsBot techNewsBot;

    public MessageListener(TechNewsBot techNewsBot)
    {
        this.techNewsBot = techNewsBot;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        System.out.println("I am ready to use!");
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        if (isAuthorTechBot(event.getAuthor()))
            return;
    }

    private boolean isAuthorTechBot(User user)
    {
        return user.getIdLong() == this.techNewsBot.getBotId();
    }
}
