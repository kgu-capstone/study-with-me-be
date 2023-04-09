package com.kgu.studywithme.member.domain.review;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PeerReviews {
    @OneToMany(mappedBy = "reviewee", cascade = CascadeType.PERSIST)
    private List<PeerReview> peerReviews = new ArrayList<>();

    public static PeerReviews createPeerReviewsPage() {
        return new PeerReviews();
    }

    public void writeReview(PeerReview review) {
        validateFirstReview(review.getReviewer());
        peerReviews.add(review);
    }

    private void validateFirstReview(Member reviewer) {
        if (isAlreadyReview(reviewer)) {
            throw StudyWithMeException.type(MemberErrorCode.ALREADY_REVIEW);
        }
    }

    private boolean isAlreadyReview(Member reviewer) {
        return peerReviews.stream()
                .anyMatch(review -> review.getReviewer().isSameMember(reviewer));
    }
}
