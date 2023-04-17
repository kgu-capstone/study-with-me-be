package com.kgu.studywithme.upload.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.global.dto.SimpleResponseWrapper;
import com.kgu.studywithme.upload.controller.dto.request.ImageUploadRequest;
import com.kgu.studywithme.upload.utils.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies/{studyId}/weeks/{week}/image")
public class ImageUploadApiController {
    private final FileUploader uploader;

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SimpleResponseWrapper<String>> uploadImage(@PathVariable Long studyId,
                                                                     @PathVariable int week,
                                                                     @ExtractPayload Long memberId,
                                                                     @ModelAttribute @Valid ImageUploadRequest request) {
        String uploadLink = uploader.uploadWeeklyImage(studyId, week, memberId, request.file());
        return ResponseEntity.ok(new SimpleResponseWrapper<>(uploadLink));
    }
}
