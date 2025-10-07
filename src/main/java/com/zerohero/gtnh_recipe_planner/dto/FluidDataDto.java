package com.zerohero.gtnh_recipe_planner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FluidDataDto {
    private String name;
    private String unlocalizedName;
    private String modFluidId;
    private Integer modId;
    private Integer density;
    private Integer temperature;
    private Integer viscosity;
    private Boolean isGaseous;
    private Integer rarity;
    private String iconName;
}