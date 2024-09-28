package org.cookieandkakao.babting.domain.food.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.cookieandkakao.babting.domain.member.entity.Member;

@Entity
@Table(name = "non_preference_food")
public class NonPreferenceFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nonPreferenceFoodId;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne
//    @JoinColumn(name = "member_id", nullable = false)
//   로그인기능 구현 후 변경할 것
    @JoinColumn(name = "member_id")
    private Member member;

    protected NonPreferenceFood() {}

    public NonPreferenceFood(Food food) {
        this.food = food;
    }

    public Food getFood() {
        return food;
    }
}
