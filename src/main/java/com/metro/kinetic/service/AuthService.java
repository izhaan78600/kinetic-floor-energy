package com.metro.kinetic.service;

import com.metro.kinetic.model.AdminUser;
import com.metro.kinetic.model.User;
import com.metro.kinetic.repository.AdminUserRepository;
import com.metro.kinetic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AdminUserRepository adminRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==========================================
    // USER AUTHENTICATION
    // ==========================================
    public User registerUser(String name, String email, String rawPassword) {
        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        String hashed = passwordEncoder.encode(rawPassword);
        User user = new User(name, email, hashed, "USER");
        return userRepo.save(user);
    }

    public User loginUser(String email, String rawPassword) {
        Optional<User> opt = userRepo.findByEmail(email);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Invalid user email or password");
        }
        User user = opt.get();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid user email or password");
        }
        return user;
    }

    // ==========================================
    // ADMIN AUTHENTICATION
    // ==========================================
    public AdminUser registerAdmin(String name, String email, String rawPassword) {
        if (adminRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Admin with email " + email + " already exists");
        }
        String hashed = passwordEncoder.encode(rawPassword);
        AdminUser admin = new AdminUser(name, email, hashed);
        return adminRepo.save(admin);
    }

    public AdminUser loginAdmin(String email, String rawPassword) {
        Optional<AdminUser> opt = adminRepo.findByEmail(email);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Invalid admin email or password");
        }
        AdminUser admin = opt.get();
        if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
            throw new IllegalArgumentException("Invalid admin email or password");
        }
        return admin;
    }

    public String encodePassword(String raw) {
        return passwordEncoder.encode(raw);
    }
}
