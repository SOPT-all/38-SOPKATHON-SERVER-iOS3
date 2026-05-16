package org.sopt.domain.mistake.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MistakeCreateRequest(

        @NotBlank(message = "이미지 오브젝트 키는 필수입니다.")
        String imageObjectKey,

        @NotBlank(message = "실수 제목은 필수입니다.")
        String title,

        @NotBlank(message = "실수 내용은 필수입니다.")
        String content
) {}
