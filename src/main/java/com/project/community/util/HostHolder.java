package com.project.community.util;

import com.project.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author makwingchi
 * @Description Hold user information, and replace session
 * @create 2020-04-16 21:51
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
