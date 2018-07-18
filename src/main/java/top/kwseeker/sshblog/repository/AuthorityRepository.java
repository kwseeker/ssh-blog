package top.kwseeker.sshblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.kwseeker.sshblog.domain.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    //这里有很多继承过来的接口方法
}
