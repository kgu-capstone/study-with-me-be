package com.kgu.studywithme.study.domain.week.attachment;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.week.Week;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.WeekFixture.STUDY_WEEKLY_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study-Week-Attachment 도메인 테스트")
class AttachmentTest {
    private static final Member HOST = JIWON.toMember();
    private static final Study STUDY = SPRING.toOnlineStudy(HOST);
    private static final Week WEEK = STUDY_WEEKLY_1.toWeek(STUDY);

    @Test
    @DisplayName("Attachment를 생성한다")
    void construct() {
        final UploadAttachment uploadAttachment = UploadAttachment.of("hello.pdf", "uuid.pdf");
        final Attachment attachment = Attachment.addAttachmentFile(WEEK, uploadAttachment);

        assertAll(
                () -> assertThat(attachment.getWeek()).isEqualTo(WEEK),
                () -> assertThat(attachment.getUploadAttachment().getUploadFileName()).isEqualTo("hello.pdf"),
                () -> assertThat(attachment.getUploadAttachment().getLink()).isEqualTo("uuid.pdf")
        );
    }
}
