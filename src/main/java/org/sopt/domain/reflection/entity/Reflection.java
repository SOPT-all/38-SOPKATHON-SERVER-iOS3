package org.sopt.domain.reflection.entity;

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
import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "reflections")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reflection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reflection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mistake_id", nullable = false, unique = true)
    private Mistake mistake;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "emoji_index", nullable = false)
    private int emojiIndex = 0;

    private Reflection(Mistake mistake, String content, int emojiIndex) {
        this.mistake = mistake;
        this.content = content;
        this.emojiIndex = emojiIndex;
    }

    public static Reflection create(Mistake mistake, String content, int emojiIndex) {
        return new Reflection(mistake, content, emojiIndex);
    }
}
