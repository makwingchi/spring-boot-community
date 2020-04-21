package com.project.community.util;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-18 20:00
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREDIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";

    /**
     * Like of some entity (post or comment)
     * @return like:entity:entityType:entityId -> {userId1, userId2, ... }
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     *
     * @param userId
     * @return like:user:userId -> int
     */
    public static String getUserLikeKey(int userId) {
        return PREDIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * entities followed by a given user
     * @param userId
     * @param entityType
     * @return followee:userId:entityType -> zset(entityType, now)
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * followers of a given entity
     * @param entityType
     * @param entityId
     * @return follower:entityType:entityId -> zset(userId, now)
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    // uv for a single day
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    // uv for an interval
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    // dau for a single day
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    // dau for an interval
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

}
