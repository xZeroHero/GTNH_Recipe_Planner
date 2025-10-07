package com.zerohero.gtnh_recipe_planner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OreDictDto extends BaseRecipeItemDto {
    @JsonProperty("oreDict")
    private List<String> oreDict;


    public String getOreName() {
        return oreDict != null && !oreDict.isEmpty() ? oreDict.get(0) : null;
    }
}