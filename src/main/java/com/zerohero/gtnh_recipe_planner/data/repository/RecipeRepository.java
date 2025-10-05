package com.zerohero.gtnh_recipe_planner.data.repository;

import com.zerohero.gtnh_recipe_planner.entities.Recipe;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends BaseRepository<Recipe, Long> {
}