package com.example.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("STUDENT")
public class Student extends User {
	
	
	@ManyToOne
	@JoinColumn(name = "teacher_id", referencedColumnName = "id")
	private Teacher teacher;
	
	 @Column(name = "student_id", length = 50, unique = true)
	    private String studentId;
	    
	    @Column(length = 100)
	    private String course;
	    
	    @Column(name = "year_level")
	    private Integer yearLevel;
	    
	    @OneToMany(mappedBy = "student")
	    private List<Application> applications;
	    
	    @Column(name = "school_name", length = 100)
	    private String schoolName;
	    
	    
	    public Student() {
	        this.setRole("ROLE_STUDENT");
	    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }
    public String getSchoolName() {
        return schoolName;
    }
    
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
