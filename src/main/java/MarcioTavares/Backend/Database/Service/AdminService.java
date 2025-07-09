package MarcioTavares.Backend.Database.Service;


import MarcioTavares.Backend.Database.DTO.AdminUpdateRequest;
import MarcioTavares.Backend.Database.Model.Admin;
import MarcioTavares.Backend.Database.Model.Department;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Repository.AdminRepository;

import MarcioTavares.Backend.Database.Repository.DepartmentRepository;
import MarcioTavares.Backend.Database.Repository.EmployeeRepository;

import MarcioTavares.Backend.Security.Model.Role;
import MarcioTavares.Backend.Security.Model.User;
import MarcioTavares.Backend.Security.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
@AllArgsConstructor
public class AdminService {
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;




    public void createAdmin(Admin admin){
        adminRepository.save(admin);
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
        employeeUser.setApikey(UUID.randomUUID().toString());
        employeeUser.setRole(Role.EMPLOYEE);
        employeeUser.setUsername(employee.getFirstName() + " " + employee.getLastName());
        employeeUser.setActive(true);

        employeeRepository.save(emp);
        userRepository.save(employeeUser);
        emailService.sendActivationMail(employee.getEmail(),employeeUser.getApikey());

        return emp;

    }

    @Transactional
    public Admin updateAdminData(AdminUpdateRequest adminUpdate, String adminEmail) {

        Admin admin = adminRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        

        if (adminUpdate.getOrganizationName() != null) {
            admin.setOrganizationName(adminUpdate.getOrganizationName());
        }
        if (adminUpdate.getFirstName() != null) {
            admin.setFirstName(adminUpdate.getFirstName());
        }
        if (adminUpdate.getLastName() != null) {
            admin.setLastName(adminUpdate.getLastName());
        }
        if (adminUpdate.getPhoneNumber() != null) {
            admin.setPhoneNumber(adminUpdate.getPhoneNumber());
        }
        

        User user = userRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (adminUpdate.getEmail() != null && !adminUpdate.getEmail().equals(adminEmail)) {
            if (userRepository.existsByEmail(adminUpdate.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            admin.setEmail(adminUpdate.getEmail());
            user.setEmail(adminUpdate.getEmail());
        }

        if (adminUpdate.getFirstName() != null || adminUpdate.getLastName() != null) {
            String newUsername = (adminUpdate.getFirstName() != null ? adminUpdate.getFirstName() : admin.getFirstName()) + 
                               " " + (adminUpdate.getLastName() != null ? adminUpdate.getLastName() : admin.getLastName());
            user.setUsername(newUsername);
        }
        

        if (adminUpdate.getPassword() != null && 
            adminUpdate.getConfirmPassword() != null && 
            adminUpdate.getPassword().equals(adminUpdate.getConfirmPassword())) {
            user.setPassword(adminUpdate.getPassword());
        } else if (adminUpdate.getPassword() != null || adminUpdate.getConfirmPassword() != null) {
            throw new RuntimeException("Password and confirm password must match");
        }

        adminRepository.save(admin);
        userRepository.save(user);
        
        return admin;
    }



    }




