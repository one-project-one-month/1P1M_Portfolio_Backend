package com._p1m.portfolio.features.devProfile.repository;

import com._p1m.portfolio.data.models.lookup.TechStack;
import com._p1m.portfolio.data.repositories.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TechStackFinder {

    private final TechStackRepository techStackRepository;

    public TechStack findOrCreateByName(String name) {
        return techStackRepository.findAll().stream()
                .filter(stack -> stack.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> techStackRepository.save(
                        TechStack.builder().name(name).build()
                ));
    }
}