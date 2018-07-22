package top.kwseeker.sshblog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.kwseeker.sshblog.domain.Blog;
import top.kwseeker.sshblog.domain.Catalog;
import top.kwseeker.sshblog.domain.User;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Deprecated
//    Page<Blog> findByUserAndTitleLikeOrderByCreateTimeDesc(User user, String title, Pageable pageable);

    Page<Blog> findByUserAndTitleLike(User user, String title, Pageable pageable);

    Page<Blog> findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(
            String title,User user,String tags,User user2,Pageable pageable);

    Page<Blog> findByCatalog(Catalog catalog, Pageable pageable);
}
