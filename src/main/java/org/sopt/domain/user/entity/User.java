package org.sopt.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "streak_count", nullable = false)
    private int streakCount = 0;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    public void updateStreak(LocalDate today) {
        if (lastActiveDate != null && lastActiveDate.equals(today)) {
            return;
        }
        if (lastActiveDate != null && lastActiveDate.equals(today.minusDays(1))) {
            streakCount++;
        } else {
            streakCount = 1;
        }
        lastActiveDate = today;
    }
}
