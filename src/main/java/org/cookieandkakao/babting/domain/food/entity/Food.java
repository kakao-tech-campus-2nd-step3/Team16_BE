package org.cookieandkakao.babting.domain.food.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;

    @ManyToOne
    @JoinColumn(name = "food_category_id", nullable = false)
    private FoodCategory foodCategory;

    @Column(nullable = false)
    private String name;

    public Food(Long foodId, FoodCategory foodCategory, String name) {
        this.foodId = foodId;
        this.foodCategory = foodCategory;
        this.name = name;
    }

    protected Food() {}

    public Long getFoodId() {
        return foodId;
    }

    public FoodCategory getFoodCategory() {
        return foodCategory;
    }

    public String getName() {
        return name;
    }
}
