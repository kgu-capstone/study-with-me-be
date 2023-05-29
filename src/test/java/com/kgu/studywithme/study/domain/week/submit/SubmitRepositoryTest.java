package com.kgu.studywithme.study.domain.week.submit;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.WeekRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Repository Layer] -> SubmitRepository 테스트")
class SubmitRepositoryTest extends RepositoryTest {
    @Autowired
    private SubmitRepository submitRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private WeekRepository weekRepository;

    private Member host;
    private Member participant;
    private Study study;
    private Week week;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        participant = memberRepository.save(GHOST.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));
        week = weekRepository.save(STUDY_WEEKLY_1.toWeekWithAssignment(study));
    }

    @Test
    @DisplayName("특정 주차에 제출한 과제를 조회한다")
    void findByParticipantIdAndWeek() {
        // given
        final UploadAssignment uploadAssignment = UploadAssignment.withLink("https://notion.link");
        week.submitAssignment(host, uploadAssignment);

        // when
        Optional<Submit> hostSubmit = submitRepository.findByParticipantIdAndWeek(host.getId(), STUDY_WEEKLY_1.getWeek());
        Optional<Submit> participantSubmit = submitRepository.findByParticipantIdAndWeek(participant.getId(), STUDY_WEEKLY_1.getWeek());

        // then
        assertThat(hostSubmit).isPresent();
        assertThat(participantSubmit).isEmpty();

        Submit submit = hostSubmit.get();
        assertAll(
                () -> assertThat(submit.getUploadAssignment()).isEqualTo(uploadAssignment),
                () -> assertThat(submit.getWeek()).isEqualTo(week),
                () -> assertThat(submit.getParticipant()).isEqualTo(host)
        );
    }
}
