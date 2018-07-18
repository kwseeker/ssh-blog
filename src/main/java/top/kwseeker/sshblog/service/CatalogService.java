package top.kwseeker.sshblog.service;

import top.kwseeker.sshblog.domain.Catalog;
import top.kwseeker.sshblog.domain.User;

import java.util.List;

public interface CatalogService {

    //保存目录
    Catalog saveCatalog(Catalog catalog);

    //删除目录
    void removeCatalog(Long id);

    //根据ID获取目录
    Catalog getCatalogById(Long id);

    //获取目录列表
    List<Catalog> listCatalogs(User user);
}
