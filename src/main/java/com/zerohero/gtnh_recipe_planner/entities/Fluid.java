package com.zerohero.gtnh_recipe_planner.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fluids")
@Getter @Setter
@ToString(callSuper = true)
public class Fluid extends NamedEntity {

    private Integer modId;
    private Integer density;
    private Integer temperature;
    private Integer viscosity;
    private Boolean isGaseous;
    private Integer rarity;

    @OneToMany(mappedBy = "fluid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeInput> recipeInputs = new ArrayList<>();
}




