package com.jolyn.meetingroomreservationsystem.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String contact;
    private String role;
}
