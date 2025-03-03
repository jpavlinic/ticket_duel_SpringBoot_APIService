package com.rit.gamifiedticketing.dto;

import lombok.*;
import com.rit.gamifiedticketing.entity.Ticket.TicketStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {

    @NotBlank
    @Size(min = 5, max = 255)
    private String title;

    @NotBlank
    private String description;

    @Min(1)
    private int points;

    private TicketStatus status;
}
