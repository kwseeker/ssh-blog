package top.kwseeker.sshblog.domain;

// 用户管理模块： 用户实体

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//以分离对象方式进行传递及远程过程调用必须实现序列化接口
@Entity     //定义实体类表示是数据库表的映射
public class User implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    @Id     //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) //自增策略
    private Long id;

    //姓名
    @NotEmpty(message = "姓名不能为空")
    @Size(min = 2, max = 20)
    @Column(nullable = false, length = 20)  //最大长度为20
    private String name;

    @NotEmpty(message = "邮箱不能为空")
    @Size(max = 50)
    @Email(message = "邮箱格式不对")  //检查邮箱字段格式
    @Column(nullable = false, length = 50, unique = true)   //唯一键
    private String email;

    //登录账号名
    @NotEmpty(message = "账户不能为空")
    @Size(min = 3, max = 20)
    @Column(nullable = false, length = 20, unique = true)
    private String username;

    @NotEmpty(message = "密码不能为空")
    @Size(max = 100)
    @Column(length = 100)
    private String password;

    //头像图片URL地址
    @Column(length = 200)
    private String avatar;

    //此用户拥有的角色权限列表
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<Authority> authorities;

    //==================================================================
    //JPA要求类必须有一个public或protected的无参构造函数
    public User() {}

    public User(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    //返回SimpleGrantedAuthority类型的权限列表
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        for(GrantedAuthority authority : this.authorities) {
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
        }
        return simpleGrantedAuthorities;
    }

    //BCrypt加密密码
    public void setEncodePassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    //TODO: 实现账号过期、锁定、
    //有些公司会记录用户上次登录时间距今的时间，如果超过规定的时间，默认账号无效。
    @Override
    public boolean isAccountNonExpired() {
        return true; //账号永远不会过期
    }

    //频繁错误登录被锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;    //账号永远不会被锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;    //密码永远不会过期
    }

    @Override
    public boolean isEnabled() {
        return true;    //用户永远有效
    }
    //==================================================================
    //getter or setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
