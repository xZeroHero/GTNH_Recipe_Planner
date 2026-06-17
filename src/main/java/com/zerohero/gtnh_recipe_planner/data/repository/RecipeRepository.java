package com.zerohero.gtnh_recipe_planner.data.repository;

import com.zerohero.gtnh_recipe_planner.entities.Recipe;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends BaseRepository<Recipe, Long> {

    @Query("SELECT DISTINCT r FROM Recipe r " +
            "JOIN r.outputs o " +
            "WHERE (:type = 'item' AND o.item.id = :id) OR " +
            "      (:type = 'fluid' AND o.fluid.id = :id)")
    List<Recipe> findByOutputTypeAndId(@Param("type") String type, @Param("id") Long id);


}