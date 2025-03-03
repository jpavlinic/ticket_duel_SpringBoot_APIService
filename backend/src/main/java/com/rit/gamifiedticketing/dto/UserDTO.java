package com.rit.gamifiedticketing.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotBlank
    @Size(min = 4, max = 50)
    private String username;

    @Email
    @NotBlank
    @Size(max = 100)
    private String email;

    @Size(max = 10)
    private int points;

}
