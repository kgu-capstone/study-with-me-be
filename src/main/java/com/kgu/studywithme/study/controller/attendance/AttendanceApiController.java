package com.kgu.studywithme.study.controller.attendance;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.study.controller.dto.request.AttendanceRequest;
import com.kgu.studywithme.study.service.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/attendance/{participantId}")
public class AttendanceApiController {
    private final AttendanceService attendanceService;

    @PatchMapping
    public ResponseEntity<Void> manualCheckAttendance(@PathVariable Long studyId,
                                                 @PathVariable Long participantId,
                                                 @RequestBody @Valid AttendanceRequest request,
                                                 @ExtractPayload Long hostId) {
        attendanceService.manualCheckAttendance(studyId, participantId, hostId, request.status(), request.week());
        return ResponseEntity.noContent().build();
    }
}
