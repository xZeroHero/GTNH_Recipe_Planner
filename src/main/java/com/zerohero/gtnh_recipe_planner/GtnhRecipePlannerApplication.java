package com.zerohero.gtnh_recipe_planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GtnhRecipePlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GtnhRecipePlannerApplication.class, args);
    }

}
