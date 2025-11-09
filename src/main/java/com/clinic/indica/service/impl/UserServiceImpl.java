package com.clinic.indica.service.impl;

import com.clinic.indica.dto.CreateUserRequest;
import com.clinic.indica.dto.UpdateUserRequest;
import com.clinic.indica.dto.UserResponse;
import com.clinic.indica.entity.Role;
import com.clinic.indica.entity.User;
import com.clinic.indica.exception.DuplicateResourceException;
import com.clinic.indica.exception.ResourceNotFoundException;
import com.clinic.indica.repository.RefreshTokenRepository;
import com.clinic.indica.repository.RoleRepository;
import com.clinic.indica.repository.UserRepository;
import com.clinic.indica.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("User", "username", user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }
        
        // Mã hóa password nếu chưa được mã hóa
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Kiểm tra username trùng (nếu thay đổi)
        if (!user.getUsername().equals(userDetails.getUsername()) && 
            userRepository.existsByUsername(userDetails.getUsername())) {
            throw new DuplicateResourceException("User", "username", userDetails.getUsername());
        }

        // Kiểm tra email trùng (nếu thay đổi)
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new DuplicateResourceException("User", "email", userDetails.getEmail());
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFullName(userDetails.getFullName());
        user.setEnabled(userDetails.getEnabled());

        // Chỉ cập nhật password nếu có thay đổi
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User addRoleToUser(Long userId, Role.RoleName roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
        
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User removeRoleFromUser(Long userId, Role.RoleName roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
        
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserRoles(Long userId, Set<Role.RoleName> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName)))
                .collect(Collectors.toSet());
        
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void clearAllRefreshTokens(Long userId) {
        // Xóa tất cả refresh tokens của user (logout tất cả thiết bị)
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @Override
    @Transactional
    public UserResponse createUserFromRequest(CreateUserRequest request) {
        // Kiểm tra username và email trùng
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);

        // Gán roles nếu có
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = request.getRoles().stream()
                    .map(roleName -> {
                        try {
                            Role.RoleName roleEnum = Role.RoleName.valueOf(roleName.toUpperCase());
                            return roleRepository.findByName(roleEnum)
                                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                        } catch (IllegalArgumentException e) {
                            throw new ResourceNotFoundException("Role", "name", roleName);
                        }
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUserFromRequest(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Kiểm tra username trùng (nếu thay đổi)
        if (request.getUsername() != null && !request.getUsername().isEmpty() &&
            !user.getUsername().equals(request.getUsername()) &&
            userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        // Kiểm tra email trùng (nếu thay đổi)
        if (request.getEmail() != null && !request.getEmail().isEmpty() &&
            !user.getEmail().equals(request.getEmail()) &&
            userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Cập nhật các trường
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            // Khi đổi mật khẩu, xóa tất cả refresh tokens để logout tất cả thiết bị
            refreshTokenRepository.deleteAllByUserId(id);
        }

        // Cập nhật roles nếu có
        if (request.getRoles() != null) {
            Set<Role> roles = request.getRoles().stream()
                    .map(roleName -> {
                        try {
                            Role.RoleName roleEnum = Role.RoleName.valueOf(roleName.toUpperCase());
                            return roleRepository.findByName(roleEnum)
                                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                        } catch (IllegalArgumentException e) {
                            throw new ResourceNotFoundException("Role", "name", roleName);
                        }
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    @Override
    public UserResponse getUserResponseById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return convertToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUserResponses() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setEnabled(user.getEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // Convert roles to string set
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        response.setRoles(roleNames);
        
        return response;
    }
}

