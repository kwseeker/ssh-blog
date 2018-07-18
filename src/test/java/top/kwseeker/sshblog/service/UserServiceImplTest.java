package top.kwseeker.sshblog.service;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void aSaveUser() {
        User user = new User("Monkey D Luffy", "luffy@gmail.com",
                "luffy", "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi");
        userRepository.save(user);
        assertEquals("luffy", userRepository.findByUsername("luffy").getUsername());
    }

    @Test
    public void bUpdateUser() {
        User user = userRepository.findByUsername("luffy");
        user.setUsername("monkey_d_luffy");
        userRepository.save(user);
        assertEquals("Monkey D Luffy", userRepository.findByUsername("monkey_d_luffy").getName());
    }

    @Test
    public void cRemoveUser() {
        assertNotNull(userRepository.findByUsername("monkey_d_luffy"));
        userRepository.deleteById(userRepository.findByUsername("monkey_d_luffy").getId());
        assertNull(userRepository.findByUsername("monkey_d_luffy"));
    }

    @Test
    public void removeUserInBatch() {
    }

    @Test
    public void getUserById() {
        User user = new User();
        user.setId(1L);
        Example<User> example = Example.of(user);
        User administrator = userRepository.findOne(example).orElse(null);
        assertNotNull(userRepository.findOne(example).orElse(null));
        assertEquals("admin", administrator.getUsername());
    }

    @Test
    public void listUsers() {
        List<User> userList = userRepository.findAll();
        System.out.println("全部用户列表：");
        for (User user : userList) {
            System.out.println(user.toString());
        }
    }

    @Test
    public void listUsersByNameLike() {
        String name = "%Arvin%";
        List<User> userList = userRepository.findByNameLike(name);
        System.out.println("模糊查询(" + name + ")用户列表：");
        for (User user : userList) {
            System.out.println(user.toString());
        }
    }

    @Test
    public void listUsersByUsernames() {
        List<String> usernameList = new ArrayList<>();
        usernameList.add("kwseeker");
        usernameList.add("admin");
        List<User> userList = userRepository.findByUsernameIn(usernameList);
        System.out.println("用户名列表查询用户列表：");
        for (User user : userList) {
            System.out.println(user.toString());
        }
    }
}