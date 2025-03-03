package com.rit.gamifiedticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FullTicketResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String createdBy; // Only username
    private String assignedTo; // Only username (nullable)
    private String status;
    private int points;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private String finalSolution;
    private List<CommentDTO> comments; // ✅ Changed from String to List<CommentDTO>
}
