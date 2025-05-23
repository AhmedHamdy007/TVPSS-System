package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;


@Entity
@Table(name = "school_versions")  
public class SchoolVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "version")
    private String version;

    @Column(name = "description")
    private String description;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @ElementCollection
    @CollectionTable(name = "version_features", 
                    joinColumns = @JoinColumn(name = "version_id"))
    @Column(name = "feature")
    private Set<String> selectedFeatures = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    @Column(name = "version_level")
    private SchoolVersionLevel versionLevel;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "teacher_name")
    private String teacherName;
    
    
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "status")
    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "admin_remarks")
    private String adminRemarks;

    // Add proper relationship mapping for equipment
    @OneToMany(mappedBy = "schoolVersion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Equipment> equipment = new ArrayList<>();  // Initialize the list
    
 // Add helper methods for managing bidirectional relationship
    public void addEquipment(Equipment equipment) {
        this.equipment.add(equipment);
        equipment.setSchoolVersion(this);
    }

    public void removeEquipment(Equipment equipment) {
        this.equipment.remove(equipment);
        equipment.setSchoolVersion(null);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }
    
    public List<Equipment> getEquipment() { return equipment; }
    public void setEquipment(List<Equipment> equipment) { this.equipment = equipment; }
    
    public Set<String> getSelectedFeatures() {
        return selectedFeatures;
    }

    public void setSelectedFeatures(Set<String> selectedFeatures) {
        this.selectedFeatures = selectedFeatures;
    }

    public SchoolVersionLevel getVersionLevel() {
        return versionLevel;
    }

    public void setVersionLevel(SchoolVersionLevel versionLevel) {
        this.versionLevel = versionLevel;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

}