package com.zerohero.gtnh_recipe_planner.dto;

import com.zerohero.gtnh_recipe_planner.entities.OreDictionary;
import com.zerohero.gtnh_recipe_planner.entities.Recipe;
import com.zerohero.gtnh_recipe_planner.entities.RecipeInput;
import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecipeInputDto {
    private Long id;
    private ItemType itemType;
    private Long itemId;
    private Long fluidId;
    private List<String> oreDict;
    private Integer amount;

    public static RecipeInputDto fromEntity(RecipeInput input) {
        if (input == null) return null;

        RecipeInputDto dto = new RecipeInputDto();
        dto.setId(input.getId());
        dto.setItemType(input.getItemType());
        dto.setAmount(input.getAmount());

        if (input.getItem() != null) dto.setItemId(input.getItem().getId());
        if (input.getFluid() != null) dto.setFluidId(input.getFluid().getId());
        if (input.getOreDict() != null && !input.getOreDict().isEmpty()) {
            dto.setOreDict(input.getOreDict().stream()
                    .map(OreDictionary::getOreName)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
