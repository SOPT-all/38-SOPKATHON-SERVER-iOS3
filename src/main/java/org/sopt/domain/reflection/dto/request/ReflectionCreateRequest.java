package org.sopt.domain.reflection.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReflectionCreateRequest(

        @NotNull(message = "감정 상태는 필수입니다.")
        Integer emojiIndex,

        @NotBlank(message = "회고 내용은 필수입니다.")
        String content
) {}
