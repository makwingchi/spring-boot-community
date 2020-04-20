package com.project.community.dao;

import com.project.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-17 17:41
 */
@Mapper
public interface CommentMapper {

    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

}
