package com.zerohero.gtnh_recipe_planner.data.repository;

import com.zerohero.gtnh_recipe_planner.entities.Fluid;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FluidRepository extends BaseRepository<Fluid, Long> {
    Optional<Fluid> findByUnlocalizedName(String unlocalizedName);
}
