package com.zerohero.gtnh_recipe_planner.dto;

import com.zerohero.gtnh_recipe_planner.entities.RecipeOutput;
import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import lombok.Data;

@Data
public class RecipeOutputDto {
    private Long id;
    private ItemType itemType;
    private Long itemId;
    private Long fluidId;
    private Integer amount;
    private Integer chance;

    public static RecipeOutputDto fromEntity(RecipeOutput output) {
        if (output == null) return null;

        RecipeOutputDto dto = new RecipeOutputDto();
        dto.setId(output.getId());
        dto.setItemType(output.getItemType());
        dto.setAmount(output.getAmount());
        dto.setChance(output.getChance());

        if (output.getItem() != null) dto.setItemId(output.getItem().getId());
        if (output.getFluid() != null) dto.setFluidId(output.getFluid().getId());

        return dto;
    }
}
