package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.assignment.Assignment;
import com.kgu.studywithme.study.domain.assignment.Assignments;
import com.kgu.studywithme.study.domain.assignment.Period;
import com.kgu.studywithme.study.domain.attendance.Attendance;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.domain.notice.Notice;
import com.kgu.studywithme.study.domain.participant.Capacity;
import com.kgu.studywithme.study.domain.participant.Participants;
import com.kgu.studywithme.study.domain.review.Review;
import com.kgu.studywithme.study.domain.review.Reviews;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kgu.studywithme.study.domain.RecruitmentStatus.COMPLETE;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study")
public class Study extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StudyName name;

    @Embedded
    private Description description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_type", nullable = false, updatable = false)
    private StudyType type;

    @Embedded
    private StudyArea area;

    @Enumerated(EnumType.STRING)
    @Column(name = "recruitment_status", nullable = false)
    private RecruitmentStatus recruitmentStatus;

    @Embedded
    private Participants participants;

    @Embedded
    private Assignments assignments;

    @Embedded
    private Reviews reviews;

    @Column(name = "is_closed", nullable = false)
    private boolean closed;

    @ElementCollection
    @CollectionTable(
            name = "hashtag",
            joinColumns = @JoinColumn(name = "study_id", referencedColumnName = "id")
    )
    @Column(name = "hashtag")
    private Set<String> hashtags = new HashSet<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Attendance> attendances = new ArrayList<>();

    @Builder
    private Study(Member host, StudyName name, Description description, Capacity capacity,
                  Category category, StudyType type, StudyArea area, Set<String> hashtags) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.type = type;
        this.area = area;
        this.recruitmentStatus = IN_PROGRESS;
        this.participants = Participants.of(host, capacity);
        this.hashtags = hashtags;
        this.closed = false;
        this.assignments = Assignments.createAssignmentsPage();
        this.reviews = Reviews.createReviewsPage();
    }

    public static Study createOnlineStudy(Member host, StudyName name, Description description, Capacity capacity,
                                    Category category, StudyType type, Set<String> hashtags) {
        return new Study(host, name, description, capacity, category, type, null, hashtags);
    }

    public static Study createOfflineStudy(Member host, StudyName name, Description description, Capacity capacity,
                                          Category category, StudyType type, StudyArea area, Set<String> hashtags) {
        return new Study(host, name, description, capacity, category, type, area, hashtags);
    }

    public void completeRecruitment() {
        this.recruitmentStatus = COMPLETE;
    }

    public void close() {
        this.closed = true;
    }

    public void addNotice(String title, String content) {
        notices.add(Notice.writeNotice(this, title, content));
    }

    public void recordAttendance(int week, AttendanceStatus status, Member participant) {
        validateMemberIsParticipant(participant);
        attendances.add(Attendance.recordAttendance(week, status, this, participant));
    }

    public void registerAssignment(int week, Period period, Member creator, String title, String content) {
        validateMemberIsParticipant(creator);
        assignments.registerAssignment(Assignment.createAssignment(week, period, this, creator, title, content));
    }

    public void validateMemberIsParticipant(Member participant) {
        participants.validateMemberIsParticipant(participant);
    }

    public void writeReview(Member writer, String content) {
        validateMemberIsStudyGraduate(writer);
        reviews.writeReview(Review.writeReview(this, writer, content));
    }

    private void validateMemberIsStudyGraduate(Member writer) {
        participants.validateMemberIsStudyGraduate(writer);
    }

    public void delegateStudyHostAuthority(Member newHost) {
        validateStudyIsProceeding();
        participants.delegateStudyHostAuthority(newHost);
    }

    public void applyParticipation(Member member) {
        validateRecruitmentIsProceeding();
        participants.apply(this, member);
    }

    public void approveParticipation(Member member) {
        validateStudyIsProceeding();
        participants.approve(member);
    }

    public void rejectParticipation(Member member) {
        validateStudyIsProceeding();
        participants.reject(member);
    }

    public void cancelParticipation(Member participant) {
        validateStudyIsProceeding();
        participants.cancel(participant);
    }

    public void graduateParticipant(Member participant) {
        validateStudyIsProceeding();
        participants.graduate(participant);
    }

    private void validateRecruitmentIsProceeding() {
        validateStudyIsProceeding();
        validateRecruitmentStatus();
    }

    private void validateStudyIsProceeding() {
        if (closed) {
            throw StudyWithMeException.type(StudyErrorCode.ALREADY_CLOSED);
        }
    }

    private void validateRecruitmentStatus() {
        if (isRecruitmentComplete()) {
            throw StudyWithMeException.type(StudyErrorCode.RECRUITMENT_IS_COMPLETE);
        }
    }

    public boolean isRecruitmentComplete() {
        return recruitmentStatus == COMPLETE;
    }

    public void validateMemberIsApplier(Member member) {
        participants.validateMemberIsApplier(member);
    }

    // Add Getter
    public String getNameValue() {
        return name.getValue();
    }

    public String getDescriptionValue() {
        return description.getValue();
    }

    public Member getHost() {
        return participants.getHost();
    }

    public List<Member> getParticipants() {
        return participants.getParticipants();
    }

    public List<Member> getApplier() {
        return participants.getApplier();
    }

    public List<Member> getApproveParticipants() {
        return participants.getApproveParticipants();
    }

    public List<Member> getGraduatedParticipants() {
        return participants.getGraduatedParticipants();
    }

    public Capacity getCapacity() {
        return participants.getCapacity();
    }
}
