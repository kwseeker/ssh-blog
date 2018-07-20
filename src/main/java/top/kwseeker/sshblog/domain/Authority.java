package top.kwseeker.sshblog.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * id name
 */
@Entity
public class Authority implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) //自增长
    private Long id;

    @Column(nullable = false)   //name列不能为空
    private String name;

    protected Authority() {}

    public Authority(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    //授予一个对象的权限（类似"ROLE_ADMIN"/"ROLE_USER"这种字符串）
    //GrantedAuthority的方法
    @Override
    public String getAuthority() {
        return name;
    }

    //=================================================================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
