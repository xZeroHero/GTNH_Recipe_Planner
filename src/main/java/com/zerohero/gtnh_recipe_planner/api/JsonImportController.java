package com.zerohero.gtnh_recipe_planner.api;

import com.zerohero.gtnh_recipe_planner.business.service.JsonImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/json-import")
public class JsonImportController {

    private final JsonImportService jsonImportService;

    public JsonImportController(JsonImportService jsonImportService) {
        this.jsonImportService = jsonImportService;
    }

    @GetMapping
    public ResponseEntity<Boolean> startJsonImport() {
        try {
            jsonImportService.initPlaceholders();
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
            } catch (Exception e) {
                System.out.println("Error reading recipes folder: " + e);
            }

            System.out.println("\nImport completed successfully!");
            return ResponseEntity.ok(true);

        } catch (Exception e) {
            System.err.println("Error during import: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(false);

        }
    }


}


