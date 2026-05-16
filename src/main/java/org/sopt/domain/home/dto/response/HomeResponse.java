package org.sopt.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.sopt.domain.user.entity.User;

@Schema(description = "홈 화면 응답")
public record HomeResponse(
        @Schema(description = "유저 정보")
        UserInfo user,
        @Schema(description = "날짜별 실수 기록 목록")
        List<MistakeDateItem> dates
) {

    public static HomeResponse of(User user, Map<LocalDate, Boolean> dates) {
        List<MistakeDateItem> dateItems = dates.entrySet()
                .stream()
                .map(entry -> MistakeDateItem.of(entry.getKey(), entry.getValue()))
                .toList();

        return new HomeResponse(UserInfo.from(user), dateItems);
    }

    @Schema(description = "유저 정보")
    record UserInfo(
            @Schema(description = "유저 ID", example = "1")
            Long userId,
            @Schema(description = "유저 이름", example = "홍길동")
            String name,
            @Schema(description = "연속 기록 일수", example = "7")
            int streakCount
    ) {

        public static UserInfo from(User user) {
            return new UserInfo(user.getId(), user.getName(), user.getStreakCount());
        }
    }

    @Schema(description = "날짜별 실수 기록 항목")
    public record MistakeDateItem(
            @Schema(description = "날짜", example = "2026-05-17")
            LocalDate date,
            @Schema(description = "요일 (한국어 약어)", example = "토")
            String dayOfWeek,
            @Schema(description = "해당 날짜에 실수 기록 존재 여부", example = "true")
            boolean hasMistake
    ) {

        public static MistakeDateItem of(LocalDate date, boolean hasMistake) {
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

            return new MistakeDateItem(date, dayOfWeek, hasMistake);
        }
    }
}
