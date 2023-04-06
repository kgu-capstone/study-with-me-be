package com.kgu.studywithme.study.infra.query;

import com.kgu.studywithme.study.infra.query.dto.response.CommentInformation;
import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;
import com.kgu.studywithme.study.infra.query.dto.response.QCommentInformation;
import com.kgu.studywithme.study.infra.query.dto.response.QNoticeInformation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kgu.studywithme.member.domain.QMember.member;
import static com.kgu.studywithme.study.domain.notice.QNotice.notice;
import static com.kgu.studywithme.study.domain.notice.comment.QComment.comment;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyInformationQueryRepositoryImpl implements StudyInformationQueryRepository{
    private final JPAQueryFactory query;

    @Override
    public List<NoticeInformation> findNoticeWithCommentsByStudyId(Long studyId) {
        List<NoticeInformation> noticeResult = query
                .select(new QNoticeInformation(
                        notice.id, notice.title, notice.content, notice.createdAt, notice.modifiedAt,
                        member.id, member.nickname))
                .from(notice)
                .innerJoin(notice.writer, member)
                .orderBy(notice.id.desc())
                .fetch();

        applyCommentsInNotice(noticeResult);
        return noticeResult;
    }

    private void applyCommentsInNotice(List<NoticeInformation> noticeResult) {
        List<CommentInformation> commentResult = query
                .select(new QCommentInformation(comment.id, comment.notice.id, comment.content, member.id, member.nickname))
                .from(comment)
                .innerJoin(comment.writer, member)
                .fetch();

        noticeResult.forEach(notice -> {
            notice.setComments(
                    commentResult.stream()
                            .filter(comment -> comment.getNoticeId().equals(notice.getId()))
                            .toList()
            );
        });
    }
}
