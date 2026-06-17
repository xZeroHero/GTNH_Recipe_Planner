package com.zerohero.gtnh_recipe_planner.dto;

import com.zerohero.gtnh_recipe_planner.entities.RecipeOutput;
import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import lombok.Data;

@Data
public class RecipeOutputDetailedDto {
    private Long id;
    private ItemType itemType;
    private ItemDto item;
    private FluidDto fluid;
    private Integer amount;
    private Integer chance;

    public static RecipeOutputDetailedDto fromEntity(RecipeOutput output) {
        if (output == null) return null;

        RecipeOutputDetailedDto dto = new RecipeOutputDetailedDto();
        dto.setId(output.getId());
        dto.setItemType(output.getItemType());
        dto.setAmount(output.getAmount());
        dto.setChance(output.getChance());
        dto.setItem(ItemDto.fromEntity(output.getItem()));
        dto.setFluid(FluidDto.fromEntity(output.getFluid()));

        return dto;
    }
}
