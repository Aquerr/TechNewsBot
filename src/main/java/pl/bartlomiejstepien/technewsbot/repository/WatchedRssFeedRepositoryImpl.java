package pl.bartlomiejstepien.technewsbot.repository;

import com.google.inject.Singleton;
import org.hibernate.Session;
import pl.bartlomiejstepien.technewsbot.repository.model.WatchedRssFeed;
import pl.bartlomiejstepien.technewsbot.repository.provider.h2.EntityManagerProvider;

import javax.persistence.EntityManager;
import java.util.List;

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
        final WatchedRssFeed watchedGithubSite = entityManager.find(WatchedRssFeed.class, watchedNews.getId());
        entityManager.remove(watchedGithubSite);
        entityManager.close();
    }

    private static EntityManager getEntityManager()
    {
        return EntityManagerProvider.provide();
    }
}
