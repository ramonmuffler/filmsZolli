package org.example.films.auth;

import java.time.Instant;

import org.example.films.user.AppUser;
import org.example.films.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Dieser Username ist bereits vergeben.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Diese E-Mail ist bereits registriert.");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRegistrationDate(Instant.now());
        return userRepository.save(user);
    }

    public AppUser login(LoginRequest request) {
        String usernameOrEmail = request.usernameOrEmail().trim();
        AppUser user = userRepository.findByUsernameIgnoreCase(usernameOrEmail)
                .or(() -> userRepository.findByEmailIgnoreCase(usernameOrEmail))
                .orElseThrow(() -> new IllegalArgumentException("Login fehlgeschlagen."));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Login fehlgeschlagen.");
        }

        return user;
    }
}
