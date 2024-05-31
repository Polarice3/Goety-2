package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {

    public ModItemTagsProvider(PackOutput p_256095_, CompletableFuture<HolderLookup.Provider> p_256572_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_256095_, Registries.ITEM, p_256572_, (p_256665_) -> {
            return p_256665_.builtInRegistryHolder().key();
        }, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_255894_) {
        this.tag(ModTags.Items.WANDS).add(ModItems.DARK_WAND.get())
                .addTag(ModTags.Items.STAFFS).replace(false);
        this.tag(ModTags.Items.STAFFS).add(ModItems.NECRO_STAFF.get(),
                ModItems.NAMELESS_STAFF.get(),
                ModItems.OMINOUS_STAFF.get(),
                ModItems.FROST_STAFF.get(),
                ModItems.WILD_STAFF.get(),
                ModItems.WIND_STAFF.get(),
                ModItems.STORM_STAFF.get(),
                ModItems.NETHER_STAFF.get()).replace(false);
    }
}
