package com.kgu.studywithme.favorite.service;

import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteValidator {
    private final FavoriteRepository favoriteRepository;

    public void validateNonExist(Long studyId, Long hostId) {
        if (favoriteRepository.existsByStudyIdAndMemberId(studyId, hostId)) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_EXIST);
        }
    }

    public void validateExist(Long studyId, Long hostId) {
        if (!favoriteRepository.existsByStudyIdAndMemberId(studyId, hostId)) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_EXIST);
        }
    }
}
