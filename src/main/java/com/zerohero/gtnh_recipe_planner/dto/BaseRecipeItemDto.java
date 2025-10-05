// File: src/main/java/com/zerohero/gtnh_recipe_planner/dto/BaseRecipeItemDto.java
package com.zerohero.gtnh_recipe_planner.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ItemStackDto.class, name = "item"),
        @JsonSubTypes.Type(value = FluidStackDto.class, name = "fluid"),
        @JsonSubTypes.Type(value = OreDictDto.class, name = "oreDict")
})
public class BaseRecipeItemDto {
    protected Integer metadata;
    protected String item;
    protected Integer amount;
    protected String displayName;
    protected String name;
    protected String oreDict;
    protected String fluid;

    public String getType() {
        if (item != null) return "item";
        if (fluid != null) return "fluid";
        if (oreDict != null) return "oreDict";
        return null;
    }
}