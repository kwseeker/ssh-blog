package top.kwseeker.sshblog.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.kwseeker.sshblog.domain.Catalog;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.repository.CatalogRepository;
import top.kwseeker.sshblog.repository.UserRepository;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CatalogServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Test
    public void saveCatalog() {
        Catalog catalog = new Catalog();
        User user = userRepository.findByUsername("kwseeker");
        catalog.setName("Test");
        catalog.setUser(user);
        catalogRepository.save(catalog);
    }

    @Test
    public void removeCatalog() {
    }

    @Test
    public void getCatalogById() {
    }

    @Test
    public void listCatalogs() {
    }
}