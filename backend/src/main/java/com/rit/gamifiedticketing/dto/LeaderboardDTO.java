package com.rit.gamifiedticketing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardDTO {
    private String username;
    private int totalPoints;
}
