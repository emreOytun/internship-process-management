package com.teamaloha.internshipprocessmanagement.dto.user;

import com.teamaloha.internshipprocessmanagement.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/* The class to use inside the backend, not for client/API. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private int id;
    private Date createDate;
    private Date updateDate;
    private String mail;
    private String password;
    private RoleEnum roleEnum;
    private String firstName;
    private String lastName;
}
