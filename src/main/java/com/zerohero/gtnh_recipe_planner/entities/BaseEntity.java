package com.zerohero.gtnh_recipe_planner.entities;

import com.zerohero.gtnh_recipe_planner.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}