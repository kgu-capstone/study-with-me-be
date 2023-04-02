package com.kgu.studywithme.member.service.dto.response;

import com.kgu.studywithme.study.infra.query.dto.response.SimpleStudy;

import java.util.List;

public record RelatedStudy(List<SimpleStudy> result) {
}
