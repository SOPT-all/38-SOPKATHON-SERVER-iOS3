package org.sopt.domain.reflection.service;

import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.domain.mistake.exception.MistakeAccessDeniedException;
import org.sopt.domain.mistake.exception.MistakeNotFoundException;
import org.sopt.domain.mistake.repository.MistakeRepository;
import org.sopt.domain.reflection.dto.request.ReflectionCreateRequest;
import org.sopt.domain.reflection.dto.response.ReflectionCreateResponse;
import org.sopt.domain.reflection.entity.Reflection;
import org.sopt.domain.reflection.exception.ReflectionDuplicateException;
import org.sopt.domain.reflection.exception.ReflectionInvalidEmojiIndexException;
import org.sopt.domain.reflection.repository.ReflectionRepository;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.exception.UserNotFoundException;
import org.sopt.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReflectionService {

    private static final Set<Integer> VALID_EMOJI_INDEXES = Set.of(1, 2, 3, 4);

    private final ReflectionRepository reflectionRepository;
    private final MistakeRepository mistakeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReflectionCreateResponse create(Long userId, Long mistakeId, ReflectionCreateRequest request) {
        User user = getUserOrThrow(userId);
        Mistake mistake = getMistakeOrThrow(mistakeId);

        if (!mistake.getUser().getId().equals(user.getId())) {
            throw new MistakeAccessDeniedException();
        }

        if (!VALID_EMOJI_INDEXES.contains(request.emojiIndex())) {
            throw new ReflectionInvalidEmojiIndexException();
        }

        if (reflectionRepository.existsByMistake(mistake)) {
            throw new ReflectionDuplicateException();
        }

        Reflection reflection = Reflection.create(mistake, request.content(), request.emojiIndex());
        reflectionRepository.save(reflection);

        user.updateStreak(LocalDate.now());

        return ReflectionCreateResponse.from(reflection);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private Mistake getMistakeOrThrow(Long mistakeId) {
        return mistakeRepository.findById(mistakeId)
                .orElseThrow(MistakeNotFoundException::new);
    }
}
