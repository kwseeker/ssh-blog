package top.kwseeker.sshblog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.kwseeker.sshblog.domain.User;

import java.util.Collection;
import java.util.List;

/**
 * 用户服务接口
 * 声明增删改查用户消息的方法
 */
public interface UserService {

    User saveUser(User user);

    void removeUser(Long id);

    //列表批量删除
    void removeUserInBatch(List<User> userList);

    User updateUser(User user);

    User getUserById(Long id);

    List<User> listUsers();

    //根据用户名进行分页模糊查询
    Page<User> listUsersByNameLike(String name, Pageable pageable);

    List<User> listUsersByUsernames(Collection<String> usernames);
}
