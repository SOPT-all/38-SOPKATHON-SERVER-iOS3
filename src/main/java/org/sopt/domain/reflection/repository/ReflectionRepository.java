package org.sopt.domain.reflection.repository;

import org.sopt.domain.mistake.entity.Mistake;
import org.sopt.domain.reflection.entity.Reflection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReflectionRepository extends JpaRepository<Reflection, Long> {

    boolean existsByMistake(Mistake mistake);
}
