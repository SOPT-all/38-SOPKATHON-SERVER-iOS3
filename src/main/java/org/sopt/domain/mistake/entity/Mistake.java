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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.domain.user.entity.User;
import org.sopt.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "mistakes")
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Mistake(User user, String imageUrl, String content) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.content = content;
    }

    public static Mistake create(User user, String imageUrl, String content) {
        return new Mistake(user, imageUrl, content);
    }
}
