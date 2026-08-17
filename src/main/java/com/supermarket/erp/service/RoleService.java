package com.supermarket.erp.service;

import com.supermarket.erp.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> getAllRoles();

    Role getRoleById(Long id);

    Role getOrCreateRole(String name);

}
