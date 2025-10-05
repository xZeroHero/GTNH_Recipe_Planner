package com.zerohero.gtnh_recipe_planner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FluidStackDto extends BaseRecipeItemDto {
    private Integer temperature;
    private Boolean isGaseous;
}