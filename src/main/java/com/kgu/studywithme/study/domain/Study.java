package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.post.domain.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study")
public class Study {
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
    private Set<String> hashtags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private StudyStatus status;

    @OneToMany(mappedBy = "member")
    private Set<StudyMember> studyMembers = new HashSet<>();

    int currentMembers;

    @OneToMany(mappedBy = "study")
    private ArrayList<Post> postList = new ArrayList<>();


}
