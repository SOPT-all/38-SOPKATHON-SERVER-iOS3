package org.sopt.domain.mistake.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.mistake.dto.request.MistakeCreateRequest;
import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.domain.mistake.exception.MistakeDuplicateException;
import org.sopt.domain.mistake.repository.MistakeRepository;
import org.sopt.domain.user.entity.User;
import org.sopt.domain.user.exception.UserNotFoundException;
import org.sopt.domain.user.repository.UserRepository;
import org.sopt.global.storage.service.ObjectStorageUrlResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MistakeService {

    private final MistakeRepository mistakeRepository;
    private final UserRepository userRepository;
    private final ObjectStorageUrlResolver objectStorageUrlResolver;

    @Transactional
    public void create(Long userId, MistakeCreateRequest request) {
        User user = getUserOrThrow(userId);
        LocalDate today = LocalDate.now();

        if (mistakeRepository.existsByUserAndDate(user, today)) {
            throw new MistakeDuplicateException();
        }

        String imageUrl = objectStorageUrlResolver.generatePublicUrl(request.imageObjectKey());
        Mistake mistake = Mistake.create(user, imageUrl, request.content(), today);
        mistakeRepository.save(mistake);
        user.updateStreak(today);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
