package MarcioTavares.Backend.Database.DTO;

import lombok.Data;

@Data
public class AdminUpdateRequest {
    private String organizationName;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String adminId;
    private String password;
    private String confirmPassword;
}
