package MarcioTavares.Backend.Database.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateAccountRequest {
    private String email;
    private String apiKey;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
