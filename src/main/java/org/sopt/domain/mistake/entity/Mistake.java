package org.sopt.domain.mistake.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.domain.user.entity.User;
import org.sopt.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(
    name = "mistakes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mistake extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mistake_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Mistake(User user, String imageUrl, String content, LocalDate date) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.content = content;
        this.date = date;
    }

    public static Mistake create(User user, String imageUrl, String content, LocalDate date) {
        return new Mistake(user, imageUrl, content, date);
    }
}
