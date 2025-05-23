package com.example.repository;

import com.example.model.*;
import com.example.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
	List<Application> findByUser(Student student);
	List<Application> findByTeacherId(Long teacherId);
	 List<Application> findByStudent(Student student);
	List<Application> findByApplicationType(String applicationType);
	
	// Find applications by status and teacher
    List<Application> findByStatusAndTeacherId(String status, Long teacherId);
	
	@Query("SELECT a FROM Application a WHERE a.status = :status " +
	           "AND NOT EXISTS (SELECT i FROM Interview i WHERE i.application.id = a.id)")
	    List<Application> findByStatusAndInterviewIsNull(@Param("status") String status);
}
