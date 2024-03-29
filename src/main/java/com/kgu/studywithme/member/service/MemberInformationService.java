package com.kgu.studywithme.member.service;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.review.PeerReviewRepository;
import com.kgu.studywithme.member.infra.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.service.dto.response.*;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleGraduatedStudy;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInformationService {
    private final MemberFindService memberFindService;
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final PeerReviewRepository peerReviewRepository;

    public MemberInformation getInformation(Long memberId) {
        Member member = memberFindService.findByIdWithInterests(memberId);
        return new MemberInformation(member);
    }

    public RelatedStudy getApplyStudy(Long memberId) {
        List<SimpleStudy> participateStudy = studyRepository.findApplyStudyByMemberId(memberId);
        return new RelatedStudy(participateStudy);
    }

    public RelatedStudy getParticipateStudy(Long memberId) {
        List<SimpleStudy> participateStudy = studyRepository.findParticipateStudyByMemberId(memberId);
        return new RelatedStudy(participateStudy);
    }

    public RelatedStudy getFavoriteStudy(Long memberId) {
        List<SimpleStudy> favoriteStudy = studyRepository.findFavoriteStudyByMemberId(memberId);
        return new RelatedStudy(favoriteStudy);
    }

    public GraduatedStudy getGraduatedStudy(Long memberId) {
        List<SimpleGraduatedStudy> graduatedStudy = studyRepository.findGraduatedStudyByMemberId(memberId);
        return new GraduatedStudy(graduatedStudy);
    }

    public PeerReviewAssembler getPeerReviews(Long memberId) {
        List<String> peerReviews = peerReviewRepository.findPeerReviewByMemberId(memberId);
        return new PeerReviewAssembler(peerReviews);
    }

    public AttendanceRatioAssembler getAttendanceRatio(Long memberId) {
        List<AttendanceRatio> attendanceRatios = memberRepository.findAttendanceRatioByMemberId(memberId);
        return new AttendanceRatioAssembler(attendanceRatios);
    }
}
