package top.kwseeker.sshblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import top.kwseeker.sshblog.domain.Comment;
import top.kwseeker.sshblog.repository.CommentRepository;

import javax.transaction.Transactional;

public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public void removeComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.getOne(id);
    }
}
