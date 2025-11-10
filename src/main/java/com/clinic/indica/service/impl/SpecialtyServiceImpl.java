package com.clinic.indica.service.impl;

import com.clinic.indica.dto.CreateSpecialtyRequest;
import com.clinic.indica.dto.SpecialtyResponse;
import com.clinic.indica.dto.UpdateSpecialtyRequest;
import com.clinic.indica.entity.Specialty;
import com.clinic.indica.exception.DuplicateResourceException;
import com.clinic.indica.exception.ResourceNotFoundException;
import com.clinic.indica.repository.SpecialtyRepository;
import com.clinic.indica.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Override
    @Transactional
    public Specialty createSpecialty(Specialty specialty) {
        if (specialtyRepository.existsByName(specialty.getName())) {
            throw new DuplicateResourceException("Specialty", "name", specialty.getName());
        }
        return specialtyRepository.save(specialty);
    }

    @Override
    @Transactional
    public Specialty updateSpecialty(Long id, Specialty specialtyDetails) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", "id", id));

        // Kiểm tra tên trùng (nếu thay đổi)
        if (!specialty.getName().equals(specialtyDetails.getName()) &&
            specialtyRepository.existsByName(specialtyDetails.getName())) {
            throw new DuplicateResourceException("Specialty", "name", specialtyDetails.getName());
        }

        specialty.setName(specialtyDetails.getName());
        specialty.setDescription(specialtyDetails.getDescription());
        specialty.setEnabled(specialtyDetails.getEnabled());

        return specialtyRepository.save(specialty);
    }

    @Override
    @Transactional
    public void deleteSpecialty(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", "id", id));
        specialtyRepository.delete(specialty);
    }

    @Override
    public Optional<Specialty> getSpecialtyById(Long id) {
        return specialtyRepository.findById(id);
    }

    @Override
    public Optional<Specialty> getSpecialtyByName(String name) {
        return specialtyRepository.findByName(name);
    }

    @Override
    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    @Override
    public boolean existsByName(String name) {
        return specialtyRepository.existsByName(name);
    }

    @Override
    @Transactional
    public SpecialtyResponse createSpecialtyFromRequest(CreateSpecialtyRequest request) {
        // Kiểm tra tên trùng
        if (specialtyRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Specialty", "name", request.getName());
        }

        // Tạo specialty mới
        Specialty specialty = new Specialty();
        specialty.setName(request.getName());
        specialty.setDescription(request.getDescription());
        specialty.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);

        Specialty savedSpecialty = specialtyRepository.save(specialty);
        return convertToSpecialtyResponse(savedSpecialty);
    }

    @Override
    @Transactional
    public SpecialtyResponse updateSpecialtyFromRequest(Long id, UpdateSpecialtyRequest request) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", "id", id));

        // Kiểm tra tên trùng (nếu thay đổi)
        if (request.getName() != null && !request.getName().isEmpty() &&
            !specialty.getName().equals(request.getName()) &&
            specialtyRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Specialty", "name", request.getName());
        }

        // Cập nhật các trường
        if (request.getName() != null && !request.getName().isEmpty()) {
            specialty.setName(request.getName());
        }
        if (request.getDescription() != null) {
            specialty.setDescription(request.getDescription());
        }
        if (request.getEnabled() != null) {
            specialty.setEnabled(request.getEnabled());
        }

        Specialty updatedSpecialty = specialtyRepository.save(specialty);
        return convertToSpecialtyResponse(updatedSpecialty);
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponse getSpecialtyResponseById(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", "id", id));
        return convertToSpecialtyResponse(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getAllSpecialtyResponses() {
        return specialtyRepository.findAll().stream()
                .map(this::convertToSpecialtyResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> searchSpecialties(String name, Boolean enabled) {
        List<Specialty> specialties = specialtyRepository.findByFilters(name, enabled);
        return specialties.stream()
                .map(this::convertToSpecialtyResponse)
                .collect(Collectors.toList());
    }

    private SpecialtyResponse convertToSpecialtyResponse(Specialty specialty) {
        SpecialtyResponse response = new SpecialtyResponse();
        response.setId(specialty.getId());
        response.setName(specialty.getName());
        response.setDescription(specialty.getDescription());
        response.setEnabled(specialty.getEnabled());
        response.setCreatedAt(specialty.getCreatedAt());
        response.setUpdatedAt(specialty.getUpdatedAt());
        
        // Đếm số lượng bác sĩ thuộc chuyên khoa này
        response.setDoctorCount(specialty.getDoctors() != null ? specialty.getDoctors().size() : 0);
        
        return response;
    }
}

