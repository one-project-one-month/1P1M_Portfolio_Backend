package com._p1m.portfolio.common.util;

import com._p1m.portfolio.common.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ServerUtil {

    @Value("${spring.mail.username}")
    private String fromMail;

    private final JavaMailSender javaMailSender;
    private final EmailService emailService;

    public String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String generateNumericCode(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }

        return code.toString();
    }

    public void sendOtpCode(String email , String otpCode ) throws IOException, MessagingException {
        String userName = email.split("@")[0];
        String htmlTemplate = loadTemplate("templates/mailTemplates/sendOtpMail.html");
        String htmlContent =htmlTemplate
                .replace("{{username}}" , userName)
                .replace("{{code}}" , otpCode);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message , true , "UTF-8");

        helper.setTo(email);
        helper.setFrom(fromMail);
        helper.setSubject("Your One Project One Month Confirmation Code");

        helper.setText(htmlContent , true);
        helper.addInline("logoImage", new ClassPathResource("templates/logo/logo.png"));

        this.emailService.sendEmail(email, "Your One Project One Month Confirmation Code", htmlContent);

    }
}
