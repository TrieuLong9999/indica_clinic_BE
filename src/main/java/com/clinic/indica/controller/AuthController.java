package com.clinic.indica.controller;

import com.clinic.indica.dto.ApiResponse;
import com.clinic.indica.dto.AuthResponse;
import com.clinic.indica.dto.LoginRequest;
import com.clinic.indica.dto.RefreshTokenRequest;
import com.clinic.indica.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API xác thực người dùng với JWT")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(
            summary = "Đăng nhập",
            description = "Xác thực người dùng và trả về JWT access token cùng refresh token"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Đăng nhập thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Thông tin đăng nhập không hợp lệ",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            ApiResponse<AuthResponse> response = ApiResponse.success("Đăng nhập thành công", authResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<AuthResponse> response = ApiResponse.unauthorized("Thông tin đăng nhập không hợp lệ");
            return ResponseEntity.status(401).body(response);
        }
    }

    @Operation(
            summary = "Làm mới Access Token",
            description = "Sử dụng refresh token để lấy access token mới khi access token đã hết hạn"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Làm mới token thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Refresh token không hợp lệ hoặc đã hết hạn",
                    content = @Content
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);
            ApiResponse<AuthResponse> response = ApiResponse.success("Làm mới token thành công", authResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<AuthResponse> response = ApiResponse.unauthorized("Refresh token không hợp lệ hoặc đã hết hạn");
            return ResponseEntity.status(401).body(response);
        }
    }
}

