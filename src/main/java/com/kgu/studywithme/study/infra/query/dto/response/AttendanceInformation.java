package com.kgu.studywithme.study.infra.query.dto.response;

import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.service.dto.response.StudyMember;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceInformation {
    private final StudyMember participant;
    private final int week;
    private final String attendanceStatus;

    @QueryProjection
    public AttendanceInformation(Long id, Nickname nickname, int week, AttendanceStatus attendanceStatus) {
        this.participant = new StudyMember(id, nickname.getValue());
        this.week = week;
        this.attendanceStatus = attendanceStatus.getDescription();
    }
}
