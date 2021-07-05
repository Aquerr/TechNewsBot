package pl.bartlomiejstepien.technewsbot.watching.github;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class GithubRelease
{
    String projectName;
    String tag;
    String releaseNote;
    LocalDateTime releaseDateTime;
    String releaseUrl;
}
