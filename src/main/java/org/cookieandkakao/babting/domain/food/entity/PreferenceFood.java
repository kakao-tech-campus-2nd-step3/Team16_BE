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
@Table(name = "preference_food")
public class PreferenceFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long PreferenceFoodId;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
