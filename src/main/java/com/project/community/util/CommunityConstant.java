package com.project.community.util;


/**
 * @author makwingchi
 * @Description
 * @create 2020-04-15 23:33
 */
public interface CommunityConstant {

    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;

    // default max age in seconds
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    // max age if remember me is selected
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    // post
    int ENTITY_TYPE_POST = 1;
    // comment
    int ENTITY_TYPE_COMMENT = 2;
    // user
    int ENTITY_TYPE_USER = 3;

    // event topics
    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";
    String TOPIC_PUBLISH = "publish";

    // system
    int SYSTEM_USER_ID = 1;

    // authentication
    String AUTHORITY_USER = "user";
    String AUTHORITY_ADMIN = "admin";
    String AUTHORITY_MODERATOR = "moderator";


}
