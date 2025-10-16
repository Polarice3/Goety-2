package com.Polarice3.Goety.common.world.features;

import com.Polarice3.Goety.init.ModTags;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;

public class EndChorusFeature extends Feature<NetherForestVegetationConfig> {
    public EndChorusFeature(Codec<NetherForestVegetationConfig> p_66361_) {
        super(p_66361_);
    }

    public boolean place(FeaturePlaceContext<NetherForestVegetationConfig> p_160068_) {
        WorldGenLevel worldgenlevel = p_160068_.level();
        BlockPos blockpos = p_160068_.origin();
        BlockState blockstate = worldgenlevel.getBlockState(blockpos.below());
        NetherForestVegetationConfig netherforestvegetationconfig = p_160068_.config();
        RandomSource randomsource = p_160068_.random();
        if (!blockstate.is(ModTags.Blocks.CHORUS_GROW)) {
            return false;
        } else {
            int i = blockpos.getY();
            if (i >= worldgenlevel.getMinBuildHeight() + 1 && i + 1 < worldgenlevel.getMaxBuildHeight()) {
                int j = 0;

                for(int k = 0; k < netherforestvegetationconfig.spreadWidth * netherforestvegetationconfig.spreadWidth; ++k) {
                    BlockPos blockpos1 = blockpos.offset(randomsource.nextInt(netherforestvegetationconfig.spreadWidth) - randomsource.nextInt(netherforestvegetationconfig.spreadWidth), randomsource.nextInt(netherforestvegetationconfig.spreadHeight) - randomsource.nextInt(netherforestvegetationconfig.spreadHeight), randomsource.nextInt(netherforestvegetationconfig.spreadWidth) - randomsource.nextInt(netherforestvegetationconfig.spreadWidth));
                    BlockState blockstate1 = netherforestvegetationconfig.stateProvider.getState(randomsource, blockpos1);
                    if (!blockstate1.isAir() && worldgenlevel.isEmptyBlock(blockpos1) && blockpos1.getY() > worldgenlevel.getMinBuildHeight() && blockstate1.canSurvive(worldgenlevel, blockpos1)) {
                        worldgenlevel.setBlock(blockpos1, blockstate1, 2);
                        ++j;
                    }
                }

                return j > 0;
            } else {
                return false;
            }
        }
    }
}
