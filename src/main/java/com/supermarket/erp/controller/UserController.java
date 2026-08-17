package com.supermarket.erp.controller;

import com.supermarket.erp.entity.User;
import com.supermarket.erp.service.RoleService;
import com.supermarket.erp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("formTitle", "Add User");
        return "users/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("formTitle", "Edit User");
        return "users/form";
    }

    @PostMapping("/save")
    public String saveUser(@RequestParam(required = false) Long id,
                            @RequestParam String fullName,
                            @RequestParam String username,
                            @RequestParam(required = false) String password,
                            @RequestParam Long roleId,
                            @RequestParam String email,
                            @RequestParam String contactNo,
                            @RequestParam(defaultValue = "ACTIVE") String status,
                            RedirectAttributes redirectAttributes) {
        User user = (id != null) ? userService.getUserById(id) : new User();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setRole(roleService.getRoleById(roleId));
        user.setEmail(email);
        user.setContactNo(contactNo);
        user.setStatus(status);

        userService.saveUser(user, password);
        redirectAttributes.addFlashAttribute("successMessage", "User '" + username + "' saved successfully.");
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/users";
    }
}
