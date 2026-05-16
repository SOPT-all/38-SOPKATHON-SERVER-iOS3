package org.sopt.domain.reflection.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.sopt.domain.reflection.entity.Reflection;

@Schema(description = "회고 기록 생성 응답")
public record ReflectionCreateResponse(
        @Schema(description = "회고 기록 ID", example = "10")
        Long reflectionId,
        @Schema(description = "연관된 실수 카드 ID", example = "1")
        Long mistakeId,
        @Schema(description = "이모지 인덱스", example = "2")
        int emojiIndex,
        @Schema(description = "회고 내용", example = "앞으로 코드 리뷰를 꼼꼼히 하겠다.")
        String content,
        @Schema(description = "회고 기록 생성일시", example = "2026-05-17T14:30:00")
        LocalDateTime createdAt
) {

    public static ReflectionCreateResponse from(Reflection reflection) {
        return new ReflectionCreateResponse(
                reflection.getId(),
                reflection.getMistake().getId(),
                reflection.getEmojiIndex(),
                reflection.getContent(),
                reflection.getCreatedAt()
        );
    }
}
