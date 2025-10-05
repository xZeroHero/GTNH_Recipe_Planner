package com.zerohero.gtnh_recipe_planner.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "items")
@Getter @Setter
@ToString(callSuper = true)
public class Item extends NamedEntity {
    private Integer metadata;
    private String nbt;
    private String ModItemId;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "item_ore_dictionary",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "ore_dict_id")
    )
    private Set<OreDictionary> oreDictionaries = new HashSet<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeInput> recipeInputs = new ArrayList<>();


}
