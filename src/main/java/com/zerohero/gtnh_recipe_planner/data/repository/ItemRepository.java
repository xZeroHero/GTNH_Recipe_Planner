package com.zerohero.gtnh_recipe_planner.data.repository;

import com.zerohero.gtnh_recipe_planner.entities.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ItemRepository extends BaseRepository<Item, Long> {

    Optional<Item> findByUnlocalizedName(String unlocalizedName);

    @Query("SELECT i.unlocalizedName FROM Item i")
    Set<String> findAllUnlocalizedNames();

    @Query("SELECT i FROM Item i WHERE i.unlocalizedName = :unlocalizedName")
    List<Item> findAllByUnlocalizedName(@Param("unlocalizedName") String unlocalizedName);

    @Query("SELECT i FROM Item i WHERE i.modItemId = :modItemId AND i.metadata = :metadata")
    List<Item> findByModItemIdAndMetadata(
            @Param("modItemId") String modItemId,
            @Param("metadata") int metadata
    );
    @Query("SELECT i FROM Item i WHERE i.modItemId = :modItemId")

    List<Item> findByModItemId(
            @Param("modItemId") String modItemId
    );
    default Optional<Item> findFirstByModItemId(String modItemId) {
        List<Item> items = findByModItemId(modItemId);
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    default Optional<Item> findFirstByModItemIdAndMetadata(String modItemId, int metadata) {
        List<Item> items = findByModItemIdAndMetadata(modItemId, metadata);
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.oreDictionaries")
    List<Item> findAllWithOreDictionaries();

}