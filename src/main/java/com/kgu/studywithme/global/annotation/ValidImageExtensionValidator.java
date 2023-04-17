package com.kgu.studywithme.global.annotation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidImageExtensionValidator implements ConstraintValidator<ValidImageExtension, MultipartFile> {
    private final List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        if (isNotAllowedExtendsion(file)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("이미지는 jpg, jpeg, png, gif만 허용합니다.")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isNotAllowedExtendsion(MultipartFile file) {
        String extension = getExtention(file);
        return !allowedExtensions.contains(extension);
    }

    private String getExtention(MultipartFile file) {
        String uploadName = file.getOriginalFilename();
        return uploadName.substring(uploadName.lastIndexOf(".") + 1);
    }
}
