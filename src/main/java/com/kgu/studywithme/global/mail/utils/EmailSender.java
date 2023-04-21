package com.kgu.studywithme.global.mail.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.kgu.studywithme.global.mail.utils.EmailMetadata.EMAIL_SUBJECT;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String serviceEmail;
    private static final String APPROVE_TEMPLATE = "ParticipationApproveEmailTemplate";
    private static final String REJECT_TEMPLATE = "ParticipationRejectEmailTemplate";

    public void sendParticipationApproveMail(String applierEmail, String nickname, String studyName) throws Exception {
        Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("studyName", studyName);

        String mailBody = templateEngine.process(APPROVE_TEMPLATE, context);
        sendMail(applierEmail, mailBody);
    }

    public void sendParticipationRejectMail(String applierEmail, String nickname, String studyName, String reason) throws Exception {
        Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("studyName", studyName);
        context.setVariable("reason", reason);

        String mailBody = templateEngine.process(REJECT_TEMPLATE, context);
        sendMail(applierEmail, mailBody);
    }

    private void sendMail(String applierEmail, String mailBody) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, applierEmail);
        message.setSubject(EMAIL_SUBJECT);
        message.setText(mailBody, "UTF-8", "HTML");
        message.setFrom(new InternetAddress(serviceEmail, "여기서 구해볼래?"));

        mailSender.send(message);
    }
}
