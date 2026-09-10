package zentry.back.api.core.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Código de Verificación - Zentry Community");
        message.setText("Hola,\n\n"
                + "Tu código de verificación de un solo uso (OTP) es: " + otpCode + "\n\n"
                + "Por favor, ingresa este código en la aplicación para continuar. "
                + "Si no solicitaste este código, ignora este mensaje.\n\n"
                + "Saludos,\nEl equipo de Zentry");

        mailSender.send(message);
    }
}
