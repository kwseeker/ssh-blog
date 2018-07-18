package top.kwseeker.sshblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kwseeker.sshblog.domain.Catalog;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.repository.CatalogRepository;

import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private CatalogRepository catalogRepository;

    //TODO: 这里加事务注解会怎么样？
//    @Transactional
    @Override
    public Catalog saveCatalog(Catalog catalog) {
        //分类表catalog依附于用户表user(多对一的关系)
        //添加新分类时要判断此分类此用户是否已经拥有这个标签
//        List<Catalog> list = catalogRepository.findByUserAndName(catalog.getUser(), catalog.getName());
//        if(list !=null && list.size() > 0) {
//            throw new IllegalArgumentException("该分类已经存在了");
//        }
        Catalog originCatalog = catalogRepository.findByUserAndName(catalog.getUser(), catalog.getName());
        if(originCatalog != null) {
            throw new IllegalArgumentException("该分类已经存在了");
        }
        return catalogRepository.save(catalog);
    }

    @Override
    public void removeCatalog(Long id) {
        catalogRepository.deleteById(id);
    }

    @Override
    public Catalog getCatalogById(Long id) {
        return catalogRepository.getOne(id);
    }

    @Override
    public List<Catalog> listCatalogs(User user) {
        return catalogRepository.findByUser(user);
    }
}
