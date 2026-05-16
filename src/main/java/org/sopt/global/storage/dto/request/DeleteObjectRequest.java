package org.sopt.global.storage.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteObjectRequest(
        @NotBlank(message = "객체 키는 필수입니다.")
        String objectKey
) {
}
