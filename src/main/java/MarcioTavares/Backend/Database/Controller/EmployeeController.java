package MarcioTavares.Backend.Database.Controller;
import MarcioTavares.Backend.Database.DTO.*;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Service.EmployeeService;
import MarcioTavares.Backend.Database.Service.TimingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor

public class EmployeeController {

    private final EmployeeService employeeService;
    private final TimingService timingService;



    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/profile")
    public ResponseEntity<EmployeeDTO> getEmployeeProfile() {
        try {
            EmployeeDTO profile = employeeService.getEmployeeProfile();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            System.out.printf("Error fetching employee profile: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @PostMapping("/activate-account")
    public ResponseEntity<?> activateAccount(@RequestBody ActivateAccountRequest request) {
        try{
            employeeService.activateAccount(request.getEmail(), request.getApiKey());
            return ResponseEntity.ok().body("Account activated");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    @PostMapping("/Profile/confirm")
    public ResponseEntity<?> confirmEmployee(@RequestBody EmployeeUpdateRequest employeeUpdateRequest, @RequestParam String employeeEmail) {
        try{
            Employee emp = employeeService.updateEmployeeData(employeeUpdateRequest, employeeEmail);
            return ResponseEntity.ok(emp);
        }catch (Exception e){
            System.out.printf("Error: %s\n",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping("/update-details")
    public ResponseEntity<?> updateEmployeeDetails(@RequestBody EmpDetailsDTO empDetailsDTO) {
        try {
            Employee currentEmployee = employeeService.getCurrentAuthenticatedEmployee();
            Employee updatedEmployee = employeeService.updateEmployeeDetails(empDetailsDTO, currentEmployee.getEmail());
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            System.out.printf("Error updating employee details: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping("/update-password")
    public ResponseEntity<?> updateEmployeePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        try {
            Employee currentEmployee = employeeService.getCurrentAuthenticatedEmployee();
            employeeService.updateEmployeePassword(passwordUpdateDTO, currentEmployee.getEmail());
            return ResponseEntity.ok().body("Password updated successfully");
        } catch (Exception e) {
            System.out.printf("Error updating password: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/weekly-report")
    public ResponseEntity<?> getEmployeeWeeklyReport(@RequestParam String startDate) {
        try {
            LocalDate weekStart = LocalDate.parse(startDate);
            Employee currentEmployee = employeeService.getCurrentAuthenticatedEmployee();
            WeeklyTimesheetDTO report = timingService.getWeeklyReport(currentEmployee, weekStart);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            System.out.printf("Error fetching employee weekly report: %s\n", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }





}

