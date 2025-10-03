package com._p1m.portfolio.common.service.impl;

import com._p1m.portfolio.common.event.EmailEvent;
import com._p1m.portfolio.common.service.EmailService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final ApplicationEventPublisher eventPublisher;

    public EmailServiceImpl(final ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void sendEmail(final String toEmail, final String subject, final String body) {
        this.eventPublisher.publishEvent(new EmailEvent(this, toEmail, subject, body));
    }
}
