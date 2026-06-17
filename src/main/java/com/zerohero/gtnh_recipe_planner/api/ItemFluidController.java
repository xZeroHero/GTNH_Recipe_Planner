package com.zerohero.gtnh_recipe_planner.api;

import com.zerohero.gtnh_recipe_planner.business.service.ItemFluidService;
import com.zerohero.gtnh_recipe_planner.dto.ItemFluidDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items-fluids")
public class ItemFluidController {

    private final ItemFluidService itemFluidService;

    public ItemFluidController(ItemFluidService itemFluidService) {
        this.itemFluidService = itemFluidService;
    }

    @GetMapping
    public ResponseEntity<List<ItemFluidDto>> getAllItemsAndFluids() {
        return ResponseEntity.ok(itemFluidService.getAllItemsAndFluids());
    }

    @GetMapping("/{type}/{id}")
    public ResponseEntity<ItemFluidDto> getItemOrFluidById(@PathVariable Long id, @PathVariable String type) {
        boolean isFluid = type.equalsIgnoreCase("fluid");
        ItemFluidDto dto = itemFluidService.getItemOrFluidByIdAndType(id, isFluid);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

}
