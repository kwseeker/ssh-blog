package top.kwseeker.sshblog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.kwseeker.sshblog.domain.User;

import java.util.Collection;
import java.util.List;

/**
 * JpaRepository 继承 PagingAndSortingRepository(提供分页查询相关的方法) QueryByExampleExecutor(提供通过实例匹配查询的方法)，
 * PagingAndSortingRepository又继承 CurdRepository(提供基本查询的方法)
 */
//public interface UserRepository extends CrudRepository<User, Long> {
public interface UserRepository extends JpaRepository<User, Long> {

    //根据用户名分页查询用户列表
    Page<User> findByNameLike(String name, Pageable pageable);

    List<User> findByNameLike(String name);

    User findByUsername(String username);

    List<User> findByUsernameIn(Collection<String> usernames);
}