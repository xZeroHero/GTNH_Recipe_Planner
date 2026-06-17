package com.zerohero.gtnh_recipe_planner.dto;

import com.zerohero.gtnh_recipe_planner.entities.OreDictionary;
import com.zerohero.gtnh_recipe_planner.entities.RecipeInput;
import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class RecipeInputDetailedDto {
    private Long id;
    private ItemType itemType;
    private ItemDto item;
    private FluidDto fluid;
    private List<ItemDto> matchingItems; // Items that match all oreDicts
    private Integer amount;



    public static RecipeInputDetailedDto fromEntity(RecipeInput input) {
        if (input == null) return null;

        RecipeInputDetailedDto dto = new RecipeInputDetailedDto();
        dto.setId(input.getId());
        dto.setItemType(input.getItemType());
        dto.setAmount(input.getAmount());
        dto.setItem(ItemDto.fromEntity(input.getItem()));
        dto.setFluid(FluidDto.fromEntity(input.getFluid()));

        if (input.getOreDict() != null && !input.getOreDict().isEmpty()) {
            Set<OreDictionary> oreDicts = input.getOreDict();

            List<ItemDto> itemsMatchingAllOreDicts = oreDicts.stream()
                    .flatMap(oreDict -> oreDict.getItems().stream())
                    .distinct()
                    .filter(item -> item.getOreDictionaries().containsAll(oreDicts))
                    .map(ItemDto::fromEntity)
                    .collect(Collectors.toList());

            dto.setMatchingItems(itemsMatchingAllOreDicts);
        } else {
            dto.setMatchingItems(Collections.emptyList());
        }

        return dto;
    }
}