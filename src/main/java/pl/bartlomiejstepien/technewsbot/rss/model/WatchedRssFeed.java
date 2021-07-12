package pl.bartlomiejstepien.technewsbot.rss.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
