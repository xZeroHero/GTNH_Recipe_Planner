// File: src/main/java/com/zerohero/gtnh_recipe_planner/dto/BaseRecipeItemDto.java
package com.zerohero.gtnh_recipe_planner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseRecipeItemDto {
    // Common fields
    @JsonProperty("type")
    protected String type;

    @JsonProperty("amount")
    protected Integer amount;

    @JsonProperty("unlocalizedName")
    protected String unlocalizedName;

    // Item specific fields
    @JsonProperty("modItemId")
    protected String modItemId;

    @JsonProperty("metadata")
    protected Integer metadata;

    // Fluid specific fields
    @JsonProperty("modFluidId")
    protected String modFluidId;

    //OreDict specific fields
    @JsonProperty("oreDict")
    protected List<String> oreDict;
}