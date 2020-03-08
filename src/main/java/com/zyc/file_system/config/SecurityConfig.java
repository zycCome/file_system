package com.zyc.file_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

//TODO cors
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,jsr250Enabled=true,securedEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailService myUserDetailsService;

    @Autowired
    private MyAuthenticationFailHandler myAuthenticationFailHandler;

    @Autowired
    private SuccessAuthenticationHandler successAuthenticationHandler;



    protected void configure(HttpSecurity http) throws Exception  {
        http
                .formLogin()//开启formLogin默认配置
                    .loginPage("/login/auth").permitAll()//请求时未登录跳转接口
                    .failureHandler(myAuthenticationFailHandler)
                    .successHandler(successAuthenticationHandler)
                    .loginProcessingUrl("/login")//post登录接口，登录验证由系统实现
                    .usernameParameter("username")	//要认证的用户参数名，默认username
                    .passwordParameter("password")	//要认证的密码参数名，默认password
                    .and()
                .logout()//配置注销
                    .logoutUrl("/logout")//注销接口
                    .logoutSuccessUrl("/login/logout").permitAll()//注销成功跳转接口
                    .and()
                .authorizeRequests()//配置权限
//                    .antMatchers("/systemUser").permitAll()
                    .anyRequest().authenticated()//任意请求需要登录
                    .and()
                .csrf().disable();           //禁用csrf
    }


    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**
     * 注入自定义PermissionEvaluator
     */
    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler(){
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }





    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}