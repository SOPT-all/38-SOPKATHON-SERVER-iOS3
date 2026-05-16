package org.sopt.domain.mistake.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.sopt.domain.mistake.entity.Mistake;

@Schema(description = "실수 카드 목록 응답")
public record MistakeListResponse(
        @Schema(description = "실수 카드 목록")
        List<MistakeItem> items,
        @Schema(description = "다음 페이지 커서 (마지막 페이지면 null)", example = "42")
        Long nextCursor,
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {

    public static MistakeListResponse of(
            List<Mistake> mistakes,
            Map<Long, Integer> reflectionEmojiIndexes,
            boolean hasNext
    ) {
        List<MistakeItem> items = mistakes.stream()
                .map(mistake -> MistakeItem.of(mistake, reflectionEmojiIndexes.get(mistake.getId())))
                .toList();

        Long nextCursor = hasNext ? items.getLast().mistakeId() : null;

        return new MistakeListResponse(items, nextCursor, hasNext);
    }

    @Schema(description = "실수 카드 목록 항목")
    public record MistakeItem(
            @Schema(description = "실수 카드 ID", example = "1")
            Long mistakeId,
            @Schema(description = "실수 이미지 URL", example = "https://example.com/images/mistake.jpg")
            String imageUrl,
            @Schema(description = "실수 발생 날짜", example = "2026-05-17")
            LocalDate date,
            @Schema(description = "회고 기록 존재 여부", example = "false")
            boolean hasReflection,
            @Schema(description = "이모지 인덱스 (회고 없으면 null)", example = "3")
            Integer emojiIndex
    ) {

        public static MistakeItem of(Mistake mistake, Integer emojiIndex) {
            return new MistakeItem(
                    mistake.getId(),
                    mistake.getImageUrl(),
                    mistake.getDate(),
                    emojiIndex != null,
                    emojiIndex
            );
        }
    }
}
