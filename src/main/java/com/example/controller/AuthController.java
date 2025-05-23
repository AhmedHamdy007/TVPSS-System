package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.model.Admin;
import com.example.model.Student;
import com.example.model.Teacher;
import com.example.model.User;
import com.example.repository.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        // Check admin login
    	Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isPresent()) {
            System.out.println("Admin found: " + admin.get().getUsername());
            boolean matches = passwordEncoder.matches(password, admin.get().getPassword());
            System.out.println("Password matches: " + matches);
            if (matches) {
                session.setAttribute("username", username);
                session.setAttribute("userRole", "ADMIN");
                return "redirect:/admin/dashboard";
            }
        }

        // Check teacher login
        Optional<Teacher> teacher = teacherRepository.findByUsername(username);
        if (teacher.isPresent() && passwordEncoder.matches(password, teacher.get().getPassword())) {
            session.setAttribute("username", username);
            session.setAttribute("userRole", "TEACHER");
            return "redirect:/teacher/dashboard";
        }

        // Check student login
        Optional<Student> student = studentRepository.findByUsername(username);
        if (student.isPresent() && passwordEncoder.matches(password, student.get().getPassword())) {
            session.setAttribute("username", username);
            session.setAttribute("userRole", "STUDENT");
            return "redirect:/student/dashboard";
        }

        System.out.println("Login failed for username: " + username);
        return "redirect:/auth/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("student", new Student());

        // Mock list of schools for now
        model.addAttribute("schools", List.of("School A", "School B", "School C"));

        // Fetch all unique departments from teachers
        List<String> departments = teacherRepository.findAllDepartments();
        model.addAttribute("departments", departments);

        return "register";
    }



    @PostMapping("/register")
    public String registerStudent(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String course,
            @RequestParam String schoolName,
            Model model) {
        // Check if the username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(password);

        // Create and save the user
        Student student = new Student();
        student.setUsername(username);
        student.setPassword(hashedPassword);
        student.setFullName(fullName);
        student.setEmail(email);
        student.setCourse(course);
        student.setSchoolName(schoolName);
        student.setActive(true);
        student.setCreatedAt(LocalDateTime.now());

        // Assign a unique student ID
        student.setStudentId("STU" + System.currentTimeMillis());

        // Assign a teacher with the matching course and school name
        Optional<Teacher> assignedTeacher = teacherRepository.findBySchoolNameAndDepartment(schoolName, course).stream().findFirst();
        if (assignedTeacher.isPresent()) {
            student.setTeacher(assignedTeacher.get());
        } else {
            model.addAttribute("error", "No teachers available for the selected course and school.");
            return "register";
        }

        studentRepository.save(student);
        return "redirect:/auth/login";
    }

}