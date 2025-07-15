package MarcioTavares.Backend.Security.DTO;

import lombok.Data;

@Data
public class AdminSignUpRequest {
    private String organizationName;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;
    private String password;
    private String confirmPassword;
}
 