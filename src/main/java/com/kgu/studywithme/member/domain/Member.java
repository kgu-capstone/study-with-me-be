package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
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

    @Column(name = "google_profile_url", nullable = false)
    private String googleProflieUrl;

    @Embedded
    private RealProfile profileUrl;

    @Column(name = "birth", nullable = false, updatable = false)
    private LocalDate birth;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    private Region region;

    @ElementCollection
    @CollectionTable(
            name = "interest",
            joinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<Category> interests = new HashSet<>();

    @Builder
    private Member(String name, Nickname nickname, Email email, String googleProflieUrl, RealProfile profileUrl, LocalDate birth,
                   String phone, Gender gender, Region region, Set<Category> interests) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.googleProflieUrl = googleProflieUrl;
        this.profileUrl = profileUrl;
        this.birth = birth;
        this.phone = phone;
        this.gender = gender;
        this.region = region;
        this.interests = interests;
    }

    public static Member createMember(String name, Nickname nickname, Email email, String googleProflieUrl, RealProfile profileUrl, LocalDate birth,
                                      String phone, Gender gender, Region region, Set<Category> interests) {
        return new Member(name, nickname, email, googleProflieUrl, profileUrl, birth, phone, gender, region, interests);
    }

    public void changeNickname(String changeNickname) {
        this.nickname = this.nickname.update(changeNickname);
    }

    public void updateProfile(String profileUrl) {
        this.profileUrl = this.profileUrl.updateProfileUrl(profileUrl);
    }

    public void updateGoogleProfileUrl(String googleProflieUrl) {
        this.googleProflieUrl = googleProflieUrl;

        if (!profileUrl.isAvatarProfile()) {
            this.profileUrl = this.profileUrl.updateProfileUrl(googleProflieUrl);
        }
    }

    public void updateInterests(Set<Category> interests) {
        this.interests.clear();
        this.interests.addAll(interests);
    }

    public boolean isSameMember(Member member) {
        return this.email.isSameEmail(member.getEmail());
    }

    // Add Getter
    public String getNicknameValue() {
        return nickname.getValue();
    }

    public String getEmailValue() {
        return email.getValue();
    }

    public String getSelectedProfileUrl() {
        return profileUrl.getValue();
    }

    public String getRegionProvince() {
        return region.getProvince();
    }

    public String getRegionCity() {
        return region.getCity();
    }
}
