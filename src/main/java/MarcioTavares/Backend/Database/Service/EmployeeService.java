package MarcioTavares.Backend.Database.Service;


import MarcioTavares.Backend.Database.DTO.EmployeeUpdateRequest;

import MarcioTavares.Backend.Database.Model.Employee;

import MarcioTavares.Backend.Database.Repository.EmployeeRepository;
import MarcioTavares.Backend.Security.DTO.AuthRequest;
import MarcioTavares.Backend.Security.DTO.AuthResponse;
import MarcioTavares.Backend.Security.Model.User;
import MarcioTavares.Backend.Security.Repository.UserRepository;
import MarcioTavares.Backend.Security.Utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@AllArgsConstructor
public class EmployeeService {


    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;


    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }


    @Transactional
    public Employee updateEmployeeData(EmployeeUpdateRequest employeeUpdate, String email) {
        Employee emp = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employeeUpdate.getFirstName() != null) {
            emp.setFirstName(employeeUpdate.getFirstName());
        }
        if (employeeUpdate.getLastName() != null) {
            emp.setLastName(employeeUpdate.getLastName());
        }
        if (employeeUpdate.getPhoneNumber() != null) {
            emp.setPhoneNumber(employeeUpdate.getPhoneNumber());
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employeeUpdate.getFirstName() != null || employeeUpdate.getLastName() != null) {
            String newUsername = (employeeUpdate.getFirstName() != null ? employeeUpdate.getFirstName() : emp.getFirstName()) +
                    " " + (employeeUpdate.getLastName() != null ? employeeUpdate.getLastName() : emp.getLastName());
            user.setUsername(newUsername);
        }
        if (employeeUpdate.getPassword() != null &&
                employeeUpdate.getConfirmPassword() != null &&
                employeeUpdate.getPassword().equals(employeeUpdate.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(employeeUpdate.getPassword()));
        } else if (employeeUpdate.getPassword() != null || employeeUpdate.getConfirmPassword() != null) {
            throw new RuntimeException("Password and confirm password must match");
        }

        String token = jwtUtil.generateToken(createUserDetails(user));
        employeeRepository.save(emp);
        userRepository.save(user);

        return emp;
    }

    public void activateAccount(String employeeEmail,String apiKey) {
        User employee = userRepository.findByEmail(employeeEmail).orElseThrow(() -> new RuntimeException("Employee not found"));

        if(!Objects.equals(employee.getApikey(), apiKey)) {
            throw new IllegalArgumentException("Invalid API key provided");

        }

        employee.setActive(true);
        userRepository.save(employee);
    }


    public Employee getCurrentAuthenticatedEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User must be logged in");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getEmployee();
        }

        throw new RuntimeException("User must be logged in");
    }


    public AuthResponse loginEmployee(AuthRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getUsername(), user.getRole().name(), "Login successful");

    }

    private UserDetails createUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }


}