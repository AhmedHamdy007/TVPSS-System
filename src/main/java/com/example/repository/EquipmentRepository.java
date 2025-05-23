package com.example.repository;

import com.example.model.Equipment;
import com.example.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
	List<Equipment> findByTeacherName(String teacherName);
    List<Equipment> findBySchoolName(String schoolName);
    List<Equipment> findByLevel(EquipmentLevel level);
    List<Equipment> findBySchoolNameAndLevel(String schoolName, EquipmentLevel level);
    
    @Query("SELECT e FROM Equipment e WHERE e.teacherName = :teacherName AND e.schoolVersion.id = :schoolVersionId")
    Optional<Equipment> findByTeacherNameAndSchoolVersionId(@Param("teacherName") String teacherName,@Param("schoolVersionId") Long schoolVersionId);
        
}