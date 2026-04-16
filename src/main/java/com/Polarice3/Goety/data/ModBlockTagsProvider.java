package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.PlushieBlock;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {

    public ModBlockTagsProvider(PackOutput p_256095_, CompletableFuture<HolderLookup.Provider> p_256572_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_256095_, Registries.BLOCK, p_256572_, (p_256665_) -> {
            return p_256665_.builtInRegistryHolder().key();
        }, Goety.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_255894_) {
        Collection<Block> plushie = new ArrayList<>();
        Collection<Block> ominous = new ArrayList<>();
        ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block ->
        {
            if (block instanceof PlushieBlock){
                plushie.add(block);
            }
            if (block.getDescriptionId().contains("ominous_stone")) {
                ominous.add(block);
            }
        });
        if (!plushie.isEmpty()){
            for (Block block : plushie){
                this.tag(ModTags.Blocks.PLUSHIE).add(block).replace(false);
            }
        }
        if (!ominous.isEmpty()) {
            ominous.add(ModBlocks.OMINOUS_PYRE.get());
            ominous.add(ModBlocks.OMINOUS_IDOL.get());
            ominous.add(ModBlocks.WALL_SHRINE.get());
            ominous.add(ModBlocks.MANDALA.get());
            ominous.add(ModBlocks.OMINOUS_STATUE.get());
            ominous.add(ModBlocks.OMINOUS_BRAZIER_STATUE.get());
            for (Block block : ominous){
                this.tag(ModTags.Blocks.OMINOUS_BLOCKS).add(block).replace(false);
            }
        }
    }
}
