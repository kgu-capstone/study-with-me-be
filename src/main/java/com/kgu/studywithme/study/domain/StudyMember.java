package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.member.domain.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyMember {
    @Id
    @GeneratedValue
    @Column(name = "study_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private StudyMemberStatus status; // ADMIN, ONGOING, COMPLETED, LIKED

    @Builder
    public StudyMember(Study study, Member member, StudyMemberStatus status) {
        this.study = study;
        this.member = member;
        this.status = status;
    }

    public static StudyMember createStudyMember(Study study, Member member, StudyMemberStatus status) {
        return new StudyMember(study, member, status);
    }

    public void changeStatus(StudyMemberStatus status) {
        this.status = status;
    }
}
