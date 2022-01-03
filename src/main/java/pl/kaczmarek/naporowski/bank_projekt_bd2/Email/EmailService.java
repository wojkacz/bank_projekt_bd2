package pl.kaczmarek.naporowski.bank_projekt_bd2.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Override
    public void send(String to, String email) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email);
            helper.setTo(to);
            helper.setSubject("Bank Application");
            helper.setFrom("bankappbd2@gmail.com");
            mailSender.send(mimeMessage);
        } catch(MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            throw new IllegalStateException("Failed to send email!");
        }
    }
}
