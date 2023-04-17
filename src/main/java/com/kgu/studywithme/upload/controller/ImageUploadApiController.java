package com.kgu.studywithme.upload.controller;

import com.kgu.studywithme.global.annotation.ExtractPayload;
import com.kgu.studywithme.global.dto.SimpleResponseWrapper;
import com.kgu.studywithme.upload.controller.dto.request.ImageUploadRequest;
import com.kgu.studywithme.upload.utils.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageUploadApiController {
    private final FileUploader uploader;

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SimpleResponseWrapper<String>> uploadImage(@ExtractPayload Long memberId,
                                                                     @ModelAttribute @Valid ImageUploadRequest request) {
        String uploadLink = uploader.uploadWeeklyImage(request.file());
        return ResponseEntity.ok(new SimpleResponseWrapper<>(uploadLink));
    }
}
