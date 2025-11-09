package com.clinic.indica.service.impl;

import com.clinic.indica.entity.Role;
import com.clinic.indica.exception.DuplicateResourceException;
import com.clinic.indica.exception.ResourceNotFoundException;
import com.clinic.indica.repository.RoleRepository;
import com.clinic.indica.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public Role createRole(Role.RoleName roleName) {
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new DuplicateResourceException("Role", "name", roleName);
        }
        
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        
        // Kiểm tra tên role trùng (nếu thay đổi)
        if (!role.getName().equals(roleDetails.getName()) && 
            roleRepository.findByName(roleDetails.getName()).isPresent()) {
            throw new DuplicateResourceException("Role", "name", roleDetails.getName());
        }
        
        role.setName(roleDetails.getName());
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        roleRepository.delete(role);
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleByName(Role.RoleName roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public void initializeRoles() {
        for (Role.RoleName roleName : Role.RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }
}

