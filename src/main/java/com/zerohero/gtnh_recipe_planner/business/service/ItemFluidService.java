package com.zerohero.gtnh_recipe_planner.business.service;

import com.zerohero.gtnh_recipe_planner.data.repository.FluidRepository;
import com.zerohero.gtnh_recipe_planner.data.repository.ItemRepository;
import com.zerohero.gtnh_recipe_planner.dto.ItemFluidDto;
import com.zerohero.gtnh_recipe_planner.entities.Fluid;
import com.zerohero.gtnh_recipe_planner.entities.Item;
import com.zerohero.gtnh_recipe_planner.mapper.ItemFluidDtoMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Service
public class ItemFluidService {

    private final ItemRepository itemRepository;
    private final FluidRepository fluidRepository;

    public ItemFluidService(ItemRepository itemRepository, FluidRepository fluidRepository) {
        this.itemRepository = itemRepository;
        this.fluidRepository = fluidRepository;
    }


    private volatile List<ItemFluidDto> cachedItemsAndFluids;
    private final Object lock = new Object();

    @PostConstruct
    public void init() {
        // Initialize cache if not already done by CommandLineRunner
        if (cachedItemsAndFluids == null) {
            preloadItemsAndFluids();
        }
    }

    @Transactional(readOnly = true)
    public void preloadItemsAndFluids() {
        if (cachedItemsAndFluids == null) {
            synchronized (lock) {
                if (cachedItemsAndFluids == null) {
                    log.info("Starting to preload items and fluids...");
                    long startTime = System.currentTimeMillis();
                    List<ItemFluidDto> loadedData = loadAllItemsAndFluids();
                    cachedItemsAndFluids = loadedData;
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Preloaded {} items and fluids in {} ms", loadedData.size(), duration);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ItemFluidDto> loadAllItemsAndFluids() {
        log.debug("Loading all items and fluids from database...");
        List<Item> items = itemRepository.findAll();
        List<ItemFluidDto> itemDtos = items.stream()
                .map(ItemFluidDtoMapper::ItemToDto)
                .collect(Collectors.toList());

        List<Fluid> fluids = fluidRepository.findAll();
        List<ItemFluidDto> fluidDtos = fluids.stream()
                .map(ItemFluidDtoMapper::FluidToDto)
                .collect(Collectors.toList());

        List<ItemFluidDto> all = new ArrayList<>();
        all.addAll(itemDtos);
        all.addAll(fluidDtos);
        return all;
    }

    @Transactional(readOnly = true)
    public List<ItemFluidDto> getAllItemsAndFluids() {
        if (cachedItemsAndFluids == null) {
            return loadAllItemsAndFluids();
        }
        return new ArrayList<>(cachedItemsAndFluids);
    }


//    @Scheduled(fixedRate = 3600000) // Refresh every hour
//    public void refreshCache() {
//        synchronized (lock) {
//            log.info("Refreshing items and fluids cache...");
//            cachedItemsAndFluids = null;
//            preloadItemsAndFluids();
//        }
//    }

    public ItemFluidDto getItemOrFluidByIdAndType(Long id, boolean isFluid) {
        if (isFluid){
            return ItemFluidDtoMapper.FluidToDto(fluidRepository.findById(id).orElse(null));
        }else {
            return ItemFluidDtoMapper.ItemToDto(itemRepository.findById(id).orElse(null));
        }
    }

}