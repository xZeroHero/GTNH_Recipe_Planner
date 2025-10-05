package com.zerohero.gtnh_recipe_planner.data.repository;

import com.zerohero.gtnh_recipe_planner.entities.OreDictionary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OreDictionaryRepository extends BaseRepository<OreDictionary, Long> {
    Optional<OreDictionary> findByOreName(String oreName);
}