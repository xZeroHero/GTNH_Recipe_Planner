package com.zerohero.gtnh_recipe_planner.business.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerohero.gtnh_recipe_planner.data.repository.FluidRepository;
import com.zerohero.gtnh_recipe_planner.data.repository.ItemRepository;
import com.zerohero.gtnh_recipe_planner.data.repository.OreDictionaryRepository;
import com.zerohero.gtnh_recipe_planner.data.repository.RecipeRepository;
import com.zerohero.gtnh_recipe_planner.dto.*;
import com.zerohero.gtnh_recipe_planner.entities.*;
import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import com.zerohero.gtnh_recipe_planner.enums.RecipeType;
import com.zerohero.gtnh_recipe_planner.exception.ImportException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonImportService {
    private final ObjectMapper objectMapper;
    private final RecipeRepository recipeRepository;
    private final ItemRepository itemRepository;
    private final FluidRepository fluidRepository;
    private final OreDictionaryRepository oreDictRepository;


    @Transactional
    public void importItems(Path filePath) throws ImportException {
        try {
            String jsonContent = Files.readString(filePath);
            List<ItemDataDto> itemDtos = objectMapper.readValue(
                    jsonContent,
                    new TypeReference<>() {}
            );

            for (ItemDataDto dto : itemDtos) {
                Item item = findOrCreateItem(dto);
                // Handle ore dictionary entries
                if (dto.getOreDict() != null) {
                    for (String oreName : dto.getOreDict()) {
                        OreDictionary oreDict = findOrCreateOreDict(oreName);
                        item.getOreDictionaries().add(oreDict);
                        oreDict.getItems().add(item);
                        oreDictRepository.save(oreDict);
                    }
                }
                itemRepository.save(item);
                log.info("Imported item: {}", item.getUnlocalizedName());
            }
        } catch (IOException e) {
            throw new ImportException("Failed to read items file: " + filePath, e);
        }
    }

    @Transactional
    public void importFluids(Path filePath) throws ImportException {
        try {
            String jsonContent = Files.readString(filePath);
            List<FluidDataDto> fluidDtos = objectMapper.readValue(
                    jsonContent,
                    new TypeReference<>() {}
            );

            for (FluidDataDto dto : fluidDtos) {
                Fluid fluid = new Fluid();
                fluid.setUnlocalizedName(dto.getUnlocalizedName());
                fluid.setDisplayName(dto.getName());
                fluid.setTemperature(dto.getTemperature());
                fluid.setIsGaseous(Boolean.TRUE.equals(dto.getIsGaseous()));
                fluid.setDensity(dto.getDensity());
                fluid.setViscosity(dto.getViscosity());
                fluid.setModId(dto.getModId());
                fluid.setRarity(dto.getRarity());
                fluid.setIconName(dto.getIconName());

                fluidRepository.save(fluid);
                log.info("Imported fluid: {}", fluid.getUnlocalizedName());
            }
        } catch (IOException e) {
            throw new ImportException("Failed to read fluids file: " + filePath, e);
        }
    }

    private Item findOrCreateItem(ItemDataDto dto) {
        return itemRepository.findByUnlocalizedName(dto.getUnlocalizedName())
                .orElseGet(() -> {
                    Item item = new Item();
                    item.setUnlocalizedName(dto.getUnlocalizedName());
                    item.setDisplayName(dto.getName());
                    item.setMetadata(dto.getMetadata());
                    item.setModItemId(dto.getItem());
                    item.setIconName(dto.getIconName());
                    return itemRepository.save(item);
                });
    }

    private OreDictionary findOrCreateOreDict(String oreName) {
        return oreDictRepository.findByOreName(oreName)
                .orElseGet(() -> {
                    OreDictionary oreDict = new OreDictionary();
                    oreDict.setOreName(oreName);
                    return oreDictRepository.save(oreDict);
                });
    }

    @Transactional
    public void importRecipes(Path filePath) throws ImportException {
        try {
            String jsonContent = Files.readString(filePath);
            List<RecipeJsonDto> recipeDtos = objectMapper.readValue(
                    jsonContent,
                    new TypeReference<>() {}
            );

            for (RecipeJsonDto dto : recipeDtos) {
                try {
                    Recipe recipe = convertToRecipe(dto, filePath.getFileName().toString());
                    recipeRepository.save(recipe);
                    log.info("Imported recipe: {}", recipe.getId());
                } catch (Exception e) {
                    log.error("Failed to import recipe from file: {}", filePath, e);
                    throw new ImportException("Failed to import recipe: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new ImportException("Failed to read file: " + filePath, e);
        }
    }

    private Recipe convertToRecipe(RecipeJsonDto dto, String sourceFile) {
        Recipe recipe = new Recipe();
        recipe.setType(determineRecipeType(dto));
        recipe.setSourceFile(sourceFile);

        // Set common recipe properties
        recipe.setDuration(dto.getDuration());
        recipe.setEut(dto.getEut());
        recipe.setAmperage(dto.getAmperage());
        recipe.setVoltage(dto.getVoltage());
        recipe.setRequiresCleanroom(Boolean.TRUE.equals(dto.getRequiresCleanroom()));
        recipe.setRequiresLowGravity(Boolean.TRUE.equals(dto.getRequiresLowGravity()));
        recipe.setWidth(dto.getWidth());
        recipe.setHeight(dto.getHeight());

        // Process inputs
        if (dto.getInputs() != null) {
            for (int i = 0; i < dto.getInputs().size(); i++) {
                BaseRecipeItemDto inputDto = dto.getInputs().get(i);
                RecipeInput input = convertToRecipeInput(inputDto);
                input.setRecipe(recipe);
                recipe.getInputs().add(input);
            }
        }

        // Process outputs
        if (dto.getOutputs() != null) {
            for (int i = 0; i < dto.getOutputs().size(); i++) {
                BaseRecipeItemDto outputDto = dto.getOutputs().get(i);
                RecipeOutput output = convertToRecipeOutput(outputDto, i, dto.getChances());
                output.setRecipe(recipe);
                recipe.getOutputs().add(output);
            }
        }

        return recipe;
    }

    private RecipeType determineRecipeType(RecipeJsonDto dto) {
        if (dto.getType() == null) {
            throw new IllegalArgumentException("Recipe type cannot be null");
        }

        if (dto.isShaped()) return RecipeType.SHAPED;
        if (dto.isShapeless()) return RecipeType.SHAPELESS;
        if (dto.isFurnace()) return RecipeType.FURNACE;
        if (dto.isGregTech()) return RecipeType.GREG_MACHINE;

        throw new IllegalArgumentException("Unknown recipe type: " + dto.getType());
    }

    private RecipeInput convertToRecipeInput(BaseRecipeItemDto dto) {
        RecipeInput input = new RecipeInput();
        input.setAmount(dto.getAmount() != null ? dto.getAmount() : 1);

        switch (dto) {
            case ItemStackDto itemDto -> {
                Item item = findOrCreateItem(itemDto);
                input.setItem(item);
                input.setItemType(ItemType.ITEM);
            }
            case FluidStackDto fluidDto -> {
                Fluid fluid = findOrCreateFluid(fluidDto);
                input.setFluid(fluid);
                input.setItemType(ItemType.FLUID);
            }
            case OreDictDto oreDictDto -> {
                OreDictionary oreDict = findOrCreateOreDict(oreDictDto);
                input.setOreDict(oreDict);
                input.setItemType(ItemType.ORE_DICT);
            }
            default -> throw new IllegalArgumentException("Unsupported input type: " + dto.getClass().getSimpleName());
        }

        return input;
    }

    private RecipeOutput convertToRecipeOutput(BaseRecipeItemDto dto, int index, List<Integer> chances) {
        RecipeOutput output = new RecipeOutput();
        output.setAmount(dto.getAmount() != null ? dto.getAmount() : 1);

        if (dto instanceof ItemStackDto itemDto) {
            Item item = findOrCreateItem(itemDto);
            output.setItem(item);
            output.setItemType(ItemType.ITEM);
        } else if (dto instanceof FluidStackDto fluidDto) {
            Fluid fluid = findOrCreateFluid(fluidDto);
            output.setFluid(fluid);
            output.setItemType(ItemType.FLUID);
        } else {
            throw new IllegalArgumentException("Output cannot be an ore dictionary entry");
        }

        // Set chance if available
        if (chances != null && index < chances.size()) {
            output.setChance(chances.get(index));
        }

        return output;
    }

    private Item findOrCreateItem(ItemStackDto dto) {
        if (!StringUtils.hasText(dto.getItem())) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }

        return itemRepository.findByUnlocalizedName(dto.getItem())
                .orElseGet(() -> {
                    Item item = new Item();
                    item.setUnlocalizedName(dto.getItem());
                    item.setDisplayName(dto.getDisplayName() != null ? dto.getDisplayName() : dto.getItem());
                    item.setMetadata(dto.getMetadata() != null ? dto.getMetadata() : 0);
                    item.setNbt(dto.getNbt());
                    return itemRepository.save(item);
                });
    }

    private Fluid findOrCreateFluid(FluidStackDto dto) {
        if (!StringUtils.hasText(dto.getFluid())) {
            throw new IllegalArgumentException("Fluid name cannot be empty");
        }

        return fluidRepository.findByUnlocalizedName(dto.getFluid())
                .orElseGet(() -> {
                    Fluid fluid = new Fluid();
                    fluid.setUnlocalizedName(dto.getFluid());
                    fluid.setDisplayName(dto.getDisplayName() != null ? dto.getDisplayName() : dto.getFluid());
                    fluid.setTemperature(dto.getTemperature());
                    fluid.setIsGaseous(Boolean.TRUE.equals(dto.getIsGaseous()));
                    return fluidRepository.save(fluid);
                });
    }

    private OreDictionary findOrCreateOreDict(OreDictDto dto) {
        if (!StringUtils.hasText(dto.getOreName())) {
            throw new IllegalArgumentException("Ore dictionary name cannot be empty");
        }

        return oreDictRepository.findByOreName(dto.getOreName())
                .orElseGet(() -> {
                    OreDictionary oreDict = new OreDictionary();
                    oreDict.setOreName(dto.getOreName());
                    return oreDictRepository.save(oreDict);
                });
    }
}