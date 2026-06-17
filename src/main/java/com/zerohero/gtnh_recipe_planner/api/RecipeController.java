package com.zerohero.gtnh_recipe_planner.api;

import com.zerohero.gtnh_recipe_planner.data.repository.RecipeRepository;
import com.zerohero.gtnh_recipe_planner.dto.RecipeDetailedDto;
import com.zerohero.gtnh_recipe_planner.dto.RecipeDto;
import com.zerohero.gtnh_recipe_planner.entities.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeRepository recipeRepository;

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long id) {
        return recipeRepository.findById(id)
                .map(RecipeDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/raw/{type}/{id}")
    public ResponseEntity<List<RecipeDto>> getRecipesByOutputRaw(
            @PathVariable String type,
            @PathVariable Long id) {

        if (!type.equals("item") && !type.equals("fluid")) {
            return ResponseEntity.badRequest().build();
        }

        List<Recipe> recipes = recipeRepository.findByOutputTypeAndId(type, id);
        if (recipes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<RecipeDto> dtos = recipes.stream()
                .map(RecipeDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/detailed/{type}/{id}")
    public ResponseEntity<List<RecipeDetailedDto>> getRecipesByOutputDetailed(
            @PathVariable String type,
            @PathVariable Long id) {

        if (!type.equals("item") && !type.equals("fluid")) {
            return ResponseEntity.badRequest().build();
        }

        List<Recipe> recipes = recipeRepository.findByOutputTypeAndId(type, id);
        if (recipes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<RecipeDetailedDto> dtos = recipes.stream()
                .map(RecipeDetailedDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

}