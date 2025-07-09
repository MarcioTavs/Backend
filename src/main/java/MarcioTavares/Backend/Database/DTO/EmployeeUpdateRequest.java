package MarcioTavares.Backend.Database.DTO;

import lombok.Data;

@Data
public class EmployeeUpdateRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String confirmPassword;

}
