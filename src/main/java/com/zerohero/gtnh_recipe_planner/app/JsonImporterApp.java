package com.zerohero.gtnh_recipe_planner.app;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerohero.gtnh_recipe_planner.business.service.JsonImportService;
import com.zerohero.gtnh_recipe_planner.data.repository.FluidRepository;
import com.zerohero.gtnh_recipe_planner.data.repository.ItemRepository;
import com.zerohero.gtnh_recipe_planner.entities.Fluid;
import com.zerohero.gtnh_recipe_planner.entities.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = "com.zerohero.gtnh_recipe_planner.data.repository",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.zerohero\\.gtnh_recipe_planner\\.data\\.repository\\..*Repository"
        )
)
@EntityScan("com.zerohero.gtnh_recipe_planner.entities")
@ComponentScan({
        "com.zerohero.gtnh_recipe_planner.business.service"})
public class JsonImporterApp {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
//        manualImportTest(args);

        SpringApplication.run(JsonImporterApp.class, args);

    }

    @Bean
    public CommandLineRunner run(JsonImportService jsonImportService) {
        return args -> {
            try {
                // Import items
                Path itemsPath = Paths.get("A:\\Programming\\Minecraft\\exports\\items\\items.json");
                System.out.println("Importing items from: " + itemsPath);
                jsonImportService.importItems(itemsPath);
                System.out.println("Successfully imported items");

                // Import fluids
                Path fluidsPath = Paths.get("A:\\Programming\\Minecraft\\exports\\items\\fluids.json");
                System.out.println("Importing fluids from: " + fluidsPath);
                jsonImportService.importFluids(fluidsPath);
                System.out.println("Successfully imported fluids");

//                Path recipesPath = Paths.get("A:\\Programming\\Minecraft\\exports\\recipes\\furnace.json");
//                System.out.println("Importing recipes from: " + recipesPath);
//                jsonImportService.importRecipes(recipesPath);
//                System.out.println("Successfully imported recipes from: " + recipesPath);


                try {
                    String folderPath = "A:\\Programming\\Minecraft\\exports\\recipes";
                    List<Path> filePaths = Files.walk(Paths.get(folderPath))
                            .filter(Files::isRegularFile)
                            .toList();


                for (Path filePath : filePaths) {
                    System.out.println("Importing recipes from: " + filePath);
                    jsonImportService.importRecipes(filePath);
                    System.out.println("Successfully imported recipes from: " + filePath);
                }
                } catch (Exception e){
                    System.out.println("Error reading recipes folder: " + e);
                }

                System.out.println("\nImport completed successfully!");
                System.exit(0);

            } catch (Exception e) {
                System.err.println("Error during import: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        };
    }

    private static void manualImportTest(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar your-app.jar <itemsJsonPath> <fluidsJsonPath>");
            System.out.println("Example: java -jar your-app.jar items.json fluids.json");
            System.exit(1);
        }

        String itemsPath = args[0];
        String fluidsPath = args[1];

        try {
            // Load items
            System.out.println("Loading items from: " + itemsPath);
            List<Item> items = Arrays.asList(objectMapper.readValue(new File(itemsPath), Item[].class));
            System.out.println("Successfully loaded " + items.size() + " items");

            // Load fluids
            System.out.println("Loading fluids from: " + fluidsPath);
            List<Fluid> fluids = Arrays.asList(objectMapper.readValue(new File(fluidsPath), Fluid[].class));
            System.out.println("Successfully loaded " + fluids.size() + " fluids");

            // Here you can add your processing logic
            System.out.println("\nImport completed successfully!");

        } catch (IOException e) {
            System.err.println("Error reading JSON files: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}