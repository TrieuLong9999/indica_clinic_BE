package com.clinic.indica.controller;

import com.clinic.indica.dto.ApiResponse;
import com.clinic.indica.dto.CreateUserRequest;
import com.clinic.indica.dto.UpdateUserRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API quản lý người dùng - Chỉ SUPERADMIN và ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Tạo người dùng mới",
            description = "Chỉ SUPERADMIN và ADMIN mới có quyền tạo tài khoản người dùng mới"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Tạo người dùng thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền truy cập",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Username hoặc email đã tồn tại",
                    content = @Content
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse userResponse = userService.createUserFromRequest(request);
        ApiResponse<UserResponse> response = ApiResponse.created("Tạo người dùng thành công", userResponse);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
            summary = "Lấy danh sách tất cả người dùng",
            description = "Chỉ SUPERADMIN và ADMIN mới có quyền xem danh sách người dùng"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền truy cập",
                    content = @Content
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUserResponses();
        ApiResponse<List<UserResponse>> response = ApiResponse.success("Lấy danh sách người dùng thành công", users);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Lấy thông tin người dùng theo ID",
            description = "Chỉ SUPERADMIN và ADMIN mới có quyền xem thông tin người dùng"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy thông tin thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy người dùng",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền truy cập",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserResponseById(id);
        ApiResponse<UserResponse> response = ApiResponse.success("Lấy thông tin người dùng thành công", userResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cập nhật thông tin người dùng",
            description = "Chỉ SUPERADMIN và ADMIN mới có quyền cập nhật thông tin người dùng"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy người dùng",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền truy cập",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Username hoặc email đã tồn tại",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse userResponse = userService.updateUserFromRequest(id, request);
        String message = request.getPassword() != null && !request.getPassword().isEmpty() 
                ? "Cập nhật thông tin thành công. Mật khẩu đã được đổi, tất cả các thiết bị đã bị đăng xuất."
                : "Cập nhật thông tin thành công";
        ApiResponse<UserResponse> response = ApiResponse.success(message, userResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Xóa người dùng",
            description = "Chỉ SUPERADMIN và ADMIN mới có quyền xóa người dùng"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Xóa thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy người dùng",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền truy cập",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        ApiResponse<Object> response = ApiResponse.success("Xóa người dùng thành công", null);
        return ResponseEntity.ok(response);
    }
}

