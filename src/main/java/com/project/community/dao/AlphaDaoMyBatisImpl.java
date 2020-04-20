package com.project.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-14 17:41
 */
@Repository
@Primary
public class AlphaDaoMyBatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "mybatis";
    }
}
