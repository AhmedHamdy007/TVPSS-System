package com.example.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;



@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @OneToMany(mappedBy = "application")
    private Set<Interview> interviews = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private Teacher teacher;
    
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;

    @Column(name = "application_type", length = 50)
    private String applicationType;

    @Column(length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Column(name = "reviewed_by", length = 50)
    private String reviewedBy;
    
    private String details;

    @Column(length = 255)
    private String description;

    private Integer duration;

    @Column(length = 255)
    private String email;

    @Column(name = "equipment_requested", length = 255)
    private String equipmentRequested;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String purpose;

    @Column(name = "submit_date")
    private LocalDateTime submitDate;

    @Column(name = "youtube_link", length = 255)
    private String youtubeLink;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getName() {
        return name;
    }
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getEquipmentRequested() {
        return equipmentRequested;
    }

    public void setEquipmentRequested(String equipmentRequested) {
        this.equipmentRequested = equipmentRequested;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDateTime submitDate) {
        this.submitDate = submitDate;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    @PrePersist
    public void prePersist() {
        if (this.submitDate == null) {
            this.submitDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "PENDING";
        }
        // Generate details field
        this.details = String.format("Application Type: %s\nPurpose: %s\nDescription: %s", 
            this.applicationType,
            this.purpose,
            this.description);
    }
}
