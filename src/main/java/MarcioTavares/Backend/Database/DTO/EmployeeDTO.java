package MarcioTavares.Backend.Database.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String project;
    private String phoneNumber;
    private String email;
    private DepartmentDTO departmentDTO;

}
