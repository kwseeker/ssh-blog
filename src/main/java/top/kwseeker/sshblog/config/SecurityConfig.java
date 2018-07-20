package top.kwseeker.sshblog.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 配置类
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //启用方法级别的认证 @PreAuthorize @PostAuthorize @PostFilter @PreFilter 注释的方法
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String KEY = "kwseeker.top";

    @Autowired
    private UserDetailsService userDetailsService;  //借助JPA访问数据库获取用户详细信息

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Bean
    public PasswordEncoder passwordEncoder() {  //没有这个方法是不是启用默认的加密方式 ？？？
        return new BCryptPasswordEncoder();     //对密码使用 BCrypt 加密
    }

    /**
     * 自定义配置
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                    .antMatchers("/css/**", "/js/**", "/fonts/**", "/index").permitAll()    //对这些URL所有用户都可访问
                    .antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/admins/**").hasRole("ADMIN")  //对于/admin/下URL只允许管理员 ROLE_ADMIN 访问
                    .and()
                .formLogin()    //基于Form表单登录验证
                    .loginPage("/login").failureUrl("/login-error")     //自定义登录界面及登录失败返回界面
                    .and()
                .rememberMe()
                    .key(KEY)                        //启用 Remember-Me 功能
                    .and()
                .exceptionHandling()
                    .accessDeniedPage("/403");//处理异常，拒绝访问返回403界面
//        httpSecurity.csrf().disable();
        httpSecurity.csrf().ignoringAntMatchers("/h2-console/**");  //禁用 H2 控制台的 CSRF 防护
        httpSecurity.headers().frameOptions().sameOrigin();         //允许来自同一来源的H2 控制台的请求
    }

    //自定义认证处理器
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    /**
     * 认证信息管理
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }
}
