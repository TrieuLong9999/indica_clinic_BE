package com.clinic.indica.repository;

import com.clinic.indica.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    Optional<Specialty> findByName(String name);
    boolean existsByName(String name);
    
    @Query("SELECT s FROM Specialty s WHERE " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:enabled IS NULL OR s.enabled = :enabled)")
    List<Specialty> findByFilters(@Param("name") String name, @Param("enabled") Boolean enabled);
    
    List<Specialty> findByEnabledTrue();
    List<Specialty> findByEnabledFalse();
}

