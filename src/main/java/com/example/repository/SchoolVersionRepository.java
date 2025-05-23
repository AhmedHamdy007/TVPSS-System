package com.example.repository;

import com.example.model.SchoolVersion;
import com.example.model.SchoolVersionLevel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SchoolVersionRepository extends JpaRepository<SchoolVersion, Long> {
	List<SchoolVersion> findByTeacherName(String teacherName);
    List<SchoolVersion> findByVersionLevel(SchoolVersionLevel versionLevel);
    List<SchoolVersion> findBySchoolNameAndVersionLevel(String schoolName, SchoolVersionLevel versionLevel);
    SchoolVersion findFirstBySchoolNameOrderByCreatedDateDesc(String schoolName);
    
    @Query("SELECT sv FROM SchoolVersion sv JOIN FETCH sv.teacher")
    List<SchoolVersion> findAllWithTeacher();

    @Query("SELECT sv FROM SchoolVersion sv WHERE sv.schoolName = :schoolName")
    List<SchoolVersion> findBySchoolName(@Param("schoolName") String schoolName);

	
}
