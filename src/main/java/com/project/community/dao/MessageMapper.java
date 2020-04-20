package com.project.community.dao;

import com.project.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-17 22:56
 */
@Mapper
public interface MessageMapper {

    // query for message list
    List<Message> selectConversations(int userId, int offset, int limit);

    // query for message counts
    int selectConversationCount(int userId);

    // query for message list for given conversation
    List<Message> selectLetter(String conversationId, int offset, int limit);

    // query for message counts for given conversation
    int selectLetterCount(String conversationId);

    // query for unread messages
    int selectLetterUnreadCount(int userId, String conversationId);

    // add a new message
    int insertMessage(Message message);

    // update status of a given set of messages
    int updateStatus(List<Integer> ids, int status);

    // query for the latest message of each topic
    Message selectLatestNotice(int userId, String topic);

    // query for the number of messages of each topic
    int selectNoticeCount(int userId, String topic);

    // query for unread messages of each topic
    int selectNoticeUnreadCount(int userId, String topic);

    List<Message> selectNotices(int userId, String topic, int offset, int limit);



}
