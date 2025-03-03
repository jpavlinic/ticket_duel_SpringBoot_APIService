package com.rit.gamifiedticketing.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rit.gamifiedticketing.dto.UserDTO;
import com.rit.gamifiedticketing.entity.User;
import com.rit.gamifiedticketing.repository.UserRepository;

@Service
public class PublicService {
    private final UserRepository userRepository;

    public PublicService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getLeaderboard() {
        List<User> topSolvers = userRepository.findTop30ByRoleOrderByPointsDesc("SOLVER");
        
        // Convert to DTO to avoid exposing sensitive info
        return topSolvers.stream()
                .map(user -> new UserDTO(user.getUsername(), user.getEmail(), user.getPoints()))
                .collect(Collectors.toList());
    }
}

