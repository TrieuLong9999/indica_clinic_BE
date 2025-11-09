package com.clinic.indica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    private String username;

    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    private String password;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String fullName;

    private Boolean enabled;

    private Set<String> roles; // Tên các roles: SUPERADMIN, ADMIN, etc.
}

