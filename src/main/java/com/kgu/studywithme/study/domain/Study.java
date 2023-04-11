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
import com.kgu.studywithme.study.domain.hashtag.Hashtag;
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

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<Hashtag> hashtags = new ArrayList<>();

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
        this.closed = false;
        this.assignments = Assignments.createAssignmentsPage();
        this.reviews = Reviews.createReviewsPage();
        applyHashtags(hashtags);
    }

    public static Study createOnlineStudy(Member host, StudyName name, Description description, Capacity capacity,
                                    Category category, StudyType type, Set<String> hashtags) {
        return new Study(host, name, description, capacity, category, type, null, hashtags);
    }

    public static Study createOfflineStudy(Member host, StudyName name, Description description, Capacity capacity,
                                          Category category, StudyType type, StudyArea area, Set<String> hashtags) {
        return new Study(host, name, description, capacity, category, type, area, hashtags);
    }

    public void applyHashtags(Set<String> hashtags) {
        this.hashtags.clear();
        this.hashtags.addAll(
                hashtags.stream()
                        .map(value -> Hashtag.applyHashtag(this, value))
                        .toList()
        );
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
        participants.delegateStudyHostAuthority(this, newHost);
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

    public List<String> getHashtags() {
        return hashtags.stream()
                .map(Hashtag::getName)
                .toList();
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

    public int getMaxMembers() {
        return participants.getCapacity().getValue();
    }

    public List<Review> getReviews() {
        return reviews.getReviews();
    }

    private void setName(String name) {
        this.name = StudyName.from(name);
    }

    private void setDescription(String description) {
        this.description = Description.from(description);
    }

    private void setRecruitmentStatus(String recruitmentStatus) {
        if (recruitmentStatus.equals(IN_PROGRESS.getDescription())) {
            this.recruitmentStatus = IN_PROGRESS;
        } else {
            this.recruitmentStatus = COMPLETE;
        }
    }

    private void setCapacity(Integer capacity) {
        this.participants.changeCapacity(capacity);
    }

    private void setTypeAndArea(String type, String province, String city) {
        if (type.equals(StudyType.ONLINE.getDescription())) {
            this.type = StudyType.ONLINE;
            this.area = null;
        } else {
            this.type = StudyType.OFFLINE;
            this.area = StudyArea.of(province, city);
        }
    }

    private void setCategory(Long category) {
        this.category = Category.from(category);
    }

    public void change(String name, String description, Integer capacity, Long category, String type,
                       String province, String city, String recruitmentStatus, Set<String> hashtags) {
        setName(name);
        setDescription(description);
        setCategory(category);
        setTypeAndArea(type, province, city);
        setCapacity(capacity);
        setRecruitmentStatus(recruitmentStatus);
        applyHashtags(hashtags);
    }
}
