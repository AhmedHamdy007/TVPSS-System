package com.example.repository;

import com.example.model.Interview;
import com.example.model.Interview.InterviewStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    @Query("SELECT i FROM Interview i WHERE " +
           "(:studentName IS NULL OR LOWER(i.application.name) LIKE LOWER(CONCAT('%', :studentName, '%'))) AND " +
           "(:date IS NULL OR DATE(i.scheduledDate) = DATE(:date))")
    List<Interview> findByFilters(@Param("studentName") String studentName, 
                                 @Param("date") LocalDateTime date);
    
    

    @Query("SELECT i FROM Interview i " +
    	       "JOIN i.application a " +
    	       "WHERE a.student.id = :studentId " +
    	       "AND (:date IS NULL OR DATE(i.scheduledDate) = :date) " +
    	       "AND (:status IS NULL OR i.status = :status)")
    	List<Interview> findByFiltersAndStudent(
    	    @Param("studentId") Long studentId,
    	    @Param("date") LocalDate date,
    	    @Param("status") String status
    	);

    @Query("SELECT i FROM Interview i " +
    	       "WHERE i.teacher.id = :teacherId " +
    	       "AND i.scheduledDate >= CURRENT_TIMESTAMP " +
    	       "ORDER BY i.scheduledDate ASC")
    	List<Interview> findUpcomingInterviewsByTeacherId(@Param("teacherId") Long teacherId);
 
    @Query("SELECT i FROM Interview i " +
    	       "WHERE i.application.student.id = :studentId " +
    	       "AND i.scheduledDate >= CURRENT_TIMESTAMP " +
    	       "ORDER BY i.scheduledDate ASC")
    	List<Interview> findByStudentIdAndUpcoming(@Param("studentId") Long studentId);

    
    
     @Query("SELECT i FROM Interview i " +
    	       "WHERE i.application.student.id = :studentId " +
    	       "AND i.scheduledDate >= :date " +
    	       "ORDER BY i.scheduledDate ASC")
    	List<Interview> findByStudentIdAndDateCloser(@Param("studentId") Long studentId, @Param("date") LocalDateTime date);


     @Query("SELECT i FROM Interview i WHERE i.application.student.id = :studentId")
     List<Interview> findByStudentId(@Param("studentId") Long studentId);

     
     @Query("SELECT i FROM Interview i WHERE i.teacher.id = :teacherId AND i.scheduledDate >= :date ORDER BY i.scheduledDate ASC")
     List<Interview> findByTeacherIdAndDateCloser(@Param("teacherId") Long teacherId, @Param("date") LocalDateTime date);

     List<Interview> findByTeacherId(Long teacherId);

    
     
     // Find interviews by date range
     List<Interview> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);
     
     // Find interviews by status
     List<Interview> findByStatus(InterviewStatus status);
     
     @Query("SELECT i FROM Interview i JOIN FETCH i.application a JOIN FETCH i.teacher WHERE i.teacher.id = :teacherId")
     List<Interview> findAllWithApplicationsByTeacherId(@Param("teacherId") Long teacherId);
}