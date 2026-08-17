package com.supermarket.erp.config;

import com.supermarket.erp.entity.Location;
import com.supermarket.erp.entity.Role;
import com.supermarket.erp.entity.User;
import com.supermarket.erp.repository.LocationRepository;
import com.supermarket.erp.repository.RoleRepository;
import com.supermarket.erp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds roles, a default admin login, and a default location on first run
 * so the app is usable immediately after `mvn spring-boot:run`.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                            LocationRepository locationRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByNameIgnoreCase("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));
        roleRepository.findByNameIgnoreCase("MANAGER").orElseGet(() -> roleRepository.save(new Role("MANAGER")));
        roleRepository.findByNameIgnoreCase("CASHIER").orElseGet(() -> roleRepository.save(new Role("CASHIER")));

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setFullName("System Administrator");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);
            admin.setEmail("admin@supermarket.local");
            admin.setContactNo("0000000000");
            admin.setStatus("ACTIVE");
            userRepository.save(admin);
        }

        if (locationRepository.count() == 0) {
            locationRepository.save(new Location("Main Warehouse", "Head Office"));
        }
    }
}
