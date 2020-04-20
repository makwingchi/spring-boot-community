package com.project.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-14 17:38
 */
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {

    @Override
    public String select() {
        return "Hibernate";
    }
}
