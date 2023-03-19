package com.kgu.studywithme.favorite.domain;

import com.kgu.studywithme.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "favorite")
public class Favorite extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    private Favorite(Long studyId, Long memberId) {
        this.studyId = studyId;
        this.memberId = memberId;
    }

    public static Favorite favoriteMarking(Long studyId, Long memberId) {
        return new Favorite(studyId, memberId);
    }
}
