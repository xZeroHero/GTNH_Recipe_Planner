package com.zerohero.gtnh_recipe_planner.data.repository;

import com.zerohero.gtnh_recipe_planner.entities.Fluid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FluidRepository extends BaseRepository<Fluid, Long> {
    Optional<Fluid> findByUnlocalizedName(String unlocalizedName);

    @Query("SELECT f.unlocalizedName FROM Fluid f")
    Set<String> findAllUnlocalizedNames();

    @Query("SELECT f FROM Fluid f WHERE f.modFluidId = :modFluidId")
    List<Fluid> findByModFluidId(@Param("modFluidId") String modFluidId);

    default Optional<Fluid> findFirstByModFluidId(String modFluidId) {
        List<Fluid> fluids = findByModFluidId(modFluidId);
        return fluids.isEmpty() ? Optional.empty() : Optional.of(fluids.get(0));
    }

}

