package com.example.repository;

import com.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    
    Optional<Student> findByUsernameAndPassword(String username, String password);
}
