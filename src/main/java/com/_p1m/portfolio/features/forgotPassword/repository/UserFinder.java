package com._p1m.portfolio.features.forgotPassword.repository;

import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFinder {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
