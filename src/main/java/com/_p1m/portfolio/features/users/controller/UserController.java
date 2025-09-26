package com._p1m.portfolio.features.users.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/auth/users")
@Tag(name = "User API", description = "Endpoints for managing users")
public class UserController {
}
