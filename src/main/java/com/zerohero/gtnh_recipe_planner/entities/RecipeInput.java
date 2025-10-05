package com.zerohero.gtnh_recipe_planner.entities;

import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static com.zerohero.gtnh_recipe_planner.enums.ItemType.*;

@Entity
@Table(name = "recipe_inputs")
@Getter
@Setter
public class RecipeInput extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ore_dict_id", insertable = false, updatable = false)
    private OreDictionary oreDict;

    @Column(name = "ore_dict_id")
    private Long oreDictId;

    @Column(nullable = false)
    private int amount;

    public Long getEntityId() {
        return switch (itemType) {
            case ITEM -> itemId;
            case FLUID -> fluidId;
            case ORE_DICT -> oreDictId;
        };
    }

    public void setEntityId(Long id) {
        if (id == null) {
            this.itemId = null;
            this.fluidId = null;
            this.oreDictId = null;
            return;
        }

        switch (itemType) {
            case ITEM -> {
                this.itemId = id;
                this.fluidId = null;
                this.oreDictId = null;
            }
            case FLUID -> {
                this.fluidId = id;
                this.itemId = null;
                this.oreDictId = null;
            }
            case ORE_DICT -> {
                this.oreDictId = id;
                this.itemId = null;
                this.fluidId = null;
            }
        }
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        if (recipe != null && !recipe.getInputs().contains(this)) {
            recipe.getInputs().add(this);
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

    public void setOreDict(OreDictionary oreDict) {
        this.oreDict = oreDict;
        this.itemType = ItemType.ORE_DICT;
    }
}