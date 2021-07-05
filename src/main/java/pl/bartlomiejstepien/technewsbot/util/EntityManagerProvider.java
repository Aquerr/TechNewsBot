package pl.bartlomiejstepien.technewsbot.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider
{
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pl.bartlomiejstepien.technewsbot");

//    public static Connection getConnection()
//    {
//        Connection conn = null;
//        try
//        {
//            conn = DriverManager.getConnection("jdbc:h2:~/technewsbot", "technewsbot", "technewsbot");
//            conn.setAutoCommit(false);
//        }
//        catch (SQLException throwables)
//        {
//            throwables.printStackTrace();
//        }
//
//        return conn;
//         add application code here
//    }

    public static EntityManager provide()
    {
        return entityManagerFactory.createEntityManager();
    }
}
