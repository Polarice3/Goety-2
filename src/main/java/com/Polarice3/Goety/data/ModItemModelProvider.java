package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator output, ExistingFileHelper existingFileHelper) {
        super(output, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (Item item : ForgeRegistries.ITEMS) {
            if (ForgeRegistries.ITEMS.getKey(item) != null) {
                ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(item);
                if (resourceLocation != null) {
                    if (item instanceof SpawnEggItem && resourceLocation.getNamespace().equals(Goety.MOD_ID)) {
                        getBuilder(resourceLocation.getPath())
                                .parent(getExistingFile(new ResourceLocation("item/template_spawn_egg")));
                    }
                }
            }
        }
    }
}
