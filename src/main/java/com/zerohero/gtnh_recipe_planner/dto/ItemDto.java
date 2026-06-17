package com.zerohero.gtnh_recipe_planner.dto;

import com.zerohero.gtnh_recipe_planner.entities.Item;
import com.zerohero.gtnh_recipe_planner.entities.OreDictionary;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String modId;
    private String unlocalizedName;
    private String iconName;
    private List<String> oreDict;

    public static ItemDto fromEntity(Item item) {
        if (item == null) return null;

        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getDisplayName());
        dto.setModId(item.getModItemId());
        dto.setUnlocalizedName(item.getUnlocalizedName());
        dto.setIconName(item.getIconName());
        dto.setOreDict(item.getOreDictionaries().stream().map(OreDictionary::getOreName).collect(Collectors.toList()));
        return dto;
    }
}
