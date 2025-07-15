package MarcioTavares.Backend.Database.Controller;
import MarcioTavares.Backend.Database.DTO.EmployeeUpdateRequest;
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
    public ResponseEntity<?> activateAccount(@RequestParam String email, @RequestParam String apiKey) {
        try{
            employeeService.activateAccount(email,apiKey);
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
}
