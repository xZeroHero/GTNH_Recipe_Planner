package com.zerohero.gtnh_recipe_planner.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter
@Setter
@ToString(callSuper = true)
public abstract class NamedEntity extends BaseEntity {
    protected String unlocalizedName;
    protected String displayName;
    protected String iconName;
    //Icon Name is always: folder/unlocalisedName_metadata.png

    //Items:
    //String iconName = "items/" + itemName + "_" + stack.getItemDamage() + ".png";


    //Fluids:
    //String iconName = "fluids/" + fluid.getUnlocalizedName() + ".png";

    //iconName = iconName.replace(" ", "_")
    //                   .replace(":", "_")
    //                   .replace("|", "_");
}