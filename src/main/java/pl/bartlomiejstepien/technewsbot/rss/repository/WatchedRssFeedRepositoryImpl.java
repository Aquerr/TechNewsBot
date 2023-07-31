package pl.bartlomiejstepien.technewsbot.rss.repository;

import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import pl.bartlomiejstepien.technewsbot.rss.model.WatchedRssFeed;
import pl.bartlomiejstepien.technewsbot.util.EntityManagerProvider;

import java.util.List;
import java.util.Optional;

@Singleton
public class WatchedRssFeedRepositoryImpl implements WatchedRssFeedRepository
{
    @Override
    public WatchedRssFeed find(Integer id)
    {
        EntityManager entityManager = getEntityManager();
        Session session = entityManager.unwrap(Session.class);
        WatchedRssFeed watchedGithubSite = session.find(WatchedRssFeed.class, id);
        session.close();
        entityManager.close();
        return watchedGithubSite;
    }

    @Override
    public WatchedRssFeed find(String url)
    {
        EntityManager entityManager = getEntityManager();

        WatchedRssFeed watchedGithubSite = entityManager.createNamedQuery("RSS_FEED.FIND_BY_URL", WatchedRssFeed.class)
                .setParameter("url", url)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        entityManager.close();
        return watchedGithubSite;
    }

    @Override
    public List<WatchedRssFeed> findAll()
    {
        EntityManager entityManager = getEntityManager();
        List<WatchedRssFeed> watchedGithubSites = entityManager.createNamedQuery("RSS_FEED.FIND_ALL", WatchedRssFeed.class)
                .getResultList();
        entityManager.close();
        return watchedGithubSites;
    }

    @Override
    public void saveOrUpdate(WatchedRssFeed watchedNews)
    {
        EntityManager entityManager = getEntityManager();
        Session session = entityManager.unwrap(Session.class);
        session.beginTransaction();
        session.saveOrUpdate(watchedNews);
        session.getTransaction().commit();
        session.close();
        entityManager.close();
    }

    @Override
    public void delete(WatchedRssFeed watchedNews)
    {
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        final WatchedRssFeed watchedGithubSite = entityManager.find(WatchedRssFeed.class, watchedNews.getId());
        entityManager.remove(watchedGithubSite);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void delete(String url)
    {
        Optional.ofNullable(url)
                .ifPresent(url1 -> {
                    EntityManager entityManager = getEntityManager();
                    entityManager.getTransaction().begin();
                    entityManager.createNamedQuery("RSS_FEED.FIND_BY_URL", WatchedRssFeed.class)
                            .setParameter("url", url1)
                            .getResultList()
                            .stream()
                            .findFirst()
                            .ifPresent(entityManager::remove);
                    entityManager.getTransaction().commit();
                    entityManager.close();
                });
    }

    private static EntityManager getEntityManager()
    {
        return EntityManagerProvider.provide();
    }
}
