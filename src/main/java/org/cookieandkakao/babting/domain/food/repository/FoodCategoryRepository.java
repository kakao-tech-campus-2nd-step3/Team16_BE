package org.cookieandkakao.babting.domain.food.repository;

import org.cookieandkakao.babting.domain.food.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
}
