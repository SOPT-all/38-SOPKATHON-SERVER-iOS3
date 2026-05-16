package org.sopt.domain.reflection.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회고 기록 생성 요청")
public record ReflectionCreateRequest(

        @Schema(description = "이모지 인덱스", example = "2")
        @NotNull(message = "감정 상태는 필수입니다.")
        Integer emojiIndex,

        @Schema(description = "회고 내용", example = "앞으로 코드 리뷰를 꼼꼼히 하겠다.")
        @NotBlank(message = "회고 내용은 필수입니다.")
        String content
) {}
