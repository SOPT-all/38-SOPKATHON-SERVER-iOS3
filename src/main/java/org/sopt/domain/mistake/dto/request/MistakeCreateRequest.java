package org.sopt.domain.mistake.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "실수 카드 생성 요청")
public record MistakeCreateRequest(

        @Schema(description = "S3 이미지 오브젝트 키", example = "images/2026/05/17/abc123.jpg")
        @NotBlank(message = "이미지 오브젝트 키는 필수입니다.")
        String imageObjectKey,

        @Schema(description = "실수 제목", example = "변수명 오타")
        @NotBlank(message = "실수 제목은 필수입니다.")
        String title,

        @Schema(description = "실수 내용", example = "userId를 usreId로 잘못 입력했다.")
        @NotBlank(message = "실수 내용은 필수입니다.")
        String content
) {}
