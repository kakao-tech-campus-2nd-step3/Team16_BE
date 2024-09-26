package org.cookieandkakao.babting.domain.food.repository;


import org.cookieandkakao.babting.domain.food.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
