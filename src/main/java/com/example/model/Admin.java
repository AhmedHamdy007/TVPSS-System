package com.example.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    
    @Column(name = "employeeid", length = 50, unique = true)
    private String employeeId;

    @Column(length = 100)
    private String department;

    public Admin() {
        setRole("ROLE_ADMIN");
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
}