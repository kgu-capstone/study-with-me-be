package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.BasicAttendance;
import com.kgu.studywithme.study.infra.query.dto.response.BasicHashtag;
import com.kgu.studywithme.study.infra.query.dto.response.BasicWeekly;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;

import java.util.List;

public interface StudySimpleQueryRepository {
    List<BasicHashtag> findHashtags();
    List<SimpleStudy> findApplyStudyByMemberId(Long memberId);
    List<SimpleStudy> findParticipateStudyByMemberId(Long memberId);
    List<SimpleStudy> findGraduatedStudyByMemberId(Long memberId);
    List<SimpleStudy> findFavoriteStudyByMemberId(Long memberId);
    List<BasicWeekly> findAutoAttendanceAndPeriodEndWeek();
    List<BasicAttendance> findBasicAttendanceInformation();
}
