package com.example.controller;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @Autowired
    private SchoolVersionRepository schoolVersionRepository;
    
    
    @Autowired
    private InterviewRepository interviewRepository;
    
    @GetMapping("/dashboard")
    public String teacherDashboard(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");

        if (username == null || !"TEACHER".equals(userRole)) {
            return "redirect:/auth/login";
        }

        Teacher teacher = teacherRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Fetch only applications assigned to this teacher
        List<Application> talentApplications = applicationRepository.findByTeacherId(teacher.getId());

        Equipment latestEquipment = equipmentRepository.findByTeacherName(teacher.getFullName())
            .stream()
            .max(Comparator.comparing(Equipment::getLastUpdated, Comparator.nullsLast(Comparator.naturalOrder())))
            .orElse(null);

        SchoolVersion latestVersion = schoolVersionRepository
            .findFirstBySchoolNameOrderByCreatedDateDesc(teacher.getSchoolName());

        model.addAttribute("teacherName", teacher.getFullName());
        model.addAttribute("talentApplications", talentApplications);
        model.addAttribute("equipment", latestEquipment);
        model.addAttribute("schoolVersion", latestVersion);
        model.addAttribute("activePage", "dashboard");

        return "TeacherDashboard";
    }


    
    @PostMapping("/review-application")
    public String reviewApplication(
            @RequestParam Long id,
            @RequestParam String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }

        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(status);
        application.setReviewDate(LocalDateTime.now());
        application.setReviewedBy(teacher.getFullName());
        applicationRepository.save(application);

        redirectAttributes.addFlashAttribute("message",
                "Application " + (status.equals("APPROVED") ? "approved" : "rejected") + " successfully");

        return "redirect:/teacher/dashboard";
    }


    // Keep your existing equipment and school version related methods
    @GetMapping("/equipment")
    public String equipmentForm(Model model, HttpSession session) {
    	validateSession(session, "TEACHER");
        model.addAttribute("equipmentOptions", getEquipmentOptions());
        model.addAttribute("activePage", "equipment");
        return "equipmentForm";
    }
    
    @PostMapping("/submit-equipment")
    public String submitEquipment(
            @RequestParam List<String> selectedEquipment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        validateSession(session, "TEACHER");

        String username = (String) session.getAttribute("username");
        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Fetch or create the SchoolVersion for the teacher's school
        SchoolVersion schoolVersion = schoolVersionRepository.findBySchoolName(teacher.getSchoolName())
                .stream()
                .filter(SchoolVersion::isActive)
                .findFirst()
                .orElseGet(() -> {
                    SchoolVersion newVersion = new SchoolVersion();
                    newVersion.setSchoolName(teacher.getSchoolName());
                    newVersion.setTeacherName(teacher.getFullName());
                    newVersion.setVersionLevel(SchoolVersionLevel.VERSION_1); // Default version level
                    newVersion.setStatus("APPROVED");
                    newVersion.setActive(true);
                    newVersion.setCreatedBy(teacher.getUsername());
                    newVersion.setCreatedDate(LocalDateTime.now());
                    schoolVersionRepository.save(newVersion);
                    return newVersion;
                });

        // Create or update the equipment associated with this teacher and SchoolVersion
        Equipment equipment = equipmentRepository.findByTeacherNameAndSchoolVersionId(
                teacher.getFullName(), schoolVersion.getId())
                .orElse(new Equipment());

        // Set or update equipment properties
        if (equipment.getId() == null) {
            // New equipment entry
            equipment.setTeacherName(teacher.getFullName());
            equipment.setSchoolName(teacher.getSchoolName());
            equipment.setSchoolVersion(schoolVersion);
            equipment.setStatus("PENDING_APPROVAL");
            equipment.setActive(true);
            equipment.setUpdatedBy(teacher.getUsername());
        }

        // Update selected items and other fields
        equipment.setSelectedItems(new HashSet<>(selectedEquipment));
        equipment.setLevel(determineEquipmentLevel(selectedEquipment));
        equipment.setLastUpdated(LocalDateTime.now());

        // Save the equipment
        equipmentRepository.save(equipment);

        redirectAttributes.addFlashAttribute("message",
                "Equipment submitted successfully. Your level is: " + equipment.getLevel().getDisplayName());

        return "redirect:/teacher/dashboard";
    }





    
    private void validateSession(HttpSession session, String expectedRole) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");

        if (username == null || !expectedRole.equals(userRole)) {
            throw new SecurityException("Unauthorized access");
        }
    }


    @GetMapping("/school-version")
    public String schoolVersion(Model model) {
        model.addAttribute("featureOptions", getFeatureOptions());
        model.addAttribute("activePage", "version");
        return "schoolVersion";
    }
    
    @PostMapping("/submit-version")
    public String submitVersion(
            @RequestParam List<String> selectedFeatures,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("username");
        Teacher teacher = teacherRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        SchoolVersion version = new SchoolVersion();
        version.setSelectedFeatures(new HashSet<>(selectedFeatures));
        version.setVersionLevel(determineVersionLevel(selectedFeatures));
        version.setTeacherName(teacher.getFullName());
        version.setSchoolName(teacher.getSchoolName());
        version.setCreatedBy(teacher.getUsername());
        version.setCreatedDate(LocalDateTime.now());
        version.setStatus("PENDING"); // Ensure status is PENDING
        version.setActive(false); // New submissions should be inactive

        schoolVersionRepository.save(version);

        redirectAttributes.addFlashAttribute("message", 
            "School version submitted successfully. Your version is: " + 
            version.getVersionLevel().getDisplayName() + " - " + 
            version.getVersionLevel().getDescription());
        
        return "redirect:/teacher/dashboard";
    }

    
    @ModelAttribute("equipmentOptions")
    public List<String> getEquipmentOptions() {
        return Arrays.asList(
            "TV Program Room", "Editing Room", "Smartphone", "Webcam",
            "Tripod", "Camera", "External Mic", "Monopod", "Ring Light",
            "Mobile lighting", "Mobile green screen set", 
            "editing software (free version)", "editing software (pro version)",
            "green screen (permanent)", "Wireless Mic"
        );
    }
    
    @ModelAttribute("featureOptions")
    public List<String> getFeatureOptions() {
        return Arrays.asList(
            "Brand Name", "Logo", "TV Studio", "In-School recording",
            "upload on youtube", "recording inside and outside the school",
            "collaborate with external agencies", "Using green screen technology"
        );
    }
    
    private EquipmentLevel determineEquipmentLevel(List<String> selectedEquipment) {
        Set<String> equipmentSet = new HashSet<>(selectedEquipment);
        
        // Define the required items for each level
        Set<String> beginnerItems = new HashSet<>(Arrays.asList(
            "TV Program Room", "Smartphone", "External Mic", 
            "Monopod", "Ring Light"
        ));
        
        Set<String> intermediateItems = new HashSet<>(Arrays.asList(
            "TV Program Room", "Editing Room", "Webcam", "Tripod",
            "Wireless Mic", "Mobile lighting", "Mobile green screen set",
            "editing software (free version)"
        ));
        
        Set<String> advancedItems = new HashSet<>(Arrays.asList(
            "TV Program Room", "Editing Room", "Camera", "Tripod",
            "Wireless Mic", "Mobile lighting", "green screen (permanent)",
            "editing software (pro version)"
        ));
        
        // Check if the selected equipment contains ALL items for each level
        boolean hasAllBeginnerItems = beginnerItems.stream()
            .allMatch(equipmentSet::contains);
            
        boolean hasAllIntermediateItems = intermediateItems.stream()
            .allMatch(equipmentSet::contains);
            
        boolean hasAllAdvancedItems = advancedItems.stream()
            .allMatch(equipmentSet::contains);
        
        // Determine the level based on which set of items is completely included
        if (hasAllAdvancedItems) {
            return EquipmentLevel.ADVANCED;
        } else if (hasAllIntermediateItems) {
            return EquipmentLevel.INTERMEDIATE;
        } else if (hasAllBeginnerItems) {
            return EquipmentLevel.BEGINNER;
        }
        
        // If none of the complete sets are present, count the items in each category
        long beginnerCount = beginnerItems.stream()
            .filter(equipmentSet::contains)
            .count();
        long intermediateCount = intermediateItems.stream()
            .filter(equipmentSet::contains)
            .count();
        long advancedCount = advancedItems.stream()
            .filter(equipmentSet::contains)
            .count();
        
        // Determine level based on which category has the most matches
        if (advancedCount >= intermediateCount && advancedCount >= beginnerCount) {
            return EquipmentLevel.ADVANCED;
        } else if (intermediateCount >= beginnerCount) {
            return EquipmentLevel.INTERMEDIATE;
        }
        
        return EquipmentLevel.BEGINNER;
    }
    
    private SchoolVersionLevel determineVersionLevel(List<String> selectedFeatures) {
        Set<String> featuresSet = new HashSet<>(selectedFeatures);
        
        Set<String> version1Features = new HashSet<>(Arrays.asList(
            "Brand Name", "Logo", "TV Studio"
        ));
        
        Set<String> version2Features = new HashSet<>(Arrays.asList(
            "Brand Name", "Logo", "TV Studio",
            "In-School recording", "upload on youtube"
        ));
        
        Set<String> version3Features = new HashSet<>(Arrays.asList(
            "Brand Name", "Logo", "TV Studio",
            "In-School recording", "upload on youtube",
            "recording inside and outside the school",
            "collaborate with external agencies"
        ));
        
        Set<String> version4Features = new HashSet<>(Arrays.asList(
            "Brand Name", "Logo", "TV Studio",
            "In-School recording", "upload on youtube",
            "recording inside and outside the school",
            "collaborate with external agencies",
            "Using green screen technology"
        ));
        
        if (featuresSet.containsAll(version4Features)) {
            return SchoolVersionLevel.VERSION_4;
        } else if (featuresSet.containsAll(version3Features)) {
            return SchoolVersionLevel.VERSION_3;
        } else if (featuresSet.containsAll(version2Features)) {
            return SchoolVersionLevel.VERSION_2;
        } else if (featuresSet.containsAll(version1Features)) {
            return SchoolVersionLevel.VERSION_1;
        }
        
        return SchoolVersionLevel.VERSION_1;
    }

    @GetMapping("/interviewStudent")
    public String interviewStudent(
            Model model,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null || !"TEACHER".equals(session.getAttribute("userRole"))) {
            return "redirect:/auth/login";
        }

        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Fetch approved applications with no associated interview
        List<Application> approvedApplications = applicationRepository.findByStatusAndInterviewIsNull("APPROVED");

        // Fetch scheduled interviews for the teacher
        List<Interview> interviews = interviewRepository.findByTeacherId(teacher.getId());

        model.addAttribute("approvedApplications", approvedApplications);
        model.addAttribute("interviews", interviews);
        model.addAttribute("teacherName", teacher.getFullName());

        return "interviewStudent";
    }


    
    @PostMapping("/schedule-interview")
    public String scheduleInterview(
            @RequestParam Long applicationId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime interviewDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }

        Teacher teacher = teacherRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (interviewDate == null) {
            redirectAttributes.addFlashAttribute("error", "Interview date cannot be null.");
            return "redirect:/teacher/interviewStudent";
        }

        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setTeacher(teacher);
        interview.setScheduledDate(interviewDate);
        interview.setStatus(Interview.InterviewStatus.SCHEDULED);
        interview.setCreatedAt(LocalDateTime.now());

        interviewRepository.save(interview);

        redirectAttributes.addFlashAttribute("message",
                "Interview scheduled successfully for " + application.getName());

        return "redirect:/teacher/interviewStudent";
    }

}