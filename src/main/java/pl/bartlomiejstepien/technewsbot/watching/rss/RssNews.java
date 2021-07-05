package pl.bartlomiejstepien.technewsbot.watching.rss;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class RssNews
{
    String projectName;
    String newsTitle;
    String description;
    LocalDateTime releaseDateTime;
    String url;
}
