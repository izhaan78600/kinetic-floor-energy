package com.metro.kinetic.controller;

import com.metro.kinetic.model.AdminUser;
import com.metro.kinetic.model.User;
import com.metro.kinetic.repository.AdminUserRepository;
import com.metro.kinetic.repository.UserRepository;
import com.metro.kinetic.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AdminUserRepository adminRepo;

    // ==========================================
    // USER SIGN UP & LOGIN
    // ==========================================
    @PostMapping("/user/signup")
    public ResponseEntity<?> userSignUp(@RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("name");
            String email = payload.get("email");
            String password = payload.get("password");

            if (name == null || email == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name, email, and password are required"));
            }

            User user = authService.registerUser(name, email, password);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "User registration successful");
            resp.put("userId", user.getId());
            resp.put("name", user.getName());
            resp.put("email", user.getEmail());
            resp.put("role", user.getRole());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> userLogin(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String password = payload.get("password");

            User user = authService.loginUser(email, password);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "User login successful");
            resp.put("userId", user.getId());
            resp.put("name", user.getName());
            resp.put("email", user.getEmail());
            resp.put("role", user.getRole());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // ADMIN SIGN UP & LOGIN
    // ==========================================
    // Admin Sign Up is disabled - only pre-configured administrators may sign in.
    @PostMapping("/admin/signup")
    public ResponseEntity<?> adminSignUp(@RequestBody Map<String, String> payload) {
        return ResponseEntity.status(403).body(Map.of("error", "Admin sign-up is disabled. Only pre-configured administrators may sign in."));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String password = payload.get("password");

            AdminUser admin = authService.loginAdmin(email, password);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Admin login successful");
            resp.put("adminId", admin.getId());
            resp.put("name", admin.getName());
            resp.put("email", admin.getEmail());
            resp.put("role", "ADMIN");
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // MANAGE USERS & ADMINS
    // ==========================================
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @GetMapping("/admins")
    public ResponseEntity<List<AdminUser>> getAllAdmins() {
        return ResponseEntity.ok(adminRepo.findAll());
    }
}
