package pl.bartlomiejstepien.technewsbot.github.repository;

import com.google.inject.Singleton;
import org.hibernate.Session;
import pl.bartlomiejstepien.technewsbot.github.model.WatchedGithubSite;
import pl.bartlomiejstepien.technewsbot.util.EntityManagerProvider;

import javax.persistence.EntityManager;
import java.util.List;

@Singleton
public class WatchedGithubSiteRepositoryImpl implements WatchedGithubSiteRepository
{
    @Override
    public WatchedGithubSite find(Integer id)
    {
        EntityManager entityManager = getEntityManager();
        Session session = entityManager.unwrap(Session.class);
        WatchedGithubSite watchedGithubSite = session.find(WatchedGithubSite.class, id);
        session.close();
        entityManager.close();
        return watchedGithubSite;
    }

    @Override
    public WatchedGithubSite find(String url)
    {
        EntityManager entityManager = getEntityManager();

        WatchedGithubSite watchedGithubSite = entityManager.createNamedQuery("GITHUB_SITE.FIND_BY_URL", WatchedGithubSite.class)
                .setParameter("url", url)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        entityManager.close();
        return watchedGithubSite;
    }

    @Override
    public List<WatchedGithubSite> findAll()
    {
        EntityManager entityManager = getEntityManager();
        List<WatchedGithubSite> watchedGithubSites = entityManager.createNamedQuery("GITHUB_SITE.FIND_ALL", WatchedGithubSite.class)
                .getResultList();
        entityManager.close();
        return watchedGithubSites;
    }

    @Override
    public void saveOrUpdate(WatchedGithubSite watchedNews)
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
    public void delete(WatchedGithubSite watchedNews)
    {
        EntityManager entityManager = getEntityManager();
        final WatchedGithubSite watchedGithubSite = entityManager.find(WatchedGithubSite.class, watchedNews.getId());
        entityManager.remove(watchedGithubSite);
        entityManager.close();
    }

    private static EntityManager getEntityManager()
    {
        return EntityManagerProvider.provide();
    }
}
