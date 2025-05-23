package com.example.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;


@Entity
@Table(name = "equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_version_id")
    private SchoolVersion schoolVersion;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "equipment_items", joinColumns = @JoinColumn(name = "equipment_id"))
    @Column(name = "item")
    private Set<String> selectedItems = new HashSet<>();


    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_level")
    private EquipmentLevel level;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "teacher_name")
    private String teacherName;


    private String name;
    private String description;
    private String status; // AVAILABLE, IN_USE, MAINTENANCE
    private String current_holder;
    private LocalDateTime last_updated;
    private String updated_by;
    private boolean is_active;
    // Getters and Setters
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCurrentHolder() { return current_holder; }
    public void setCurrentHolder(String currentHolder) { this.current_holder = currentHolder; }
    
    public LocalDateTime getLastUpdated() { return last_updated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.last_updated = lastUpdated; }
    
    public String getUpdatedBy() { return updated_by; }
    public void setUpdatedBy(String updatedBy) { this.updated_by = updatedBy; }
    
    public boolean isActive() { return is_active; }
    public void setActive(boolean active) { this.is_active = active; }
    
    public SchoolVersion getSchoolVersion() { return schoolVersion; }
    public void setSchoolVersion(SchoolVersion schoolVersion) { 
        this.schoolVersion = schoolVersion; 
    }
    
    public Set<String> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(Set<String> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public EquipmentLevel getLevel() {
        return level;
    }

    public void setLevel(EquipmentLevel level) {
        this.level = level;
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
    
    
    
}
