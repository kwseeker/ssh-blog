package top.kwseeker.sshblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.kwseeker.sshblog.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
