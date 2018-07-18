package top.kwseeker.sshblog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.kwseeker.sshblog.domain.Blog;
import top.kwseeker.sshblog.domain.Catalog;
import top.kwseeker.sshblog.domain.User;

public interface BlogService {

    //保存Blog
    Blog saveBlog(Blog blog);

    //删除Blog
    void removeBlog(Long id);

    //根据id获取Blog
    Blog getBlogById(Long id);

    //根据用户名进行分页模糊查询（最新文章）
//    Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable);

    //根据用户名进行分页模糊查询（最热文章）
    Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable);

    //根据分类进行查询
    Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable);

    //阅读量阅后递增
    void readingIncrease(Long id);

    //发表评论
    Blog createComment(Long blogId, String commentContent);

    //删除评论
    void removeComment(Long blogId, Long commentId);

    //点赞
    Blog createVote(Long blogId);

    //取消点赞
    void removeVote(Long blogId, Long voteId);
}
