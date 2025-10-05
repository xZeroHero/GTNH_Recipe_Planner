// File: src/main/java/com/zerohero/gtnh_recipe_planner/dto/RecipeJsonDto.java
package com.zerohero.gtnh_recipe_planner.dto;

import com.zerohero.gtnh_recipe_planner.enums.RecipeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeJsonDto {
    // Common fields
    private String type;
    private List<BaseRecipeItemDto> inputs;
    private List<BaseRecipeItemDto> outputs;

    // Crafting specific
    private Integer width;
    private Integer height;
    private Integer input_count;
    private Integer converted_count;

    // GT machine specific
    private Boolean requiresCleanroom;
    private Boolean requiresLowGravity;
    private List<Integer> chances;
    private Integer duration;
    private Integer eut;
    private Integer amperage;
    private String voltage;
    private String sourceFile;

    // Helper methods
    public boolean isShaped() {
        return type != null && type.contains("Shaped");
    }

    public boolean isShapeless() {
        return type != null && type.contains("Shapeless");
    }

    public boolean isGregTech() {
        return type != null && type.startsWith("gregtech");
    }

    public boolean isFurnace() {
        return type != null && type.equals("furnace");
    }

    public RecipeType getRecipeType() {
        if (isShaped()) return RecipeType.SHAPED;
        if (isShapeless()) return RecipeType.SHAPELESS;
        if (isFurnace()) return RecipeType.FURNACE;
        if (isGregTech()) return RecipeType.GREG_MACHINE;
        return null;
    }
}