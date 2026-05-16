package org.sopt.domain.mistake.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.mistake.dto.request.MistakeCreateRequest;
import org.sopt.domain.mistake.dto.response.MistakeDetailResponse;
import org.sopt.domain.mistake.dto.response.MistakeListResponse;
import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.domain.mistake.exception.MistakeAccessDeniedException;
import org.sopt.domain.mistake.exception.MistakeDuplicateException;
import org.sopt.domain.mistake.exception.MistakeNotFoundException;
import org.sopt.domain.mistake.repository.MistakeRepository;
import org.sopt.domain.reflection.entity.Reflection;
import org.sopt.domain.reflection.repository.ReflectionRepository;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.exception.UserNotFoundException;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.storage.config.ObjectStorageProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MistakeService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final MistakeRepository mistakeRepository;
    private final UserRepository userRepository;
    private final ReflectionRepository reflectionRepository;
    private final ObjectStorageProperties objectStorageProperties;

    public MistakeListResponse getList(Long userId, Long cursor, Integer size) {
        User user = getUserOrThrow(userId);
        int pageSize = getPageSize(size);

        List<Mistake> mistakes = mistakeRepository.findByUserWithCursor(
                user,
                cursor,
                PageRequest.of(0, pageSize + 1)
        );
        boolean hasNext = mistakes.size() > pageSize;
        List<Mistake> pageMistakes = hasNext ? mistakes.subList(0, pageSize) : mistakes;
        Map<Long, Integer> reflectionEmojiIndexes = getReflectionEmojiIndexes(pageMistakes);

        return MistakeListResponse.of(pageMistakes, reflectionEmojiIndexes, hasNext);
    }

    public MistakeDetailResponse getDetail(Long userId, Long mistakeId) {
        User user = getUserOrThrow(userId);
        Mistake mistake = getMistakeOrThrow(mistakeId);
        if (!mistake.getUser().getId().equals(user.getId())) {
            throw new MistakeAccessDeniedException();
        }
        Reflection reflection = reflectionRepository.findByMistake(mistake).orElse(null);
        return MistakeDetailResponse.of(mistake, reflection);
    }

    @Transactional
    public void create(Long userId, MistakeCreateRequest request) {
        User user = getUserOrThrow(userId);
        LocalDate today = LocalDate.now();

        if (mistakeRepository.existsByUserAndDate(user, today)) {
            throw new MistakeDuplicateException();
        }

        String imageUrl = objectStorageProperties.endpoint() + "/" + objectStorageProperties.bucket() + "/" + request.imageObjectKey();
        Mistake mistake = Mistake.create(user, imageUrl, request.title(), request.content(), today);
        mistakeRepository.save(mistake);
        user.updateStreak(today);
    }

    private int getPageSize(Integer size) {
        if (size == null) {
            return DEFAULT_PAGE_SIZE;
        }
        return size;
    }

    private Map<Long, Integer> getReflectionEmojiIndexes(List<Mistake> mistakes) {
        if (mistakes.isEmpty()) {
            return Map.of();
        }

        return reflectionRepository.findByMistakeIn(mistakes)
                .stream()
                .collect(Collectors.toMap(reflection -> reflection.getMistake().getId(), Reflection::getEmojiIndex));
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
