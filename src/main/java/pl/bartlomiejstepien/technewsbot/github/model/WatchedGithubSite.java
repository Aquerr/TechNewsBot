package pl.bartlomiejstepien.technewsbot.github.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "watched_github_site")
@NamedQuery(name = "GITHUB_SITE.FIND_ALL", query = "SELECT site FROM WatchedGithubSite site")
@NamedQuery(name = "GITHUB_SITE.FIND_BY_URL", query = "SELECT site FROM WatchedGithubSite site WHERE site.url = :url")
@Getter
@Setter
public class WatchedGithubSite
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

    @Column(name = "current_release_tag")
    private String currentReleaseTag;

    @Column(name = "release_date_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime relaseDateTime;
}
