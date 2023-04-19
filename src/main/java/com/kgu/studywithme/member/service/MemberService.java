package com.kgu.studywithme.member.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberValidator memberValidator;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public Long signUp(Member member) {
        validateUniqueFields(member);
        return memberRepository.save(member).getId();
    }

    private void validateUniqueFields(Member member) {
        memberValidator.validateEmail(member.getEmail());
        memberValidator.validateNickname(member.getNickname());
        memberValidator.validatePhone(member.getPhone());
    }

    @Transactional
    public Long report(Long reporteeId, Long reporterId, String reason) {
        validatePreviousReportIsStillPending(reporteeId, reporterId);

        Report report = Report.createReportWithReason(reporteeId, reporterId, reason);
        return reportRepository.save(report).getId();
    }

    private void validatePreviousReportIsStillPending(Long reporteeId, Long reporterId) {
        if (memberRepository.isReportReceived(reporteeId, reporterId)) {
            throw StudyWithMeException.type(MemberErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING);
        }
    }
}
