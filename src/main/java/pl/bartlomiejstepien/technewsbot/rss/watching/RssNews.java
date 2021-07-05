package pl.bartlomiejstepien.technewsbot.rss.watching;

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
