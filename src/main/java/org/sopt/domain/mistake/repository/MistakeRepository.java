package org.sopt.domain.mistake.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MistakeRepository extends JpaRepository<Mistake, Long> {

    boolean existsByUserAndDate(User user, LocalDate date);

    List<Mistake> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
