package com.example.security;

import com.example.model.Admin;
import com.example.model.Student;
import com.example.model.Teacher;
import com.example.repository.AdminRepository;
import com.example.repository.StudentRepository;
import com.example.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check Admin
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            return new User(
                admin.getUsername(),
                admin.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // Check Teacher
        Teacher teacher = teacherRepository.findByUsername(username).orElse(null);
        if (teacher != null) {
            return new User(
                teacher.getUsername(),
                teacher.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"))
            );
        }

        // Check Student
        Student student = studentRepository.findByUsername(username).orElse(null);
        if (student != null) {
            return new User(
                student.getUsername(),
                student.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
            );
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}