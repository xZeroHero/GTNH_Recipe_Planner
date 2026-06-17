package com.zerohero.gtnh_recipe_planner.dto;

import com.zerohero.gtnh_recipe_planner.entities.Recipe;
import com.zerohero.gtnh_recipe_planner.enums.RecipeType;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecipeDetailedDto {
    private Long id;
    private RecipeType type;
    private String sourceFile;
    private Integer width;
    private Integer height;
    private Integer duration;
    private Integer eut;
    private Integer amperage;
    private String voltage;
    private Boolean requiresCleanroom;
    private Boolean requiresLowGravity;
    private List<RecipeInputDetailedDto> inputs;
    private List<RecipeOutputDetailedDto> outputs;

    public static RecipeDetailedDto fromEntity(Recipe recipe) {
        if (recipe == null) return null;

        RecipeDetailedDto dto = new RecipeDetailedDto();
        dto.setId(recipe.getId());
        dto.setType(recipe.getType());
        dto.setSourceFile(recipe.getSourceFile());
        dto.setWidth(recipe.getWidth());
        dto.setHeight(recipe.getHeight());
        dto.setDuration(recipe.getDuration());
        dto.setEut(recipe.getEut());
        dto.setAmperage(recipe.getAmperage());
        dto.setVoltage(recipe.getVoltage());
        dto.setRequiresCleanroom(recipe.getRequiresCleanroom());
        dto.setRequiresLowGravity(recipe.getRequiresLowGravity());

        if (recipe.getInputs() != null) {
            dto.setInputs(recipe.getInputs().stream()
                    .map(RecipeInputDetailedDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        if (recipe.getOutputs() != null) {
            dto.setOutputs(recipe.getOutputs().stream()
                    .map(RecipeOutputDetailedDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
