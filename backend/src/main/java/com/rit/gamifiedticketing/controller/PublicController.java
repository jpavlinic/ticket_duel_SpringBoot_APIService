package com.rit.gamifiedticketing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rit.gamifiedticketing.dto.UserDTO;
import com.rit.gamifiedticketing.service.PublicService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/public")
@AllArgsConstructor
public class PublicController {
    private final PublicService publicService;

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        List<UserDTO> leaderboard = publicService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

}
