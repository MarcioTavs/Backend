// Add this new DTO in the DTO package, e.g., MarcioTavares.Backend.Database.DTO.EmployeeStatusDTO.java
package MarcioTavares.Backend.Database.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeStatusDTO {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String status; // "active", "paused", or "inactive"
}