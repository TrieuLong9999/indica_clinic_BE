package com.clinic.indica.controller;

import com.clinic.indica.dto.ApiResponse;
import com.clinic.indica.dto.CreateSpecialtyRequest;
import com.clinic.indica.dto.SpecialtyResponse;
import com.clinic.indica.dto.UpdateSpecialtyRequest;
import com.clinic.indica.service.SpecialtyService;
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
@RequestMapping("/api/specialties")
@Tag(name = "Specialty Management", description = "API quản lý chuyên khoa - Chỉ SUPERADMIN và ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class SpecialtyController {

    @Autowired
    private SpecialtyService specialtyService;

    @Operation(
            summary = "Tạo chuyên khoa mới",
            description = "Chỉ SUPERADMIN và ADMIN mới có quyền tạo chuyên khoa mới"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Tạo chuyên khoa thành công",
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
                    description = "Tên chuyên khoa đã tồn tại",
                    content = @Content
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> createSpecialty(@Valid @RequestBody CreateSpecialtyRequest request) {
        SpecialtyResponse specialtyResponse = specialtyService.createSpecialtyFromRequest(request);
        ApiResponse<SpecialtyResponse> response = ApiResponse.created("Tạo chuyên khoa thành công", specialtyResponse);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
            summary = "Lấy danh sách tất cả chuyên khoa",
            description = "Lấy danh sách tất cả chuyên khoa với khả năng tìm kiếm và lọc"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialtyResponse>>> getAllSpecialties(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean enabled) {
        List<SpecialtyResponse> specialties;
        
        if (name != null || enabled != null) {
            // Tìm kiếm với filter
            specialties = specialtyService.searchSpecialties(name, enabled);
        } else {
            // Lấy tất cả
            specialties = specialtyService.getAllSpecialtyResponses();
        }
        
        ApiResponse<List<SpecialtyResponse>> response = ApiResponse.success("Lấy danh sách chuyên khoa thành công", specialties);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Lấy thông tin chuyên khoa theo ID",
            description = "Lấy thông tin chi tiết của một chuyên khoa theo ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy thông tin thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy chuyên khoa",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> getSpecialtyById(@PathVariable Long id) {
        SpecialtyResponse specialtyResponse = specialtyService.getSpecialtyResponseById(id);
        ApiResponse<SpecialtyResponse> response = ApiResponse.success("Lấy thông tin chuyên khoa thành công", specialtyResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cập nhật thông tin chuyên khoa",
            description = "Chỉ SUPERADMIN và ADMIN mới có quyền cập nhật thông tin chuyên khoa"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy chuyên khoa",
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
                    description = "Tên chuyên khoa đã tồn tại",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> updateSpecialty(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSpecialtyRequest request) {
        SpecialtyResponse specialtyResponse = specialtyService.updateSpecialtyFromRequest(id, request);
        ApiResponse<SpecialtyResponse> response = ApiResponse.success("Cập nhật thông tin chuyên khoa thành công", specialtyResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Xóa chuyên khoa",
            description = "Chỉ SUPERADMIN và ADMIN mới có quyền xóa chuyên khoa"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Xóa thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy chuyên khoa",
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
    public ResponseEntity<ApiResponse<Object>> deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        ApiResponse<Object> response = ApiResponse.success("Xóa chuyên khoa thành công", null);
        return ResponseEntity.ok(response);
    }
}

