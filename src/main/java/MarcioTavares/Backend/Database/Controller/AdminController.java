package MarcioTavares.Backend.Database.Controller;

import MarcioTavares.Backend.Database.DTO.AdminUpdateRequest;
import MarcioTavares.Backend.Database.DTO.DepartmentDTO;
import MarcioTavares.Backend.Database.DTO.DepartmentUpdateRequest;
import MarcioTavares.Backend.Database.DTO.EmployeeDTO;
import MarcioTavares.Backend.Database.Model.Admin;
import MarcioTavares.Backend.Database.Model.Department;
import MarcioTavares.Backend.Database.Service.AdminService;
import MarcioTavares.Backend.Database.Service.DepartmentService;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;



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
    public ResponseEntity<?> updateDepartment(@RequestBody DepartmentUpdateRequest departmentUpdateRequest, @RequestParam String departId) {
        try{
            Department updatedDepartment =departmentService.updateDepartment(departmentUpdateRequest , departId);
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


    @PutMapping("/update-admin")
    public ResponseEntity<?> updateAdmin(@RequestBody AdminUpdateRequest adminUpdateRequest , String adminEmail) {
        try{
            Admin updateAdmin = adminService.updateAdminData(adminUpdateRequest, adminEmail);
            return ResponseEntity.ok().body(updateAdmin);

        }catch (Exception e){
            System.out.printf("Error: %s\n",e.getMessage());
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









}
