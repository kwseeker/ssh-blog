package top.kwseeker.sshblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.kwseeker.sshblog.domain.Catalog;
import top.kwseeker.sshblog.domain.User;

import java.util.List;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    List<Catalog> findByUser(User user);

//    List<Catalog> findByUserAndName(User user, String name);
    Catalog findByUserAndName(User user, String name);
}
