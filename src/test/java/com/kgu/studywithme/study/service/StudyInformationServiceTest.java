package com.kgu.studywithme.study.service;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.utils.MemberAgeCalculator;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.notice.comment.Comment;
import com.kgu.studywithme.study.infra.query.dto.response.CommentInformation;
import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;
import com.kgu.studywithme.study.service.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study [Service Layer] -> StudyInformationService 테스트")
class StudyInformationServiceTest extends ServiceTest {
    @Autowired
    private StudyInformationService studyInformationService;

    private Member host;
    private Study study;
    private final Member[] members = new Member[5];
    private final Notice[] notices = new Notice[3];

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host));

        members[0] = memberRepository.save(DUMMY1.toMember());
        members[1] = memberRepository.save(DUMMY2.toMember());
        members[2] = memberRepository.save(DUMMY3.toMember());
        members[3] = memberRepository.save(DUMMY4.toMember());
        members[4] = memberRepository.save(DUMMY5.toMember());

        for (Member member : members) {
            study.applyParticipation(member);
            study.approveParticipation(member);
        }
    }

    @Test
    @DisplayName("스터디의 기본 정보를 조회한다")
    void getInformation() {
        // when
        StudyInformation information = studyInformationService.getInformation(study.getId());

        // then
        assertAll(
                // Study
                () -> assertThat(information.id()).isEqualTo(study.getId()),
                () -> assertThat(information.name()).isEqualTo(study.getNameValue()),
                () -> assertThat(information.description()).isEqualTo(study.getDescriptionValue()),
                () -> assertThat(information.category()).isEqualTo(study.getCategory().getName()),
                () -> assertThat(information.type()).isEqualTo(study.getType().getDescription()),
                () -> assertThat(information.area()).isNull(),
                () -> assertThat(information.recruitmentStatus()).isEqualTo(study.getRecruitmentStatus().getDescription()),
                () -> assertThat(information.currentMembers()).isEqualTo(study.getApproveParticipants().size()),
                () -> assertThat(information.maxMembers()).isEqualTo(study.getMaxMembers()),
                () -> assertThat(information.averageAge()).isEqualTo(MemberAgeCalculator.getAverage(getBirthList())),
                () -> assertThat(information.hashtags()).containsAll(study.getHashtags()),
                // Host
                () -> assertThat(information.host().id()).isEqualTo(host.getId()),
                () -> assertThat(information.host().nickname()).isEqualTo(host.getNicknameValue())
        );
    }
    
    @Test
    @DisplayName("스터디 졸업자들의 리뷰를 조회한다")
    void getReviews() {
        // given
        graduateAllParticipant();
        
        // when
        ReviewAssembler result = studyInformationService.getReviews(study.getId());

        // then
        assertAll(
                () -> assertThat(result.graduateCount()).isEqualTo(members.length),
                () -> assertThat(result.reviews()).hasSize(members.length),
                () -> {
                    List<String> reviewers = result.reviews()
                            .stream()
                            .map(StudyReview::reviewer)
                            .map(StudyMember::nickname)
                            .toList();

                    assertThat(reviewers).containsAll(
                            Arrays.stream(members)
                                    .map(Member::getNicknameValue)
                                    .toList()
                    );
                }
        );
    }

    @Test
    @DisplayName("스터디의 공지사항 관련 정보를 조회한다")
    void getNotices() {
        // given
        initNotices();
        List<List<Member>> commentWriters = List.of(
                List.of(members[0], members[1], members[3], members[4]),
                List.of(members[1], members[2], members[3]),
                List.of()
        );
        writeComment(notices[0], commentWriters.get(0));
        writeComment(notices[1], commentWriters.get(1));
        writeComment(notices[2], commentWriters.get(2));

        // when
        NoticeAssembler assembler = studyInformationService.getNotices(study.getId());

        List<NoticeInformation> result = assembler.result();
        assertThat(result).hasSize(3);
        assertThatNoticeInformationMatch(result.get(0), notices[2], commentWriters.get(2));
        assertThatNoticeInformationMatch(result.get(1), notices[1], commentWriters.get(1));
        assertThatNoticeInformationMatch(result.get(2), notices[0], commentWriters.get(0));
    }

    private List<LocalDate> getBirthList() {
        List<LocalDate> list = new ArrayList<>();
        list.add(host.getBirth());

        for (Member member : members) {
            list.add(member.getBirth());
        }

        return list;
    }

    private void graduateAllParticipant() {
        for (Member member : members) {
            study.graduateParticipant(member);
            study.writeReview(member, "좋은 스터디");
        }
    }

    private void initNotices() {
        for (int i = 0; i < notices.length; i++) {
            notices[i] = Notice.writeNotice(study, "공지" + (i + 1), "내용" + (i + 1));
            noticeRepository.save(notices[i]);
        }
    }

    private void writeComment(Notice notice, List<Member> members) {
        for (Member member : members) {
            commentRepository.save(Comment.writeComment(notice, member, "댓글"));
        }
    }

    private void assertThatNoticeInformationMatch(NoticeInformation information, Notice notice, List<Member> members) {
        assertAll(
                () -> assertThat(information.getId()).isEqualTo(notice.getId()),
                () -> assertThat(information.getTitle()).isEqualTo(notice.getTitle()),
                () -> assertThat(information.getContent()).isEqualTo(notice.getContent()),
                () -> assertThat(information.getWriter().id()).isEqualTo(host.getId()),
                () -> assertThat(information.getWriter().nickname()).isEqualTo(host.getNicknameValue())
        );

        final int totalSize = members.size();
        List<Long> ids = members.stream()
                .map(Member::getId)
                .toList();
        List<String> nicknames = members.stream()
                .map(Member::getNicknameValue)
                .toList();

        List<CommentInformation> comments = information.getComments();
        assertAll(
                () -> assertThat(comments).hasSize(totalSize),
                () -> assertThat(comments)
                        .map(CommentInformation::getWriter)
                        .map(StudyMember::id)
                        .containsAll(ids),
                () -> assertThat(comments)
                        .map(CommentInformation::getWriter)
                        .map(StudyMember::nickname)
                        .containsAll(nicknames)
        );
    }
}
