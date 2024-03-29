package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> WeekRepository 테스트")
class WeekRepositoryTest extends RepositoryTest {
    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Study study;
    private Week week1;
    private Week week2;
    private Week week3;
    private Week week4;

    @BeforeEach
    void setUp() {
        Member host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));

        week1 = weekRepository.save(STUDY_WEEKLY_1.toWeekWithAssignment(study));
        week2 = weekRepository.save(STUDY_WEEKLY_2.toWeekWithAssignment(study));
        week3 = weekRepository.save(STUDY_WEEKLY_3.toWeekWithAssignment(study));
        week4 = weekRepository.save(STUDY_WEEKLY_4.toWeekWithAssignment(study));
        weekRepository.save(STUDY_WEEKLY_5.toWeek(study));
        weekRepository.save(STUDY_WEEKLY_6.toWeek(study));
    }

    @Test
    @DisplayName("스터디 ID(PK) + 주차 정보를 통해서 Week을 조회한다")
    void findByStudyIdAndWeek() {
        // when
        Week findWeek1 = weekRepository.findByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_1.getWeek()).orElseThrow();
        Week findWeek2 = weekRepository.findByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_2.getWeek()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findWeek1).isEqualTo(week1),
                () -> assertThat(findWeek2).isEqualTo(week2)
        );
    }

    @Test
    @DisplayName("자동 출석인 주차들을 조회한다")
    void findAutoAttendanceWeek() {
        // when
        List<Week> weeks = weekRepository.findAutoAttendanceWeek();

        // then
        assertThat(weeks).containsExactlyInAnyOrder(week1, week2, week3, week4);
    }
}
