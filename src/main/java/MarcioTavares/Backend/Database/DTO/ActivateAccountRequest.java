package MarcioTavares.Backend.Database.DTO;


import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateAccountRequest {
    private String email;
    private String apiKey;


}
