package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModItemTagsProvider extends TagsProvider<Item> {

    public ModItemTagsProvider(DataGenerator p_126517_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126517_, Registry.ITEM, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
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
