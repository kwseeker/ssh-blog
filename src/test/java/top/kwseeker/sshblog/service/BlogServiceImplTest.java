package top.kwseeker.sshblog.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.kwseeker.sshblog.domain.Blog;
import top.kwseeker.sshblog.domain.Catalog;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.repository.BlogRepository;
import top.kwseeker.sshblog.repository.CatalogRepository;
import top.kwseeker.sshblog.repository.UserRepository;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BlogServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private BlogRepository blogRepository;

    /**
     * 因为 blog 和 user catalog 表是关联表，保存blog之前必须使内部的user和catalog数据已存在，且从数据库获取联结表的数据
     * 否则会报如下异常： TransientPropertyValueException: object references an unsaved transient instance
     * 或者修改@ManyToOne(cascade = CascadeType.xxx) 为自动更新联结表数据的类型
     */
    @Test
    public void saveBlog() {
        //文章所属用户信息（项目部署后这个信息是从当前登录用户获取的）
        // User user = (User)userDetailsService.loadUserByUsername(username);
        User user = userRepository.findByUsername("kwseeker");
        //文章分类信息
//        Catalog catalog = new Catalog();  //这是错误的
//        catalog.setName("Test");
//        catalog.setUser(user);
        Catalog catalog = catalogRepository.findByUserAndName(user, "Test");
        Blog newBlog = new Blog();
        newBlog.setTitle("Hello Blog");
        newBlog.setContent("This is a test article");
        newBlog.setHtmlContent("This is a test html article");
        newBlog.setSummary("Test article");
        newBlog.setCatalog(catalog);
        newBlog.setTags("Test");
        newBlog.setUser(user);
        blogRepository.save(newBlog);
    }

    @Test
    public void removeBlog() {
    }

    @Test
    public void getBlogById() {
    }

    @Test
    public void listBlogsByTitleVoteAndSort() {
    }

    @Test
    public void listBlogsByCatalog() {
    }

    @Test
    public void readingIncrease() {
    }

    @Test
    public void createComment() {
    }

    @Test
    public void removeComment() {
    }

    @Test
    public void createVote() {
    }

    @Test
    public void removeVote() {
    }
}