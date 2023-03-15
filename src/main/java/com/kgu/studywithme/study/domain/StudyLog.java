package com.kgu.studywithme.study.domain;

import javax.persistence.*;
import java.time.LocalDate;

public class StudyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany
    private Study study;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    private LocalDate date;
}
