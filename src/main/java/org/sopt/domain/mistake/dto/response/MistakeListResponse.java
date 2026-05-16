package org.sopt.domain.mistake.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.sopt.domain.mistake.entity.Mistake;

public record MistakeListResponse(
        List<MistakeItem> items,
        Long nextCursor,
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

    public record MistakeItem(
            Long mistakeId,
            String imageUrl,
            LocalDate date,
            boolean hasReflection,
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
