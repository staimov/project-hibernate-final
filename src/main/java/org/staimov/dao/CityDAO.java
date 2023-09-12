package org.staimov.dao;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.staimov.domain.City;

import java.util.List;

public class CityDAO {

    private final SessionFactory sessionFactory;

    public CityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<City> getItems(int offset, int limit) {
        Query<City> query = sessionFactory.getCurrentSession().createQuery(
                "from City", City.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    public int getCount() {
        Query query = sessionFactory.getCurrentSession().createQuery(
                "select count(*) from City");
        return Math.toIntExact((Long) query.uniqueResult());
    }
}
