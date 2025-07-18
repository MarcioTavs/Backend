package MarcioTavares.Backend.Database.Service;


import MarcioTavares.Backend.Database.DTO.EmployeeUpdateRequest;

import MarcioTavares.Backend.Database.Model.Employee;

import MarcioTavares.Backend.Database.Repository.EmployeeRepository;
import MarcioTavares.Backend.Security.Model.User;
import MarcioTavares.Backend.Security.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@AllArgsConstructor
public class EmployeeService {


    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;



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
            user.setPassword(employeeUpdate.getPassword());
        } else if (employeeUpdate.getPassword() != null || employeeUpdate.getConfirmPassword() != null) {
            throw new RuntimeException("Password and confirm password must match");
        }
        employeeRepository.save(emp);
        userRepository.save(user);
        
        return emp;
    }

    public void activateAccount(String employeeEmail,String apiKey) {
        User employee = userRepository.findByEmail(employeeEmail).orElseThrow(() -> new RuntimeException("Employee not found"));

        if(!Objects.equals(employee.getApikey(), apiKey)) {
            throw new IllegalArgumentException("Invalid API key provided");

        }
    }



}
