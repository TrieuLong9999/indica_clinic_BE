package com.clinic.indica.service;

import com.clinic.indica.dto.CreateSpecialtyRequest;
import com.clinic.indica.dto.SpecialtyResponse;
import com.clinic.indica.dto.UpdateSpecialtyRequest;
import com.clinic.indica.entity.Specialty;

import java.util.List;
import java.util.Optional;

public interface SpecialtyService {
    Specialty createSpecialty(Specialty specialty);
    Specialty updateSpecialty(Long id, Specialty specialty);
    void deleteSpecialty(Long id);
    Optional<Specialty> getSpecialtyById(Long id);
    Optional<Specialty> getSpecialtyByName(String name);
    List<Specialty> getAllSpecialties();
    boolean existsByName(String name);
    
    // Methods với DTOs
    SpecialtyResponse createSpecialtyFromRequest(CreateSpecialtyRequest request);
    SpecialtyResponse updateSpecialtyFromRequest(Long id, UpdateSpecialtyRequest request);
    SpecialtyResponse getSpecialtyResponseById(Long id);
    List<SpecialtyResponse> getAllSpecialtyResponses();
    List<SpecialtyResponse> searchSpecialties(String name, Boolean enabled);
}

