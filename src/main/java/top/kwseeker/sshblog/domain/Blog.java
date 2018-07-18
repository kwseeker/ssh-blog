package top.kwseeker.sshblog.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * 存储博客文章
 * id title summary htmlContent
 *
 * Hibernate: create table blog (id bigint not null auto_increment, comment_size integer, content longtext not null,
 * create_time datetime not null, html_content longtext not null, read_size integer, summary varchar(300) not null,
 * tags varchar(255), title varchar(50) not null, vote_size integer, catalog_id bigint, user_id bigint, primary key (id))
 */
@Entity
public class Blog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "标题不能为空")   //这个作用于对象
    @Size(min = 2, max = 50)    //标题在2-50字节
    @Column(nullable = false, length = 50)  //这个作用于数据库
    private String title;

    @NotEmpty(message = "摘要不能为空")
    @Size(min = 2, max = 300)
    @Column(nullable = false)
    private String summary;

    @Lob    //大对象，自动生成表的时候会将此字段设置为Long Text类型
    @Basic(fetch = FetchType.LAZY)  //懒加载(针对关联数据实际用到的时候才会去数据库取)
    @NotEmpty(message = "内容不能为空")
    @Size(min = 2)
    @Column(nullable = false)
    private String content;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @NotEmpty(message = "内容不能为空")
    @Size(min = 2)
    @Column(nullable = false)
    private String htmlContent;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY) //一对一级联，级联分离(撤销外键约束)、懒加载
    @JoinColumn(name="user_id") //user表的id主键作为blog表的外键
    private User user;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp createTime;

    //@Column(name = "readSize") 这一句是多余的，不写也是按照变量名的驼峰式"readSize"转化为下划线式"read_size"
    private Integer readSize = 0;

    private Integer commentSize = 0;

    private Integer voteSize = 0;

    //TODO: CascadeTpe 和 FetchType 具体是怎么控制数据库联结表数据处理行为的？这里为什么用EAGER？
    //TODO: 还有另一种写法,不用创建中间表，参考 https://blog.csdn.net/lyg_2012/article/details/70195062
    // @OneToMany(mappedBy = "blog") 对应Common.java添加blog属性及注解 @ManyToOne(optional = false)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)      //表的关联查询, Blog的一个数据对应Comment的多个数据, 反过来是多对一,见Comment.java
    @JoinTable(name = "blog_comment", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id"))
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)   //
    @JoinTable(name = "blog_vote", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vote_id", referencedColumnName = "id"))
    private List<Vote> votes;

    //博客的分类
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name="catalog_id")  //定义一个指向另一个表的外链
    private Catalog catalog;

    @Column(length = 100)
    private String tags;

    //===============================================================
    public Blog() {}

    public Blog(String title, String summary, String content) {
        this.title = title;
        this.summary = summary;
        this.content = content;
    }

    //添加评论
    public void addComment(Comment comment) {
        this.comments.add(comment);     //TODO: 操作 Entity Blog comments 属性可以间接操作 comment 表, 实现原理？
        this.commentSize = this.comments.size();
    }

    public void removeComment(Long commentId) {
        for (int index=0; index < this.comments.size(); index ++ ) {
            if (comments.get(index).getId().longValue() == commentId.longValue()) {
                this.comments.remove(index);
                break;
            }
        }
        this.commentSize = this.comments.size();
    }

    //点赞
    public boolean addVote(Vote vote) {
        //是否已经点过赞
        boolean isExist = false;
        for(Vote voteCase: this.votes) {
            if(voteCase.getUser().getId().longValue() == vote.getUser().getId().longValue()) {
                isExist = true;
                break;
            }
        }

        if(!isExist) {
            this.votes.add(vote);
            this.voteSize = this.votes.size();
        }
        return isExist;
    }

    //取消点赞
    public void removeVote(Long voteId) {
        for (int index=0; index < this.votes.size(); index ++ ) {
            if (this.votes.get(index).getId() == voteId) {
                this.votes.remove(index);
                break;
            }
        }
        this.voteSize = this.votes.size();
    }


    //getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getTimestamp() {
        return createTime;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.createTime = timestamp;
    }

    public Integer getReadSize() {
        return readSize;
    }

    public void setReadSize(Integer readSize) {
        this.readSize = readSize;
    }

    public Integer getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(Integer commentSize) {
        this.commentSize = commentSize;
    }

    public Integer getVoteSize() {
        return voteSize;
    }

    public void setVoteSize(Integer voteSize) {
        this.voteSize = voteSize;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
