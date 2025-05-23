package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.model.Student;
import com.example.model.Teacher;
import com.example.model.Application;
import com.example.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import com.example.model.*;
import javax.servlet.http.HttpSession;

import java.util.Collections;

@Controller
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private InterviewRepository interviewRepository;  // Add this repository

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @GetMapping("/dashboard")
    public String studentDashboard(Model model, HttpSession session) {
    	
    	if (!validateSession(session, "STUDENT")) {
            return "redirect:/auth/login";
        }

        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");
        
        if (username == null || !"STUDENT".equals(userRole)) {
            return "redirect:/auth/login";
        }
        
        Student student = studentRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Student not found"));
            
        List<Application> applications = applicationRepository.findByUser(student);
        model.addAttribute("student", student);
        model.addAttribute("studentApplications", applications);
        
        return "StudentDashboard";
    }

    @GetMapping("/talent-form")
    public String talentForm(Model model, HttpSession session) {
        if (!validateSession(session, "STUDENT")) {
            return "redirect:/auth/login";
        }

        String username = (String) session.getAttribute("username");
        Student student = studentRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        // Get teachers from the same school as the student
        List<Teacher> teachers = teacherRepository.findBySchoolName(student.getSchoolName());

        model.addAttribute("teachers", teachers);
        model.addAttribute("talentApplication", new Application());

        return "ApplicationForm";
    }


    @PostMapping("/submit-talent")
    public String submitTalentApplication(
            @ModelAttribute("application") Application application,
            @RequestParam("teacherId") Long teacherId,
            HttpSession session) {

        validateSession(session, "STUDENT");

        String username = (String) session.getAttribute("username");
        Student student = studentRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (!student.getSchoolName().equals(teacher.getSchoolName())) {
            throw new RuntimeException("You can only select teachers from your school.");
        }

        // Set student name in the application
        application.setName(student.getFullName());
        application.setStudent(student);
        application.setUser(student);
        application.setTeacher(teacher);
        application.setSubmitDate(LocalDateTime.now());
        application.setApplicationType("Talent");
        application.setStatus("PENDING");

        applicationRepository.save(application);

        return "redirect:/student/dashboard";
    }


    @GetMapping("/info")
    public String viewStudentInfo(HttpSession session, Model model) {
    	 validateSession(session, "STUDENT");
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }
        
        // Fetch the student from the database
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No student found with username: " + username));
        model.addAttribute("student", student);

        return "studentInfo";
    }

    @PostMapping("/update-info")
    public String updateStudentInfo(
            @ModelAttribute Student updatedStudent,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        validateSession(session, "STUDENT");
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/auth/login";
        }

        // Fetch the student record from the database
        Student existingStudent = studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No student found with username: " + username));

        // Validate the entered password
        if (!passwordEncoder.matches(password, existingStudent.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Invalid password. Please try again.");
            return "redirect:/student/info";
        }

        // Update only the allowed fields (e.g., full name and email)
        existingStudent.setFullName(updatedStudent.getFullName());
        existingStudent.setEmail(updatedStudent.getEmail());

        // Save the updated student
        studentRepository.save(existingStudent);

        redirectAttributes.addFlashAttribute("success", "Information updated successfully.");
        return "redirect:/student/info";
    }

    
    
    @GetMapping("/interviews")
    public String viewInterviews(
            Model model,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime date,
            HttpSession session) {

        validateSession(session, "STUDENT");

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }

        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Interview> interviews;

        // Fetch interviews scheduled after now
        if (date == null) {
            interviews = interviewRepository.findByStudentIdAndUpcoming(student.getId());
        } else {
            interviews = interviewRepository.findByStudentIdAndDateCloser(student.getId(), date);
        }

        model.addAttribute("interviews", interviews);
        model.addAttribute("selectedDate", date);
        model.addAttribute("student", student);

        return "interview";
    }


    
    private boolean validateSession(HttpSession session, String expectedRole) {
        String username = (String) session.getAttribute("username");
        String userRole = (String) session.getAttribute("userRole");

        if (username == null || !expectedRole.equals(userRole)) {
            return false;
        }
        return true;
    }


   

}
