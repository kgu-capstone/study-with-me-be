package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.interest.Interest;
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

    @Column(name = "profile_url", nullable = false)
    private String profileUrl;

    @Column(name = "birth", nullable = false, updatable = false)
    private LocalDate birth;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    private Region region;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Interest> interests = new ArrayList<>();

    @Builder
    private Member(String name, Nickname nickname, Email email, String profileUrl, LocalDate birth,
                   String phone, Gender gender, Region region, Set<Category> interests) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = profileUrl;
        this.birth = birth;
        this.phone = phone;
        this.gender = gender;
        this.region = region;
        applyInterests(interests);
    }

    public static Member createMember(String name, Nickname nickname, Email email, String profileUrl, LocalDate birth,
                                      String phone, Gender gender, Region region, Set<Category> interests) {
        return new Member(name, nickname, email, profileUrl, birth, phone, gender, region, interests);
    }

    public void changeNickname(String changeNickname) {
        this.nickname = this.nickname.update(changeNickname);
    }

    public void updateProfile(String profileUrl) {
        this.profileUrl = profileUrl;
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
}
