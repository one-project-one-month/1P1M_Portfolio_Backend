package com._p1m.portfolio.common.listener;

import com._p1m.portfolio.common.event.EmailEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailEventListener {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailEventListener(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    @EventListener
    public void handleEmailEvent(EmailEvent event) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(event.getToEmail());
        helper.setSubject(event.getSubject());
        helper.setText(event.getBody(), true); // HTML enabled

        javaMailSender.send(message);
        System.out.println("✅ Email sent to: " + event.getToEmail());
    }
}