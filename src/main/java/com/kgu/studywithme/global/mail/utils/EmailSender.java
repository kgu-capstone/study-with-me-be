package com.kgu.studywithme.global.mail.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.kgu.studywithme.global.mail.utils.EmailMetadata.*;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String serviceEmail;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    public void sendParticipationApproveMail(String applierEmail, String nickname, String studyName) throws Exception {
        Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("studyName", studyName);

        String mailBody = templateEngine.process(APPROVE_TEMPLATE, context);
        sendMail(
                PARTICIPATION_SUBJECT,
                applierEmail,
                mailBody,
                new ClassPathResource("static/images/top.png")
        );
    }

    public void sendParticipationRejectMail(String applierEmail, String nickname, String studyName, String reason) throws Exception {
        Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("studyName", studyName);
        context.setVariable("reason", reason);

        String mailBody = templateEngine.process(REJECT_TEMPLATE, context);
        sendMail(
                PARTICIPATION_SUBJECT,
                applierEmail,
                mailBody,
                new ClassPathResource("static/images/top.png")
        );
    }

    public void sendStudyCertificateMail(String participantEmail, String nickname, String studyName) throws Exception {
        Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("studyName", studyName);
        context.setVariable("completionDate", LocalDate.now().format(DATE_TIME_FORMATTER));

        String mailBody = templateEngine.process(CERTIFICATE_TEMPLATE, context);
        sendMail(
                COMPLETION_SUBJECT,
                participantEmail,
                mailBody,
                new ClassPathResource("static/images/stamp.png")
        );
    }

    private void sendMail(String subjectType, String email, String mailBody, ClassPathResource imageResource) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setSubject(subjectType);
        helper.setTo(email);
        helper.setFrom(new InternetAddress(serviceEmail, "여기서 구해볼래?"));
        helper.setText(mailBody, true);
        helper.addInline("image", imageResource);

        mailSender.send(message);
    }
}
