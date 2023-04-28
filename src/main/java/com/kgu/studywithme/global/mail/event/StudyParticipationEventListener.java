package com.kgu.studywithme.global.mail.event;

import com.kgu.studywithme.global.mail.utils.EmailSender;
import com.kgu.studywithme.study.event.StudyApprovedEvent;
import com.kgu.studywithme.study.event.StudyGraduatedEvent;
import com.kgu.studywithme.study.event.StudyRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StudyParticipationEventListener {
    private final EmailSender emailSender;

    @Async("emailAsyncExecutor")
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendParticipationApproveMail(StudyApprovedEvent event) throws Exception {
        emailSender.sendParticipationApproveMail(event.email(), event.nickname(), event.studyName());
    }

    @Async("emailAsyncExecutor")
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendParticipationRejectMail(StudyRejectedEvent event) throws Exception {
        emailSender.sendParticipationRejectMail(event.email(), event.nickname(), event.studyName(), event.reason());
    }

    @Async("emailAsyncExecutor")
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendStudyCertificateMail(StudyGraduatedEvent event) throws Exception {
        emailSender.sendStudyCertificateMail(event.email(), event.nickname(), event.studyName());
    }
}
