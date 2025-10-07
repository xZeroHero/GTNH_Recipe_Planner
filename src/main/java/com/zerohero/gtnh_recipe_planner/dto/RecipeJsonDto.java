// File: src/main/java/com/zerohero/gtnh_recipe_planner/dto/RecipeJsonDto.java
package com.zerohero.gtnh_recipe_planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.zerohero.gtnh_recipe_planner.enums.RecipeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeJsonDto {
    @JsonProperty("outputs")
    private List<BaseRecipeItemDto> outputs = new ArrayList<>();

    @JsonProperty("inputs")
    private List<BaseRecipeItemDto> inputs = new ArrayList<>();

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("className")
    private String className;

    @JsonProperty("type")
    private String type;

    private Integer eut;
    private Integer duration;
    private Boolean requiresCleanroom;
    private Boolean requiresLowGravity;
    private String voltage;
    private Integer amperage;
}