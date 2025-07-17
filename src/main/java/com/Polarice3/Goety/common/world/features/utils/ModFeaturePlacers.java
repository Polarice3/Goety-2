package com.Polarice3.Goety.common.world.features.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Stolen from @TeamTwilight: <a href="https://github.com/TeamTwilight/twilightforest/blob/1.20.1/src/main/java/twilightforest/util/FeaturePlacers.java">...</a>
 */
public final class ModFeaturePlacers {
    public static final BiFunction<LevelSimulatedReader, BlockPos, Boolean> VALID_TREE_POS = TreeFeature::validTreePos;

    public static <T extends Mob> void placeEntity(EntityType<T> entityType, BlockPos pos, ServerLevelAccessor levelAccessor) {
        Mob mob = entityType.create(levelAccessor.getLevel());

        if (mob == null) return;

        mob.setPersistenceRequired();
        mob.moveTo(pos, 0.0F, 0.0F);
        ForgeEventFactory.onFinalizeSpawn(mob, levelAccessor, levelAccessor.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null, null);
        levelAccessor.addFreshEntityWithPassengers(mob);
        levelAccessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
    }

    /**
     * Draws a line from {x1, y1, z1} to {x2, y2, z2}
     * This takes all variables for setting Branch
     */
    public static void drawBresenhamBranch(LevelAccessor world, BiConsumer<BlockPos, BlockState> trunkPlacer, RandomSource random, BlockPos start, BlockPos end, BlockStateProvider config) {
        for (BlockPos pixel : new VoxelBresenhamIterator(start, end)) {
            placeIfValidTreePos(world, trunkPlacer, random, pixel, config);
        }
    }

    /**
     * Build a root, but don't let it stick out too far into thin air because that's weird
     */
    public static void buildRoot(LevelAccessor world, BiConsumer<BlockPos, BlockState> placer, RandomSource rand, BlockPos start, double offset, int b, BlockStateProvider config) {
        BlockPos dest = ModFeatureLogic.translate(start.below(b + 2), 5, 0.3 * b + offset, 0.8);

        // go through block by block and stop drawing when we head too far into open air
        for (BlockPos coord : new VoxelBresenhamIterator(start.below(), dest)) {
            if (!placeIfValidRootPos(world, placer, rand, coord, config)) return;
        }
    }

    /**
     * Draws a line from {x1, y1, z1} to {x2, y2, z2}
     * This just takes a BlockState, used to set Trunk
     */
    public static void drawBresenhamTree(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> placer, BiFunction<LevelSimulatedReader, BlockPos, Boolean> predicate, BlockPos from, BlockPos to, BlockStateProvider config, RandomSource random) {
        for (BlockPos pixel : new VoxelBresenhamIterator(from, to)) {
            placeProvidedBlock(world, placer, predicate, pixel, config, random);
        }
    }

    public static void placeProvidedBlock(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> worldPlacer, BiFunction<LevelSimulatedReader, BlockPos, Boolean> predicate, BlockPos pos, BlockStateProvider config, RandomSource random) {
        if (predicate.apply(world, pos)) worldPlacer.accept(pos, config.getState(random, pos));
    }

    public static void placeLeaf(LevelSimulatedReader world, FoliagePlacer.FoliageSetter setter, BiFunction<LevelSimulatedReader, BlockPos, Boolean> predicate, BlockPos pos, BlockStateProvider config, RandomSource random) {
        if (predicate.apply(world, pos)) setter.set(pos, config.getState(random, pos));
    }

    // Use for trunks with Odd-count widths
    public static void placeCircleOdd(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> placer, BiFunction<LevelSimulatedReader, BlockPos, Boolean> predicate, RandomSource random, BlockPos centerPos, float radius, BlockStateProvider config) {
        // Normally, I'd use mutable pos here but there are multiple bits of logic down the line that force
        // the pos to be immutable causing multiple same BlockPos instances to exist.
        float radiusSquared = radius * radius;
        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos, config, random);

        // trace out a quadrant
        for (int x = 0; x <= radius; x++) {
            for (int z = 1; z <= radius; z++) {
                // if we're inside the blob, fill it
                if (x * x + z * z <= radiusSquared) {
                    // do four at a time for easiness!
                    ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset(  x, 0,  z), config, random);
                    ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -x, 0, -z), config, random);
                    ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -z, 0,  x), config, random);
                    ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset(  z, 0, -x), config, random);
                    // Confused how this circle pixel-filling algorithm works exactly? https://www.desmos.com/calculator/psqynhk21k
                }
            }
        }
    }

    // Use for trunks with Even-count widths
    public static void placeCircleEven(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> placer, BiFunction<LevelSimulatedReader, BlockPos, Boolean> predicate, RandomSource random, BlockPos centerPos, float radius, BlockStateProvider config) {
        // Normally, I'd use mutable pos here but there are multiple bits of logic down the line that force
        // the pos to be immutable causing multiple same BlockPos instances to exist.
        float radiusSquared = radius * radius;
        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos, config, random);

        // trace out a quadrant
        for (int x = 0; x <= radius; x++) {
            for (int z = 0; z <= radius; z++) {
                // if we're inside the blob, fill it
                if (x * x + z * z <= radiusSquared) {
                    // do four at a time for easiness!
                    ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( 1+x, 0, 1+z), config, random);
                    ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -x, 0, -z), config, random);
                    ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -x, 0, 1+z), config, random);
                    ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( 1+x, 0, -z), config, random);
                    // Confused how this circle pixel-filling algorithm works exactly? https://www.desmos.com/calculator/psqynhk21k
                }
            }
        }
    }

    public static void placeSpheroid(LevelSimulatedReader world, FoliagePlacer.FoliageSetter setter, BiFunction<LevelSimulatedReader, BlockPos, Boolean> predicate, RandomSource random, BlockPos centerPos, float xzRadius, float yRadius, float verticalBias, BlockStateProvider config) {
        float xzRadiusSquared = xzRadius * xzRadius;
        float yRadiusSquared = yRadius * yRadius;
        float superRadiusSquared = xzRadiusSquared * yRadiusSquared;
        ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos, config, random);

        for (int y = 0; y <= yRadius; y++) {
            if (y > yRadius) continue;

            ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( 0,  y, 0), config, random);
            ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( 0, -y, 0), config, random);
        }

        for (int x = 0; x <= xzRadius; x++) {
            for (int z = 1; z <= xzRadius; z++) {
                if (x * x + z * z > xzRadiusSquared) continue;

                ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  x, 0,  z), config, random);
                ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -x, 0, -z), config, random);
                ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -z, 0,  x), config, random);
                ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  z, 0, -x), config, random);

                for (int y = 1; y <= yRadius; y++) {
                    float xzSquare = ((x * x + z * z) * yRadiusSquared);

                    if (xzSquare + (((y - verticalBias) * (y - verticalBias)) * xzRadiusSquared) <= superRadiusSquared) {
                        ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  x,  y,  z), config, random);
                        ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -x,  y, -z), config, random);
                        ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -z,  y,  x), config, random);
                        ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  z,  y, -x), config, random);
                    }

                    if (xzSquare + (((y + verticalBias) * (y + verticalBias)) * xzRadiusSquared) <= superRadiusSquared) {
                        ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  x, -y,  z), config, random);
                        ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -x, -y, -z), config, random);
                        ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -z, -y,  x), config, random);
                        ModFeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  z, -y, -x), config, random);
                    }
                }
            }
        }
    }

    // Version without the `verticalBias` unlike above
    public static void placeSpheroid(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> placer, BiFunction<LevelSimulatedReader, BlockPos, Boolean> predicate, RandomSource random, BlockPos centerPos, float xzRadius, float yRadius, BlockStateProvider config) {
        float xzRadiusSquared = xzRadius * xzRadius;
        float yRadiusSquared = yRadius * yRadius;
        float superRadiusSquared = xzRadiusSquared * yRadiusSquared;
        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos, config, random);

        for (int y = 0; y <= yRadius; y++) {
            if (y > yRadius) continue;

            ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( 0,  y, 0), config, random);
            ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( 0, -y, 0), config, random);
        }

        for (int x = 0; x <= xzRadius; x++) {
            for (int z = 1; z <= xzRadius; z++) {
                if (x * x + z * z > xzRadiusSquared) continue;

                ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset(  x, 0,  z), config, random);
                ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -x, 0, -z), config, random);
                ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -z, 0,  x), config, random);
                ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset(  z, 0, -x), config, random);

                for (int y = 1; y <= yRadius; y++) {
                    float xzSquare = ((x * x + z * z) * yRadiusSquared);

                    if (xzSquare + (y * y) * xzRadiusSquared <= superRadiusSquared) {
                        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset(  x,  y,  z), config, random);
                        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -x,  y, -z), config, random);
                        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -z,  y,  x), config, random);
                        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset(  z,  y, -x), config, random);

                        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset(  x, -y,  z), config, random);
                        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -x, -y, -z), config, random);
                        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset( -z, -y,  x), config, random);
                        ModFeaturePlacers.placeProvidedBlock(world, placer, predicate, centerPos.offset(  z, -y, -x), config, random);
                    }
                }
            }
        }
    }

    // [VanillaCopy] TrunkPlacer.placeLog - Swapped TreeConfiguration for BlockStateProvider
    // If possible, use TrunkPlacer.placeLog instead
    public static boolean placeIfValidTreePos(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> placer, RandomSource random, BlockPos pos, BlockStateProvider config) {
        if (TreeFeature.validTreePos(world, pos)) {
            placer.accept(pos, config.getState(random, pos));
            return true;
        } else {
            return false;
        }
    }

    public static boolean placeIfValidRootPos(LevelSimulatedReader world, BiConsumer<BlockPos, BlockState> placer, RandomSource random, BlockPos pos, BlockStateProvider config) {
        if (ModFeatureLogic.canRootGrowIn(world, pos)) {
            placer.accept(pos, config.getState(random, pos));
            return true;
        } else {
            return false;
        }
    }

    private static void setIfEmpty(LevelAccessor world, BlockPos pos, BlockState state) {
        if (world.isEmptyBlock(pos)) {
            world.setBlock(pos, state,3);
        }
    }

    public static BlockState transferAllStateKeys(BlockState stateIn, Block blockOut) {
        return transferAllStateKeys(stateIn, blockOut.defaultBlockState());
    }

    public static BlockState transferAllStateKeys(BlockState stateIn, BlockState stateOut) {
        for (Property<?> property : stateOut.getProperties()) {
            stateOut = transferStateKey(stateIn, stateOut, property);
        }
        return stateOut;
    }

    public static <T extends Comparable<T>> BlockState transferStateKey(BlockState stateIn, BlockState stateOut, Property<T> property) {
        if(!stateIn.hasProperty(property) || !stateOut.hasProperty(property)) return stateOut;
        return stateOut.setValue(property, stateIn.getValue(property));
    }

    public static boolean isAreaSuitable(WorldGenLevel world, BlockPos pos, int width, int height, int depth) {
        return isAreaSuitable(world, pos, width, height, depth, false);
    }
    /**
     * Checks an area to see if it consists of flat natural ground below and air above
     */
    public static boolean isAreaSuitable(WorldGenLevel world, BlockPos pos, int width, int height, int depth, boolean underwaterAllowed) {
        boolean flag = true;

        // check if there's anything within the diameter
        for (int cx = 0; cx < width; cx++) {
            for (int cz = 0; cz < depth; cz++) {
                BlockPos pos_ = pos.offset(cx, 0, cz);
                // check if the blocks even exist?
                if (world.hasChunkAt(pos_)) {
                    // is there grass, dirt or stone below?
                    BlockState state = world.getBlockState(pos_.below());
                    if (!state.isSolidRender(world, pos_) || ModFeatureLogic.isBlockNotOk(state)) {
                        if (underwaterAllowed && state.liquid()) {
                            continue;
                        }
                        flag = false;
                    }

                    for (int cy = 0; cy < height; cy++) {
                        // blank space above?
                        if (!world.isEmptyBlock(pos_.above(cy)) && !world.getBlockState(pos_.above(cy)).canBeReplaced()) {
                            if(underwaterAllowed && world.getBlockState(pos_.above(cy)).liquid()) {
                                continue;
                            }
                            flag = false;
                        }
                    }
                } else {
                    flag = false;
                }
            }
        }

        // Okie dokie
        return flag;
    }

    /**
     * Draw a giant blob of whatevs.
     */
    public static void drawBlob(LevelAccessor world, BlockPos pos, int rad, BlockState state) {
        // then trace out a quadrant
        for (byte dx = 0; dx <= rad; dx++) {
            for (byte dy = 0; dy <= rad; dy++) {
                for (byte dz = 0; dz <= rad; dz++) {
                    // determine how far we are from the center.
                    int dist;
                    if (dx >= dy && dx >= dz) {
                        dist = dx + (Math.max(dy, dz) >> 1) + (Math.min(dy, dz) >> 2);
                    } else if (dy >= dx && dy >= dz) {
                        dist = dy + (Math.max(dx, dz) >> 1) + (Math.min(dx, dz) >> 2);
                    } else {
                        dist = dz + (Math.max(dx, dy) >> 1) + (Math.min(dx, dy) >> 2);
                    }


                    // if we're inside the blob, fill it
                    if (dist <= rad) {
                        // do eight at a time for easiness!
                        world.setBlock(pos.offset(+dx, +dy, +dz), state, 3);
                        world.setBlock(pos.offset(+dx, +dy, -dz), state, 3);
                        world.setBlock(pos.offset(-dx, +dy, +dz), state, 3);
                        world.setBlock(pos.offset(-dx, +dy, -dz), state, 3);
                        world.setBlock(pos.offset(+dx, -dy, +dz), state, 3);
                        world.setBlock(pos.offset(+dx, -dy, -dz), state, 3);
                        world.setBlock(pos.offset(-dx, -dy, +dz), state, 3);
                        world.setBlock(pos.offset(-dx, -dy, -dz), state, 3);
                    }
                }
            }
        }
    }

    /**
     * Does the block have at least 1 air block adjacent
     */
    private static final Direction[] directionsExceptDown = new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public static boolean hasAirAround(LevelAccessor world, BlockPos pos) {
        for (Direction e : directionsExceptDown) {
            if (world.isEmptyBlock(pos.relative(e))) {
                return true;
            }
        }

        return false;
    }

    public static boolean isNearSolid(LevelReader world, BlockPos pos) {
        for (Direction e : Direction.values()) {
            if (world.hasChunkAt(pos.relative(e))
                    && world.getBlockState(pos.relative(e)).isSolid()) {
                return true;
            }
        }

        return false;
    }
}
