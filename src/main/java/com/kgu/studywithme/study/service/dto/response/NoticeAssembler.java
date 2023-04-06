package com.kgu.studywithme.study.service.dto.response;

import com.kgu.studywithme.study.infra.query.dto.response.NoticeInformation;

import java.util.List;

public record NoticeAssembler(List<NoticeInformation> result) {
}
