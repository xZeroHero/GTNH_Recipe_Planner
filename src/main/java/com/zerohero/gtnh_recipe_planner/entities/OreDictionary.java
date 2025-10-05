package com.zerohero.gtnh_recipe_planner.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ore_dictionary")
@Getter @Setter
public class OreDictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String oreName;

    @ManyToMany(mappedBy = "oreDictionaries")
    private Set<Item> items = new HashSet<>();
}