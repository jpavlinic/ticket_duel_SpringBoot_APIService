package com.rit.gamifiedticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserStatisticsDTO {
    private int points;
    private int completedTickets;
    private int rank;
}
