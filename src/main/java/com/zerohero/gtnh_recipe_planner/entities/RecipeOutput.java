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
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;

    @Column(name = "item_id")
    private Long itemId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fluid_id", insertable = false, updatable = false)
    private Fluid fluid;

    @Column(name = "fluid_id")
    private Long fluidId;

    @Column(nullable = false)
    private int amount;

    private int chance = 100; // 100% by default

    public Long getEntityId() {
        return switch (itemType) {
            case ITEM -> itemId;
            case FLUID -> fluidId;
            case ORE_DICT -> throw new IllegalStateException("Recipe outputs cannot be ore dictionary entries");
        };
    }

    public void setEntityId(Long id) {
        if (id == null) {
            this.itemId = null;
            this.fluidId = null;
            return;
        }

        switch (itemType) {
            case ITEM -> {
                this.itemId = id;
                this.fluidId = null;
            }
            case FLUID -> {
                this.fluidId = id;
                this.itemId = null;
            }
            case ORE_DICT ->
                    throw new IllegalArgumentException("Recipe outputs cannot be ore dictionary entries");
        }
    }

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