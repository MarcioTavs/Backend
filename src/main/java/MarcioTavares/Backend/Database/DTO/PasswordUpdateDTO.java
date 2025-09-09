package MarcioTavares.Backend.Database.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateDTO {
    private String newPassword;
    private String confirmPassword;

}
