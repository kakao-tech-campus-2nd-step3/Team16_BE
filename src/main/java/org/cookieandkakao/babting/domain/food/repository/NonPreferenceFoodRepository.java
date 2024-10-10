package org.cookieandkakao.babting.domain.food.repository;

import org.cookieandkakao.babting.domain.food.entity.Food;
import org.cookieandkakao.babting.domain.food.entity.NonPreferenceFood;
import org.cookieandkakao.babting.domain.food.entity.PreferenceFood;
import org.cookieandkakao.babting.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NonPreferenceFoodRepository extends JpaRepository<NonPreferenceFood, Long> {
    boolean existsByFoodAndMember(Food food, Member member);

    Optional<NonPreferenceFood> findByFoodAndMember(Food food, Member member);

    void deleteByFoodAndMember(Food food, Member member);

    List<PreferenceFood> findAllByMember(Member member);
}
