package pl.bartlomiejstepien.technewsbot.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerProvider
{
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pl.bartlomiejstepien.technewsbot");

    public static EntityManager provide()
    {
        return entityManagerFactory.createEntityManager();
    }
}
