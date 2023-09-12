package org.staimov.dao;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.staimov.domain.Country;

import java.util.List;

public class CountryDAO {
    private final SessionFactory sessionFactory;

    public CountryDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Country> getAll() {
        return sessionFactory.getCurrentSession().createQuery(
                "select c from Country c join fetch c.languages", Country.class).list();
    }
}
