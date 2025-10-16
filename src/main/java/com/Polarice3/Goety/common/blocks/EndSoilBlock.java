package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.world.features.ConfiguredFeatures;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.MapColor;

public class EndSoilBlock extends Block implements BonemealableBlock {

    public EndSoilBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .strength(1.25F)
                .sound(SoundType.SAND));
    }

    public boolean isValidBonemealTarget(LevelReader p_256229_, BlockPos p_256432_, BlockState p_255677_, boolean p_256630_) {
        return p_256229_.getBlockState(p_256432_.above()).isAir();
    }

    public boolean isBonemealSuccess(Level p_221275_, RandomSource p_221276_, BlockPos p_221277_, BlockState p_221278_) {
        return true;
    }

    public void performBonemeal(ServerLevel p_221270_, RandomSource p_221271_, BlockPos p_221272_, BlockState p_221273_) {
        BlockPos blockpos = p_221272_.above();
        BlockState blockstate = ModBlocks.CHORUS_STALK.get().defaultBlockState();

        label49:
        for(int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;

            for(int j = 0; j < i / 16; ++j) {
                blockpos1 = blockpos1.offset(p_221271_.nextInt(3) - 1, (p_221271_.nextInt(3) - 1) * p_221271_.nextInt(3) / 2, p_221271_.nextInt(3) - 1);
                if (!p_221270_.getBlockState(blockpos1.below()).is(this) || p_221270_.getBlockState(blockpos1).isCollisionShapeFullBlock(p_221270_, blockpos1)) {
                    continue label49;
                }
            }

            BlockState blockstate1 = p_221270_.getBlockState(blockpos1);
            BlockState blockstate2 = p_221270_.getBlockState(blockpos1.below());
            if (blockstate1.is(blockstate.getBlock()) && p_221271_.nextInt(10) == 0) {
                ((BonemealableBlock)blockstate.getBlock()).performBonemeal(p_221270_, p_221271_, blockpos1, blockstate1);
            }

            if (blockstate1.isAir() && blockstate2.is(ModTags.Blocks.END_SOIL_BLOCKS) && blockstate2.is(ModTags.Blocks.CHORUS_GROW)) {
                ResourceKey<ConfiguredFeature<?, ?>> resourcekey = ConfiguredFeatures.END_SOIL_BONEMEAL;

                Holder<ConfiguredFeature<?, ?>> holder = p_221270_.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse(null);
                if (holder != null) {
                    holder.value().place(p_221270_, p_221270_.getChunkSource().getGenerator(), p_221271_, blockpos1);
                }
            }
        }

    }
}
