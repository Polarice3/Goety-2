package com.Polarice3.Goety.common.items;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;

public class ModSpawnEggItem extends ForgeSpawnEggItem {

    public ModSpawnEggItem(final RegistryObject<? extends EntityType<? extends Mob>> entityTypeSupplier, int primaryColorIn, int secondaryColorIn, Properties builder) {
        super(Lazy.of(entityTypeSupplier), primaryColorIn, secondaryColorIn, builder);
    }
}
