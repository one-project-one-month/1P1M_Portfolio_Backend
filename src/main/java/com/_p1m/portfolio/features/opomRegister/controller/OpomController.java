package com._p1m.portfolio.features.opomRegister.controller;

import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.dto.response.UserRegisterResponse;
import com._p1m.portfolio.features.opomRegister.service.OpomRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/opom")
@RequiredArgsConstructor
public class OpomController {

    private final OpomRegisterService service;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(service.registerUser(request));
    }

    @GetMapping("/admin/list")
    public ResponseEntity<Page<UserRegisterResponse>> getAllOpomRegisters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String dateOrder,
            @RequestParam(required = false) String role
    ) {
        return ResponseEntity.ok(service.getAllOpomRegisters(page, size, dateOrder, role));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<UserRegisterResponse> updateOpomRegisterData(
            @PathVariable Long id,
            @Valid @RequestBody UserRegisterRequest request
    ) {
        return ResponseEntity.ok(service.updateOpomRegisterData(id, request));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> softDeleteOpomRegister(@PathVariable Long id) {
        service.softDeleteOpomRegister(id);
        return ResponseEntity.noContent().build();
    }
}
