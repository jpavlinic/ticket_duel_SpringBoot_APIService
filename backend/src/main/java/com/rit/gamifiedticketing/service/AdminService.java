package com.rit.gamifiedticketing.service;

import com.rit.gamifiedticketing.repository.CommentRepository;
import com.rit.gamifiedticketing.repository.TicketRepository;
import com.rit.gamifiedticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String resetLeaderboard() {
        userRepository.resetAllUserPoints();
        return "Leaderboard has been reset. All user points set to 0.";
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String resetTickets() {
        commentRepository.deleteAll();
        ticketRepository.deleteAll();
        return "All tickets and comments have been deleted.";
    }
}
