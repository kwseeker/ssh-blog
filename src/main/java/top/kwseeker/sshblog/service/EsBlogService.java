package top.kwseeker.sshblog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.kwseeker.sshblog.domain.User;
import top.kwseeker.sshblog.domain.es.EsBlog;
import top.kwseeker.sshblog.vo.TagVO;

import java.util.List;

public interface EsBlogService {

    //删除Blog
    void removeEsBlog(String id);

    //更新EsBlog
    EsBlog updateEsBlog(EsBlog esBlog);

    //根据id获取Blog
    EsBlog getEsBlogByBlogId(Long blogId);

    //最新博客列表，分页
    Page<EsBlog> listNewestEsBlogs(String keyword, Pageable pageable);

    //最热博客列表，分页
    Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable);

    //博客列表，分页
    Page<EsBlog> listEsBlogs(Pageable pageable);

    //最新前5
    List<EsBlog> listTop5NewestEsBlogs();

    //最热前5
    List<EsBlog> listTop5HotestEsBlogs();

    //最热前30标签
    List<TagVO> listTop30Tags();

    //最热前12用户
    List<User> listTop12Users();

}
