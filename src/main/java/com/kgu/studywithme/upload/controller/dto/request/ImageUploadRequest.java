package com.kgu.studywithme.upload.controller.dto.request;

import com.kgu.studywithme.upload.utils.validator.ValidImageExtension;
import com.kgu.studywithme.upload.utils.validator.ValidImageUploadType;
import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequest(
        @ValidImageUploadType
        String type,

        @ValidImageExtension
        MultipartFile file
) {
}
