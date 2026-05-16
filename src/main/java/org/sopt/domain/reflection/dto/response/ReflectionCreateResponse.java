package org.sopt.domain.reflection.dto.response;

import java.time.LocalDateTime;
import org.sopt.domain.reflection.entity.Reflection;

public record ReflectionCreateResponse(
        Long reflectionId,
        Long mistakeId,
        int emojiIndex,
        String content,
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
