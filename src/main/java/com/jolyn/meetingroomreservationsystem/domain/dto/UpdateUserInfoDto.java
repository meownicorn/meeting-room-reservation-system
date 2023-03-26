package com.jolyn.meetingroomreservationsystem.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoDto {
    private String firstName;
    private String lastName;
    private String email;
    private String contact;
}
