package com.kgu.studywithme.favorite.service;

import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteEnrollService {
    private final FavoriteRepository favoriteRepository;
    private final FavoriteValidator favoriteValidator;

    @Transactional
    public Long enroll(Long studyId, Long hostId) {
        validateEnroll(studyId, hostId);

        Favorite favoriteStudy = Favorite.favoriteMarking(studyId, hostId);
        return favoriteRepository.save(favoriteStudy).getStudyId();
    }

    @Transactional
    public void unenroll(Long studyId, Long hostId) {
        validateUnenroll(studyId, hostId);

        Favorite deleteFavorite = favoriteRepository.findFavoriteByStudyIdAndMemberId(studyId, hostId)
                        .orElseThrow();
        favoriteRepository.delete(deleteFavorite);
    }

    private void validateEnroll(Long studyId, Long hostId) {
        favoriteValidator.validateNonExist(studyId, hostId);
    }

    private void validateUnenroll(Long studyId, Long hostId) {
        favoriteValidator.validateExist(studyId, hostId);
    }
}
