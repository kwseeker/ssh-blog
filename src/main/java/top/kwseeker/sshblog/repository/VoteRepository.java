package top.kwseeker.sshblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.kwseeker.sshblog.domain.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
