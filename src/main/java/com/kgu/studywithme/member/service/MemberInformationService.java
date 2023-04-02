package com.kgu.studywithme.member.service;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.dto.response.MemberInformation;
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

    public MemberInformation getInformation(Long memberId) {
        Member member = memberFindService.findByIdWithInterests(memberId);
        return new MemberInformation(member);
    }

    public RelatedStudy getRelatedStudy(Long memberId) {
        List<SimpleStudy> participateStudy = studyRepository.findParticipateStudyByMemberId(memberId);
        List<SimpleStudy> graduatedStudy = studyRepository.findGraduatedStudyByMemberId(memberId);
        List<SimpleStudy> favoriteStudy = studyRepository.findFavoriteStudyByMemberId(memberId);

        return new RelatedStudy(participateStudy, graduatedStudy, favoriteStudy);
    }
}
