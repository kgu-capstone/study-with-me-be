package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.post.domain.Post;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study")
public class Study {

    public final int MAXMEMBER = 10;
    public final int MAXDESCRIPTION = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "onoff", nullable = false)
    private OnOff onOff;

    @Enumerated(EnumType.STRING)
    private Color color;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "hashtags",
            joinColumns = @JoinColumn(name = "study_id")
    )
    @Column(name = "hashtag") // tag, keyword
    private ArrayList<String> hashtags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private StudyStatus status;

    @OneToMany(mappedBy = "member")
    private ArrayList<StudyMember> studyMembers = new ArrayList<>();

    int currentMembers;

    // 공지사항
    @OneToMany(mappedBy = "study")
    private ArrayList<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    private ArrayList<StudyLog> studyLogList = new ArrayList<>();

    @Builder
    public Study(String name, String description, Category category, OnOff onOff, Color color, ArrayList<String> hashtags, StudyStatus status) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.onOff = onOff;
        this.color = color;
        this.hashtags = hashtags;
        this.status = status;

        currentMembers = 0;
    }

    // 생성 메서드
    public static Study createStudy(String name, String description, Category category, OnOff onOff, Color color, ArrayList<String> hashtags, StudyStatus status) {
        return new Study(name, description, category, onOff, color, hashtags, status);
    }

    // 추가 메서드
    public void addStudyMember(StudyMember studyMember) {
        if (this.status != StudyStatus.REQUIREMENT)
            throw StudyWithMeException.type(StudyErrorCode.STUDY_NOT_REQUIREMENT_CAPACITY);

        if (currentMembers > MAXMEMBER)
            throw StudyWithMeException.type(StudyErrorCode.STUDY_OVER_CAPACITY);
        this.studyMembers.add(studyMember);
        currentMembers++;

        if (currentMembers == MAXMEMBER) {
            this.status = StudyStatus.PROGRESS;
        }
    }
    
    public void addStudyLog(StudyLog studyLog) {
        if (this.status == StudyStatus.END)
            throw StudyWithMeException.type(StudyErrorCode.STUDY_ALREADY_END);

        this.studyLogList.add(studyLog);
    }

    public void addPost(Post post) {
        if (this.status == StudyStatus.END)
            throw StudyWithMeException.type(StudyErrorCode.STUDY_ALREADY_END);

        this.postList.add(post);
    }

    // 비교하지말고 전체 바꾸기 위해서
    public void changeHashtags(ArrayList<String> hashtags) {
        // 중복 제거
        HashSet<String> hashSet = new HashSet<>(hashtags);
        this.hashtags = new ArrayList<String>(hashSet);
    }

    public void changeDescription(String description) {
        if (description.length() > MAXDESCRIPTION) {
            throw StudyWithMeException.type(StudyErrorCode.STRING_OUT_OF_BOUNDARY);
        }
        this.description = description;
    }


}
