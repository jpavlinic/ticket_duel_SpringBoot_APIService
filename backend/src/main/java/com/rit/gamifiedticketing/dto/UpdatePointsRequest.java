package com.rit.gamifiedticketing.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class UpdatePointsRequest {
    
    @Min(1)
    private int points;
}
