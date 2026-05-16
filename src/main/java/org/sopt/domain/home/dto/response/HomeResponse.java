package org.sopt.domain.home.dto.response;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.sopt.domain.user.entity.User;

public record HomeResponse(
        UserInfo user,
        List<MistakeDateItem> dates
) {

    public static HomeResponse of(User user, Map<LocalDate, Boolean> dates) {
        List<MistakeDateItem> dateItems = dates.entrySet()
                .stream()
                .map(entry -> MistakeDateItem.of(entry.getKey(), entry.getValue()))
                .toList();

        return new HomeResponse(UserInfo.from(user), dateItems);
    }

    record UserInfo(
            Long userId,
            String name,
            int streakCount
    ) {

        public static UserInfo from(User user) {
            return new UserInfo(user.getId(), user.getName(), user.getStreakCount());
        }
    }

    public record MistakeDateItem(
            LocalDate date,
            String dayOfWeek,
            boolean hasMistake
    ) {

        public static MistakeDateItem of(LocalDate date, boolean hasMistake) {
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

            return new MistakeDateItem(date, dayOfWeek, hasMistake);
        }
    }
}
