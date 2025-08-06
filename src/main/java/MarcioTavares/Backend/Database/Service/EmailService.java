package MarcioTavares.Backend.Database.Service;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService {

    private static final String EMAIL_FROM = "marciotavares@gmail.com";
    private final JavaMailSender mailSender;

    public void sendActivationMail(String to, String apikey) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(EMAIL_FROM);
            helper.setSubject("Activate Your Employee Account");

            String htmlContent = createActivationEmailTemplate(to, apikey);
            helper.setText(htmlContent, true); // true indicates HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send activation email", e);
        }
    }

    private String createActivationEmailTemplate(String email, String apikey) {
        String activationLink = "http://localhost:3000/activationPage?email=" + email;
        return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Account Activation</title>
        <style>
            body {
                font-family: 'Segoe UI', Arial, sans-serif;
                line-height: 1.6;
                color: #232b36;
                max-width: 600px;
                margin: 0 auto;
                padding: 24px;
                background: #f3f6fb;
            }
            .email-container {
                background: #ffffff;
                padding: 36px;
                border-radius: 12px;
                box-shadow: 0 8px 32px rgba(34, 41, 47, 0.06);
            }
            .header {
                text-align: center;
                margin-bottom: 28px;
            }
            .welcome-text {
                font-size: 23px;
                color: #ff8800;
                margin-bottom: 14px;
                font-weight: 600;
            }
            .welcome-paragraph {
                margin-bottom: 24px;
                color: #3a3d44;
                font-size: 16px;
            }
            .credentials-box {
                background: #fff8f0;
                border-left: 4px solid #ff8800;
                padding: 20px 24px;
                margin: 28px 0 18px 0;
                border-radius: 5px;
            }
            .credential-item {
                margin: 9px 0;
                font-size: 15px;
            }
            .credential-label {
                font-weight: bold;
                color: #de7400;
                display: inline-block;
                width: 90px;
            }
            .credential-value {
                color: #232b36;
                font-family: 'Menlo', 'Consolas', 'Courier New', monospace;
                background: #fff2e0;
                padding: 2px 9px;
                border-radius: 3px;
                border: 1px solid #ffd8af;
                font-size: 15px;
                letter-spacing: 0.1px;
            }
            .activation-button {
                display: inline-block;
                background-color: #ff8800;
                color: #fff !important;
                padding: 12px 34px;
                text-decoration: none;
                border-radius: 6px;
                font-weight: 600;
                margin: 26px 0 16px 0;
                font-size: 17px;
                box-shadow: 0 2px 8px rgba(255, 136, 0, 0.15);
                transition: background 0.18s;
            }
            .activation-button:hover {
                background-color: #de7400;
            }
            .instructions {
                background-color: #fff8e1;
                border: 1px solid #ffe082;
                color: #665200;
                padding: 13px 20px;
                border-radius: 5px;
                margin: 18px 0 20px 0;
                font-size: 15px;
            }
            .footer {
                text-align: center;
                margin-top: 36px;
                font-size: 12px;
                color: #8b9098;
            }
        </style>
    </head>
    <body>
        <div class="email-container">
            <div class="header">
                <h1 class="welcome-text">Welcome to Our Company!</h1>
            </div>
            <p class="welcome-paragraph">
                Welcome to our company! We are excited to have you join our team. To activate your account, please use the email address and API key provided below. These credentials will allow you to complete your registration and access our platform. If you need any assistance during this process, our support team is here to help.
            </p>
            <div class="credentials-box">
                <div class="credential-item">
                    <span class="credential-label">Email:</span>
                    <span class="credential-value">%s</span>
                </div>
                <div class="credential-item">
                    <span class="credential-label">API Key:</span>
                    <span class="credential-value">%s</span>
                </div>
            </div>
            <div class="instructions">
                <strong>Instructions:</strong>
                <ol>
                    <li>Click the activation button below or use the API key to activate your account.</li>
                    <li>Once activated, you can log in using your email and password.</li>
                    <li>Keep your API key secure and do not share it with others.</li>
                </ol>
            </div>
            <div style="text-align: center;">
                <a href="%s" class="activation-button">Activate Account</a>
            </div>
            <p>If you have any questions or need assistance, please don't hesitate to contact our support team.</p>
            <div class="footer">
                <p>This is an automated message. Please do not reply to this email.</p>
                <p>&copy; 2025 Your Company Name. All rights reserved.</p>
            </div>
        </div>
    </body>
    </html>
    """.formatted(email, apikey, activationLink);
    }



}
