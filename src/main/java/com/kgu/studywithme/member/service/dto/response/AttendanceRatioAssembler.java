package com.kgu.studywithme.member.service.dto.response;

import com.kgu.studywithme.member.infra.query.dto.response.AttendanceRatio;

import java.util.List;

public record AttendanceRatioAssembler(List<AttendanceRatio> result) {
}
