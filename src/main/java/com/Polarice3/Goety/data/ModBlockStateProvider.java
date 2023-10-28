package com.Polarice3.Goety.data;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, Goety.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        builtinEntity(ModBlocks.HAUNTED_HANGING_SIGN.get(), "goety:block/stripped_haunted_log");
        builtinEntity(ModBlocks.HAUNTED_WALL_HANGING_SIGN.get(), "goety:block/stripped_haunted_log");
        builtinEntity(ModBlocks.ROTTEN_HANGING_SIGN.get(), "goety:block/stripped_rotten_log");
        builtinEntity(ModBlocks.ROTTEN_WALL_HANGING_SIGN.get(), "goety:block/stripped_rotten_log");
    }

    protected void builtinEntity(Block b, String particle) {
        simpleBlock(b, models().getBuilder(name(b))
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", particle));
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

}
