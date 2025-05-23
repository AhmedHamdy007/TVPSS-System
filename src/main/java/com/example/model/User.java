package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)  // Changed to JOINED strategy
@DiscriminatorColumn(name = "user_type")
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "user_type", insertable = false, updatable = false)
    private String userType;
    
    @Column(length = 50, nullable = false)
    private String username;
    
    @Column(length = 255, nullable = false)
    private String password;
    
    @Column(length = 20, nullable = false)
    private String role;
    
    @Column(name = "createdat")
    private LocalDateTime createdAt;
    
    @Column(length = 255)
    private String email;
    
    @Column(length = 255)
    private String fullname;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "user")
    private List<Application> applications = new ArrayList<>();

    // Getters and Setters
    
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public List<Application> getApplications() {
        return applications;
    }
    
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullname; }
    public void setFullName(String fullName) { this.fullname = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}