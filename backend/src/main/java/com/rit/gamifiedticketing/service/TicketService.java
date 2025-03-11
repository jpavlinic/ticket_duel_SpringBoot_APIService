package com.rit.gamifiedticketing.service;

import com.rit.gamifiedticketing.dto.CommentDTO;
import com.rit.gamifiedticketing.dto.FullTicketResponseDTO;
import com.rit.gamifiedticketing.dto.TicketDTO;
import com.rit.gamifiedticketing.dto.TicketResponseDTO;
import com.rit.gamifiedticketing.entity.Comment;
import com.rit.gamifiedticketing.entity.Ticket;
import com.rit.gamifiedticketing.entity.User;
import com.rit.gamifiedticketing.repository.CommentRepository;
import com.rit.gamifiedticketing.repository.TicketRepository;
import com.rit.gamifiedticketing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_QUESTIONNAIRE', 'ROLE_ADMIN')")
    public Ticket createTicket(TicketDTO request) {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPoints(request.getPoints());
        ticket.setCreatedBy(user);
        ticket.setStatus(Ticket.TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_QUESTIONNAIRE', 'ROLE_ADMIN')")
    public Ticket updateTicket(Long ticketId, TicketDTO request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        String username = getAuthenticatedUsername();
        boolean isCreator = ticket.getCreatedBy().getUsername().equals(username);
        boolean isAdmin = isAdmin(username);

        // Ensure only the creator and admin can update the ticket
        if (!isCreator && !isAdmin) {
            throw new RuntimeException("Unauthorized to update this ticket.");
        }

        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPoints(request.getPoints());
        return ticketRepository.save(ticket);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_QUESTIONNAIRE', 'ROLE_ADMIN')")
    public void deleteTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        String username = getAuthenticatedUsername();
        boolean isCreator = ticket.getCreatedBy().getUsername().equals(username);
        boolean isAdmin = isAdmin(username);

        if (!isCreator && !isAdmin) {
            throw new RuntimeException("Unauthorized to delete this ticket.");
        }

        ticketRepository.delete(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getMyTickets() {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = getAuthenticatedUserRole();

        List<Ticket> tickets;

        if (role.contains("ADMIN")) {
            tickets = ticketRepository.findAll(); // Admin gets all tickets
        } else if (role.contains("SOLVER")) {
            tickets = ticketRepository.findByAssignedTo(user); // Solver gets assigned tickets
        } else if (role.contains("QUESTIONNAIRE")) {
            tickets = ticketRepository.findByCreatedBy(user); // Questionnaire gets created tickets
        } else {
            throw new RuntimeException("Unauthorized to view tickets.");
        }

        // Map each Ticket entity to a TicketResponseDTO
        return tickets.stream()
                .map(ticket -> new TicketResponseDTO(
                        ticket.getId(),
                        ticket.getTitle(),
                        ticket.getDescription(),
                        ticket.getPoints(),
                        ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUsername() : null,
                        ticket.getCreatedBy().getUsername(),
                        ticket.getStatus().name()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FullTicketResponseDTO getTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Fetch comments from the comments table
        List<CommentDTO> comments = commentRepository.findByTicketId(ticketId).stream()
                .map(comment -> new CommentDTO(
                        comment.getTime().toString(),
                        comment.getUser().getUsername(),
                        comment.getMessage()))
                .collect(Collectors.toList());

        return new FullTicketResponseDTO(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCreatedBy().getUsername(), // Only username
                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUsername() : null, // Nullable username
                ticket.getStatus().name(), // Enum converted to String
                ticket.getPoints(),
                ticket.getCreatedAt(),
                ticket.getLastUpdated(),
                ticket.getFinalSolution(),
                comments // Now returns a proper list of CommentDTO instead of a JSON string
        );
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_SOLVER', 'ROLE_ADMIN')")
    public List<TicketResponseDTO> getUnassignedOpenTickets() {
        List<Ticket> tickets = ticketRepository.findByStatusAndAssignedToIsNull(Ticket.TicketStatus.OPEN);

        return tickets.stream()
                .map(ticket -> new TicketResponseDTO(
                        ticket.getId(),
                        ticket.getTitle(),
                        ticket.getDescription(),
                        ticket.getPoints(),
                        null, // No assigned solver (always null)
                        ticket.getCreatedBy().getUsername(),
                        ticket.getStatus().name()))
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_SOLVER', 'ROLE_ADMIN')")
    public String assignTicketToSelf(Long ticketId) {
        String username = getAuthenticatedUsername();
        User solver = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check if the ticket is OPEN and unassigned
        if (ticket.getStatus() != Ticket.TicketStatus.OPEN || ticket.getAssignedTo() != null) {
            throw new RuntimeException("Ticket is either already assigned or not open.");
        }

        // Assign the solver to the ticket
        ticket.setAssignedTo(solver);
        ticketRepository.save(ticket);

        return "Ticket assigned successfully to " + username;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_SOLVER', 'ROLE_ADMIN')")
    public String updateFinalSolution(Long ticketId, String finalSolution) {
        String username = getAuthenticatedUsername();
        User solver = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Ensure only assigned solver or admin can update final solution
        if (!solver.equals(ticket.getAssignedTo()) && !isAdmin(username)) {
            throw new RuntimeException("You are not authorized to update the final solution for this ticket.");
        }

        // Remove extra escape characters from finalSolution
        finalSolution = finalSolution.replaceAll("^\"|\"$", "").trim();

        ticket.setFinalSolution(finalSolution);
        ticketRepository.save(ticket);

        return "Final solution updated successfully for ticket ID " + ticketId;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SOLVER', 'ROLE_QUESTIONNAIRE')")
    public String addComment(Long ticketId, String commentText) {
        String username = getAuthenticatedUsername(); // ✅ Automatically fetch logged-in user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check if user is authorized to comment
        if (!isAdmin(username) &&
                !user.equals(ticket.getAssignedTo()) &&
                !user.equals(ticket.getCreatedBy())) {
            throw new RuntimeException("You are not authorized to comment on this ticket.");
        }

        // Trim comment text
        commentText = commentText.trim();

        // Create and save comment with auto-generated time and username
        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setUser(user);
        comment.setMessage(commentText);
        comment.setTime(LocalDateTime.now()); // ✅ Auto-set timestamp

        commentRepository.save(comment);

        return "Comment added successfully.";
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_SOLVER', 'ROLE_QUESTIONNAIRE', 'ROLE_ADMIN')")
    public String updateTicketStatus(Long ticketId, String newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        Ticket.TicketStatus currentStatus = ticket.getStatus();
        Ticket.TicketStatus updatedStatus;
    
        try {
            updatedStatus = Ticket.TicketStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status. Allowed values: OPEN, IN_PROGRESS, IN_REVIEW, COMPLETED, REJECTED");
        }
    
        // ✅ SOLVER: Move from OPEN → IN_PROGRESS
        if (updatedStatus == Ticket.TicketStatus.IN_PROGRESS) {
            if (!user.equals(ticket.getAssignedTo())) {
                throw new RuntimeException("Only the assigned solver can move the ticket to IN_PROGRESS.");
            }
            if (currentStatus != Ticket.TicketStatus.OPEN && currentStatus != Ticket.TicketStatus.IN_REVIEW) {
                throw new RuntimeException("Only OPEN or IN_REVIEW tickets can be moved to IN_PROGRESS.");
            }
        }
    
        // ✅ SOLVER: Move from IN_PROGRESS → IN_REVIEW
        if (updatedStatus == Ticket.TicketStatus.IN_REVIEW) {
            if (!user.equals(ticket.getAssignedTo())) {
                throw new RuntimeException("Only the assigned solver can move the ticket to IN_REVIEW.");
            }
            if (currentStatus != Ticket.TicketStatus.IN_PROGRESS) {
                throw new RuntimeException("Only IN_PROGRESS tickets can be moved to IN_REVIEW.");
            }
        }
    
        // ✅ SOLVER: Move from IN_PROGRESS → OPEN
        if (updatedStatus == Ticket.TicketStatus.OPEN) {
            if (!user.equals(ticket.getAssignedTo())) {
                throw new RuntimeException("Only the assigned solver can move the ticket back to OPEN.");
            }
            if (currentStatus != Ticket.TicketStatus.IN_PROGRESS) {
                throw new RuntimeException("Only IN_PROGRESS tickets can be moved back to OPEN.");
            }
            // Remove assignment since it's being moved back to OPEN
            ticket.setAssignedTo(null);
        }
    
        // ✅ QUESTIONNAIRE: Move from IN_REVIEW → IN_PROGRESS, COMPLETED, or REJECTED
        if (updatedStatus == Ticket.TicketStatus.COMPLETED || updatedStatus == Ticket.TicketStatus.REJECTED) {
            if (!user.equals(ticket.getCreatedBy())) {
                throw new RuntimeException("Only the ticket creator can mark it as COMPLETED or REJECTED.");
            }
            if (currentStatus != Ticket.TicketStatus.IN_REVIEW) {
                throw new RuntimeException("Only IN_REVIEW tickets can be moved to COMPLETED or REJECTED.");
            }
    
            // ✅ Assign points if ticket is COMPLETED
            if (updatedStatus == Ticket.TicketStatus.COMPLETED && ticket.getAssignedTo() != null) {
                User solver = ticket.getAssignedTo();
                solver.setPoints(solver.getPoints() + ticket.getPoints());
                userRepository.save(solver);
            }
        }
    
        // ✅ QUESTIONNAIRE: Move from IN_REVIEW → IN_PROGRESS
        if (updatedStatus == Ticket.TicketStatus.IN_PROGRESS && currentStatus == Ticket.TicketStatus.IN_REVIEW) {
            if (!user.equals(ticket.getCreatedBy())) {
                throw new RuntimeException("Only the ticket creator can move it back to IN_PROGRESS.");
            }
        }
    
        // ✅ Update ticket status
        ticket.setStatus(updatedStatus);
        ticketRepository.save(ticket);
    
        return "Ticket status updated successfully to " + updatedStatus;
    }    

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_QUESTIONNAIRE', 'ROLE_ADMIN')")
    public String resetRejectedTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // ✅ Ensure the ticket is in REJECTED status
        if (ticket.getStatus() != Ticket.TicketStatus.REJECTED) {
            throw new RuntimeException("Only REJECTED tickets can be reset.");
        }

        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Ensure only creator (Questionnaire) or Admin can reset
        if (!user.equals(ticket.getCreatedBy()) && !isAdmin(username)) {
            throw new RuntimeException("You are not authorized to reset this ticket.");
        }

        // ✅ Delete all comments related to the ticket
        commentRepository.deleteByTicketId(ticketId);

        // ✅ Reset the ticket details
        ticket.setFinalSolution(null);
        ticket.setAssignedTo(null);
        ticket.setStatus(Ticket.TicketStatus.OPEN);

        ticketRepository.save(ticket);

        return "Ticket reset successfully to OPEN.";
    }

    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString(); // Handles cases where the principal is a raw string
        }
    }

    private boolean isAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(user -> user.getRole().toUpperCase().contains("ADMIN"))
                .orElse(false);
    }

    private String getAuthenticatedUserRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

}
