package org.staimov;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.staimov.dao.CityDAO;
import org.staimov.dao.CountryDAO;
import org.staimov.domain.City;
import org.staimov.domain.Country;
import org.staimov.domain.CountryLanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Objects.nonNull;
import static org.staimov.config.DBConstants.*;

public class App {
    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;

    private final ObjectMapper mapper;

    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;

    public App() {
        sessionFactory = prepareRelationalDb();

        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);

        redisClient = prepareRedisClient();

        mapper = new ObjectMapper();
    }

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        List<City> allCities = fetchData();

//        City city = allCities.get(0);
//        Country country = city.getCountry();
//        CountryLanguage language = country.getLanguages().iterator().next();
//        System.out.println(city);
//        System.out.println(country);
//        System.out.println(language);

        shutdown();

    }

    private void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }

    private List<City> fetchData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();

            Transaction transaction = session.beginTransaction();

            List<Country> countries = countryDAO.getAll();

            int totalCount = cityDAO.getCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityDAO.getItems(i, step));
            }

            transaction.commit();

            return allCities;
        }
    }

    private RedisClient prepareRedisClient() {
        return null;
    }

    private SessionFactory prepareRelationalDb() {
        final SessionFactory sessionFactory;

        Properties properties = new Properties();
        properties.put(Environment.DIALECT, DB_DIALECT);
        properties.put(Environment.DRIVER, DB_DRIVER);
        properties.put(Environment.URL, DB_URL);
        properties.put(Environment.USER, DB_USER);
        properties.put(Environment.PASS, DB_PASS);
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, CURRENT_SESSION_CONTEXT_CLASS);
        properties.put(Environment.HBM2DDL_AUTO, DB_HBM2DDL_AUTO);
        properties.put(Environment.STATEMENT_BATCH_SIZE, STATEMENT_BATCH_SIZE);

        sessionFactory = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .addProperties(properties)
                .buildSessionFactory();

        return sessionFactory;
    }


}
