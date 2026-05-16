package org.sopt.domain.mistake.repository;

import java.time.LocalDate;
import java.util.List;
import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MistakeRepository extends JpaRepository<Mistake, Long> {

    boolean existsByUserAndDate(User user, LocalDate date);

    List<Mistake> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT m
            FROM Mistake m
            WHERE m.user = :user
              AND (:cursor IS NULL OR m.id < :cursor)
            ORDER BY m.id DESC
            """)
    List<Mistake> findByUserWithCursor(
            @Param("user") User user,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}
