package org.cookieandkakao.babting.domain.food.repository;

import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenceFoodRepository extends JpaRepository<PreferenceFood, Long> {
    boolean existsByFood(Food food);

    Optional<PreferenceFood> findByFood(Food food);

    void deleteByFood(Food food);
}
