package com.kgu.studywithme.post.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyMember;
import com.kgu.studywithme.study.domain.StudyMemberStatus;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.exception.StudyMemberErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "post")
public class Post { // 공지사항

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyMember_id")
    private StudyMember studyMember;

    @Lob
    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @Column(name = "lastModifiedDate")
    private LocalDate lastModifiedDate;

    // 게시글을 삭제하면 달려있는 댓글 모두 삭제
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArrayList<Comment> commentList = new ArrayList<>();

    @Builder
    public Post(String title, Study study, String content, StudyMember studyMember) {
        this.title = title;
        this.studyMember = studyMember;
        this.content = content;
        this.writer = writer;

        this.lastModifiedDate = LocalDate.now();
    }

    public static Post createPost(String title, Study study, String content, StudyMember studyMember) {
        // 이 검사를 여기서 해야할지, Study.addPost에서 해야 할지 모르겠어요
        if (studyMember.getStatus() != StudyMemberStatus.ADMIN)
            throw StudyWithMeException.type(StudyMemberErrorCode.ONLY_ADMIN_ACCESS);

        return new Post(title, study, content, studyMember);
    }

    public void addComment(Comment comment){
        //comment의 Post 설정은 comment에서 함
        commentList.add(comment);
    }

}
