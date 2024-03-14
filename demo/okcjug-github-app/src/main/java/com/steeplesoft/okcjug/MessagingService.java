package com.steeplesoft.okcjug;

import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

@ApplicationScoped
public class MessagingService {
    @Inject
    Mailer mailer;

    public void sendSms(List<String> numbers, String message) {

    }

    public void sendEmail(String to, String subject, String text) {
        mailer.send(Mail.withText(to, subject, text));
    }
}
