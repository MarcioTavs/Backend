package MarcioTavares.Backend.Database.Controller;
import MarcioTavares.Backend.Database.DTO.ActivateAccountRequest;
import MarcioTavares.Backend.Database.DTO.EmpDetailsDTO;
import MarcioTavares.Backend.Database.DTO.EmployeeUpdateRequest;
import MarcioTavares.Backend.Database.DTO.PasswordUpdateDTO;
import MarcioTavares.Backend.Database.Model.Employee;
import MarcioTavares.Backend.Database.Service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor

public class EmployeeController {

    private final EmployeeService employeeService;


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





}

