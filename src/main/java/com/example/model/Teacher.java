package com.example.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("TEACHER")
public class Teacher extends User {
    
    @Column(name = "employeeid", length = 50, unique = true)
    private String employeeId;
    
    @Column(length = 100)
    private String department;
    
    @OneToMany(mappedBy = "teacher")
    private List<Application> applications;
    
    @OneToMany(mappedBy = "teacher")
    private Set<Interview> interviews;
    
    @Column(name = "school_name", length = 100)
    private String schoolName;
    
    public Teacher() {
        this.setRole("ROLE_TEACHER");
    }

    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getSchoolName() {
        return schoolName;
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