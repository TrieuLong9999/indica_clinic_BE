package com.clinic.indica.config;

import com.clinic.indica.entity.Role;
import com.clinic.indica.entity.User;
import com.clinic.indica.service.RoleService;
import com.clinic.indica.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Khởi tạo các roles mặc định
        initializeRoles();

        // Tạo tài khoản admin mặc định
        createDefaultAdmin();
    }

    private void initializeRoles() {
        try {
            roleService.initializeRoles();
            System.out.println("✓ Đã khởi tạo các roles mặc định");
        } catch (Exception e) {
            System.out.println("⚠ Lỗi khi khởi tạo roles: " + e.getMessage());
        }
    }

    private void createDefaultAdmin() {
        try {
            // Kiểm tra xem đã có user admin chưa
            if (userService.existsByUsername("admin")) {
                System.out.println("✓ Tài khoản admin đã tồn tại");
                return;
            }

            // Lấy role SUPERADMIN
            Role superAdminRole = roleService.getRoleByName(Role.RoleName.SUPERADMIN)
                    .orElse(null);
            
            if (superAdminRole == null) {
                System.out.println("⚠ Không tìm thấy role SUPERADMIN, bỏ qua tạo admin");
                return;
            }

            // Tạo user admin
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmail("admin@indica.clinic");
            admin.setFullName("Super Administrator");
            admin.setEnabled(true);

            // Gán role SUPERADMIN
            Set<Role> roles = new HashSet<>();
            roles.add(superAdminRole);
            admin.setRoles(roles);

            // Lưu vào database
            userService.createUser(admin);
            System.out.println("✓ Đã tạo tài khoản admin mặc định:");
            System.out.println("  - Username: admin");
            System.out.println("  - Password: admin");
            System.out.println("  - Role: SUPERADMIN");
        } catch (Exception e) {
            System.out.println("⚠ Lỗi khi tạo tài khoản admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

