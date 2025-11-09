package com.clinic.indica.service;

import com.clinic.indica.dto.CreateUserRequest;
import com.clinic.indica.dto.UpdateUserRequest;
import com.clinic.indica.dto.UserResponse;
import com.clinic.indica.entity.User;
import com.clinic.indica.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User addRoleToUser(Long userId, Role.RoleName roleName);
    User removeRoleFromUser(Long userId, Role.RoleName roleName);
    User updateUserRoles(Long userId, Set<Role.RoleName> roleNames);
    void clearAllRefreshTokens(Long userId);
    
    // Methods với DTOs
    UserResponse createUserFromRequest(CreateUserRequest request);
    UserResponse updateUserFromRequest(Long id, UpdateUserRequest request);
    UserResponse getUserResponseById(Long id);
    List<UserResponse> getAllUserResponses();
    
    // Profile methods
    UserResponse updateProfile(Long userId, com.clinic.indica.dto.UpdateProfileRequest request);
    UserResponse getCurrentUserProfile(Long userId);
}

