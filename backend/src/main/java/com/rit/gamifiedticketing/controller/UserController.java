package com.rit.gamifiedticketing.controller;

import com.rit.gamifiedticketing.dto.UpdatePointsRequest;
import com.rit.gamifiedticketing.dto.UserStatisticsDTO;
import com.rit.gamifiedticketing.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, String>> getUserStatistics() {
        Map<String, String> response = new HashMap<>();
        UserStatisticsDTO stats = userService.getUserStatistics();

        response.put("points", String.valueOf(stats.getPoints()));
        response.put("completedTickets", String.valueOf(stats.getCompletedTickets()));
        response.put("rank", String.valueOf(stats.getRank()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-points")
    public ResponseEntity<String> updatePoints(@Valid @RequestBody UpdatePointsRequest request) {
        userService.addPointsToLoggedInUser(request.getPoints());
        return ResponseEntity.ok("Points updated successfully!");
    }
}
