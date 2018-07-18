package top.kwseeker.sshblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.kwseeker.sshblog.domain.*;
import top.kwseeker.sshblog.repository.BlogRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    //保存Blog
    @Override
    @Transactional
    public Blog saveBlog(Blog blog) {
        Blog returnBlog = blogRepository.save(blog);
        return returnBlog;
    }

    //通过id删除Blog
    @Override
    @Transactional
    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
    }

    //根据id获取Blog
    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.getOne(id);
    }

    //根据用户名进行分页模糊查询（最新文章）
//    @Override
//    public Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable) {
//        // 模糊查询
//        title = "%" + title + "%";
//        //Page<Blog> blogs = blogRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user, title, pageable);
//        String tags = title;
//        Page<Blog> blogs = blogRepository.findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(title,user, tags, user, pageable);
//        return blogs;
//    }

    //根据用户名进行分页模糊查询（最热文章）
    @Override
    public Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable) {
        // 模糊查询
        title = "%" + title + "%";
        Page<Blog> blogs = blogRepository.findByUserAndTitleLike(user, title, pageable);
        return blogs;
    }

    //根据分类进行查询
    @Override
    public Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable) {
        Page<Blog> blogs = blogRepository.findByCatalog(catalog, pageable);
        return blogs;
    }

    //阅读量阅后递增
    @Override
    public void readingIncrease(Long id) {
        Blog blog = blogRepository.getOne(id);
        blog.setReadSize(blog.getCommentSize()+1);
        this.saveBlog(blog);
    }

    //发表评论
    @Override
    public Blog createComment(Long blogId, String commentContent) {
        Blog blog = blogRepository.getOne(blogId);
        List<Comment> commentList = blog.getComments();
        //从Security中获取用户
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = new Comment(user, commentContent);
        blog.addComment(comment);
        return saveBlog(blog);
    }

    //根据评论ID删除评论
    @Override
    public void removeComment(Long blogId, Long commentId) {
        Blog blog = blogRepository.getOne(blogId);
        blog.removeComment(commentId);
        this.saveBlog(blog);
    }

    //点赞
    @Override
    public Blog createVote(Long blogId) {
        Blog blog = blogRepository.getOne(blogId);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Vote vote = new Vote(user);
        boolean isExist = blog.addVote(vote);
        if(isExist) {
            throw new IllegalArgumentException("您已经点过赞了");
        }
        return this.saveBlog(blog);
    }

    //取消点赞
    @Override
    public void removeVote(Long blogId, Long voteId) {
        Blog blog = blogRepository.getOne(blogId);
        blog.removeVote(voteId);
        this.saveBlog(blog);
    }
}
