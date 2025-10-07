package com.zerohero.gtnh_recipe_planner.entities;

import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recipe_outputs")
@Getter
@Setter
public class RecipeOutput extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fluid_id")
    private Fluid fluid;

    @Column(nullable = false)
    private int amount;

    private int chance = 100; // 100% by default



    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        if (recipe != null && !recipe.getOutputs().contains(this)) {
            recipe.getOutputs().add(this);
        }
    }

    public void setItem(Item item) {
        this.item = item;
        this.itemType = ItemType.ITEM;
    }

    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
        this.itemType = ItemType.FLUID;
    }

}