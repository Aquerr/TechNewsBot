package pl.bartlomiejstepien.technewsbot.discord;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import pl.bartlomiejstepien.technewsbot.github.watching.GithubRelease;
import pl.bartlomiejstepien.technewsbot.rss.watching.RssNews;

import java.awt.*;

@Singleton
public class DiscordMessagePublisher
{
    private final TextChannel newsChannel;

    @Inject
    public DiscordMessagePublisher(TextChannel newsChannel)
    {
        this.newsChannel = newsChannel;
    }

    public void publishReleaseNotes(GithubRelease githubRelease)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);

        embedBuilder.setTitle("There is a new release of " + githubRelease.getProjectName() + " - " + githubRelease.getTag(), githubRelease.getReleaseUrl());
        String releaseNote = githubRelease.getReleaseNote();
        String releaseNotesFooter = null;
        if (releaseNote.length() > MessageEmbed.TEXT_MAX_LENGTH)
        {
            releaseNote = releaseNote.substring(0, MessageEmbed.TEXT_MAX_LENGTH);
            releaseNotesFooter = githubRelease.getReleaseNote().substring(MessageEmbed.TEXT_MAX_LENGTH);
            if (releaseNotesFooter.length() > MessageEmbed.TEXT_MAX_LENGTH)
            {
                releaseNotesFooter = releaseNotesFooter.substring(0, MessageEmbed.TEXT_MAX_LENGTH);
            }
        }

        embedBuilder.setDescription(releaseNote);
        embedBuilder.setFooter(releaseNotesFooter);

        this.newsChannel.sendMessage(embedBuilder.build()).complete();
    }

    public void publishReleaseNotes(RssNews rssNews)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);

        embedBuilder.setTitle(rssNews.getProjectName() + " - " + rssNews.getNewsTitle(), rssNews.getUrl());
        String releaseNote = rssNews.getDescription();
        String releaseNotesFooter = null;
        if (releaseNote.length() > MessageEmbed.TEXT_MAX_LENGTH)
        {
            releaseNote = releaseNote.substring(0, MessageEmbed.TEXT_MAX_LENGTH);
            releaseNotesFooter = rssNews.getDescription().substring(MessageEmbed.TEXT_MAX_LENGTH);
            if (releaseNotesFooter.length() > MessageEmbed.TEXT_MAX_LENGTH)
            {
                releaseNotesFooter = releaseNotesFooter.substring(0, MessageEmbed.TEXT_MAX_LENGTH);
            }
        }

        embedBuilder.setDescription(releaseNote);
        embedBuilder.setFooter(releaseNotesFooter);

        this.newsChannel.sendMessage(embedBuilder.build()).complete();
    }
}
