package com.kgu.studywithme.study.controller.attendance;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.global.annotation.ValidStudyParticipant;
import com.kgu.studywithme.study.controller.dto.request.AttendanceRequest;
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

    @ValidStudyParticipant
    @PatchMapping
    public ResponseEntity<Void> manualCheckAttendance(@PathVariable Long studyId,
                                                      @PathVariable Long memberId,
                                                      @RequestBody @Valid AttendanceRequest request,
                                                      @ExtractPayload Long hostId) {
        attendanceService.manualCheckAttendance(studyId, memberId, hostId, request.status(), request.week());
        return ResponseEntity.noContent().build();
    }
}
