package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.PlushieBlock;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
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
                ModItems.GEO_STAFF.get(),
                ModItems.ABYSS_STAFF.get(),
                ModItems.VOID_STAFF.get(),
                ModItems.NETHER_STAFF.get()).replace(false);
        this.tag(ModTags.Items.ROBES).add(ModItems.DARK_ROBE.get(),
                ModItems.DARK_ROBE_FANCY.get(),
                ModItems.GRAND_ROBE.get(),
                ModItems.ILLUSION_ROBE.get(),
                ModItems.ILLUSION_ROBE_MIRROR.get(),
                ModItems.GEO_ROBE.get(),
                ModItems.FROST_ROBE.get(),
                ModItems.FROST_ROBE_CRYO.get(),
                ModItems.WIND_ROBE.get(),
                ModItems.STORM_ROBE.get(),
                ModItems.WILD_ROBE.get(),
                ModItems.ABYSS_ROBE.get(),
                ModItems.VOID_ROBE.get(),
                ModItems.WITCH_ROBE.get(),
                ModItems.WITCH_ROBE_HEDGE.get(),
                ModItems.WARLOCK_ROBE.get(),
                ModItems.WARLOCK_ROBE_DARK.get(),
                ModItems.NETHER_ROBE.get(),
                ModItems.NETHER_ROBE_WARPED.get(),
                ModItems.UNHOLY_ROBE.get()).replace(false);
        this.tag(ModTags.Items.CAPES).add(ModItems.NECRO_CAPE.get(),
                ModItems.NAMELESS_CAPE.get()).replace(false);
        this.tag(ModTags.Items.CROWNS).add(ModItems.NECRO_CROWN.get(),
                ModItems.NAMELESS_CROWN.get(),
                ModItems.FROST_CROWN.get(),
                ModItems.WIND_CROWN.get(),
                ModItems.STORM_CROWN.get(),
                ModItems.WILD_CROWN.get(),
                ModItems.ABYSS_CROWN.get(),
                ModItems.VOID_CROWN.get(),
                ModItems.NETHER_CROWN.get(),
                ModItems.DARK_HAT.get(),
                ModItems.GRAND_TURBAN.get(),
                ModItems.UNHOLY_HAT.get(),
                ModItems.UNHOLY_HAT_HALO.get()).replace(false);
        Collection<Item> focuses = new ArrayList<>();
        Collection<Item> plushie = new ArrayList<>();
        ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(item ->
        {
            if (item instanceof MagicFocus){
                focuses.add(item);
            }
            if (item instanceof BlockItem item1 && item1.getBlock() instanceof PlushieBlock) {
                plushie.add(item);
            }
        });
        if (!focuses.isEmpty()){
            for (Item item : focuses){
                this.tag(ModTags.Items.FOCUSES).add(item).replace(false);
            }
        }
        if (!plushie.isEmpty()){
            for (Item item : plushie){
                this.tag(ModTags.Items.PLUSHIE).add(item).replace(false);
            }
        }
    }
}
