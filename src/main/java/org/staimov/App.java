package org.staimov;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
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
import org.staimov.redis.CityCountry;
import org.staimov.redis.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

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

        List<CityCountry> preparedData = transformData(allCities);

        pushToRedis(preparedData);

        testSpeed();

        shutdown();
    }

    private void testSpeed() {
        //закроем текущую сессию, чтоб точно делать запрос к БД, а не вытянуть данные из кэша
        sessionFactory.getCurrentSession().close();

        // 10 существующих id
        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));
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

    private List<City> fetchData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();

            Transaction transaction = session.beginTransaction();

            int totalCount = cityDAO.getCount();
            int step = FETCH_STEP;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityDAO.getItems(i, step));
            }

            transaction.commit();

            return allCities;
        }
    }

    private void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();
            for (Integer id : ids) {
                City city = cityDAO.getById(id);
                // явно запросим у страны список языков,
                // чтоб наверняка получить полный объект (без прокси-заглушек)
                Set<CountryLanguage> languages = city.getCountry().getLanguages();
            }
            transaction.commit();
        }
    }

    private List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(city -> {
            CityCountry cityCountry = new CityCountry();

            cityCountry.setCityId(city.getId());
            cityCountry.setCityName(city.getName());
            cityCountry.setCityPopulation(city.getPopulation());
            cityCountry.setCityDistrict(city.getDistrict());

            Country country = city.getCountry();
            cityCountry.setAlternativeCountryCode(country.getAlternativeCode());
            cityCountry.setContinent(country.getContinent());
            cityCountry.setCountryCode(country.getCode());
            cityCountry.setCountryName(country.getName());
            cityCountry.setCountryPopulation(country.getPopulation());
            cityCountry.setCountryRegion(country.getRegion());
            cityCountry.setCountrySurfaceArea(country.getSurfaceArea());

            Set<CountryLanguage> countryLanguages = country.getLanguages();
            Set<Language> languages = countryLanguages.stream().map(countryLanguage -> {
                Language language = new Language();
                language.setLanguageName(countryLanguage.getLanguageName());
                language.setIsOfficial(countryLanguage.isOfficial());
                language.setPercentage(countryLanguage.getPercentage());
                return language;
            }).collect(Collectors.toSet());
            cityCountry.setLanguages(languages);

            return cityCountry;
        }).collect(Collectors.toList());
    }

    private RedisClient prepareRedisClient() {
        RedisClient redisClient = RedisClient.create(RedisURI.create(REDIS_HOST, REDIS_PORT));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            System.out.println("******************\nConnected to Redis\n******************\n");
        }
        return redisClient;
    }

    private void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getCityId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    mapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }
}
