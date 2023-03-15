package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.member.domain.Member;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

public class StudyAttendance {

    @ManyToOne(fetch = FetchType.LAZY)
    private StudyLog studyLog;

    @ManyToOne(fetch = FetchType.LAZY)
    private StudyMember studyMember;

    @Column(name = "attendance")
    private boolean isAttendance;

}
