package com.zerohero.gtnh_recipe_planner.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerohero.gtnh_recipe_planner.data.repository.*;
import com.zerohero.gtnh_recipe_planner.dto.*;
import com.zerohero.gtnh_recipe_planner.entities.*;
import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import com.zerohero.gtnh_recipe_planner.enums.RecipeType;
import com.zerohero.gtnh_recipe_planner.exception.ImportException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonImportService {
    private final ObjectMapper objectMapper;
    private final RecipeRepository recipeRepository;
    private final ItemRepository itemRepository;
    private final FluidRepository fluidRepository;
    private final OreDictionaryRepository oreDictRepository;
    private final RecipeInputRepository recipeInputRepository;
    private final RecipeOutputRepository recipeOutputRepository;

    private Map<String, Item> itemCache;
    private Map<String, Fluid> fluidCache;
    private Map<String, OreDictionary> oreDictCache;

    private Item missingItem;
    private Fluid missingFluid;
    private OreDictionary missingOreDict;
    private Item emptyItem;

    //Item and Fluid imports:
    @Transactional
    public void importItems(Path filePath) throws ImportException {
        try {
            String jsonContent = Files.readString(filePath);
            List<ItemDataDto> itemDtos = objectMapper.readValue(jsonContent, new TypeReference<>() {
            });

            // Pre-fetch existing items to reduce database queries
            Set<String> existingItemNames = itemRepository.findAllUnlocalizedNames();
            Map<String, OreDictionary> existingOreDicts = oreDictRepository.findAll().stream()
                    .collect(Collectors.toMap(OreDictionary::getOreName, Function.identity()));

            List<Item> itemsToSave = new ArrayList<>();
            List<OreDictionary> oreDictsToSave = new ArrayList<>();

            for (ItemDataDto dto : itemDtos) {
                Item item = findOrCreateItem(dto, existingItemNames);
                itemsToSave.add(item);

                if (dto.getOreDict() != null) {
                    for (String oreName : dto.getOreDict()) {
                        OreDictionary oreDict = existingOreDicts.computeIfAbsent(oreName, name -> {
                            OreDictionary od = new OreDictionary();
                            od.setOreName(name);
                            return od;
                        });
                        item.getOreDictionaries().add(oreDict);
                        oreDict.getItems().add(item);
                        if (!oreDictsToSave.contains(oreDict)) {
                            oreDictsToSave.add(oreDict);
                        }
                    }
                }
            }

            // Batch save
            oreDictRepository.saveAll(oreDictsToSave);
            itemRepository.saveAll(itemsToSave);

            log.info("Successfully imported {} items", itemsToSave.size());
        } catch (IOException e) {
            throw new ImportException("Failed to read items file: " + filePath, e);
        }
    }

    private Item findOrCreateItem(ItemDataDto dto, Set<String> existingItemNames) {
        if (existingItemNames.contains(dto.getUnlocalizedName())) {
            return itemRepository.findByUnlocalizedName(dto.getUnlocalizedName())
                    .orElseThrow(() -> new IllegalStateException("Item exists in names set but not in database: " + dto.getUnlocalizedName()));
        }

        Item item = new Item();
        item.setUnlocalizedName(dto.getUnlocalizedName());
        item.setDisplayName(dto.getName());
        item.setMetadata(dto.getMetadata());
        item.setModItemId(dto.getModItemId());
        item.setModItemId(dto.getItem());
        item.setIconName(dto.getIconName());
        return item;
    }

    @Transactional
    public void importFluids(Path filePath) throws ImportException {
        try {
            String jsonContent = Files.readString(filePath);
            List<FluidDataDto> fluidDtos = objectMapper.readValue(jsonContent, new TypeReference<>() {
            });

            // Pre-fetch existing fluid names to reduce database queries
            Set<String> existingFluidNames = fluidRepository.findAllUnlocalizedNames();
            List<Fluid> fluidsToSave = new ArrayList<>();

            for (FluidDataDto dto : fluidDtos) {
                if (!existingFluidNames.contains(dto.getUnlocalizedName())) {
                    Fluid fluid = new Fluid();
                    fluid.setUnlocalizedName(dto.getUnlocalizedName());
                    fluid.setDisplayName(dto.getName());
                    fluid.setTemperature(dto.getTemperature());
                    fluid.setIsGaseous(Boolean.TRUE.equals(dto.getIsGaseous()));
                    fluid.setDensity(dto.getDensity());
                    fluid.setViscosity(dto.getViscosity());
                    fluid.setModId(dto.getModId());
                    fluid.setModFluidId(dto.getModFluidId());
                    fluid.setRarity(dto.getRarity());
                    fluid.setIconName(dto.getIconName());

                    fluidsToSave.add(fluid);
                }
            }

            // Batch save new fluids
            if (!fluidsToSave.isEmpty()) {
                fluidRepository.saveAll(fluidsToSave);
                log.info("Imported {} new fluids", fluidsToSave.size());
            } else {
                log.info("No new fluids to import");
            }

        } catch (IOException e) {
            throw new ImportException("Failed to read fluids file: " + filePath, e);
        }

    }


    //Recipe imports:

    @Transactional
    public void importRecipes(Path filePath) throws ImportException {
        try {
            // Initialize caches if not already done
            if (itemCache == null || fluidCache == null || oreDictCache == null) {
                initializeCaches();
            }

            // Make sure placeholders are set up
            if (missingItem == null || missingFluid == null || missingOreDict == null || emptyItem == null) {
                initPlaceholders();
                initializeCaches(); // Re-initialize caches to include placeholders
            }

            String jsonContent = Files.readString(filePath);
            List<RecipeJsonDto> recipeDtos = objectMapper.readValue(jsonContent,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RecipeJsonDto.class));

            log.info("Starting import of {} recipes", recipeDtos.size());

            // Process recipes in smaller batches to avoid memory issues
//            int batchSize = 100;
//            for (int i = 0; i < recipeDtos.size(); i += batchSize) {
//                int end = Math.min(i + batchSize, recipeDtos.size());
//                List<RecipeJsonDto> batch = recipeDtos.subList(i, end);
//                log.debug("Processing batch {}-{}/{}", i, end, recipeDtos.size());
//                processRecipeBatch(batch);
//            }

            processRecipeBatch(recipeDtos, filePath);

            log.info("Successfully imported {} recipes", recipeDtos.size());

        } catch (IOException e) {
            throw new ImportException("Failed to read recipes file: " + filePath, e);
        } catch (Exception e) {
            log.error("Unexpected error during recipe import", e);
            throw new ImportException("Failed to import recipes: " + e.getMessage(), e);
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processRecipeBatch(List<RecipeJsonDto> recipeDtos, Path filePath) {
        List<Recipe> recipes = new ArrayList<>();
        List<RecipeInput> allInputs = new ArrayList<>();
        List<RecipeOutput> allOutputs = new ArrayList<>();

        // First pass: create all recipes and collect their inputs/outputs
        for (RecipeJsonDto dto : recipeDtos) {
            Recipe recipe = createRecipe(dto, filePath);
            if (recipe != null) {
                recipes.add(recipe);

                // Collect inputs
                if (recipe.getInputs() != null) {
                    allInputs.addAll(recipe.getInputs());
                }

                // Collect outputs
                if (recipe.getOutputs() != null) {
                    allOutputs.addAll(recipe.getOutputs());
                }
            }
        }

        // Save all entities
        if (!recipes.isEmpty()) {
            List <Recipe> recipeList =recipeRepository.saveAll(recipes);
            List <RecipeInput> inputList = recipeInputRepository.saveAll(allInputs);
            List <RecipeOutput> outputList =recipeOutputRepository.saveAll(allOutputs);
            log.info("Saved {} recipes, {} inputs, and {} outputs", recipeList.size(), inputList.size(), outputList.size());

        }
    }

    public Recipe createRecipe(RecipeJsonDto dto, Path filePath) {
        try {

//            log.debug("Processing recipe DTO: {}", dto);
//            log.debug("Recipe type from DTO: '{}'", dto.getType());

            if ("unknown".equalsIgnoreCase(dto.getType())) {
//                log.warn("Skipping recipe with unknown type: {}", dto.getOutput() != null ? dto.getOutputs().getUnlocalizedName() : "No output");
                return null;
            }

            Recipe recipe = new Recipe();
            try {
                String typeStr = dto.getType().toUpperCase();
//                log.debug("Attempting to convert to enum: {}", typeStr);
                RecipeType recipeType = RecipeType.valueOf(typeStr);
//                log.debug("Converted to enum: {}", recipeType);
                recipe.setType(recipeType);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown recipe type: '{}'", dto.getType(), e);
                return null;
            }

            // Set basic properties
            recipe.setSourceFile(filePath.toString());
            recipe.setWidth(dto.getWidth());
            recipe.setHeight(dto.getHeight());
            recipe.setDuration(dto.getDuration());

            // Set GT-specific properties if available
            if (dto.getEut() != null) recipe.setEut(dto.getEut());
            if (dto.getAmperage() != null) recipe.setAmperage(dto.getAmperage());
            if (dto.getVoltage() != null) recipe.setVoltage(dto.getVoltage());
            if (dto.getRequiresCleanroom() != null)
                recipe.setRequiresCleanroom(dto.getRequiresCleanroom());
            if (dto.getRequiresLowGravity() != null)
                recipe.setRequiresLowGravity(dto.getRequiresLowGravity());

            if (dto.getInputs() != null) convertRecipeInputs(dto.getInputs(), recipe);
            if (dto.getOutputs() != null) convertRecipeOutputs(dto.getOutputs(), recipe);


////             First save the recipe to get an ID
//            recipe = recipeRepository.save(recipe);
//            log.debug("Created recipe with ID: {}", recipe.getId());
//
////             Process inputs
//            if (dto.getInputs() != null) {
//                List<RecipeInput> inputs = new ArrayList<>();
//                for (BaseRecipeItemDto inputDto : dto.getInputs()) {
//                    try {
//                        if (inputDto != null) {
//                            RecipeInput input = createRecipeInput(inputDto);
//                            input.setRecipe(recipe);
//                            inputs.add(input);
//                            log.debug("Added input: {} x{}",
//                                    input.getItem() != null ? input.getItem().getUnlocalizedName() : "null",
//                                    input.getAmount());
//                        }
//                    } catch (Exception e) {
//                        log.error("Error creating input for recipe: {}", e.getMessage(), e);
//                        continue;
//                    }
//                }
//                recipe.setInputs(inputs);
//            }
//
//            // Process outputs
//            if (dto.getOutput() != null) {
//                try {
//                    RecipeOutput output = createRecipeOutput(dto.getOutput());
//                    output.setRecipe(recipe);
//                    recipe.setOutputs(new ArrayList<>(Collections.singletonList(output)));
//                    log.debug("Added output: {} x{}",
//                            output.getItem() != null ? output.getItem().getUnlocalizedName() : "null",
//                            output.getAmount());
//                } catch (Exception e) {
//                    log.error("Error creating output for recipe: {}", e.getMessage(), e);
//                    return null;
//                }
//            }
//
//            // Save the recipe again to update with relationships
//
//            log.debug("Saving recipe - Type: {}, Width: {}, Height: {}",
//                    recipe.getType(), recipe.getWidth(), recipe.getHeight());
//            recipe = recipeRepository.save(recipe);
//            log.debug("Saved recipe with ID: {}", recipe.getId());
//
//            Recipe savedRecipe = recipeRepository.findById(recipe.getId()).orElse(null);
//            log.debug("Retrieved saved recipe - Type: {}, Width: {}, Height: {}",
//                    savedRecipe != null ? savedRecipe.getType() : "null",
//                    savedRecipe != null ? savedRecipe.getWidth() : "null",
//                    savedRecipe != null ? savedRecipe.getHeight() : "null");

            return recipe;
        } catch (Exception e) {
            log.error("Error creating recipe: {}", dto, e);
            throw e;
        }
    }

    private void convertRecipeInputs(List<BaseRecipeItemDto> dtoList, Recipe recipe) {
        List<RecipeInput> inputs = new ArrayList<>();
        for (BaseRecipeItemDto dto : dtoList) {
            RecipeInput input = new RecipeInput();
            input.setRecipe(recipe);
            if (dto == null) {
                input.setItem(emptyItem);
                input.setAmount(1);
                inputs.add(input);
                continue;
            }
            if (dto.getType().equalsIgnoreCase("item")) {
                Item item = itemCache.get(dto.getUnlocalizedName());
                if (item == null) item = missingItem;
                input.setItem(item);
                input.setItemType(ItemType.ITEM);
                if (dto.getAmount() != null) input.setAmount(dto.getAmount());
                 else input.setAmount(1);
                inputs.add(input);
            } else if (dto.getType().equalsIgnoreCase("fluid")) {
                Fluid fluid = fluidCache.get(dto.getUnlocalizedName());
                if (fluid == null) fluid = missingFluid;
                input.setFluid(fluid);
                input.setItemType(ItemType.FLUID);
                if (dto.getAmount() != null) input.setAmount(dto.getAmount());
                else input.setAmount(1);
                inputs.add(input);
            } else if (dto.getType().equalsIgnoreCase("oreDict")) {
                for (String oreName : dto.getOreDict()) {
                    OreDictionary oreDict = oreDictCache.get(oreName);
                    if (oreDict == null) oreDict = missingOreDict;
                    input.setOreDict(oreDict);
                    input.setItemType(ItemType.ORE_DICT);
                    if (dto.getAmount() != null) input.setAmount(dto.getAmount());
                    else input.setAmount(1);
                    inputs.add(input);
                }
            }
        }
        recipe.setInputs(inputs);
    }

    private void convertRecipeOutputs(List<BaseRecipeItemDto> dtoList, Recipe recipe) {
        List<RecipeOutput> outputs = new ArrayList<>();
        int i = 0;
        for (BaseRecipeItemDto dto : dtoList) {
            RecipeOutput output = new RecipeOutput();
            output.setRecipe(recipe);
            if (dto == null) {
                output.setItem(emptyItem);
                output.setItemType(ItemType.ITEM);
                output.setAmount(1);
                outputs.add(output);
            }
            if (dto.getType().equalsIgnoreCase("item")) {
                Item item = itemCache.get(dto.getUnlocalizedName());
                if (item == null) {
//                item = itemRepository.findByUnlocalizedName(dto.getUnlocalizedName()).orElse(missingItem);
                    item = missingItem;
                }
                output.setItem(item);
                output.setItemType(ItemType.ITEM);
                output.setAmount(dto.getAmount());

                outputs.add(output);
            } else if (dto.getType().equalsIgnoreCase("fluid")) {
                Fluid fluid = fluidCache.get(dto.getUnlocalizedName());
                if (fluid == null) fluid = missingFluid;

                output.setFluid(fluid);
                output.setItemType(ItemType.FLUID);
                output.setAmount(dto.getAmount());
                outputs.add(output);
            }
        }
        recipe.setOutputs(outputs);
    }

    private void convertRecipeOutput(BaseRecipeItemDto dto, Recipe recipe) {
        List<RecipeOutput> outputs = new ArrayList<>();
        RecipeOutput output = new RecipeOutput();
        output.setRecipe(recipe);

        if (dto == null) {
            output.setItem(emptyItem);
            output.setItemType(ItemType.ITEM);
            output.setAmount(1);
            outputs.add(output);
        }
        if (dto.getType().equalsIgnoreCase("item")) {
            Item item = itemCache.get(dto.getUnlocalizedName());
            if (item == null) {
//                item = itemRepository.findByUnlocalizedName(dto.getUnlocalizedName()).orElse(missingItem);
                item = missingItem;
            }
            output.setItem(item);
            output.setItemType(ItemType.ITEM);
            output.setAmount(dto.getAmount());
            outputs.add(output);
        } else if (dto.getType().equalsIgnoreCase("fluid")) {
            Fluid fluid = fluidCache.get(dto.getUnlocalizedName());
            if (fluid == null) fluid = missingFluid;

            output.setFluid(fluid);
            output.setItemType(ItemType.FLUID);
            output.setAmount(dto.getAmount());
            outputs.add(output);
        }

        recipe.setOutputs(outputs);
    }


    private RecipeInput createEmptyInput() {
        RecipeInput emptyInput = new RecipeInput();
        emptyInput.setItem(emptyItem);
        emptyInput.setAmount(0);  // Set amount to 0 to indicate it's an empty slot
        return emptyInput;
    }

    private RecipeOutput createRecipeOutput(BaseRecipeItemDto dto) {
        RecipeOutput output = new RecipeOutput();
        output.setAmount(dto.getAmount());
        output.setChance(10000); // Default chance if not specified

        // Assuming you have a method to find or create items by modItemId
        Item item = findItemByModId(dto.getModItemId());
        output.setItem(item);

        return output;
    }

//    private void setItemOutput(RecipeOutput output, Map<String, Object> itemData) {
//        output.setItemType(ItemType.ITEM);
//        String unlocalizedName = ((String) itemData.get("unlocalizedName")).toLowerCase();
//        Item item = itemCache.get(unlocalizedName);
//
//        if (item != null) {
//            output.setItem(item);
//            output.setItemId(item.getId());
//        } else {
//            log.warn("Output item not found in cache: {}", itemData);
//            output.setItem(missingItem);
//            output.setItemId(missingItem.getId());
//        }
//    }
//
//    private void setFluidOutput(RecipeOutput output, Map<String, Object> fluidData) {
//        output.setItemType(ItemType.FLUID);
//        String unlocalizedName = ((String) fluidData.get("unlocalizedName")).toLowerCase();
//        Fluid fluid = fluidCache.get(unlocalizedName);
//
//        if (fluid != null) {
//            output.setFluid(fluid);
//            output.setFluidId(fluid.getId());
//        } else {
//            log.warn("Output fluid not found in cache: {}", fluidData);
//            output.setFluid(missingFluid);
//            output.setFluidId(missingFluid.getId());
//        }
//    }

    @SuppressWarnings("unchecked")
    private void setRecipeProperties(Recipe recipe, Map<String, Object> recipeData) {
        // Common properties
        recipe.setDuration((int) recipeData.getOrDefault("duration", 0));

        // GT Recipe specific properties
        if (recipe.getType() == RecipeType.GREG_MACHINE) {
            recipe.setEut((Integer) recipeData.get("eut"));
            recipe.setAmperage((Integer) recipeData.get("amperage"));
            recipe.setVoltage((String) recipeData.get("voltage"));
            recipe.setRequiresCleanroom((Boolean) recipeData.get("requiresCleanroom"));
            recipe.setRequiresLowGravity((Boolean) recipeData.get("requiresLowGravity"));
        }

        // Shaped recipe specific properties
        if (recipe.getType() == RecipeType.SHAPED) {
            recipe.setWidth((Integer) recipeData.get("width"));
            recipe.setHeight((Integer) recipeData.get("height"));
        }

    }

//    private RecipeInput createRecipeInput(BaseRecipeItemDto dto) {
//        RecipeInput input = new RecipeInput();
//        input.setAmount(dto.getAmount());
//
//        // Assuming you have a method to find or create items by modItemId
//        Item item = findItemByModId(dto.getModItemId());
//        input.setItem(item);
//
//        return input;
//    }
//
//    private RecipeInput createItemInput(RecipeInput input, Map<String, Object> itemData) {
//        input.setItemType(ItemType.ITEM);
//        String unlocalizedName = ((String) itemData.get("unlocalizedName")).toLowerCase();
//        Item item = itemCache.get(unlocalizedName);
//
//        if (item == null) {
//            String modItemId = (String) itemData.get("modItemId");
//            int metadata = (int) itemData.getOrDefault("metadata", 0);
//            item = itemRepository.findFirstByModItemIdAndMetadata(modItemId, metadata).orElse(null);
//
//            // If found, add to cache
//            if (item != null) {
//                itemCache.put(item.getUnlocalizedName().toLowerCase(), item);
//            }
//        }
//
//        if (item != null) {
//            input.setItem(item);
//            input.setItemId(item.getId());
//        } else {
//            log.warn("Item not found: {}", itemData);
//            input.setItem(missingItem);
//            input.setItemId(missingItem.getId());
//        }
//
//        return input;
//    }
//
//    private RecipeInput createFluidInput(RecipeInput input, Map<String, Object> fluidData) {
//        input.setItemType(ItemType.FLUID);
//        String unlocalizedName = ((String) fluidData.get("unlocalizedName")).toLowerCase();
//        Fluid fluid = fluidCache.get(unlocalizedName);
//
//        if (fluid == null) {
//            String modFluidId = (String) fluidData.get("modFluidId");
//            fluid = fluidRepository.findFirstByModFluidId(modFluidId).orElse(null);
//
//            if (fluid != null) {
//                fluidCache.put(fluid.getUnlocalizedName().toLowerCase(), fluid);
//            }
//        }
//
//        if (fluid != null) {
//            input.setFluid(fluid);
//            input.setFluidId(fluid.getId());
//        } else {
//            log.warn("Fluid not found: {}", fluidData);
//            input.setFluid(missingFluid);
//            input.setFluidId(missingFluid.getId());
//        }
//
//        return input;
//    }
//
//    @SuppressWarnings("unchecked")
//    private RecipeInput createOreDictInput(RecipeInput input, Map<String, Object> oreDictData) {
//        input.setItemType(ItemType.ORE_DICT);
//        List<String> oreNames = (List<String>) oreDictData.get("oreDict");
//
//        if (oreNames == null || oreNames.isEmpty()) {
//            log.warn("No ore names provided for ore dict input");
//            input.setOreDict(missingOreDict);
//            input.setOreDictId(missingOreDict.getId());
//            return input;
//        }
//
//        // Try to find any of the provided ore names
//        for (String oreName : oreNames) {
//            OreDictionary oreDict = oreDictCache.get(oreName.toLowerCase());
//            if (oreDict != null) {
//                input.setOreDict(oreDict);
//                input.setOreDictId(oreDict.getId());
//                return input;
//            }
//        }
//
//        log.warn("No matching ore dict found for names: {}", oreNames);
//        input.setOreDict(missingOreDict);
//        input.setOreDictId(missingOreDict.getId());
//        return input;
//    }

    private Item findItemByModId(String modItemId) {
        if (modItemId == null) {
            log.warn("Received null modItemId");
            return missingItem;
        }

        log.debug("Looking up item by: {}", modItemId);
        String lowerId = modItemId.toLowerCase();

        // First try exact match in cache (both modItemId and unlocalizedName are in the cache)
        Item item = itemCache.get(lowerId);
        if (item != null) {
            log.debug("Found item in cache: {}", modItemId);
            return item;
        }

        // If not found, try case-insensitive search in cache
        for (Map.Entry<String, Item> entry : itemCache.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(modItemId)) {
                log.debug("Found item in cache (case-insensitive): {} -> {}", modItemId, entry.getKey());
                return entry.getValue();
            }
        }

        // If still not found, try to find by unlocalized name in database
        Optional<Item> foundItem = itemRepository.findByUnlocalizedName(modItemId);
        if (foundItem.isPresent()) {
            item = foundItem.get();
            log.debug("Found item by unlocalizedName: {}", modItemId);
            // Add to cache for future lookups
            if (item.getModItemId() != null) {
                itemCache.put(item.getModItemId().toLowerCase(), item);
            }
            if (item.getUnlocalizedName() != null) {
                itemCache.put(item.getUnlocalizedName().toLowerCase(), item);
            }
            return item;
        }

        // If still not found, try to find by modItemId in database
        foundItem = itemRepository.findFirstByModItemId(modItemId);
        if (foundItem.isPresent()) {
            item = foundItem.get();
            log.debug("Found item by modItemId: {}", modItemId);
            // Add to cache for future lookups
            if (item.getModItemId() != null) {
                itemCache.put(item.getModItemId().toLowerCase(), item);
            }
            if (item.getUnlocalizedName() != null) {
                itemCache.put(item.getUnlocalizedName().toLowerCase(), item);
            }
            return item;
        }

        log.warn("Item not found: {}", modItemId);
        log.debug("Cache size: {}", itemCache.size());
        log.debug("First few cache keys: {}",
                itemCache.keySet().stream().limit(5).collect(Collectors.toList()));

        return missingItem;
    }

    @PostConstruct
    private void initPlaceholders() {
        // Create placeholder item
        missingItem = new Item();
        missingItem.setUnlocalizedName("missing_item");
        missingItem.setDisplayName("Missing Item");
        missingItem = itemRepository.save(missingItem);

        // Create placeholder fluid
        missingFluid = new Fluid();
        missingFluid.setUnlocalizedName("missing_fluid");
        missingFluid.setDisplayName("Missing Fluid");
        missingFluid = fluidRepository.save(missingFluid);

        // Create placeholder oredict
        missingOreDict = new OreDictionary();
        missingOreDict.setOreName("missing_ore");
        missingOreDict = oreDictRepository.save(missingOreDict);

        // Create placeholder empty item (For Shaped Recipes)

        emptyItem = new Item();
        emptyItem.setUnlocalizedName("empty_item");
        emptyItem.setDisplayName("Empty Item");
        emptyItem = itemRepository.save(emptyItem);

    }

    private void initializeCaches() {
        // Initialize caches with both modItemId and unlocalizedName as keys
        itemCache = itemRepository.findAll().stream()
                .collect(Collectors.toMap(
                        item -> item.getUnlocalizedName(),
                        item -> item,
                        (existing, replacement) -> existing
                ));

        fluidCache = fluidRepository.findAll().stream()
                .collect(Collectors.toMap(
                        fluid -> fluid.getUnlocalizedName(),
                        fluid -> fluid,
                        (existing, replacement) -> existing
                ));

        oreDictCache = oreDictRepository.findAll().stream()
                .collect(Collectors.toMap(
                        oreDict -> oreDict.getOreName(),
                        oreDict -> oreDict,
                        (existing, replacement) -> existing
                ));

//        // Make sure placeholders are in caches
//        itemCache.put("missing_item", missingItem);
//        itemCache.put("empty_item", emptyItem);
//        fluidCache.put("missing_fluid", missingFluid);
//        oreDictCache.put("missing_ore", missingOreDict);

        log.info("Initialized caches with {} items, {} fluids, and {} ore dict entries",
                itemCache.size(), fluidCache.size(), oreDictCache.size());
    }
}