package MarcioTavares.Backend.Database.Service;


import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private static final String EMAIL_FROM = "marciotavares@gmail.com";
    private JavaMailSender mailSender;

    public void sendActivationMail(String to, String apikey) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(EMAIL_FROM);
        message.setSubject("Please login and activate your account using the email and apikey");
        message.setText("Email: " + to + "\nAPI Key: " + apikey );
        mailSender.send(message);
    }

}
