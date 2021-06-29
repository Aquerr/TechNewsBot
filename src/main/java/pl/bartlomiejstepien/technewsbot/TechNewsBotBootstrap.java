package pl.bartlomiejstepien.technewsbot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TechNewsBotBootstrap
{
    public static void main(String[] args)
    {
        //

        new TechNewsBot(loadProperties()).start();
    }

    private static Properties loadProperties()
    {
        Properties properties = System.getProperties();
        InputStream inputStream = TechNewsBotBootstrap.class.getClassLoader().getResourceAsStream("techbot.properties");
        try
        {
            properties.load(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return properties;

    }
}
