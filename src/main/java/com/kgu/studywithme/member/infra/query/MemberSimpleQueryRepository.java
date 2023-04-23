package com.kgu.studywithme.member.infra.query;


import com.kgu.studywithme.member.infra.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.infra.query.dto.response.StudyAttendanceMetadata;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;

import java.util.List;

public interface MemberSimpleQueryRepository {
    boolean isReportReceived(Long reporteeId, Long reporterId);
    List<StudyAttendanceMetadata> findStudyAttendanceMetadataByMemberId(Long memberId);
    Long getAttendanceCount(Long studyId, Long memberId, AttendanceStatus status);
    List<AttendanceRatio> findAttendanceRatioByMemberId(Long memberId);
}
