package com.Polarice3.Goety.common.world.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class ReplaceProcessor extends StructureProcessor {
    public static final Codec<ReplaceProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("chance").orElse(1.0F).forGetter((processor) -> processor.chance),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_replace").forGetter((processor) -> processor.toReplace),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("replace_to").forGetter((processor) -> processor.replaceTo)
    ).apply(instance, ReplaceProcessor::new));
    private final float chance;
    private final Block toReplace;
    private final Block replaceTo;

    public ReplaceProcessor(float chance, Block toReplace, Block replaceTo) {
        this.chance = chance;
        this.toReplace = toReplace;
        this.replaceTo = replaceTo;
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74016_, BlockPos p_74017_, BlockPos p_74018_, StructureTemplate.StructureBlockInfo p_74019_, StructureTemplate.StructureBlockInfo p_74020_, StructurePlaceSettings p_74021_) {
        RandomSource random = p_74021_.getRandom(p_74020_.pos());
        BlockState oldState = p_74020_.state();
        BlockPos blockPos = p_74020_.pos();
        BlockState newState = null;
        if (oldState.is(this.toReplace)) {
            newState = this.replaceBlock(random, oldState);
        }

        return newState != null ? new StructureTemplate.StructureBlockInfo(blockPos, newState, p_74020_.nbt()) : p_74020_;
    }

    @Nullable
    private BlockState replaceBlock(RandomSource random, BlockState oldState) {
        if (random.nextFloat() > this.chance) {
            return null;
        }
        return this.replaceTo.withPropertiesOf(oldState);
    }

    protected StructureProcessorType<?> getType() {
        return ModProcessors.REPLACE_PROCESSOR.get();
    }
}
