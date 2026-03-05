package com.pm.authservice.config;

import com.pm.authservice.model.User;
import com.pm.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DefaultUserSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${auth.seed.email:test@example.com}")
    private String seedEmail;

    @Value("${auth.seed.password:password123}")
    private String seedPassword;

    @Value("${auth.seed.role:PATIENT}")
    private String seedRole;

    public DefaultUserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }

        if (userRepository.findByEmail(seedEmail).isPresent()) {
            return;
        }

        User user = new User();
        user.setEmail(seedEmail);
        user.setPassword(passwordEncoder.encode(seedPassword));
        user.setRole(seedRole);
        userRepository.save(user);

        log.info("Seeded default auth user: {}", seedEmail);
    }
}
