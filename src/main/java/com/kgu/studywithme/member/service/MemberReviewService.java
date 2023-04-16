package com.kgu.studywithme.member.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.review.PeerReview;
import com.kgu.studywithme.member.domain.review.PeerReviewRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.member.infra.query.dto.response.StudyAttendanceMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberReviewService {
    private final MemberFindService memberFindService;
    private final MemberRepository memberRepository;
    private final PeerReviewRepository peerReviewRepository;

    @Transactional
    public void writeReview(Long revieweeId, Long reviewerId, String content) {
        validateSelfReviewNotAllowed(revieweeId, reviewerId);
        validateColleague(revieweeId, reviewerId);

        Member reviewee = memberFindService.findById(revieweeId);
        Member reviewer = memberFindService.findById(reviewerId);

        reviewee.applyPeerReview(reviewer, content);
    }

    private void validateSelfReviewNotAllowed(Long revieweeId, Long reviewerId) {
        if (revieweeId.equals(reviewerId)) {
            throw StudyWithMeException.type(MemberErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }
    }

    private void validateColleague(Long revieweeId, Long reviewerId) {
        List<StudyAttendanceMetadata> revieweeMetadata = memberRepository.findStudyAttendanceMetadataByMemberId(revieweeId);
        List<StudyAttendanceMetadata> reviewerMetadata = memberRepository.findStudyAttendanceMetadataByMemberId(reviewerId);

        boolean hasCommonMetadata = revieweeMetadata.stream()
                .anyMatch(revieweeData ->
                        reviewerMetadata.stream()
                                .anyMatch(reviewerData -> hasCommonMetadata(revieweeData, reviewerData))
                );

        if (!hasCommonMetadata) {
            throw StudyWithMeException.type(MemberErrorCode.COMMON_STUDY_NOT_FOUND);
        }
    }

    private boolean hasCommonMetadata(StudyAttendanceMetadata revieweeData, StudyAttendanceMetadata reviewerData) {
        return revieweeData.studyId().equals(reviewerData.studyId()) && revieweeData.week() == reviewerData.week();
    }

    @Transactional
    public void updateReview(Long revieweeId, Long reviewerId, String content) {
        PeerReview peerReview = peerReviewRepository.findByRevieweeIdAndReviewerId(revieweeId, reviewerId)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.REVIEW_NOT_FOUND));
        peerReview.updateReview(content);
    }
}
