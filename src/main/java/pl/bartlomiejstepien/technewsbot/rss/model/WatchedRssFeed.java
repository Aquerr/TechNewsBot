package pl.bartlomiejstepien.technewsbot.rss.model;

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
@Table(name = "watched_rss_feed")
@NamedQuery(name = "RSS_FEED.FIND_ALL", query = "SELECT feed FROM WatchedRssFeed feed")
@NamedQuery(name = "RSS_FEED.FIND_BY_URL", query = "SELECT feed FROM WatchedRssFeed feed WHERE feed.url = :url")
@Getter
@Setter
public class WatchedRssFeed
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

    @Column(name = "latest_news_date_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime relaseDateTime;
}
