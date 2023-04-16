package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.interest.Interest;
import com.kgu.studywithme.member.domain.review.PeerReview;
import com.kgu.studywithme.member.domain.review.PeerReviews;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Email email;

    @Column(name = "birth", nullable = false, updatable = false)
    private LocalDate birth;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    private Region region;

    @Embedded
    private PeerReviews peerReviews;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>();

    @Builder
    private Member(String name, Nickname nickname, Email email, LocalDate birth, String phone,
                   Gender gender, Region region, Set<Category> interests) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.birth = birth;
        this.phone = phone;
        this.gender = gender;
        this.region = region;
        this.peerReviews = PeerReviews.createPeerReviewsPage();
        applyInterests(interests);
    }

    public static Member createMember(String name, Nickname nickname, Email email, LocalDate birth, String phone,
                                      Gender gender, Region region, Set<Category> interests) {
        return new Member(name, nickname, email, birth, phone, gender, region, interests);
    }

    public void changeNickname(String changeNickname) {
        this.nickname = this.nickname.update(changeNickname);
    }

    public void applyInterests(Set<Category> interests) {
        this.interests.clear();
        this.interests.addAll(
                interests.stream()
                        .map(value -> Interest.applyInterest(this, value))
                        .toList()
        );
    }

    public boolean isSameMember(Member member) {
        return this.email.isSameEmail(member.getEmail());
    }

    public void applyPeerReview(Member reviewer, String content) {
        peerReviews.writeReview(PeerReview.doReview(this, reviewer, content));
    }

    // Add Getter
    public String getNicknameValue() {
        return nickname.getValue();
    }

    public String getEmailValue() {
        return email.getValue();
    }

    public String getRegionProvince() {
        return region.getProvince();
    }

    public String getRegionCity() {
        return region.getCity();
    }

    public List<Category> getInterests() {
        return interests.stream()
                .map(Interest::getCategory)
                .toList();
    }

    public List<PeerReview> getPeerReviews() {
        return peerReviews.getPeerReviews();
    }
}
