package pl.bartlomiejstepien.technewsbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pl.bartlomiejstepien.technewsbot.listener.MessageListener;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Properties;

public class TechNewsBot
{
    private JDA jda;

    private Properties properties;

    private String botToken;

    public TechNewsBot(Properties properties)
    {
        this.properties = properties;
    }

    public void start()
    {
        try
        {
            this.jda = JDABuilder.createLight(this.properties.getProperty("BOT_TOKEN"), EnumSet.of(GatewayIntent.GUILD_MESSAGES)) // slash commands don't need any intents
                    .addEventListeners(new MessageListener(this))
                    .build();
        }
        catch (LoginException e)
        {
            e.printStackTrace();
        }
    }

    public JDA getJda()
    {
        return jda;
    }

    public long getBotId()
    {
        return Long.parseLong(this.properties.getProperty("BOT_ID"));
    }
}
