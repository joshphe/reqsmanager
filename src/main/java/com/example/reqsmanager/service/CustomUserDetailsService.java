package com.example.reqsmanager.service;

import com.example.reqsmanager.entity.User;
import com.example.reqsmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义的用户详情服务。
 * Spring Security 通过这个服务来加载用户信息以进行认证。
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名加载用户详情。
     * 这是 UserDetailsService 接口中唯一需要实现的方法。
     *
     * @param username 前端登录表单中提交的用户名
     * @return 一个实现了 UserDetails 接口的对象，其中包含了用户名、加密后的密码和权限信息。
     * @throws UsernameNotFoundException 如果根据用户名在数据库中找不到用户，必须抛出此异常。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 使用 UserRepository 从数据库中根据用户名查找我们自己的 User 实体
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username)
                );

        // 2. 将我们自己的 User 实体，转换成 Spring Security 标准的 UserDetails 对象
        //    - user.getUsername(): 用户名
        //    - user.getPassword(): 从数据库查出的、已经 BCrypt 加密过的密码字符串
        //    - Collections.emptyList(): 用户的权限/角色列表。对于简单的登录功能，我们可以先提供一个空列表。
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
