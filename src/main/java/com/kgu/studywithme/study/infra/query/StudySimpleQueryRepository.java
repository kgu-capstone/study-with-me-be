package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.BasicAttendance;
import com.kgu.studywithme.study.infra.query.dto.response.BasicWeekly;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleGraduatedStudy;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;

import java.util.List;

public interface StudySimpleQueryRepository {
    List<SimpleStudy> findApplyStudyByMemberId(Long memberId);
    List<SimpleStudy> findParticipateStudyByMemberId(Long memberId);
    List<SimpleStudy> findFavoriteStudyByMemberId(Long memberId);
    List<SimpleGraduatedStudy> findGraduatedStudyByMemberId(Long memberId);
    List<BasicWeekly> findAutoAttendanceAndPeriodEndWeek();
    List<BasicAttendance> findNonAttendanceInformation();
    boolean isStudyParticipant(Long studyId, Long memberId);
    int getNextWeek(Long studyId);
    boolean isLatestWeek(Long studyId, Integer week);
    void deleteSpecificWeek(Long studyId, Integer week);
}
