package com.rit.gamifiedticketing.service;

import com.rit.gamifiedticketing.dto.UserStatisticsDTO;
import com.rit.gamifiedticketing.entity.User;
import com.rit.gamifiedticketing.repository.TicketRepository;
import com.rit.gamifiedticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_SOLVER', 'ROLE_ADMIN')")
    public UserStatisticsDTO getUserStatistics() {
        String username = getAuthenticatedUsername();

        int points = userRepository.findUserPoints(username);
        int completedTickets = ticketRepository.countCompletedTickets(username);
        int rank = userRepository.getUserRank(username);

        return new UserStatisticsDTO(points, completedTickets, rank);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_SOLVER', 'ROLE_ADMIN')")
    public void addPointsToLoggedInUser(int points) {
        String username = getAuthenticatedUsername();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPoints(user.getPoints() + points);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();  // Handles cases where the principal is a raw string
        }
    }
}
