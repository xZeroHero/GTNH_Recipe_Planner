package com.zerohero.gtnh_recipe_planner.config;

import com.zerohero.gtnh_recipe_planner.business.service.ItemFluidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataPreloadConfig implements ApplicationRunner {

    private final ItemFluidService itemFluidService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting preload of items and fluids data...");
        long startTime = System.currentTimeMillis();
        itemFluidService.preloadItemsAndFluids();
        long duration = System.currentTimeMillis() - startTime;
        log.info("Completed preload of items and fluids in {} ms", duration);
    }
}