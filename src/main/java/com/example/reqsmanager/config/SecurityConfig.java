package com.example.reqsmanager.config;

import com.example.reqsmanager.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义的用户服务，告诉 Spring Security 如何根据用户名加载用户
     */
    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 认证管理器，登录认证的核心组件
     * Spring Boot 3.x / Security 6.x 的标准配置方式
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置 HTTP 安全规则，定义哪些 URL 需要保护，哪些可以匿名访问
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
                // 1. 关闭 CSRF 防护
                .csrf(csrf -> csrf.disable())

                // 2. 注册我们自定义的 AuthenticationProvider
                .authenticationProvider(authenticationProvider)

                // 3. 配置 URL 的访问权限
                .authorizeHttpRequests(auth -> auth
                        // 允许所有人访问登录页面和所有静态资源
                        .requestMatchers("/login", "/css/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()
                        // 其他所有请求都必须经过认证
                        .anyRequest().authenticated()
                )

                // 4. 配置表单登录
                .formLogin(form -> form
                        .loginPage("/login") // 指定登录页的 URL
                        .loginProcessingUrl("/perform_login") // 登录表单提交的 URL
                        .defaultSuccessUrl("/", true) // 登录成功后强制跳转到主页
                        .failureUrl("/login?error=true") // 登录失败后跳转的 URL
                        .permitAll()
                )

                // 5. 配置登出
                .logout(logout -> logout
                        .logoutUrl("/logout") // 触发登出的 URL
                        .logoutSuccessUrl("/login?logout=true") // 登出成功后跳转的 URL
                        .invalidateHttpSession(true) // 使 HttpSession 失效
                        .deleteCookies("JSESSIONID") // 删除 Cookie
                        .permitAll()
                );

        return http.build();
    }
}
