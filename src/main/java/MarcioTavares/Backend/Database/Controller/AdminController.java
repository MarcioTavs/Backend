package MarcioTavares.Backend.Database.Controller;


import MarcioTavares.Backend.Database.DTO.*;
import MarcioTavares.Backend.Database.Model.Admin;
import MarcioTavares.Backend.Database.Model.Department;
import MarcioTavares.Backend.Database.Service.AdminService;
import MarcioTavares.Backend.Database.Service.DepartmentService;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Service.EmployeeService;
import MarcioTavares.Backend.Database.Service.TimingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import MarcioTavares.Backend.Database.DTO.DepartmentDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor

public class AdminController {
    private final AdminService adminService;
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final TimingService timingService;




    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<AdminDetailsDTO> getAdminProfile() {
        try {
            AdminDetailsDTO profile = adminService.getAdminProfile();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            System.out.printf("Error fetching admin profile: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-department")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        try {
            Department department = new Department();
            department.setDepartmentId(departmentDTO.getDepartmentId());
            department.setName(departmentDTO.getDepartmentName());
            departmentService.createDepartment(department);
            return ResponseEntity.ok(department);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/update-department")
    public ResponseEntity<?> updateDepartment(@RequestBody DepartmentDTO departmentDTO, @RequestParam String departId) {
        try{
            Department updatedDepartment =departmentService.updateDepartment(departmentDTO , departId);
            return ResponseEntity.ok(updatedDepartment);

        }catch (Exception e){
            System.out.printf("Error: %s\n",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }


    @GetMapping("/get-all-departments")
    public ResponseEntity<?> getAllDepartments() {
        try{
            List<Department> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(departments);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add-employee-department")
    public ResponseEntity<?> addEmployeeInDepartment(@RequestBody Employee emp, @RequestParam String departmentId) {
        try{
            Employee employee = adminService.addEmployeeToDepartment(emp, departmentId);
            EmployeeDTO employeeDTO = convertEmployeeToEmployeeDTO(employee, departmentId);

            return ResponseEntity.ok(employeeDTO);

        }catch (Exception e){
            System.out.printf("Error: %s\n",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }


    }
    public EmployeeDTO convertEmployeeToEmployeeDTO(Employee emp,String departmentId) {

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId(emp.getEmployeeId());
        employeeDTO.setFirstName(emp.getFirstName());
        employeeDTO.setLastName(emp.getLastName());
        employeeDTO.setEmail(emp.getEmail());
        employeeDTO.setPhoneNumber(emp.getPhoneNumber());


        employeeDTO.setProject(emp.getProject());


        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setDepartmentName(emp.getDepartmentName());
        departmentDTO.setDepartmentId(departmentId);

        employeeDTO.setDepartmentDTO(departmentDTO);

        return employeeDTO;

    }



    @PostMapping("/add-admin")
    public ResponseEntity<?> addAdmin(@RequestBody Admin admin) {
        try {
            adminService.createAdmin(admin);
            return ResponseEntity.ok().body("Admin created");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-details")
    public ResponseEntity<?> updateAdminDetails(@RequestBody AdminDetailsDTO adminDetailsDTO) {
        try {
            AdminDetailsDTO updatedAdminDTO = adminService.updateAdminDetails(adminDetailsDTO);
            return ResponseEntity.ok(updatedAdminDTO);
        } catch (Exception e) {
            System.out.printf("Error updating admin details: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // New endpoint for updating admin password
    @PreAuthorize("hasRole('ADMIN')")  // Ensure only admins can access
    @PutMapping("/update-password")
    public ResponseEntity<?> updateAdminPassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        try {
            adminService.updateAdminPassword(passwordUpdateDTO);
            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            System.out.printf("Error updating password: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/get-all-employees")
    public ResponseEntity<?> getAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(employees);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-loggedin-employees-today")
    public ResponseEntity<List<EmployeeAttendanceDTO>> getLoggedInEmployeesToday() {
        try {
            List<EmployeeAttendanceDTO> list = adminService.getTodaysEmployeeAttendance();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-employees-weekly-timesheet")
    public ResponseEntity<?> getAllEmployeesWeeklyTimesheet(@RequestParam String weekStart) {
        try {
            LocalDate startDate = LocalDate.parse(weekStart);
            List<EmployeeWeeklyTimesheetDTO> timesheets = timingService.getAllEmployeesWeeklyReport(startDate);
            return ResponseEntity.ok(timesheets);
        } catch (Exception e) {
            System.out.printf("Error fetching weekly timesheets: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-employees-status")
    public ResponseEntity<List<EmployeeStatusDTO>> getAllEmployeesStatus() {
        try {
            List<EmployeeStatusDTO> statuses = adminService.getAllEmployeesStatus();
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            System.out.printf("Error fetching employee statuses: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }





}
