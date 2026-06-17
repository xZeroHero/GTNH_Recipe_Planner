package com.zerohero.gtnh_recipe_planner.dto;

import com.zerohero.gtnh_recipe_planner.entities.Fluid;
import lombok.Data;

@Data
public class FluidDto {
    private Long id;
    private String name;
    private String modId;
    private String unlocalizedName;
    private String iconName;

    public static FluidDto fromEntity(Fluid fluid) {
        if (fluid == null) return null;

        FluidDto dto = new FluidDto();
        dto.setId(fluid.getId());
        dto.setName(fluid.getDisplayName());
        dto.setModId(fluid.getModFluidId());
        dto.setUnlocalizedName(fluid.getUnlocalizedName());
        dto.setIconName(fluid.getIconName());

        return dto;
    }
}
