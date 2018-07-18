package top.kwseeker.sshblog.service;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;
import top.kwseeker.sshblog.domain.Authority;
import top.kwseeker.sshblog.repository.AuthorityRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * findOne 和 findById 的 sql 语句并没有什么不同。
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AuthorityServiceImplTest {

    private static final Long ROLE_ADMIN_AUTHORITY_ID = 1L; //管理员权限
    private static final Long ROLE_USER_AUTHORITY_ID = 2L;  //普通用户权限
    private static final String ROLE_ADMIN_AUTHORITY_NAME = "ROLE_ADMIN"; //管理员权限
    private static final String ROLE_USER_AUTHORITY_NAME = "ROLE_USER";  //普通用户权限

    @Autowired
    private AuthorityRepository authorityRepository;

    @BeforeClass
    public static void saveTestData() {
        System.out.println("存储测试数据");
//        List<Authority> authorityList = new ArrayList<>();
//        authorityList.add(new Authority(ROLE_ADMIN_AUTHORITY_ID, ROLE_ADMIN_AUTHORITY_NAME));
//        authorityList.add(new Authority(ROLE_USER_AUTHORITY_ID, ROLE_USER_AUTHORITY_NAME));
//        authorityRepository.saveAll(authorityList);
    }

    @Test
    public void getAuthorityById() {
        Authority authority = new Authority(ROLE_USER_AUTHORITY_ID, null);
        Example<Authority> example = Example.of(authority);
        authority = authorityRepository.findOne(example).orElse(null);
        if(authority == null) {
            System.out.println("没有找到符合条件的数据");
            return;
        }
        assertEquals(ROLE_USER_AUTHORITY_NAME, authority.getAuthority());
    }

    @Test
    public void getAuthorityById2() {
        Authority authority = authorityRepository.findById(ROLE_ADMIN_AUTHORITY_ID).orElse(null);
        if(authority == null) {
            System.out.println("没有找到符合条件的数据");
            return;
        }
        assertEquals(ROLE_ADMIN_AUTHORITY_NAME, authority.getAuthority());
    }
}