package com.zerohero.gtnh_recipe_planner.mapper;

import com.zerohero.gtnh_recipe_planner.dto.ItemFluidDto;
import com.zerohero.gtnh_recipe_planner.entities.Item;
import com.zerohero.gtnh_recipe_planner.entities.Fluid;
import com.zerohero.gtnh_recipe_planner.entities.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemFluidDtoMapper {

    public static ItemFluidDto ItemToDto(Item item) {
        if (item == null) return null;

        ItemFluidDto dto = new ItemFluidDto();
        dto.setId(item.getId());
        dto.setDisplayName(item.getDisplayName());
        dto.setUnlocalizedName(item.getUnlocalizedName());
        dto.setIconName(item.getIconName());

        // Safely handle oreDictionaries
        if (item.getOreDictionaries() != null) {
            dto.setOreDicts(item.getOreDictionaries().stream()
                    .map(OreDictionary::getOreName)
                    .collect(Collectors.toList()));
        } else {
            dto.setOreDicts(Collections.emptyList());
        }

        dto.setFluid(false);
        return dto;
    }
    public static List<ItemFluidDto> ItemListToDtoList(List<Item> items) {
    List<ItemFluidDto> dtoList = new ArrayList<>();
        for (Item item : items) {
        dtoList.add(ItemToDto(item));
    }
        return dtoList;
    }

    public static ItemFluidDto FluidToDto(Fluid fluid){
        ItemFluidDto dto = new ItemFluidDto();
        dto.setId(fluid.getId());
        dto.setDisplayName(fluid.getDisplayName());
        dto.setUnlocalizedName(fluid.getUnlocalizedName());
        dto.setIconName(fluid.getIconName());
        dto.setFluid(true);
        return dto;
    }
    public static List<ItemFluidDto> FluidListToDtoList(List<Fluid> fluids) {
        List<ItemFluidDto> dtoList = new ArrayList<>();
        for (Fluid fluid : fluids) {
            dtoList.add(FluidToDto(fluid));
        }
        return dtoList;
    }

}
