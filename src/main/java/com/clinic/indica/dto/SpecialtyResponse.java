package com.clinic.indica.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private Integer doctorCount; // Số lượng bác sĩ thuộc chuyên khoa này
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

