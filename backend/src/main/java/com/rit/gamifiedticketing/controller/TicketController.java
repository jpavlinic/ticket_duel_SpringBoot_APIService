package com.rit.gamifiedticketing.controller;

import com.rit.gamifiedticketing.dto.FullTicketResponseDTO;
import com.rit.gamifiedticketing.dto.TicketDTO;
import com.rit.gamifiedticketing.dto.TicketResponseDTO;
import com.rit.gamifiedticketing.service.TicketService;
import com.rit.gamifiedticketing.service.WebSocketService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final WebSocketService webSocketService;

    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketDTO request) {
        try {
            ticketService.createTicket(request);
            webSocketService.sendUpdate("Refresh");
            return ResponseEntity.ok("Ticket created successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{ticketId}/update")
    public ResponseEntity<?> updateTicket(@PathVariable Long ticketId, @Valid @RequestBody TicketDTO request) {
        try {
            ticketService.updateTicket(ticketId, request);
            webSocketService.sendUpdate("Refresh");
            return ResponseEntity.ok("Ticket updated successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long ticketId) {
        try {
            ticketService.deleteTicket(ticketId);
            webSocketService.sendUpdate("Refresh");
            return ResponseEntity.ok("Ticket deleted successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<?> getMyTickets() {
        try {
            List<TicketResponseDTO> tickets = ticketService.getMyTickets();
            return ResponseEntity.ok(tickets);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<?> getTicketById(@PathVariable Long ticketId) {
        try {
            FullTicketResponseDTO ticket = ticketService.getTicketById(ticketId);
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/open")
    public ResponseEntity<?> getUnassignedOpenTickets() {
        try {
            List<TicketResponseDTO> tickets = ticketService.getUnassignedOpenTickets();
            return ResponseEntity.ok(tickets);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{ticketId}/assign")
    public ResponseEntity<?> assignTicketToSelf(@PathVariable Long ticketId) {
        try {
            String response = ticketService.assignTicketToSelf(ticketId);
            webSocketService.sendUpdate("Refresh");
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{ticketId}/solution")
    public ResponseEntity<?> updateFinalSolution(@PathVariable Long ticketId, @RequestBody String finalSolution) {
        try {
            String response = ticketService.updateFinalSolution(ticketId, finalSolution);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<String> addComment(@PathVariable Long ticketId, @RequestBody String comment) {
        String response = ticketService.addComment(ticketId, comment);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<?> updateTicketStatus(@PathVariable Long ticketId, @RequestParam String newStatus) {
        try {
            String response = ticketService.updateTicketStatus(ticketId, newStatus);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{ticketId}/reset")
    public ResponseEntity<?> resetRejectedTicket(@PathVariable Long ticketId) {
        try {
            String response = ticketService.resetRejectedTicket(ticketId);
            webSocketService.sendUpdate("Refresh");
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
