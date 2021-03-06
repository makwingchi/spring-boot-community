package com.project.community.dao;

import com.project.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-15 00:20
 */
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

}
