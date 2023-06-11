package com.kgu.studywithme.study.service;

import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.infra.query.dto.response.AttendanceInformation;
import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;
import com.kgu.studywithme.study.infra.query.dto.response.ReviewInformation;
import com.kgu.studywithme.study.infra.query.dto.response.StudyApplicantInformation;
import com.kgu.studywithme.study.service.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyInformationService {
    private final StudyFindService studyFindService;
    private final StudyRepository studyRepository;

    public StudyInformation getInformation(Long studyId) {
        Study study = studyFindService.findByIdWithParticipants(studyId);
        return new StudyInformation(study);
    }

    public ReviewAssembler getReviews(Long studyId) {
        int graduateCount = studyRepository.getGraduatedParticipantCountByStudyId(studyId);
        List<ReviewInformation> reviews = studyRepository.findReviewByStudyId(studyId);

        return new ReviewAssembler(graduateCount, reviews);
    }

    public NoticeAssembler getNotices(Long studyId) {
        List<NoticeInformation> result = studyRepository.findNoticeWithCommentsByStudyId(studyId);
        return new NoticeAssembler(result);
    }

    public StudyApplicant getApplicants(Long studyId) {
        List<StudyApplicantInformation> result = studyRepository.findApplicantByStudyId(studyId);
        return new StudyApplicant(result);
    }

    public StudyParticipant getApproveParticipants(Long studyId) {
        Study study = studyFindService.findByIdWithParticipants(studyId);

        StudyMember host = new StudyMember(study.getHost());
        List<StudyMember> participants = study.getApproveParticipantsWithoutHost()
                .stream()
                .map(StudyMember::new)
                .toList();

        return new StudyParticipant(host, participants);
    }

    public AttendanceAssmbler getAttendances(Long studyId) {
        List<AttendanceInformation> result = studyRepository.findAttendanceByStudyId(studyId);

        List<StudyMemberAttendanceResult> attendanceResults = result.stream()
                .collect(Collectors.groupingBy(AttendanceInformation::getParticipant))
                .entrySet().stream()
                .map(entry ->
                        new StudyMemberAttendanceResult(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .map(info -> new AttendanceSummary(info.getWeek(), info.getAttendanceStatus()))
                                        .toList()
                        )
                )
                .toList();

        return new AttendanceAssmbler(attendanceResults);
    }

    public WeeklyAssembler getWeeks(Long studyId) {
        List<Week> weeks = studyRepository.findWeeklyByStudyId(studyId);

        List<WeeklySummary> result = weeks.stream()
                .map(WeeklySummary::new)
                .toList();

        return new WeeklyAssembler(result);
    }
}
