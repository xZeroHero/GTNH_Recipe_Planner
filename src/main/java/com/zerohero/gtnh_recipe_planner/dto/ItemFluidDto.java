package com.zerohero.gtnh_recipe_planner.dto;

import lombok.Data;
import java.util.List;

@Data
public class ItemFluidDto {
    private Long id;
    private String displayName;
    protected String unlocalizedName;
    private String iconName;
    private List<String> oreDicts;
    private boolean isFluid;
}