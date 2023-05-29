package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceRepository;
import com.kgu.studywithme.study.domain.week.Period;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.WeekRepository;
import com.kgu.studywithme.study.infra.query.dto.response.BasicAttendance;
import com.kgu.studywithme.study.infra.query.dto.response.BasicWeekly;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleGraduatedStudy;
import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static com.kgu.studywithme.fixture.WeekFixture.*;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> StudySimpleQueryRepository 테스트")
class StudySimpleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private WeekRepository weekRepository;

    private Member host;
    private Member member;
    private final Study[] programming = new Study[7];

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());

        programming[0] = studyRepository.save(SPRING.toOnlineStudy(host));
        programming[1] = studyRepository.save(JPA.toOnlineStudy(host));
        programming[2] = studyRepository.save(REAL_MYSQL.toOfflineStudy(host));
        programming[3] = studyRepository.save(KOTLIN.toOnlineStudy(host));
        programming[4] = studyRepository.save(NETWORK.toOnlineStudy(host));
        programming[5] = studyRepository.save(EFFECTIVE_JAVA.toOnlineStudy(host));
        programming[6] = studyRepository.save(AWS.toOfflineStudy(host));
    }

    @Test
    @DisplayName("신청한 스터디에 대한 정보를 조회한다")
    void findApplyStudyByMemberId() {
        /* 7개 신청 */
        applyStudy(member, programming[0], programming[1], programming[2], programming[3], programming[4], programming[5], programming[6]);
        List<SimpleStudy> result1 = studyRepository.findApplyStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result1,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0])
        );

        /* 2개 승인 & 1개 거부 */
        participateStudy(member, programming[6], programming[5]);
        rejectStudy(member, programming[4]);
        List<SimpleStudy> result2 = studyRepository.findApplyStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result2,
                List.of(programming[3], programming[2], programming[1], programming[0])
        );

        /* 2개 승인 & 1개 거부 */
        participateStudy(member, programming[3], programming[2]);
        rejectStudy(member, programming[1]);
        List<SimpleStudy> result3 = studyRepository.findApplyStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result3,
                List.of(programming[0])
        );
    }

    @Test
    @DisplayName("참여하고 있는 스터디에 대한 정보를 조회한다")
    void findParticipateStudyByMemberId() {
        // Case 1
        applyAndParticipateStudy(member, programming[0], programming[1], programming[2], programming[3]);

        List<SimpleStudy> result1 = studyRepository.findParticipateStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result1,
                List.of(programming[3], programming[2], programming[1], programming[0])
        );

        // Case 2
        applyAndParticipateStudy(member, programming[4], programming[5], programming[6]);

        List<SimpleStudy> result2 = studyRepository.findParticipateStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result2,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0])
        );

        // Case 3 (host)
        List<SimpleStudy> result3 = studyRepository.findParticipateStudyByMemberId(host.getId());
        assertThatStudiesMatch(
                result3,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0])
        );
    }

    @Test
    @DisplayName("찜 등록한 스터디에 대한 정보를 조회한다")
    void findFavoriteStudyByMemberId() {
        // Case 1
        favoriteStudy(member, programming[0], programming[1], programming[2], programming[3]);

        List<SimpleStudy> result1 = studyRepository.findFavoriteStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result1,
                List.of(programming[3], programming[2], programming[1], programming[0])
        );

        // Case 2
        favoriteStudy(member, programming[4], programming[5], programming[6]);

        List<SimpleStudy> result2 = studyRepository.findFavoriteStudyByMemberId(member.getId());
        assertThatStudiesMatch(
                result2,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0])
        );
    }

    @Test
    @DisplayName("졸업한 스터디에 대한 정보를 조회한다")
    void findGraduatedStudyByMemberId() {
        // Case 1
        graduateStudy(member, programming[0], programming[1], programming[2], programming[3]);
        writeStudyReview(member, programming[1]);

        List<SimpleGraduatedStudy> result1 = studyRepository.findGraduatedStudyByMemberId(member.getId());
        assertThatGraduatedStudiesMatch(
                result1,
                List.of(programming[3], programming[2], programming[1], programming[0]),
                List.of(false, false, true, false)
        );

        // Case 2
        graduateStudy(member, programming[4], programming[5], programming[6]);
        writeStudyReview(member, programming[5], programming[3]);

        List<SimpleGraduatedStudy> result2 = studyRepository.findGraduatedStudyByMemberId(member.getId());
        assertThatGraduatedStudiesMatch(
                result2,
                List.of(programming[6], programming[5], programming[4], programming[3], programming[2], programming[1], programming[0]),
                List.of(false, true, false, true, false, true, false)
        );
    }

    @Test
    @DisplayName("자동 출석이 적용된 주차 중 Period EndDate 이후인 주차를 조회한다 [For Scheduling]")
    void findAutoAttendanceAndPeriodEndWeek() {
        // given
        applyWeekly(
                programming[0],
                List.of(true, true, true),
                List.of(
                        Period.of(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2)), // get
                        Period.of(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)), // get
                        Period.of(LocalDateTime.now().plusDays(0), LocalDateTime.now().plusDays(1))
                )
        );
        applyWeekly(
                programming[1],
                List.of(true, false, true, false, true, true),
                List.of(
                        Period.of(LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4)), // get
                        Period.of(LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3)),
                        Period.of(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2)), // get
                        Period.of(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)),
                        Period.of(LocalDateTime.now().plusDays(0), LocalDateTime.now().plusDays(1)),
                        Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))
                )
        );
        applyWeekly(
                programming[2],
                List.of(true, true, true, true, false, false, true, true),
                List.of(
                        Period.of(LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(6)), // get
                        Period.of(LocalDateTime.now().minusDays(6), LocalDateTime.now().minusDays(5)), // get
                        Period.of(LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4)), // get
                        Period.of(LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3)), // get
                        Period.of(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2)),
                        Period.of(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)),
                        Period.of(LocalDateTime.now().plusDays(0), LocalDateTime.now().plusDays(1)),
                        Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))
                )
        );

        // when
        List<BasicWeekly> result = studyRepository.findAutoAttendanceAndPeriodEndWeek();

        // then
        assertAll(
                () -> assertThat(result).hasSize(8),
                () -> assertThat(result)
                        .map(BasicWeekly::studyId)
                        .containsExactly(
                                programming[0].getId(), programming[0].getId(),
                                programming[1].getId(), programming[1].getId(),
                                programming[2].getId(), programming[2].getId(), programming[2].getId(), programming[2].getId()
                        ),
                () -> assertThat(result)
                        .map(BasicWeekly::week)
                        .containsExactly(
                                1, 2,
                                1, 3,
                                1, 2, 3, 4
                        )
        );
    }

    @Test
    @DisplayName("전체 미출결 출석 정보에 대해서 조회한다 [For Scheduling]")
    void findBasicAttendanceInformation() {
        // given
        applyAndParticipateStudy(member, programming[0], programming[1], programming[2]);

        applyAttendance(programming[0], 1, host);
        applyAttendance(programming[0], 2, host);
        applyAttendance(programming[0], 3, host, member);
        applyAttendance(programming[0], 4, host, member);
        applyAttendance(programming[0], 5, host, member);
        applyAttendance(programming[0], 6, host, member);

        applyAttendance(programming[1], 1, host);
        applyAttendance(programming[1], 2, host);
        applyAttendance(programming[1], 3, host, member);
        applyAttendance(programming[1], 4, host, member);
        applyAttendance(programming[1], 5, host, member);
        applyAttendance(programming[1], 6, host, member);
        applyAttendance(programming[1], 7, host, member);
        applyAttendance(programming[1], 8, host, member);

        applyAttendance(programming[2], 1, host);
        applyAttendance(programming[2], 2, host);
        applyAttendance(programming[2], 3, host, member);

        // when
        List<BasicAttendance> attendances = studyRepository.findNonAttendanceInformation();

        // then
        assertThat(attendances).hasSize(28);
    }

    @Test
    @DisplayName("스터디 참여자인지 확인한다")
    void isStudyParticipant() {
        // 1. 미참여
        assertAll(
                () -> assertThat(studyRepository.isStudyParticipant(programming[0].getId(), host.getId())).isTrue(),
                () -> assertThat(studyRepository.isStudyParticipant(programming[0].getId(), member.getId())).isFalse()
        );

        // 2. 참여 신청
        applyStudy(member, programming[0]);
        assertAll(
                () -> assertThat(studyRepository.isStudyParticipant(programming[0].getId(), host.getId())).isTrue(),
                () -> assertThat(studyRepository.isStudyParticipant(programming[0].getId(), member.getId())).isFalse()
        );

        // 3. 참여 승인
        participateStudy(member, programming[0]);
        assertAll(
                () -> assertThat(studyRepository.isStudyParticipant(programming[0].getId(), host.getId())).isTrue(),
                () -> assertThat(studyRepository.isStudyParticipant(programming[0].getId(), member.getId())).isTrue()
        );

        // 4. 졸업
        graduateStudy(member, programming[0]);
        assertAll(
                () -> assertThat(studyRepository.isStudyParticipant(programming[0].getId(), host.getId())).isTrue(),
                () -> assertThat(studyRepository.isStudyParticipant(programming[0].getId(), member.getId())).isFalse()
        );
    }
    
    @Test
    @DisplayName("다음 주차를 조회한다")
    void getNextWeek() {
        // given
        Study study = studyRepository.save(GOOGLE_INTERVIEW.toOfflineStudy(host));
        
        /* 1주차 */
        assertThat(studyRepository.getNextWeek(study.getId())).isEqualTo(1);
        
        /* 2주차 */
        weekRepository.save(STUDY_WEEKLY_1.toWeekWithAssignment(study));
        assertThat(studyRepository.getNextWeek(study.getId())).isEqualTo(2);
    }

    @Test
    @DisplayName("최신 주차인지 확인한다")
    void isLatestWeek() {
        // given
        Study study = studyRepository.save(GOOGLE_INTERVIEW.toOfflineStudy(host));
        Week week1 = weekRepository.save(STUDY_WEEKLY_1.toWeekWithAssignment(study));
        Week week2 = weekRepository.save(STUDY_WEEKLY_2.toWeekWithAssignment(study));

        // when
        boolean actual1 = studyRepository.isLatestWeek(study.getId(), week1.getWeek());
        boolean actual2 = studyRepository.isLatestWeek(study.getId(), week2.getWeek());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue()
        );
    }

    @Test
    @DisplayName("특정 주차를 삭제한다")
    void deleteSpecificWeek() {
        // given
        Study study = studyRepository.save(GOOGLE_INTERVIEW.toOfflineStudy(host));
        Week week1 = weekRepository.save(STUDY_WEEKLY_1.toWeekWithAssignment(study));
        Week week2 = weekRepository.save(STUDY_WEEKLY_2.toWeekWithAssignment(study));
        Week week3 = weekRepository.save(STUDY_WEEKLY_3.toWeekWithAssignment(study));
        Week week4 = weekRepository.save(STUDY_WEEKLY_4.toWeekWithAssignment(study));
        Week week5 = weekRepository.save(STUDY_WEEKLY_5.toWeekWithAssignment(study));
        Week week6 = weekRepository.save(STUDY_WEEKLY_6.toWeekWithAssignment(study));

        // when
        studyRepository.deleteSpecificWeek(study.getId(), week5.getWeek());
        studyRepository.deleteSpecificWeek(study.getId(), week6.getWeek());

        // then
        assertAll(
                () -> assertThat(weekRepository.existsById(week1.getId())).isTrue(),
                () -> assertThat(weekRepository.existsById(week2.getId())).isTrue(),
                () -> assertThat(weekRepository.existsById(week3.getId())).isTrue(),
                () -> assertThat(weekRepository.existsById(week4.getId())).isTrue(),
                () -> assertThat(weekRepository.existsById(week5.getId())).isFalse(),
                () -> assertThat(weekRepository.existsById(week6.getId())).isFalse()
        );
    }

    private void applyStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.applyParticipation(member);
        }
    }

    private void participateStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.approveParticipation(member);
        }
    }

    private void rejectStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.rejectParticipation(member);
        }
    }

    private void graduateStudy(Member member, Study study) {
        study.graduateParticipant(member);
    }

    private void applyAndParticipateStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }

    private void graduateStudy(Member member, Study... studies) {
        for (Study study : studies) {
            study.applyParticipation(member);
            study.approveParticipation(member);
            study.graduateParticipant(member);
        }
    }

    private void favoriteStudy(Member member, Study... studies) {
        for (Study study : studies) {
            favoriteRepository.save(Favorite.favoriteMarking(study.getId(), member.getId()));
        }
    }

    private void writeStudyReview(Member member, Study... studies) {
        for (Study study : studies) {
            study.writeReview(member, "hello world");
        }
    }

    private void applyWeekly(Study study, List<Boolean> autoAttendances, List<Period> periods) {
        for (int index = 1; index <= autoAttendances.size(); index++) {
            study.createWeekWithAssignment(
                    "Week " + index,
                    "Week " + index + "'s Content",
                    index,
                    periods.get(index - 1),
                    autoAttendances.get(index - 1),
                    List.of()
            );
        }
    }

    private void applyAttendance(Study study, int week, Member... members) {
        for (Member member : members) {
            attendanceRepository.save(Attendance.recordAttendance(study, member, week, NON_ATTENDANCE));
        }
    }

    private void assertThatStudiesMatch(List<SimpleStudy> result, List<Study> expect) {
        int expectSize = expect.size();
        assertThat(result).hasSize(expectSize);

        for (int i = 0; i < expectSize; i++) {
            SimpleStudy actual = result.get(i);
            Study expected = expect.get(i);

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName()).isEqualTo(expected.getNameValue()),
                    () -> assertThat(actual.getCategory()).isEqualTo(expected.getCategory().getName()),
                    () -> assertThat(actual.getThumbnail()).isEqualTo(expected.getThumbnail().getImageName()),
                    () -> assertThat(actual.getThumbnailBackground()).isEqualTo(expected.getThumbnail().getBackground())
            );
        }
    }

    private void assertThatGraduatedStudiesMatch(List<SimpleGraduatedStudy> result,
                                                 List<Study> expect,
                                                 List<Boolean> reviewWrittens) {
        int expectSize = expect.size();
        assertThat(result).hasSize(expectSize);

        for (int i = 0; i < expectSize; i++) {
            SimpleGraduatedStudy actual = result.get(i);
            Study expected = expect.get(i);
            Boolean reviewWritten = reviewWrittens.get(i);

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName()).isEqualTo(expected.getNameValue()),
                    () -> assertThat(actual.getCategory()).isEqualTo(expected.getCategory().getName()),
                    () -> assertThat(actual.getThumbnail()).isEqualTo(expected.getThumbnail().getImageName()),
                    () -> assertThat(actual.getThumbnailBackground()).isEqualTo(expected.getThumbnail().getBackground()),
                    () -> assertThat(actual.getReview() != null).isEqualTo(reviewWritten)
            );
        }
    }
}
