package com.kgu.studywithme.member.service;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.review.PeerReviewRepository;
import com.kgu.studywithme.member.service.dto.response.MemberInformation;
import com.kgu.studywithme.member.service.dto.response.PeerReviewAssembler;
import com.kgu.studywithme.member.service.dto.response.RelatedStudy;
import com.kgu.studywithme.study.domain.StudyRepository;
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
    private final StudyRepository studyRepository;
    private final PeerReviewRepository peerReviewRepository;

    public MemberInformation getInformation(Long memberId) {
        Member member = memberFindService.findByIdWithInterests(memberId);
        return new MemberInformation(member);
    }

    public RelatedStudy getParticipateStudy(Long memberId) {
        List<SimpleStudy> participateStudy = studyRepository.findParticipateStudyByMemberId(memberId);
        return new RelatedStudy(participateStudy);
    }

    public RelatedStudy getGraduatedStudy(Long memberId) {
        List<SimpleStudy> graduatedStudy = studyRepository.findGraduatedStudyByMemberId(memberId);
        return new RelatedStudy(graduatedStudy);
    }

    public RelatedStudy getFavoriteStudy(Long memberId) {
        List<SimpleStudy> favoriteStudy = studyRepository.findFavoriteStudyByMemberId(memberId);
        return new RelatedStudy(favoriteStudy);
    }

    public PeerReviewAssembler getPeerReviews(Long memberId) {
        List<String> peerReviews = peerReviewRepository.findPeerReviewByMemberId(memberId);
        return new PeerReviewAssembler(peerReviews);
    }
}
