package MarcioTavares.Backend.Database.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDetailsDTO {
    private String organizationName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}