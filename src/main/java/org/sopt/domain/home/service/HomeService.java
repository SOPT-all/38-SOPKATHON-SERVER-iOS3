package org.sopt.domain.home.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.home.dto.response.HomeResponse;
import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.domain.mistake.repository.MistakeRepository;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.exception.UserNotFoundException;
import org.sopt.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private static final int DATE_RANGE = 3;

    private final UserRepository userRepository;
    private final MistakeRepository mistakeRepository;

    public HomeResponse getHome(Long userId) {
        User user = getUserOrThrow(userId);
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(DATE_RANGE);
        LocalDate endDate = today.plusDays(DATE_RANGE);

        List<Mistake> mistakes = mistakeRepository.findByUserAndDateBetween(user, startDate, endDate);
        Set<LocalDate> mistakeDates = mistakes.stream()
                .map(Mistake::getDate)
                .collect(Collectors.toSet());

        Map<LocalDate, Boolean> datesMap = new TreeMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            datesMap.put(date, mistakeDates.contains(date));
        }

        return HomeResponse.of(user, datesMap);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
