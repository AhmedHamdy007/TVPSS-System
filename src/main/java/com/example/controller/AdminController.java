package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.model.*;
import com.example.repository.*;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @Autowired
    private SchoolVersionRepository schoolVersionRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) String filterLevel) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");

        if (username == null || !"ADMIN".equals(userRole)) {
            return "redirect:/auth/login";
        }

        // Fetch all equipment and versions
        List<Equipment> allEquipmentRequests = equipmentRepository.findAll().stream()
            .peek(e -> {
                if (e.getSchoolVersion() != null && e.getSchoolVersion().getTeacher() != null) {
                    e.setTeacherName(e.getSchoolVersion().getTeacher().getFullName());
                    e.setSchoolName(e.getSchoolVersion().getSchoolName());
                }
            })
            .collect(Collectors.toList());

        // Fetch only versions with 'PENDING' status for admin review
        List<SchoolVersion> filteredVersionRequests = schoolVersionRepository.findAll().stream()
            .filter(v -> "PENDING".equals(v.getStatus())) // Ensure only pending versions are shown
            .filter(v -> search == null || search.isEmpty() ||
                         v.getTeacher().getFullName().toLowerCase().contains(search.toLowerCase()) ||
                         v.getSchoolName().toLowerCase().contains(search.toLowerCase()))
            .collect(Collectors.toList());

        model.addAttribute("filteredEquipmentRequests", allEquipmentRequests);
        model.addAttribute("filteredVersionRequests", filteredVersionRequests);
        model.addAttribute("equipmentLevels", EquipmentLevel.values());
        model.addAttribute("search", search);
        model.addAttribute("filterLevel", filterLevel);

        return "AdminDashboard";
    }


    
    
    @GetMapping("/view-users")
    public String viewUsers(Model model, HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/auth/login";
        }

        // Fetch all students
        List<Student> students = studentRepository.findAll();

        // Fetch all teachers
        List<Teacher> teachers = teacherRepository.findAll();

        // Add data to the model
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);

        return "ViewUsers"; // Name of the HTML page to be created
    }


    @PostMapping("/approve-equipment/{id}")
    public String approveEquipment(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/auth/login";
        }

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid equipment ID: " + id));

        // Set equipment status to APPROVED
        equipment.setStatus("APPROVED");
        equipmentRepository.save(equipment);

        redirectAttributes.addFlashAttribute("message", "Equipment approved successfully!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/reject-equipment/{id}")
    public String rejectEquipment(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/auth/login";
        }

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid equipment ID: " + id));

        // Set equipment status to REJECTED
        equipment.setStatus("REJECTED");
        equipmentRepository.save(equipment);

        redirectAttributes.addFlashAttribute("message", "Equipment rejected successfully!");
        return "redirect:/admin/dashboard";
    }
    
    
    @PostMapping("/approve-version/{id}")
    public String approveVersion(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/auth/login";
        }

        SchoolVersion version = schoolVersionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid version ID: " + id));

        // Set version status to APPROVED
        version.setStatus("APPROVED");
        version.setActive(true);
        version.setAdminRemarks(remarks != null ? remarks : "Approved by Admin");
        schoolVersionRepository.save(version);

        redirectAttributes.addFlashAttribute("message", "Version approved successfully!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/reject-version/{id}")
    public String rejectVersion(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/auth/login";
        }

        SchoolVersion version = schoolVersionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid version ID: " + id));

        // Set version status to REJECTED
        version.setStatus("REJECTED");
        version.setActive(false);
        version.setAdminRemarks(remarks != null ? remarks : "Rejected by Admin");
        schoolVersionRepository.save(version);

        redirectAttributes.addFlashAttribute("message", "Version rejected successfully!");
        return "redirect:/admin/dashboard";
    }

    
    @GetMapping("/reports")
    public String viewReports(Model model, HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            return "redirect:/auth/login";
        }

        // Equipment statistics
        Map<EquipmentLevel, List<Equipment>> equipmentByLevel = equipmentRepository.findAll().stream()
            .collect(Collectors.groupingBy(Equipment::getLevel));
            
        // School version statistics
        Map<SchoolVersionLevel, List<SchoolVersion>> versionsByLevel = schoolVersionRepository.findAll().stream()
            .collect(Collectors.groupingBy(SchoolVersion::getVersionLevel));

        model.addAttribute("equipmentByLevel", equipmentByLevel);
        model.addAttribute("versionsByLevel", versionsByLevel);
        model.addAttribute("equipmentLevelDescriptions", getEquipmentLevelDescriptions());
        model.addAttribute("versionLevelDescriptions", getVersionLevelDescriptions());
        
        return "reports";
    }

    // Helper methods for level descriptions
    @ModelAttribute("equipmentLevelDescriptions")
    public Map<String, String> getEquipmentLevelDescriptions() {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("BEGINNER", "Basic setup: TV Program Room, Smartphone, External Mic, Monopod, Ring Light");
        descriptions.put("INTERMEDIATE", "Enhanced setup: Added Editing Room, Webcam, Tripod, Free Software");
        descriptions.put("ADVANCED", "Professional setup: Full Camera Equipment, Pro Software, Permanent Green Screen");
        return descriptions;
    }

    @ModelAttribute("versionLevelDescriptions")
    public Map<String, String> getVersionLevelDescriptions() {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("VERSION_1", "Basic: Brand Name, Logo, TV Studio");
        descriptions.put("VERSION_2", "Standard: Added In-school Recording and YouTube Uploads");
        descriptions.put("VERSION_3", "Advanced: Added External Recording and Agency Collaboration");
        descriptions.put("VERSION_4", "Professional: Full Feature Set with Green Screen Technology");
        return descriptions;
    }
}