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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     *  ============== START: 核心修正 ==============
     *  创建一个专门的 SecurityFilterChain，用于处理静态资源。
     *  @Order(1) 确保这个过滤器链在主过滤器链之前被执行。
     */
    @Bean
    @Order(1)
    public SecurityFilterChain staticResourcesFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 只匹配所有静态资源的路径
                .securityMatcher("/css/**", "/js/**", "/webjars/**", "/favicon.ico")
                // 2. 对这些路径，允许所有请求，并且不做任何安全限制
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // 3. 关闭所有不需要的安全功能
                .requestCache(AbstractHttpConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     *  主 SecurityFilterChain，用于处理所有其他请求。
     *  @Order(2) 确保它在静态资源过滤器链之后执行。
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
                // 关闭 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 注册我们的认证提供者
                .authenticationProvider(authenticationProvider)
                // 配置 URL 访问权限
                .authorizeHttpRequests(auth -> auth
                        // 允许对登录、注册、忘记密码页面的匿名访问
                        .requestMatchers("/login", "/register", "/forgot-password", "/error").permitAll()
                        // 其他所有请求都必须经过认证
                        .anyRequest().authenticated()
                )
                // 配置表单登录
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // 配置登出
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
    // ============== END: 核心修正 ==============
}
