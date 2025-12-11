package com.example.reqsmanager.service;

import com.example.reqsmanager.entity.User;
import com.example.reqsmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 固定的验证码
    private final String REGISTRATION_CODE = "BANKOFSHANGHAI";

    /**
     * 注册新用户.
     * @param username 用户名
     * @param password 原始密码
     * @param verificationCode 用户输入的验证码
     * @return 创建好的 User 对象
     * @throws Exception 如果验证码错误或用户已存在
     */
    public User registerNewUser(String username, String password, String verificationCode) throws Exception {
        // 1. 校验验证码
        if (!REGISTRATION_CODE.equals(verificationCode)) {
            throw new Exception("验证码错误！");
        }

        // 2. 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("用户名 '" + username + "' 已被注册！");
        }

        // 3. 创建新用户实例
        User newUser = new User();
        newUser.setUsername(username);
        // 4. 对密码进行加密
        newUser.setPassword(passwordEncoder.encode(password));

        // 5. 保存到数据库
        return userRepository.save(newUser);
    }

    // === START: 新增重置密码的方法 ===
    /**
     * 重置指定用户的密码.
     * @param username 要重置密码的用户名
     * @param newPassword 用户输入的新密码 (明文)
     * @param verificationCode 用户输入的验证码
     * @throws Exception 如果验证码错误或用户不存在
     */
    public void resetPassword(String username, String newPassword, String verificationCode) throws Exception {
        // 1. 校验验证码
        if (!REGISTRATION_CODE.equals(verificationCode)) {
            throw new Exception("验证码错误！");
        }

        // 2. 从数据库中找到用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("用户名 '" + username + "' 不存在！"));

        // 3. 将新密码加密并更新到用户对象
        user.setPassword(passwordEncoder.encode(newPassword));

        // 4. 保存更新后的用户
        userRepository.save(user);
    }
    // === END: 新增 ===
}
