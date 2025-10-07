package com.zerohero.gtnh_recipe_planner.entities;


import com.zerohero.gtnh_recipe_planner.enums.RecipeType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "recipes")
@Data
@Getter @Setter
@ToString
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "VARCHAR(50)")
    private RecipeType type;

    private String sourceFile;  // e.g., "gregtech_gt.recipe.thermalcentrifuge.json"

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeInput> inputs = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeOutput> outputs = new ArrayList<>();


    // For GREG_MACHINE type
    private Integer eut;
    private Integer duration;
    private Boolean requiresCleanroom;
    private Boolean requiresLowGravity;
    private String voltage;
    private Integer amperage;

    // For Shaped Recipes
    private Integer width;
    private Integer height;

    //temporary for test purposes
    private String recipeJson;  // Store the original recipe JSON if needed
}

//
//@Getter
//@Setter
//@ToString
//public class Recipe {
//    public enum RecipeType {
//        GT_RECIPE,
//        SHAPED,
//        SHAPELESS,
//        SHAPED_ORE,
//        SHAPELESS_ORE
//    }
//    private RecipeType type;
//    private List<RecipeItem> inputs;
//    private List<RecipeItem> outputs;
//
//    // Common fields
//    private int duration;
//    private int energyCost;
//    private boolean requiresCleanroom;
//    private boolean requiresLowGravity;
//
//    // Type-specific fields (nullable)
//    private Integer eut;
//    private Integer amperage;
//    private String voltage;
//    private Integer width;
//    private Integer height;
//}

