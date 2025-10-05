package com.zerohero.gtnh_recipe_planner.data.repository;

import com.zerohero.gtnh_recipe_planner.entities.Item;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends BaseRepository<Item, Long> {
    Optional<Item> findByUnlocalizedName(String unlocalizedName);
}