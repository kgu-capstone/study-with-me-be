package com.kgu.studywithme.upload.controller.dto.request;

import com.kgu.studywithme.upload.utils.validator.ValidImageExtension;
import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequest(
        @ValidImageExtension
        MultipartFile file
) {
}
