package com.logic.utils;

import com.logic.entity.Person;
import com.logic.entity.Address;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import static java.lang.Boolean.*;
import static org.hibernate.cfg.AvailableSettings.*;

public class HibernateUtil {
    //XML based configuration
    private static SessionFactory sessionFactory;

    //costruttore di default privato perché si tratta di una classe con soli metodi statici
    private HibernateUtil(){}

    private static SessionFactory buildSessionFactory() {
        try {
            //create the SessionFactory
            sessionFactory = new Configuration()
                    .addAnnotatedClass(Address.class)
                    .addAnnotatedClass(Person.class)
                    .setProperty(URL, "jdbc:mysql://localhost:3306/peindb")
                    .setProperty(USER, "root")
                    .setProperty(PASS, "Kp*d.!>3")
                    .setProperty("hibernate.agroal.maxSize", "40")
                    .setProperty("hibernate.connection.pool_size", "1")
                    .setProperty("hibernate.current_session_context_class", "thread")
                    .setProperty(SHOW_SQL, TRUE.toString())
                    .setProperty(FORMAT_SQL, TRUE.toString())
                    .buildSessionFactory();

            return sessionFactory;

        }
        catch (Throwable ex) {
            //make sure you log the exception, as it might be swallowed
            //System.err.println("Initial SessionFactory creation failed: " + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);

        }

    }

    public static SessionFactory getSessionFactory() {
        if(sessionFactory == null) sessionFactory = buildSessionFactory();
        return sessionFactory;

    }

}
