package org.sopt.domain.mistake.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.domain.reflection.entity.Reflection;

@Schema(description = "실수 카드 상세 응답")
public record MistakeDetailResponse(
        @Schema(description = "실수 카드 ID", example = "1")
        Long mistakeId,
        @Schema(description = "실수 이미지 URL", example = "https://example.com/images/mistake.jpg")
        String imageUrl,
        @Schema(description = "실수 제목", example = "변수명 오타")
        String title,
        @Schema(description = "실수 내용", example = "userId를 usreId로 잘못 입력했다.")
        String content,
        @Schema(description = "실수 발생 날짜", example = "2026-05-17")
        LocalDate date,
        @Schema(description = "회고 기록 존재 여부", example = "true")
        boolean hasReflection,
        @Schema(description = "회고 기록 정보 (없으면 null)")
        ReflectionInfo reflection
) {

    @Schema(description = "회고 기록 정보")
    public record ReflectionInfo(
            @Schema(description = "회고 기록 ID", example = "10")
            Long reflectionId,
            @Schema(description = "회고 내용", example = "앞으로 코드 리뷰를 꼼꼼히 하겠다.")
            String content,
            @Schema(description = "이모지 인덱스", example = "2")
            int emojiIndex,
            @Schema(description = "회고 기록 생성일시", example = "2026-05-17T14:30:00")
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
                mistake.getTitle(),
                mistake.getContent(),
                mistake.getDate(),
                reflection != null,
                reflection != null ? ReflectionInfo.from(reflection) : null
        );
    }
}
