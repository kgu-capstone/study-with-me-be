package com.kgu.studywithme.member.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import com.kgu.studywithme.member.domain.review.PeerReview;
import com.kgu.studywithme.member.domain.review.PeerReviewRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberValidator memberValidator;
    private final MemberFindService memberFindService;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final PeerReviewRepository peerReviewRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public Long signUp(Member member) {
        validateUniqueFields(member);
        return memberRepository.save(member).getId();
    }

    @Transactional
    public Long report(Long reporteeId, Long reporterId, String reason) {
        validatePreviousReportIsStillPending(reporteeId, reporterId);

        Report report = Report.createReportWithReason(reporteeId, reporterId, reason);
        return reportRepository.save(report).getId();
    }

    @Transactional
    public void writeReview(Long revieweeId, Long reviewerId, String content) {
        validateSelfReviewNotAllowed(revieweeId, reviewerId);
        validateColleague(revieweeId, reviewerId);

        Member reviewee = memberFindService.findById(revieweeId);
        Member reviewer = memberFindService.findById(reviewerId);

        reviewee.applyPeerReview(reviewer, content);
    }

    @Transactional
    public void updateReview(Long revieweeId, Long reviewerId, String content) {
        PeerReview peerReview = peerReviewRepository.findByRevieweeIdAndReviewerId(revieweeId, reviewerId)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.REVIEW_NOT_FOUND));
        peerReview.updateReview(content);
    }

    private void validateUniqueFields(Member member) {
        memberValidator.validateEmail(member.getEmail());
        memberValidator.validateNickname(member.getNickname());
        memberValidator.validatePhone(member.getPhone());
    }

    private void validatePreviousReportIsStillPending(Long reporteeId, Long reporterId) {
        if (memberRepository.isReportReceived(reporteeId, reporterId)) {
            throw StudyWithMeException.type(MemberErrorCode.REPORT_IS_STILL_RECEIVED);
        }
    }

    private void validateSelfReviewNotAllowed(Long revieweeId, Long reviewerId) {
        if (revieweeId.equals(reviewerId)) {
            throw StudyWithMeException.type(MemberErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }
    }

    private void validateColleague(Long revieweeId, Long reviewerId) {
        Set<Long> studyList1 = attendanceRepository.findStudyIdByParticipantId(revieweeId);
        Set<Long> studyList2 = attendanceRepository.findStudyIdByParticipantId(reviewerId);
        studyList1.retainAll(studyList2);

        for (Long studyId : studyList1) {
            Set<Integer> weekList1 = attendanceRepository.findWeekByStudyIdAndParticipantId(studyId, revieweeId);
            Set<Integer> weekList2 = attendanceRepository.findWeekByStudyIdAndParticipantId(studyId, reviewerId);
            weekList1.retainAll(weekList2);

            if (weekList1.size() >= 1) return;
        }
        throw StudyWithMeException.type(MemberErrorCode.COMMON_STUDY_NOT_FOUND);
    }
}
