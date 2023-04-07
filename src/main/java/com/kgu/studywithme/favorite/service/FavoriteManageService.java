package com.kgu.studywithme.favorite.service;

import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteManageService {
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public Long like(Long studyId, Long memberId) {
        validateLike(studyId, memberId);

        Favorite favoriteStudy = Favorite.favoriteMarking(studyId, memberId);
        return favoriteRepository.save(favoriteStudy).getId();
    }

    @Transactional
    public void cancel(Long studyId, Long memberId) {
        validateCancel(studyId, memberId);
        favoriteRepository.deleteByStudyIdAndMemberId(studyId, memberId);
    }

    private void validateLike(Long studyId, Long memberId) {
        if (favoriteRepository.existsByStudyIdAndMemberId(studyId, memberId)) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_EXIST);
        }
    }

    private void validateCancel(Long studyId, Long memberId) {
        if (!favoriteRepository.existsByStudyIdAndMemberId(studyId, memberId)) {
            throw StudyWithMeException.type(FavoriteErrorCode.STUDY_IS_NOT_FAVORITE);
        }
    }
}
