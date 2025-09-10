package MarcioTavares.Backend.Database.Service;



import MarcioTavares.Backend.Database.DTO.EmployeeAttendanceDTO;
import MarcioTavares.Backend.Database.Model.Admin;
import MarcioTavares.Backend.Database.Model.AttendanceSheet;
import MarcioTavares.Backend.Database.Model.Department;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Repository.AdminRepository;

import MarcioTavares.Backend.Database.DTO.EmployeeAttendanceDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import MarcioTavares.Backend.Database.Repository.AttendanceRepository;
import MarcioTavares.Backend.Database.Repository.DepartmentRepository;
import MarcioTavares.Backend.Database.Repository.EmployeeRepository;

import MarcioTavares.Backend.Security.Model.Role;
import MarcioTavares.Backend.Security.Model.User;
import MarcioTavares.Backend.Security.Repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import MarcioTavares.Backend.Database.DTO.AdminDetailsDTO;
import MarcioTavares.Backend.Database.DTO.PasswordUpdateDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;



@Service
@AllArgsConstructor
public class AdminService {
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttendanceRepository attendanceRepository;


    public void createAdmin(Admin admin){
        adminRepository.save(admin);
    }



    @Transactional
    public AdminDetailsDTO getAdminProfile() {
        Admin admin = getCurrentAuthenticatedAdmin();
        return new AdminDetailsDTO(
                admin.getOrganizationName(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getPhoneNumber()
        );
    }



    @Transactional
    public Employee addEmployeeToDepartment(Employee employee, String departmentId) {
        Department optionalDepartment = departmentRepository.findByDepartmentId(departmentId);
        if(optionalDepartment == null) {
            throw new IllegalArgumentException("Department not found");
        }


        Employee emp = new Employee();
        emp.setDepartment(optionalDepartment);
        emp.setFirstName(employee.getFirstName());
        emp.setLastName(employee.getLastName());
        emp.setEmail(employee.getEmail());
        emp.setPhoneNumber(employee.getPhoneNumber());
        emp.setProject(employee.getProject());
        emp.setEmployeeId(employee.getEmployeeId());
        emp.setDepartmentName(optionalDepartment.getName());

        optionalDepartment.getEmployees().add(emp);

        User employeeUser = new User();
        employeeUser.setEmail(emp.getEmail());
        employeeUser.setEmployee(emp);
       
        int randomApiKey = 1000000 + new java.util.Random().nextInt(9000000);
        employeeUser.setApikey(String.valueOf(randomApiKey));
        employeeUser.setRole(Role.EMPLOYEE);
        employeeUser.setUsername(employee.getEmail());
        employeeUser.setActive(false);
        employeeUser.setCreatedAt(LocalDateTime.now());

        employeeRepository.save(emp);
        userRepository.save(employeeUser);
        emailService.sendActivationMail(employee.getEmail(),employeeUser.getApikey());

        return emp;

    }



    @Transactional
    public AdminDetailsDTO updateAdminDetails(AdminDetailsDTO adminDetailsDTO) {
        Admin admin = getCurrentAuthenticatedAdmin();

        // Update fields if provided in the DTO
        if (adminDetailsDTO.getOrganizationName() != null) {
            admin.setOrganizationName(adminDetailsDTO.getOrganizationName());
        }
        if (adminDetailsDTO.getFirstName() != null) {
            admin.setFirstName(adminDetailsDTO.getFirstName());
        }
        if (adminDetailsDTO.getLastName() != null) {
            admin.setLastName(adminDetailsDTO.getLastName());
        }
        if (adminDetailsDTO.getPhoneNumber() != null) {
            admin.setPhoneNumber(adminDetailsDTO.getPhoneNumber());
        }

        // Save the updated admin entity
        adminRepository.save(admin);

        // Return a DTO with the updated values
        return new AdminDetailsDTO(
                admin.getOrganizationName(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getPhoneNumber()
        );
    }

    public Admin getCurrentAuthenticatedAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User must be logged in");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getAdmin() == null) {
                throw new RuntimeException("Authenticated user is not an admin");
            }
            return user.getAdmin();
        }
        throw new RuntimeException("User must be logged in");
    }

    // New method to update admin password
    @Transactional
    public void updateAdminPassword(PasswordUpdateDTO passwordUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User must be logged in");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (passwordUpdateDTO.getNewPassword() != null &&
                    passwordUpdateDTO.getConfirmPassword() != null &&
                    passwordUpdateDTO.getNewPassword().equals(passwordUpdateDTO.getConfirmPassword())) {
                user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
                userRepository.save(user);
            } else {
                throw new RuntimeException("New password and confirm password must match");
            }
        } else {
            throw new RuntimeException("User must be logged in");
        }
    }



    public List<EmployeeAttendanceDTO> getTodaysEmployeeAttendance() {
        LocalDate today = LocalDate.now();
        List<AttendanceSheet> attendanceSheets = attendanceRepository.findByDate(today);  // Assumes this method exists in AttendanceRepository; add it if not (see note below)
        List<EmployeeAttendanceDTO> dtoList = new ArrayList<>();
        for (AttendanceSheet sheet : attendanceSheets) {
            if (sheet.getClockInTime() != null) {  // Only include if they clocked in
                Employee emp = sheet.getEmployee();
                EmployeeAttendanceDTO dto = new EmployeeAttendanceDTO(
                        emp.getEmployeeId(),
                        emp.getFirstName(),
                        emp.getLastName(),
                        emp.getEmail(),
                        sheet.getClockInTime(),
                        sheet.getClockOutTime()
                );
                dtoList.add(dto);
            }
        }
        return dtoList;
    }


    }




