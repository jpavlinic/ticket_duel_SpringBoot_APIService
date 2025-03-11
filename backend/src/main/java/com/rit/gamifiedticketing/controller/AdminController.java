package com.rit.gamifiedticketing.controller;

import com.rit.gamifiedticketing.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/reset-leaderboard")
    public ResponseEntity<String> resetLeaderboard() {
        String response = adminService.resetLeaderboard();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reset-tickets")
    public ResponseEntity<String> resetTickets() {
        String response = adminService.resetTickets();
        return ResponseEntity.ok(response);
    }
}
