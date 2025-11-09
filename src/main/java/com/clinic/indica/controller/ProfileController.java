package com.clinic.indica.controller;

import com.clinic.indica.dto.ApiResponse;
import com.clinic.indica.dto.UpdateProfileRequest;
import com.clinic.indica.dto.UserResponse;
import com.clinic.indica.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile Management", description = "API quản lý thông tin cá nhân - Yêu cầu đăng nhập")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Lấy thông tin cá nhân",
            description = "Lấy thông tin cá nhân của user đang đăng nhập (yêu cầu JWT token)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy thông tin thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        Long userId = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"))
                .getId();
        
        UserResponse userResponse = userService.getCurrentUserProfile(userId);
        ApiResponse<UserResponse> response = ApiResponse.success("Lấy thông tin cá nhân thành công", userResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cập nhật thông tin cá nhân",
            description = "Cập nhật thông tin cá nhân (tên, mật khẩu, email, số điện thoại). " +
                         "Khi đổi mật khẩu, tất cả thiết bị sẽ bị đăng xuất. Yêu cầu JWT token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email đã tồn tại",
                    content = @Content
            )
    })
    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        String username = authentication.getName();
        Long userId = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"))
                .getId();
        
        UserResponse userResponse = userService.updateProfile(userId, request);
        String message = request.getPassword() != null && !request.getPassword().isEmpty()
                ? "Cập nhật thông tin thành công. Mật khẩu đã được đổi, tất cả các thiết bị đã bị đăng xuất."
                : "Cập nhật thông tin thành công";
        ApiResponse<UserResponse> response = ApiResponse.success(message, userResponse);
        return ResponseEntity.ok(response);
    }
}

