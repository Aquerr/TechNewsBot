package pl.bartlomiejstepien.technewsbot.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WatchedSiteDto
{
    private Long id;
    private String url;
    private LocalDateTime releaseDateTime;
}
