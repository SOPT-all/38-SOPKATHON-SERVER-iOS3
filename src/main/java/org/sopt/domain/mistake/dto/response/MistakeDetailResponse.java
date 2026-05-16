package org.sopt.domain.mistake.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.domain.reflection.entity.Reflection;

public record MistakeDetailResponse(
        Long mistakeId,
        String imageUrl,
        String content,
        LocalDate date,
        boolean hasReflection,
        ReflectionInfo reflection
) {

    public record ReflectionInfo(
            Long reflectionId,
            String content,
            int emojiIndex,
            LocalDateTime createdAt
    ) {
        public static ReflectionInfo from(Reflection reflection) {

            return new ReflectionInfo(
                    reflection.getId(),
                    reflection.getContent(),
                    reflection.getEmojiIndex(),
                    reflection.getCreatedAt()
            );
        }
    }

    public static MistakeDetailResponse of(Mistake mistake, Reflection reflection) {
        return new MistakeDetailResponse(
                mistake.getId(),
                mistake.getImageUrl(),
                mistake.getContent(),
                mistake.getDate(),
                reflection != null,
                reflection != null ? ReflectionInfo.from(reflection) : null
        );
    }
}
