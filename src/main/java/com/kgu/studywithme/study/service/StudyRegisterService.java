package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.StudyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyRegisterService {
    private final StudyValidator studyValidator;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long register(Study study, Long memberId) {
        validateUniqueFields(study);

        Member host = memberRepository.findById(memberId)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.MEMBER_NOT_FOUND));

        if (study.getType() == StudyType.ONLINE) {
            study = Study.createOnlineStudy(host, study.getName(), study.getDescription(),
                    study.getCapacity(), study.getCategory(), study.getType(), study.getHashtags());
        } else if (study.getType() == StudyType.OFFLINE) {
            study = Study.createOfflineStudy(host, study.getName(), study.getDescription(),
                    study.getCapacity(), study.getCategory(), study.getType(), study.getArea(), study.getHashtags());
        } else {
            throw StudyWithMeException.type(GlobalErrorCode.NOT_SUPPORTED_MEDIA_TYPE_ERROR);
        }

        return studyRepository.save(study).getId();
    }

    private void validateUniqueFields(Study study) {
        studyValidator.validateName(study.getName());
    }
}
