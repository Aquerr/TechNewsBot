package pl.bartlomiejstepien.technewsbot.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WatchedGithubSiteDto extends WatchedSiteDto
{
    private String currentReleaseTag;
}