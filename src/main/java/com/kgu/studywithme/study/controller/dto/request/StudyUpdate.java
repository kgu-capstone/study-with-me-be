package com.kgu.studywithme.study.controller.dto.request;

import com.kgu.studywithme.study.domain.Study;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static com.kgu.studywithme.study.domain.StudyType.ONLINE;

public record StudyUpdate(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "설명은 필수입니다.")
        String description,

        @NotNull(message = "참여인원은 필수입니다.")
        Integer capacity,

        @NotNull(message = "카테고리는 필수입니다.")
        Long category,

        @NotBlank(message = "온/오프라인 유무는 필수입니다.")
        String type,

        String province,

        String city,

        String recruitmentStatus,

        Set<String> hashtags
) {
        @Builder
        public StudyUpdate {}

        public static StudyUpdate buildStudyUpdate(Study study) {
                if (study.getType().equals(ONLINE)) {
                        return createStudyUpdateOnline(study);
                } else {
                        return createStudyUpdateOffline(study);
                }
        }

        public static StudyUpdate createStudyUpdateOnline(Study study) {
                return StudyUpdate.builder()
                        .name(study.getNameValue())
                        .description(study.getDescriptionValue())
                        .capacity(study.getCapacity().getValue())
                        .category(study.getCategory().getId())
                        .type(study.getType().getDescription())
                        .province(null)
                        .city(null)
                        .recruitmentStatus(study.getRecruitmentStatus().getDescription())
                        .hashtags(Set.copyOf(study.getHashtags()))
                        .build();
        }

        public static StudyUpdate createStudyUpdateOffline(Study study) {
                return StudyUpdate.builder()
                        .name(study.getNameValue())
                        .description(study.getDescriptionValue())
                        .capacity(study.getCapacity().getValue())
                        .category(study.getCategory().getId())
                        .type(study.getType().getDescription())
                        .province(study.getArea().getProvince())
                        .city(study.getArea().getCity())
                        .recruitmentStatus(study.getRecruitmentStatus().getDescription())
                        .hashtags(Set.copyOf(study.getHashtags()))
                        .build();
        }
}
