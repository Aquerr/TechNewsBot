package pl.bartlomiejstepien.technewsbot.github.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "watched_github_site")
@NamedQueries({
    @NamedQuery(name = "GITHUB_SITE.FIND_ALL", query = "SELECT site FROM WatchedGithubSite site"),
    @NamedQuery(name = "GITHUB_SITE.FIND_BY_URL", query = "SELECT site FROM WatchedGithubSite site WHERE site.url = :url")
})
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
