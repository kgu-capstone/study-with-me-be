DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS study_review;
DROP TABLE IF EXISTS study_participant;
DROP TABLE IF EXISTS study_notice_comment;
DROP TABLE IF EXISTS study_notice;
DROP TABLE IF EXISTS study_hashtag;
DROP TABLE IF EXISTS study_attendance;
DROP TABLE IF EXISTS study_assignment_submit;
DROP TABLE IF EXISTS study_week_attachment;
DROP TABLE IF EXISTS study_week;
DROP TABLE IF EXISTS study;
DROP TABLE IF EXISTS member_token;
DROP TABLE IF EXISTS member_review;
DROP TABLE IF EXISTS member_report;
DROP TABLE IF EXISTS member_interest;
DROP TABLE IF EXISTS member;

CREATE TABLE IF NOT EXISTS member
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    nickname        VARCHAR(100) NOT NULL UNIQUE,
    email           VARCHAR(150) NOT NULL UNIQUE,
    birth           DATE         NOT NULL,
    phone           VARCHAR(13)  NOT NULL UNIQUE,
    gender          VARCHAR(6)   NOT NULL,
    province        VARCHAR(100) NOT NULL,
    city            VARCHAR(100) NOT NULL,
    score           INT          NOT NULL,
    is_email_opt_in TINYINT(1)   NOT NULL,
    created_at      DATETIME     NOT NULL,
    modified_at     DATETIME     NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_interest
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT      NOT NULL,
    category  VARCHAR(20) NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_report
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    reportee_id BIGINT      NOT NULL,
    reporter_id BIGINT      NOT NULL,
    reason      TEXT        NOT NULL,
    status      VARCHAR(10) NOT NULL,
    created_at  DATETIME    NOT NULL,
    modified_at DATETIME    NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_review
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    reviewee_id BIGINT       NOT NULL,
    reviewer_id BIGINT       NOT NULL,
    content     VARCHAR(255) NOT NULL,
    created_at  DATETIME     NOT NULL,
    modified_at DATETIME     NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_token
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT       NOT NULL,
    refresh_token VARCHAR(150) NOT NULL UNIQUE
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id              BIGINT       NOT NULL,
    name                 VARCHAR(100) NOT NULL UNIQUE,
    description          TEXT         NOT NULL,
    category             VARCHAR(20)  NOT NULL,
    image                VARCHAR(100) NOT NULL,
    study_type           VARCHAR(10)  NOT NULL,
    province             VARCHAR(100),
    city                 VARCHAR(100),
    recruitment_status   VARCHAR(12)  NOT NULL,
    capacity             INT          NOT NULL,
    is_closed            TINYINT(1)   NOT NULL,
    minimum_attendance   INT          NOT NULL,
    policy_update_chance INT          NOT NULL,
    created_at           DATETIME     NOT NULL,
    modified_at          DATETIME     NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_week
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id             BIGINT       NOT NULL,
    creator_id           BIGINT       NOT NULL,
    week                 INT          NOT NULL,
    title                VARCHAR(255) NOT NULL,
    content              TEXT         NOT NULL,
    start_date           DATETIME     NOT NULL,
    end_date             DATETIME     NOT NULL,
    is_assignment_exists TINYINT(1)   NOT NULL,
    is_auto_attendance   TINYINT(1)   NOT NULL,
    created_at           DATETIME     NOT NULL,
    modified_at          DATETIME     NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_week_attachment
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    week_id          BIGINT       NOT NULL,
    upload_file_name VARCHAR(200) NOT NULL,
    link             VARCHAR(200) NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_assignment_submit
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    week_id          BIGINT       NOT NULL,
    participant_id   BIGINT       NOT NULL,
    upload_type      VARCHAR(10)  NOT NULL,
    upload_file_name VARCHAR(200),
    link             VARCHAR(255) NOT NULL,
    created_at       DATETIME     NOT NULL,
    modified_at      DATETIME     NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_attendance
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id       BIGINT      NOT NULL,
    week           INT         NOT NULL,
    participant_id BIGINT      NOT NULL,
    status         VARCHAR(15) NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_hashtag
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id BIGINT       NOT NULL,
    name     VARCHAR(100) NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_notice
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id    BIGINT       NOT NULL,
    writer_id   BIGINT       NOT NULL,
    title       VARCHAR(100) NOT NULL,
    content     TEXT         NOT NULL,
    created_at  DATETIME     NOT NULL,
    modified_at DATETIME     NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_notice_comment
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    notice_id   BIGINT   NOT NULL,
    writer_id   BIGINT   NOT NULL,
    content     TEXT     NOT NULL,
    created_at  DATETIME NOT NULL,
    modified_at DATETIME NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_participant
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id    BIGINT      NOT NULL,
    member_id   BIGINT      NOT NULL,
    status      VARCHAR(10) NOT NULL,
    created_at  DATETIME    NOT NULL,
    modified_at DATETIME    NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS study_review
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id    BIGINT       NOT NULL,
    writer_id   BIGINT       NOT NULL,
    content     VARCHAR(255) NOT NULL,
    created_at  DATETIME     NOT NULL,
    modified_at DATETIME     NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS favorite
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT   NOT NULL,
    study_id    BIGINT   NOT NULL,
    created_at  DATETIME NOT NULL,
    modified_at DATETIME NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE member_interest
    ADD CONSTRAINT interest_member_member_id_fk
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE member_report
    ADD CONSTRAINT report_member_reportee_id_fk
        FOREIGN KEY (reportee_id)
            REFERENCES member (id);

ALTER TABLE member_report
    ADD CONSTRAINT report_member_reporter_id_fk
        FOREIGN KEY (reporter_id)
            REFERENCES member (id);

ALTER TABLE member_review
    ADD CONSTRAINT review_member_reviewee_id_fk
        FOREIGN KEY (reviewee_id)
            REFERENCES member (id);

ALTER TABLE member_review
    ADD CONSTRAINT review_member_reviewer_id_fk
        FOREIGN KEY (reviewer_id)
            REFERENCES member (id);

ALTER TABLE member_token
    ADD CONSTRAINT token_member_member_id_fk
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE study
    ADD CONSTRAINT study_member_host_id_fk
        FOREIGN KEY (host_id)
            REFERENCES member (id);

ALTER TABLE study_week
    ADD CONSTRAINT week_study_study_id_fk
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_week
    ADD CONSTRAINT week_member_creator_id_fk
        FOREIGN KEY (creator_id)
            REFERENCES member (id);

ALTER TABLE study_assignment_submit
    ADD CONSTRAINT assignment_submit_week_week_id_fk
        FOREIGN KEY (week_id)
            REFERENCES study_week (id);

ALTER TABLE study_assignment_submit
    ADD CONSTRAINT assignment_submit_member_participant_id_fk
        FOREIGN KEY (participant_id)
            REFERENCES member (id);

ALTER TABLE study_attendance
    ADD CONSTRAINT attendance_study_study_id_fk
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_attendance
    ADD CONSTRAINT attendance_member_participant_id_fk
        FOREIGN KEY (participant_id)
            REFERENCES member (id);

ALTER TABLE study_hashtag
    ADD CONSTRAINT hashtag_study_study_id_fk
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_notice
    ADD CONSTRAINT notice_study_study_id_fk
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_notice
    ADD CONSTRAINT notice_member_writer_id_fk
        FOREIGN KEY (writer_id)
            REFERENCES member (id);

ALTER TABLE study_notice_comment
    ADD CONSTRAINT notice_comment_notice_notice_id_fk
        FOREIGN KEY (notice_id)
            REFERENCES study_notice (id);

ALTER TABLE study_notice_comment
    ADD CONSTRAINT notice_comment_member_writer_id_fk
        FOREIGN KEY (writer_id)
            REFERENCES member (id);

ALTER TABLE study_participant
    ADD CONSTRAINT participant_study_study_id_fk
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_participant
    ADD CONSTRAINT participant_member_member_id_fk
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE study_review
    ADD CONSTRAINT review_study_study_id_fk
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE study_review
    ADD CONSTRAINT review_member_writer_id_fk
        FOREIGN KEY (writer_id)
            REFERENCES member (id);

ALTER TABLE favorite
    ADD CONSTRAINT favorite_member_member_id_fk
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE favorite
    ADD CONSTRAINT favorite_study_study_id_fk
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE favorite
    ADD CONSTRAINT favorite_study_id_member_id_unique
        UNIQUE (member_id, study_id);
