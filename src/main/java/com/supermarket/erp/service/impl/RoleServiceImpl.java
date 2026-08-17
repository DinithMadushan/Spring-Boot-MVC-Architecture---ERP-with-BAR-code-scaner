package com.supermarket.erp.service.impl;

import com.supermarket.erp.entity.Role;
import com.supermarket.erp.repository.RoleRepository;
import com.supermarket.erp.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    @Override
    public Role getOrCreateRole(String name) {
        return roleRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> roleRepository.save(new Role(name)));
    }
}
