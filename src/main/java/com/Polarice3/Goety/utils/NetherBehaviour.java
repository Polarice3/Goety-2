package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Borrowed from @baguchan's Nether Invader codes with permission: <a href="https://github.com/baguchi/NetherInvader/blob/master/src/main/java/baguchan/nether_invader/utils/NetherBehaviour.java">...</a>
 */
public interface NetherBehaviour {
    NetherBehaviour DEFAULT = new NetherBehaviour() {

        public boolean attemptSpreadVein(LevelAccessor p_222048_, BlockPos p_222049_, BlockState p_222050_, @Nullable Collection<Direction> p_222051_, boolean p_222052_) {
            if (p_222051_ == null) {
                return NetherBehaviour.super.attemptSpreadVein(p_222048_, p_222049_, p_222050_, p_222051_, p_222052_);
            } else if (!p_222051_.isEmpty()) {
                return NetherBehaviour.super.attemptSpreadVein(p_222048_, p_222049_, p_222050_, p_222051_, p_222052_);
            } else {
                return NetherBehaviour.super.attemptSpreadVein(p_222048_, p_222049_, p_222050_, p_222051_, p_222052_);
            }
        }

        public int attemptUseCharge(NetherSpreaderUtil.ChargeCursor p_222054_, LevelAccessor p_222055_, BlockPos p_222056_, RandomSource p_222057_, NetherSpreaderUtil p_222058_, boolean p_222059_) {
            return p_222054_.getCharge();
        }

        public int updateDecayDelay(int p_222061_) {
            return Math.max(p_222061_ - 1, 0);
        }
    };

    static boolean regrowNether(LevelAccessor accessor, BlockPos blockPos, BlockState originalState) {
        BlockState above = accessor.getBlockState(blockPos.above());
        if ((originalState.is(BlockTags.NYLIUM) || originalState.getBlock() instanceof FungusBlock || above.getBlock() instanceof FungusBlock) && originalState.getBlock() instanceof BonemealableBlock bonemealableBlock) {
            if (accessor instanceof ServerLevel serverLevel) {
                if (serverLevel.random.nextInt(16) == 0) {
                    if (originalState.getBlock() instanceof FungusBlock){
                        bonemealableBlock.performBonemeal(serverLevel, accessor.getRandom(), blockPos, originalState);
                    } else if (above.getBlock() instanceof FungusBlock){
                        bonemealableBlock.performBonemeal(serverLevel, accessor.getRandom(), blockPos.above(), originalState);
                    }
                    return true;
                } else if (serverLevel.random.nextInt(64) == 0){
                    bonemealableBlock.performBonemeal(serverLevel, accessor.getRandom(), blockPos, originalState);
                    return true;
                }
            }
        }

        if (originalState.is(Tags.Blocks.ORES_IRON) || originalState.is(Tags.Blocks.ORES_GOLD)) {
            BlockState blockstate = Blocks.NETHER_GOLD_ORE.defaultBlockState();
            accessor.setBlock(blockPos, blockstate, 3);
            return true;
        }

        if (originalState.is(Tags.Blocks.ORES_COAL) || originalState.is(Tags.Blocks.ORES_COPPER)) {
            BlockState blockstate = Blocks.NETHER_QUARTZ_ORE.defaultBlockState();
            accessor.setBlock(blockPos, blockstate, 3);
            return true;
        }

        if (originalState.is(BlockTags.LOGS_THAT_BURN)){
            BlockState blockstate = Blocks.BASALT.defaultBlockState().setValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS));
            accessor.setBlock(blockPos, blockstate, 3);
            accessor.levelEvent(2001, blockPos, Block.getId(blockstate));
            changeBiome(accessor, blockPos);
            return true;
        }

        if (accessor instanceof Level level) {
            if (BlockFinder.canBeReplaced(level, blockPos) && !originalState.isAir() && !(originalState.getBlock() instanceof LiquidBlock) && !(originalState.getBlock() instanceof BaseFireBlock)) {
                if (level.random.nextFloat() <= 0.15F || (originalState.getBlock() instanceof IPlantable plantable && !Blocks.NETHERRACK.defaultBlockState().canSustainPlant(accessor, blockPos.below(), Direction.UP, plantable))){
                    BlockState fire = BaseFireBlock.getState(accessor, blockPos);
                    accessor.setBlock(blockPos, fire, 3);
                    accessor.levelEvent(2001, blockPos, Block.getId(fire));
                }
                changeBiome(accessor, blockPos);
                return true;
            }
        }

        if (originalState.is(Blocks.WATER)){
            BlockState blockstate = Blocks.MAGMA_BLOCK.defaultBlockState();
            accessor.setBlock(blockPos, blockstate, 3);
            accessor.levelEvent(2001, blockPos, Block.getId(blockstate));
            changeBiome(accessor, blockPos);
            return true;
        } else if (originalState.is(Blocks.DRIPSTONE_BLOCK)){
            BlockState blockstate = Blocks.BASALT.defaultBlockState();
            accessor.setBlock(blockPos, blockstate, 3);
            accessor.levelEvent(2001, blockPos, Block.getId(blockstate));
            changeBiome(accessor, blockPos);
            return true;
        } else if (originalState.getBlock() instanceof SnowyDirtBlock) {
            BlockState blockstate = Blocks.CRIMSON_NYLIUM.defaultBlockState();
            Holder<Biome> holder = accessor.getBiome(blockPos);
            if (!holder.is(BiomeTags.IS_FOREST) && !holder.is(Tags.Biomes.IS_SWAMP) && !holder.is(Biomes.CRIMSON_FOREST)) {
                blockstate = Blocks.NETHERRACK.defaultBlockState();
            }
            accessor.setBlock(blockPos, blockstate, 3);
            accessor.levelEvent(2001, blockPos, Block.getId(blockstate));
            if (above.getBlock() instanceof IPlantable plantable && !blockstate.canSustainPlant(accessor, blockPos, Direction.UP, plantable)){
                BlockState fire = BaseFireBlock.getState(accessor, blockPos.above());
                accessor.setBlock(blockPos.above(), fire, 3);
                accessor.levelEvent(2001, blockPos.above(), Block.getId(fire));
            }
            changeBiome(accessor, blockPos);
            return true;
        } else if (originalState.is(Tags.Blocks.STONE)) {
            BlockState blockstate = Blocks.BLACKSTONE.defaultBlockState();
            accessor.setBlock(blockPos, blockstate, 3);
            accessor.levelEvent(2001, blockPos, Block.getId(blockstate));
            changeBiome(accessor, blockPos);
            return true;
        } else if (originalState.is(Blocks.MUD) || originalState.is(Tags.Blocks.SAND)) {
            BlockState blockstate = Blocks.SOUL_SAND.defaultBlockState();
            accessor.setBlock(blockPos, blockstate, 3);
            accessor.levelEvent(2001, blockPos, Block.getId(blockstate));
            if (above.getBlock() instanceof IPlantable plantable && !blockstate.canSustainPlant(accessor, blockPos, Direction.UP, plantable)){
                BlockState fire = BaseFireBlock.getState(accessor, blockPos.above());
                accessor.setBlock(blockPos.above(), fire, 3);
                accessor.levelEvent(2001, blockPos.above(), Block.getId(fire));
            }
            changeBiome(accessor, blockPos);
            return true;
        } else if (originalState.is(Tags.Blocks.SANDSTONE)) {
            BlockState blockstate = Blocks.SOUL_SOIL.defaultBlockState();
            accessor.setBlock(blockPos, blockstate, 3);
            accessor.levelEvent(2001, blockPos, Block.getId(blockstate));
            if (above.getBlock() instanceof IPlantable plantable && !blockstate.canSustainPlant(accessor, blockPos, Direction.UP, plantable)){
                BlockState fire = BaseFireBlock.getState(accessor, blockPos.above());
                accessor.setBlock(blockPos.above(), fire, 3);
                accessor.levelEvent(2001, blockPos.above(), Block.getId(fire));
            }
            changeBiome(accessor, blockPos);
            return true;
        } else if (originalState.is(ModTags.Blocks.NETHER_SPREAD_REPLACEABLE)) {
            BlockState blockstate = Blocks.NETHERRACK.defaultBlockState();
            accessor.setBlock(blockPos, blockstate, 3);
            accessor.levelEvent(2001, blockPos, Block.getId(blockstate));
            if (above.getBlock() instanceof IPlantable plantable && !blockstate.canSustainPlant(accessor, blockPos, Direction.UP, plantable)){
                BlockState fire = BaseFireBlock.getState(accessor, blockPos.above());
                accessor.setBlock(blockPos.above(), fire, 3);
                accessor.levelEvent(2001, blockPos.above(), Block.getId(fire));
            }
            changeBiome(accessor, blockPos);
            return true;
        }
        return false;
    }

    static void changeBiome(LevelAccessor levelAccessor, BlockPos blockPos) {
        if (MobsConfig.ObsidianMonolithBiome.get()) {
            MutableInt mutableint = new MutableInt(0);
            BoundingBox boundingbox = new BoundingBox(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ() - 1, blockPos.getX() + 1, blockPos.getY() + 20, blockPos.getZ() + 1);

            List<ChunkAccess> list = new ArrayList<>();

            if (levelAccessor instanceof ServerLevel serverLevel) {
                for (int k = SectionPos.blockToSectionCoord(boundingbox.minZ()); k <= SectionPos.blockToSectionCoord(boundingbox.maxZ()); ++k) {
                    for (int l = SectionPos.blockToSectionCoord(boundingbox.minX()); l <= SectionPos.blockToSectionCoord(boundingbox.maxX()); ++l) {
                        ChunkAccess chunkaccess = serverLevel.getChunk(l, k, ChunkStatus.FULL, false);
                        list.add(chunkaccess);
                    }
                }

                for (ChunkAccess chunkaccess1 : list) {
                    chunkaccess1.fillBiomesFromNoise(makeResolver(levelAccessor, mutableint, chunkaccess1, boundingbox), serverLevel.getChunkSource().randomState().sampler());
                    chunkaccess1.setUnsaved(true);
                }
                serverLevel.getChunkSource().chunkMap.resendBiomesForChunks(list);
            }
        }
    }

    private static BiomeResolver makeResolver(LevelAccessor levelAccessor, MutableInt p_262615_, ChunkAccess p_262698_, BoundingBox p_262622_) {
        return (p_262550_, p_262551_, p_262552_, p_262553_) -> {
            int i = QuartPos.toBlock(p_262550_);
            int j = QuartPos.toBlock(p_262551_);
            int k = QuartPos.toBlock(p_262552_);
            Holder<Biome> holder = p_262698_.getNoiseBiome(p_262550_, p_262551_, p_262552_);
            if (p_262622_.isInside(i, j, k) && !holder.is(BiomeTags.IS_NETHER)) {
                p_262615_.increment();
                if (holder.is(BiomeTags.IS_FOREST) || holder.is(Tags.Biomes.IS_SWAMP)) {
                    Optional<Holder.Reference<Biome>> biomeHolder = levelAccessor.registryAccess().registryOrThrow(Registries.BIOME).getHolder(Biomes.CRIMSON_FOREST);
                    if (biomeHolder.isPresent()) {
                        return biomeHolder.get();
                    }
                }
                if (holder.is(Tags.Biomes.IS_SANDY)) {
                    Optional<Holder.Reference<Biome>> biomeHolder = levelAccessor.registryAccess().registryOrThrow(Registries.BIOME).getHolder(Biomes.SOUL_SAND_VALLEY);
                    if (biomeHolder.isPresent()) {
                        return biomeHolder.get();
                    }
                }
                if (holder.is(Tags.Biomes.IS_MOUNTAIN)) {
                    Optional<Holder.Reference<Biome>> biomeHolder = levelAccessor.registryAccess().registryOrThrow(Registries.BIOME).getHolder(Biomes.BASALT_DELTAS);
                    if (biomeHolder.isPresent()) {
                        return biomeHolder.get();
                    }
                }
                Optional<Holder.Reference<Biome>> biomeHolder = levelAccessor.registryAccess().registryOrThrow(Registries.BIOME).getHolder(Biomes.NETHER_WASTES);
                if (biomeHolder.isPresent()) {
                    return biomeHolder.get();
                }
            } else {
                return holder;
            }
            return holder;
        };
    }

    default byte getNetherSpreadDelay() {
        return 1;
    }

    default void onDischarged(LevelAccessor p_222026_, BlockState p_222027_, BlockPos p_222028_, RandomSource p_222029_) {
    }

    default boolean depositCharge(LevelAccessor p_222031_, BlockPos p_222032_, RandomSource p_222033_) {
        return false;
    }

    default boolean attemptSpreadVein(LevelAccessor p_222034_, BlockPos p_222035_, BlockState p_222036_, @Nullable Collection<Direction> p_222037_, boolean p_222038_) {
        return regrowNether(p_222034_, p_222035_, p_222036_);
    }

    default boolean canChangeBlockStateOnSpread() {
        return true;
    }

    default int updateDecayDelay(int p_222045_) {
        return 1;
    }

    int attemptUseCharge(NetherSpreaderUtil.ChargeCursor p_222039_, LevelAccessor p_222040_, BlockPos p_222041_, RandomSource p_222042_, NetherSpreaderUtil p_222043_, boolean p_222044_);
}
