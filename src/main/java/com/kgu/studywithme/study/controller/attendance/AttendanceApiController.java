package com.kgu.studywithme.study.controller.attendance;

import com.kgu.studywithme.auth.utils.ExtractPayload;
import com.kgu.studywithme.global.aop.CheckStudyHost;
import com.kgu.studywithme.study.controller.dto.request.AttendanceRequest;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import com.kgu.studywithme.study.service.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/attendance/{memberId}")
public class AttendanceApiController {
    private final AttendanceService attendanceService;

    @CheckStudyHost
    @PatchMapping
    public ResponseEntity<Void> manualCheckAttendance(@ExtractPayload Long hostId,
                                                      @PathVariable Long studyId,
                                                      @PathVariable Long memberId,
                                                      @RequestBody @Valid AttendanceRequest request) {
        attendanceService.manualCheckAttendance(studyId, memberId, request.week(), AttendanceStatus.fromDescription(request.status()));
        return ResponseEntity.noContent().build();
    }
}
