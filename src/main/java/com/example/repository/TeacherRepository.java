package com.example.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUsername(String username);
    // findById is already provided by JpaRepository
    Optional<Teacher> findByUsernameAndPassword(String username, String password);
    // Remove findByUserId as it's not needed with the inheritance structure
    
    List<Teacher> findBySchoolName(String schoolName);
    
    @Query("SELECT DISTINCT t.department FROM Teacher t")
    List<String> findAllDepartments();

    List<Teacher> findBySchoolNameAndDepartment(String schoolName, String department);
}