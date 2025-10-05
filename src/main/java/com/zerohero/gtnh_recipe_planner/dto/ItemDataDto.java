package com.zerohero.gtnh_recipe_planner.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDataDto {
    private String name;
    private String unlocalizedName;
    private Integer metadata;
    private String item;
    private List<String> oreDict;
    private String iconName;
}