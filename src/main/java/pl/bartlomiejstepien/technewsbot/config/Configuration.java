package pl.bartlomiejstepien.technewsbot.config;

import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public final class Configuration
{
    private static final Path configFilePath = Paths.get(".").resolve("techbot.properties");

    private final String botToken;
    private final String newsChannelId;

    private Configuration(Properties properties)
    {
        this.botToken = properties.getProperty("bot-token");
        this.newsChannelId = properties.getProperty("news-channel-id");
    }

    public static Configuration loadConfiguration()
    {
        if (Files.notExists(configFilePath))
            generateDefaultConfigFile();

        return new Configuration(loadProperties());
    }

    private static void generateDefaultConfigFile()
    {
        try
        {
            URL url = Resources.getResource("techbot.properties");

            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
            int character;
            OutputStream outputStream = Files.newOutputStream(configFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            while ((character = inputStreamReader.read()) != -1)
            {
                outputStream.write(character);
            }
            outputStream.close();
            inputStreamReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties()
    {
        Properties properties = new Properties();
        try
        {
            properties.load(Files.newInputStream(configFilePath, StandardOpenOption.READ));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return properties;
    }

    public String getBotToken()
    {
        return botToken;
    }

    public String getNewsChannelId()
    {
        return newsChannelId;
    }
}
