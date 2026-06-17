package com.zerohero.gtnh_recipe_planner.dto;

import com.zerohero.gtnh_recipe_planner.entities.Recipe;
import com.zerohero.gtnh_recipe_planner.enums.RecipeType;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecipeDto {
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
    private List<RecipeInputDto> inputs;
    private List<RecipeOutputDto> outputs;

    public static RecipeDto fromEntity(Recipe recipe) {
        if (recipe == null) return null;

        RecipeDto dto = new RecipeDto();
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
                    .map(RecipeInputDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        if (recipe.getOutputs() != null) {
            dto.setOutputs(recipe.getOutputs().stream()
                    .map(RecipeOutputDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}