package com.kgu.studywithme.member.infra.query;


import com.kgu.studywithme.member.infra.query.dto.response.StudyAttendanceMetadata;

import java.util.List;

public interface MemberSimpleQueryRepository {
    boolean isReportReceived(Long reporteeId, Long reporterId);
    List<StudyAttendanceMetadata> findStudyAttendanceMetadataByMemberId(Long memberId);
}
