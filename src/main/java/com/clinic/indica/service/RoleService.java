package com.clinic.indica.service;

import com.clinic.indica.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role createRole(Role.RoleName roleName);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
    Optional<Role> getRoleById(Long id);
    Optional<Role> getRoleByName(Role.RoleName roleName);
    List<Role> getAllRoles();
    void initializeRoles();
}

