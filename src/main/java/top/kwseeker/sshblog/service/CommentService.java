package top.kwseeker.sshblog.service;

import top.kwseeker.sshblog.domain.Comment;

public interface CommentService {

    //根据id获取评论
    Comment getCommentById(Long id);

    //根据id删除评论
    void removeComment(Long id);
}
