package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Column(name = "birth", nullable = false, updatable = false)
    private LocalDate birth;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    private Region region;

    @Builder
    private Member(String name, Email email, Password password, LocalDate birth, String phone, Gender gender, Region region) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birth = birth;
        this.phone = phone;
        this.gender = gender;
        this.region = region;
    }

    public static Member createMember(String name, Email email, Password password, LocalDate birth, String phone, Gender gender, Region region) {
        return new Member(name, email, password, birth, phone, gender, region);
    }

    public void changePassword(String changePassword, PasswordEncoder encoder) {
        if (this.password.isSamePassword(changePassword, encoder)) {
            throw StudyWithMeException.type(MemberErrorCode.PASSWORD_SAME_AS_BEFORE);
        }
        this.password = this.password.update(changePassword, encoder);
    }

    // Add Getter
    public String getPasswordValue() {
        return password.getValue();
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
}
