package com.example.reqsmanager.config;

import com.example.reqsmanager.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 定义密码编码器，使用 BCrypt 算法。
     * @return PasswordEncoder 的一个实例。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证提供者 (AuthenticationProvider)。
     * 这个 Bean 告诉 Spring Security 如何获取用户信息和如何验证密码。
     * @param userDetailsService Spring 会自动注入我们自定义的 CustomUserDetailsService。
     * @return 配置好的 DaoAuthenticationProvider 实例。
     */
    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // 设置用户详情服务
        authProvider.setPasswordEncoder(passwordEncoder()); // 设置密码编码器
        return authProvider;
    }

    /**
     * 静态资源安全过滤链。
     * 这个过滤器链专门用于处理静态资源，并允许所有匿名访问。
     * @Order(1) 确保它在处理业务请求的主过滤器链之前被执行。
     * @param http HttpSecurity 配置对象。
     * @return 配置好的 SecurityFilterChain 实例。
     */
    @Bean
    @Order(1)
    public SecurityFilterChain staticResourcesFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. securityMatcher() 精确匹配所有静态资源的路径
                .securityMatcher("/css/**", "/js/**", "/webjars/**", "/favicon.ico")
                // 2. 对这些路径，允许所有请求
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // 3. 关闭所有对于静态资源来说不必要的安全功能，以提升性能
                .requestCache(AbstractHttpConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * 主安全过滤链，用于处理所有业务逻辑相关的请求。
     * @Order(2) 确保它在静态资源过滤器链之后执行。
     * @param http HttpSecurity 配置对象。
     * @param authenticationProvider Spring 会自动注入我们上面定义的 authenticationProvider Bean。
     * @return 配置好的 SecurityFilterChain 实例。
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
                // 关闭 CSRF 防护
                .csrf(AbstractHttpConfigurer::disable)
                // 注册我们的认证提供者
                .authenticationProvider(authenticationProvider)
                // 配置 URL 的访问权限
                .authorizeHttpRequests(auth -> auth
                        // 允许对登录、注册、忘记密码和错误页面的匿名访问
                        .requestMatchers("/login", "/register", "/forgot-password", "/error").permitAll()
                        // 其他所有请求都必须经过认证
                        .anyRequest().authenticated()
                )
                // 配置表单登录
                .formLogin(form -> form
                        .loginPage("/login") // 自定义登录页面
                        .loginProcessingUrl("/perform_login") // 登录表单提交 URL
                        .defaultSuccessUrl("/", true) // 登录成功后强制跳转到主页
                        .failureUrl("/login?error=true") // 登录失败后跳转
                        .permitAll()
                )
                // 配置登出
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
