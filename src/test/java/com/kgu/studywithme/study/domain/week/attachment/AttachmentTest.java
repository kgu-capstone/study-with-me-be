package com.kgu.studywithme.study.domain.week.attachment;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.Period;
import com.kgu.studywithme.study.domain.week.Week;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week-Attachment 도메인 테스트")
class AttachmentTest {
    private static final Member HOST = JIWON.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);
    private static final Period PERIOD = Period.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7));
    private static final Week WEEK = Week.createWeek(STUDY, "Week 1", "지정된 시간까지 줌에 접속해주세요.", 1, PERIOD, List.of());

    @Test
    @DisplayName("Attachment를 생성한다")
    void construct() {
        final Attachment attachment = Attachment.addAttachmentFile(WEEK, "hello_world.txt");

        assertAll(
                () -> assertThat(attachment.getLink()).isEqualTo("hello_world.txt"),
                () -> assertThat(attachment.getWeek()).isEqualTo(WEEK)
        );
    }
}
