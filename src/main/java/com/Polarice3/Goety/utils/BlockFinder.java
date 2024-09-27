package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.blocks.entities.ShriekObeliskBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class BlockFinder {

    public static boolean isScytheBreak(BlockState blockState){
        return (blockState.is(BlockTags.CROPS) || blockState.getBlock() instanceof BushBlock) && !(blockState.getBlock() instanceof StemBlock);
    }

    public static double moveDownToGround(Entity entity) {
        HitResult rayTrace = rayTrace(entity);
        if (rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) rayTrace;
            if (hitResult.getDirection() == Direction.UP) {
                BlockState hitBlock = entity.level.getBlockState(hitResult.getBlockPos());
                if (hitBlock.getBlock() instanceof SlabBlock && hitBlock.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
                    return hitResult.getBlockPos().getY() + 1.0625F - 0.5F;
                } else {
                    return hitResult.getBlockPos().getY() + 1.0625F;
                }
            }
        }
        return entity.getY();
    }

    private static HitResult rayTrace(Entity entity) {
        Vec3 startPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        Vec3 endPos = new Vec3(entity.getX(), 0, entity.getZ());
        return entity.level.clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
    }

    public static boolean canSeeBlock(Entity looker, BlockPos location) {
        Vec3 vec3 = new Vec3(looker.getX(), looker.getEyeY(), looker.getZ());
        Vec3 vec31 = Vec3.atBottomCenterOf(location);
        if (vec31.distanceTo(vec3) > 128.0D) {
            return false;
        } else {
            return looker.level.clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, looker)).getType() == HitResult.Type.MISS;
        }
    }

    public static double distanceFromGround(Entity entity){
        HitResult rayTrace = rayTrace(entity);
        if (rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) rayTrace;
            if (hitResult.getDirection() == Direction.UP) {
                BlockState hitBlock = entity.level.getBlockState(hitResult.getBlockPos());
                if (hitBlock.getBlock() instanceof SlabBlock && hitBlock.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
                    return entity.getY() - (hitResult.getBlockPos().getY() - 0.5F);
                } else {
                    return entity.getY() - hitResult.getBlockPos().getY();
                }
            }
        }
        return 0;
    }

    public static double moveBlockDownToGround(Level level, BlockPos blockPos) {
        HitResult rayTrace = blockRayTrace(level, blockPos);
        if (rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) rayTrace;
            if (hitResult.getDirection() == Direction.UP) {
                BlockState hitBlock = level.getBlockState(hitResult.getBlockPos());
                if (hitBlock.getBlock() instanceof SlabBlock && hitBlock.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
                    return hitResult.getBlockPos().getY() + 1.0625F - 0.5F;
                } else {
                    return hitResult.getBlockPos().getY() + 1.0625F;
                }
            }
        }
        return blockPos.getY();
    }

    private static HitResult blockRayTrace(Level level, BlockPos blockPos) {
        Vec3 startPos = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Vec3 endPos = new Vec3(blockPos.getX(), 0, blockPos.getZ());
        return level.clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
    }

    public static boolean hasChunksAt(LivingEntity livingEntity){
        Level world = livingEntity.level;
        BlockPos.MutableBlockPos blockpos$mutable = livingEntity.blockPosition().mutable().move(0, 0, 0);
        return world.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10);
    }

    public static boolean isEmptyBlock(BlockGetter pLevel, BlockPos pPos, BlockState pBlockState, FluidState pFluidState, EntityType<?> pEntityType, boolean pWater) {
        if (pWater){
            if (pBlockState.isCollisionShapeFullBlock(pLevel, pPos)) {
                return false;
            } else {
                return !pEntityType.isBlockDangerous(pBlockState) || pFluidState.isEmpty();
            }
        } else {
            if (pBlockState.isCollisionShapeFullBlock(pLevel, pPos)) {
                return false;
            } else if (!pFluidState.isEmpty()) {
                return false;
            } else {
                return !pEntityType.isBlockDangerous(pBlockState);
            }
        }
    }

    public static double spawnWaterY(LivingEntity livingEntity, BlockPos blockPos) {
        BlockPos blockpos = blockPos;
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            FluidState fluidState = livingEntity.level.getFluidState(blockpos);
            BlockState blockstate = livingEntity.level.getBlockState(blockpos1);
            if (fluidState.is(FluidTags.WATER)) {
                if (!livingEntity.level.isWaterAt(blockpos)) {
                    BlockState blockstate1 = livingEntity.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(livingEntity.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            } else if (blockstate.isFaceSturdy(livingEntity.level, blockpos1, Direction.UP)) {
                if (!livingEntity.level.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = livingEntity.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(livingEntity.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(livingEntity.getY()) - 1);

        if (flag) {
            if (!(blockpos.getY() + d0 > livingEntity.getY() + 5)) {
                return blockpos.getY() + d0;
            } else {
                return livingEntity.getY();
            }
        } else {
            return livingEntity.getY();
        }
    }

    public static BlockPos SummonPosition(LivingEntity livingEntity, BlockPos blockPos){
        return SummonPosition(livingEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos SummonPosition(LivingEntity livingEntity, double x, double y, double z){
        double d3 = y;
        boolean flag = false;
        BlockPos blockpos = new BlockPos(x, y, z);
        Level level = livingEntity.level;
        if (level.isLoaded(blockpos)) {
            boolean flag1 = false;

            while(!flag1 && blockpos.getY() > level.getMinBuildHeight()) {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = level.getBlockState(blockpos1);
                if (blockstate.getMaterial().blocksMotion()) {
                    flag1 = true;
                } else {
                    --d3;
                    blockpos = blockpos1;
                }
            }

            if (flag1) {
                if (level.noCollision(livingEntity) && !level.containsAnyLiquid(livingEntity.getBoundingBox())) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            return blockpos;
        } else {
            return new BlockPos(x, d3, z);
        }
    }

    public static BlockPos SummonRadius(BlockPos blockPos, LivingEntity livingEntity, Level world){
        return SummonRadius(blockPos, livingEntity, world, 5);
    }

    public static BlockPos SummonRadius(BlockPos blockPos, LivingEntity livingEntity, Level world, int radius){
        return SummonRadius(blockPos, livingEntity, world, 64, radius);
    }

    public static BlockPos SummonRadius(BlockPos blockPos, LivingEntity livingEntity, Level world, int attempts, int radius){
        for (int i = 0; i < attempts; ++i) {
            BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable().move(0, 0, 0);
            blockpos$mutable.setX(blockpos$mutable.getX() + world.random.nextInt(radius) - world.random.nextInt(radius));
            blockpos$mutable.setY(blockPos.getY());
            blockpos$mutable.setZ(blockpos$mutable.getZ() + world.random.nextInt(radius) - world.random.nextInt(radius));
            if (world.noCollision(livingEntity, livingEntity.getBoundingBox().move(blockpos$mutable))
                    && !world.containsAnyLiquid(livingEntity.getBoundingBox().move(blockpos$mutable))
                    && blockpos$mutable.distToCenterSqr(Vec3.atCenterOf(blockPos)) <= Mth.square(radius * 2)) {
                blockPos = SummonPosition(livingEntity, blockpos$mutable);
                break;
            }
        }
        return blockPos;
    }

    public static BlockPos SummonAwayRadius(BlockPos blockPos, LivingEntity livingEntity, Level world, int attempts, int radius){
        BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable();
        for (int i = 0; i < attempts; ++i) {
            int xOffset = world.random.nextIntBetweenInclusive(-radius, radius);
            int zOffset = world.random.nextIntBetweenInclusive(-radius, radius);
            blockpos$mutable.setX(blockpos$mutable.getX() + xOffset);
            blockpos$mutable.setY(blockPos.getY());
            blockpos$mutable.setZ(blockpos$mutable.getZ() + zOffset);
            if (world.noCollision(livingEntity, livingEntity.getBoundingBox().move(blockpos$mutable))
                    && !world.containsAnyLiquid(livingEntity.getBoundingBox().move(blockpos$mutable))
                    && blockpos$mutable.distToCenterSqr(Vec3.atCenterOf(blockPos)) >= Mth.square(radius / 2)) {
                blockPos = SummonPosition(livingEntity, blockpos$mutable);
                break;
            }
        }
        return blockPos;
    }

    public static BlockPos SummonRadiusSight(BlockPos blockPos, LivingEntity looker, LivingEntity summoned, Level world, int radius){
        for (int i = 0; i < 64; ++i) {
            BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable().move(0, 0, 0);
            blockpos$mutable.setX(blockpos$mutable.getX() + world.random.nextInt(radius) - world.random.nextInt(radius));
            blockpos$mutable.setY(blockPos.getY());
            blockpos$mutable.setZ(blockpos$mutable.getZ() + world.random.nextInt(radius) - world.random.nextInt(radius));
            if (world.noCollision(summoned, summoned.getBoundingBox().move(blockpos$mutable))
                    && !world.containsAnyLiquid(summoned.getBoundingBox().move(blockpos$mutable))
                    && blockpos$mutable.distToCenterSqr(Vec3.atCenterOf(blockPos)) <= Mth.square(radius * 2)
                    && canSeeBlock(looker, blockpos$mutable)) {
                blockPos = SummonPosition(summoned, blockpos$mutable);
                break;
            }
        }
        return blockPos;
    }

    public static BlockPos SummonFurtherRadius(BlockPos blockPos, LivingEntity livingEntity, Level world){
        BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable();
        for (int i = 0; i < 64; ++i) {
            blockpos$mutable.setX((int) (blockpos$mutable.getX() + (world.random.nextDouble() - 0.5D) * 16));
            blockpos$mutable.setY(blockPos.getY());
            blockpos$mutable.setZ((int) (blockpos$mutable.getZ() + (world.random.nextDouble() - 0.5D) * 16));
            if (world.noCollision(livingEntity, livingEntity.getBoundingBox().move(blockpos$mutable))
                    && !world.containsAnyLiquid(livingEntity.getBoundingBox().move(blockpos$mutable))) {
                blockPos = SummonPosition(livingEntity, blockpos$mutable);
                break;
            }
        }
        return blockPos;
    }

    public static BlockPos SummonFlyingRadius(BlockPos blockPos, LivingEntity livingEntity, Level world, int radius){
        for (int i = 0; i < 128; ++i) {
            BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable().move(0, 0, 0);
            blockpos$mutable.setX(blockpos$mutable.getX() + world.random.nextInt(radius) - world.random.nextInt(radius));
            blockpos$mutable.setY(blockPos.getY());
            blockpos$mutable.setZ(blockpos$mutable.getZ() + world.random.nextInt(radius) - world.random.nextInt(radius));
            if (world.noCollision(livingEntity, livingEntity.getBoundingBox().move(blockpos$mutable))
                    && !world.containsAnyLiquid(livingEntity.getBoundingBox().move(blockpos$mutable))
                    && blockpos$mutable.distToCenterSqr(Vec3.atCenterOf(blockPos)) <= Mth.square(radius * 2)) {
                blockPos = blockpos$mutable;
                break;
            }
        }
        return blockPos;
    }

    public static BlockPos SummonWaterRadius(LivingEntity livingEntity, Level world){
        BlockPos.MutableBlockPos blockpos$mutable = livingEntity.blockPosition().mutable().move(0, 0, 0);
        blockpos$mutable.setX(blockpos$mutable.getX() + world.random.nextInt(5) - world.random.nextInt(5));
        blockpos$mutable.setY((int) BlockFinder.spawnWaterY(livingEntity, livingEntity.blockPosition()));
        blockpos$mutable.setZ(blockpos$mutable.getZ() + world.random.nextInt(5) - world.random.nextInt(5));
        if (hasChunksAt(livingEntity)
                && isEmptyBlock(world, blockpos$mutable, world.getBlockState(blockpos$mutable), world.getFluidState(blockpos$mutable), ModEntityType.ZOMBIE_SERVANT.get(), true)){
            return blockpos$mutable;
        } else {
            return livingEntity.blockPosition().mutable().move(0, (int) BlockFinder.spawnWaterY(livingEntity, livingEntity.blockPosition()), 0);
        }
    }

    /**
     * Based on Spawning codes from Man From the Fog Reimagined: <a href="https://github.com/z3n01d/man-from-the-fog-reimagined/blob/master/src/main/java/com/zen/the_fog/common/other/Util.java">...</a>
     */
    public static Vec3 getRandomSpawnBehindDirection(ServerLevel serverLevel, Random random, Vec3 origin, Vec3 direction, int minRange, int maxRange) {
        direction = direction.scale(-1);
        direction = direction.yRot((float) Math.toRadians((random.nextFloat(-60, 60))));
        if (minRange == maxRange) {
            direction = direction.scale(minRange);
        } else {
            direction = direction.scale(maxRange > minRange ? random.nextInt(minRange, maxRange) : random.nextInt(maxRange, minRange));
        }

        BlockPos blockPos = new BlockPos(origin.add(direction));
        BlockState blockState = serverLevel.getBlockState(blockPos);

        while (!blockState.isAir()) {
            blockPos = blockPos.above();
            blockState = serverLevel.getBlockState(blockPos);
            if (blockPos.getY() >= serverLevel.getMaxBuildHeight()) {
                break;
            }
        }

        BlockState blockStateDown = serverLevel.getBlockState(blockPos.below());

        while (blockStateDown.isAir()) {
            blockPos = blockPos.below();
            blockStateDown = serverLevel.getBlockState(blockPos.below());
            if (blockPos.getY() < serverLevel.getMinBuildHeight()) {
                break;
            }
        }

        return Vec3.atCenterOf(blockPos);
    }

    public static Vec3 getRandomSpawnBehindDirection(ServerLevel serverLevel, Random random, Vec3 origin, Vec3 direction) {
        return getRandomSpawnBehindDirection(serverLevel, random, origin, direction, 40, 64);
    }

    public static boolean findStructure(Level level, BlockPos blockPos, ResourceKey<Structure> resourceKey){
        if (level instanceof ServerLevel serverLevel) {
            return findStructure(serverLevel, blockPos, resourceKey);
        } else {
            return false;
        }
    }

    public static boolean findStructure(ServerLevel serverLevel, LivingEntity livingEntity, ResourceKey<Structure> resourceKey){
        return findStructure(serverLevel, livingEntity.blockPosition(), resourceKey);
    }

    public static boolean findStructure(ServerLevel serverLevel, BlockPos blockPos, ResourceKey<Structure> resourceKey){
        Structure structure = serverLevel.structureManager().registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).get(resourceKey);
        if (structure != null) {
            StructureStart structureStart = serverLevel.structureManager().getStructureWithPieceAt(blockPos, structure);
            if (!structureStart.getPieces().isEmpty()) {
                return structureStart.getBoundingBox().isInside(blockPos);
            }
        }
        return false;
    }

    public static boolean findStructure(Level level, BlockPos blockPos, TagKey<Structure> structureTagKey){
        if (level instanceof ServerLevel serverLevel) {
            return findStructure(serverLevel, blockPos, structureTagKey);
        } else {
            return false;
        }
    }

    public static boolean findStructure(ServerLevel serverLevel, LivingEntity livingEntity, TagKey<Structure> structureTagKey){
        return serverLevel.structureManager().getStructureWithPieceAt(livingEntity.blockPosition(), structureTagKey).isValid();
    }

    public static boolean findStructure(ServerLevel serverLevel, BlockPos blockPos, TagKey<Structure> structureTagKey){
        return serverLevel.structureManager().getStructureWithPieceAt(blockPos, structureTagKey).isValid();
    }

    public static boolean isEmptyBox(Level level, BlockPos p_46860_){
        return level.isEmptyBlock(p_46860_)
                && level.isEmptyBlock(p_46860_.below())
                && level.isEmptyBlock(p_46860_.above())
                && level.isEmptyBlock(p_46860_.west())
                && level.isEmptyBlock(p_46860_.east())
                && level.isEmptyBlock(p_46860_.west().north())
                && level.isEmptyBlock(p_46860_.west().south())
                && level.isEmptyBlock(p_46860_.east().north())
                && level.isEmptyBlock(p_46860_.east().south())
                && level.isEmptyBlock(p_46860_.north())
                && level.isEmptyBlock(p_46860_.south())
                && level.isEmptyBlock(p_46860_.above().west())
                && level.isEmptyBlock(p_46860_.above().east())
                && level.isEmptyBlock(p_46860_.above().west().north())
                && level.isEmptyBlock(p_46860_.above().west().south())
                && level.isEmptyBlock(p_46860_.above().east().north())
                && level.isEmptyBlock(p_46860_.above().east().south())
                && level.isEmptyBlock(p_46860_.above().north())
                && level.isEmptyBlock(p_46860_.above().south())
                && level.isEmptyBlock(p_46860_.below().west())
                && level.isEmptyBlock(p_46860_.below().east())
                && level.isEmptyBlock(p_46860_.below().west().north())
                && level.isEmptyBlock(p_46860_.below().west().south())
                && level.isEmptyBlock(p_46860_.below().east().north())
                && level.isEmptyBlock(p_46860_.below().east().south())
                && level.isEmptyBlock(p_46860_.below().north())
                && level.isEmptyBlock(p_46860_.below().south());
    }

    private static final Predicate<Block> isAir = (block) -> block == Blocks.AIR || block == Blocks.CAVE_AIR;

    public static boolean emptySpaceBetween(Level level, BlockPos blockPos, int distance, boolean up){
        BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable();
        boolean flag = false;
        if (up){
            while (blockpos$mutable.getY() < blockPos.getY() + distance && (level.getBlockState(blockpos$mutable).isAir() || canBeReplaced(level, blockpos$mutable))){
                blockpos$mutable.move(Direction.UP);
                flag = true;
            }
        } else {
            while (blockpos$mutable.getY() > blockPos.getY() - distance && level.getBlockState(blockpos$mutable).isAir() || canBeReplaced(level, blockpos$mutable)){
                blockpos$mutable.move(Direction.DOWN);
                flag = true;
            }
        }
        if (!level.getBlockState(blockpos$mutable).isAir()){
            flag = false;
        }
        return flag;
    }

    public static boolean emptySquareSpace(Level level, BlockPos blockPos, int distance, boolean up){
        return emptySpaceBetween(level, blockPos, distance, up)
                && emptySpaceBetween(level, blockPos.north(), distance, up)
                && emptySpaceBetween(level, blockPos.south(), distance, up)
                && emptySpaceBetween(level, blockPos.west(), distance, up)
                && emptySpaceBetween(level, blockPos.east(), distance, up)
                && emptySpaceBetween(level, blockPos.west().north(), distance, up)
                && emptySpaceBetween(level, blockPos.west().south(), distance, up)
                && emptySpaceBetween(level, blockPos.east().north(), distance, up)
                && emptySpaceBetween(level, blockPos.east().south(), distance, up);
    }

    public static boolean getVerticalBlock(Level level, BlockPos blockPos, BlockState blockState, int distance, boolean up){
        BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable();
        boolean flag = false;
        if (up){
            while (blockpos$mutable.getY() < blockPos.getY() + distance && level.getBlockState(blockpos$mutable).isAir()){
                blockpos$mutable.move(Direction.UP);
                flag = true;
            }
        } else {
            while (blockpos$mutable.getY() > blockPos.getY() - distance && level.getBlockState(blockpos$mutable).isAir()){
                blockpos$mutable.move(Direction.DOWN);
                flag = true;
            }
        }
        if (level.getBlockState(blockpos$mutable) != blockState){
            flag = false;
        }
        return flag;
    }

    public static Iterable<BlockPos> multiBlockBreak(LivingEntity livingEntity, BlockPos blockPos, int x, int y, int z){
        BlockHitResult blockHitResult = MobUtil.rayTrace(livingEntity, 10, false);
        Direction direction = blockHitResult.getDirection();
        boolean hasX = direction.getStepX() == 0;
        boolean hasY = direction.getStepY() == 0;
        boolean hasZ = direction.getStepZ() == 0;
        Vec3i start = new Vec3i(hasX ? -x : 0, hasY ? -y : 0, hasZ ? -z : 0);
        Vec3i end = new Vec3i(hasX ? x : 0, hasY ? (y * 2) - 1 : 0, hasZ ? z : 0);
        return BlockPos.betweenClosed(
                blockPos.offset(start),
                blockPos.offset(end));
    }

    public static void spawnRedstoneParticles(Level pLevel, BlockPos pPos) {
        double d0 = 0.5625D;
        RandomSource random = pLevel.random;

        for(Direction direction : Direction.values()) {
            BlockPos blockpos = pPos.relative(direction);
            if (!pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5D + d0 * (double)direction.getStepX() : (double)random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + d0 * (double)direction.getStepY() : (double)random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + d0 * (double)direction.getStepZ() : (double)random.nextFloat();
                pLevel.addParticle(DustParticleOptions.REDSTONE, (double)pPos.getX() + d1, (double)pPos.getY() + d2, (double)pPos.getZ() + d3, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    public static BlockState findBlock(Level pLevel, BlockPos initial, int range){
        return findBlock(pLevel, initial, range, range, range);
    }

    public static BlockState findBlock(Level pLevel, BlockPos initial, int xRange, int yRange, int zRange){
        BlockState blockState = pLevel.getBlockState(initial);
        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos blockpos1 = initial.offset(i, j, k);
                    blockState = pLevel.getBlockState(blockpos1);
                }
            }
        }
        return blockState;
    }

    @Nullable
    public static BlockEntity findBlockEntity(BlockEntityType<?> blockEntityType, Level pLevel, BlockPos initial, int range){
        return findBlockEntity(blockEntityType, pLevel, initial, range, range, range);
    }

    @Nullable
    public static BlockEntity findBlockEntity(BlockEntityType<?> blockEntityType, Level pLevel, BlockPos initial, int xRange, int yRange, int zRange){
        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos blockPos = initial.offset(i, j, k);
                    if (pLevel.getBlockEntity(blockPos) != null){
                        BlockEntity blockEntity = pLevel.getBlockEntity(blockPos);
                        if (blockEntity != null){
                            if (blockEntity.getType() == blockEntityType){
                                return blockEntity;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void copyValues(Level level, BlockPos blockPos, BlockState newBlock, BlockState oldBlock){
        if (oldBlock.getBlock() instanceof SlabBlock && newBlock.getBlock() instanceof SlabBlock) {
            copySlab(level, blockPos, newBlock, oldBlock);
        } else if (oldBlock.getBlock() instanceof StairBlock && newBlock.getBlock() instanceof StairBlock){
            copyStairs(level, blockPos, newBlock, oldBlock);
        } else if (oldBlock.getBlock() instanceof WallBlock && newBlock.getBlock() instanceof WallBlock){
            copyWalls(level, blockPos, newBlock, oldBlock);
        }
    }

    public static void copySlab(Level level, BlockPos blockPos, BlockState newBlock, BlockState oldBlock){
        if (oldBlock.getBlock() instanceof SlabBlock && newBlock.getBlock() instanceof SlabBlock) {
            level.setBlockAndUpdate(blockPos, newBlock.setValue(SlabBlock.TYPE, oldBlock.getValue(SlabBlock.TYPE)));
        }
    }

    public static void copyStairs(Level level, BlockPos blockPos, BlockState newBlock, BlockState oldBlock){
        if (oldBlock.getBlock() instanceof StairBlock && newBlock.getBlock() instanceof StairBlock) {
            level.setBlockAndUpdate(blockPos, newBlock.setValue(StairBlock.FACING, oldBlock.getValue(StairBlock.FACING)).setValue(StairBlock.HALF, oldBlock.getValue(StairBlock.HALF)).setValue(StairBlock.SHAPE, oldBlock.getValue(StairBlock.SHAPE)));
        }
    }

    public static void copyWalls(Level level, BlockPos blockPos, BlockState newBlock, BlockState oldBlock){
        if (oldBlock.getBlock() instanceof WallBlock && newBlock.getBlock() instanceof WallBlock) {
            level.setBlockAndUpdate(blockPos, newBlock.setValue(WallBlock.UP, oldBlock.getValue(WallBlock.UP)).setValue(WallBlock.WEST_WALL, oldBlock.getValue(WallBlock.WEST_WALL)).setValue(WallBlock.EAST_WALL, oldBlock.getValue(WallBlock.EAST_WALL)).setValue(WallBlock.NORTH_WALL, oldBlock.getValue(WallBlock.NORTH_WALL)).setValue(WallBlock.SOUTH_WALL, oldBlock.getValue(WallBlock.SOUTH_WALL)));
        }
    }

    public static boolean canBeReplaced(Level pLevel, BlockPos pReplaceablePos){
        return canBeReplaced(pLevel, pReplaceablePos, pReplaceablePos);
    }

    public static boolean canBeReplaced(Level pLevel, BlockPos pReplaceablePos, BlockPos pReplacedBlockPos){
        return pLevel.getBlockState(pReplaceablePos).canBeReplaced(new DirectionalPlaceContext(pLevel, pReplacedBlockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
    }

    public static Optional<BlockPos> findLightningRod(ServerLevel serverLevel, BlockPos blockPos) {
        return findLightningRod(serverLevel, blockPos, 128);
    }

    public static Optional<BlockPos> findLightningRod(ServerLevel serverLevel, BlockPos blockPos, int range) {
        Optional<BlockPos> optional = serverLevel.getPoiManager().findClosest(
                (poiTypeHolder) -> poiTypeHolder.is(PoiTypes.LIGHTNING_ROD),
                (blockPos1) -> blockPos1.getY() == serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, blockPos1.getX(), blockPos1.getZ()) - 1, blockPos, range, PoiManager.Occupancy.ANY);
        return optional.map((blockPos1) -> blockPos1.above(1));
    }

    public static void preventCreativeDropFromBottomPart(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pPos.below();
            BlockState blockstate = pLevel.getBlockState(blockpos);
            if (blockstate.is(pState.getBlock()) && blockstate.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                pLevel.setBlock(blockpos, blockstate1, 35);
                pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    //Adapted from @miyo6032 codes: https://github.com/miyo6032/bosses-of-mass-destruction/blob/f0717da78be52470e29ec6d578dacec62f305f64/src/main/kotlin/net/barribob/maelstrom/static_utilities/MathUtils.kt#L131
    public static List<Vec3> buildBlockCircle(double radius){
        int intRadius = (int) radius;
        double radiusSq = radius * radius;
        List<Vec3> points = new ArrayList<>();
        for (int x = -intRadius; x < intRadius; ++x) {
            for (int z = -intRadius; z < intRadius; ++z) {
                Vec3 pos = new Vec3(x, 0.0, z);
                if (pos.lengthSqr() <= radiusSq) {
                    points.add(pos);
                }
            }
        }
        return points;
    }

    public static List<Vec3> buildBlockCircle(Vec3 position, double radius){
        List<Vec3> properPos =new ArrayList<>();
        for (Vec3 value : buildBlockCircle(radius)) {
            properPos.add(position.add(value));
        }
        return properPos;
    }

    public static List<Vec3> buildOuterBlockCircle(Vec3 position, double radius, double reduction){
        List<Vec3> properPos = buildBlockCircle(position, radius);
        for (Vec3 value : buildBlockCircle(position, reduction)) {
            properPos.remove(value);
        }
        return properPos;
    }

    public static List<Vec3> buildOuterBlockCircle(Vec3 position, double radius){
        return buildOuterBlockCircle(position, radius, radius - 1.0D);
    }

    public static boolean findIllagerWard(ServerLevel level, Player player, int soulEnergy){
        for(int i = -4; i <= 4; ++i) {
            for(int j = -4; j <= 4; ++j) {
                LevelChunk levelchunk = level.getChunkAt(player.blockPosition().offset(i * 16, 0, j * 16));

                for(BlockEntity blockentity : levelchunk.getBlockEntities().values()) {
                    if (blockentity instanceof ShriekObeliskBlockEntity obelisk) {
                        int radius = obelisk.getPower();
                        AABB alignedBB = new AABB(obelisk.getBlockPos()).inflate(radius);
                        if (player.getBoundingBox().intersects(alignedBB)){
                            return obelisk.shriek(level, player, soulEnergy);
                        }
                    }
                }
            }
        }
        return false;
    }

    //Based from Bosses of Mass Destruction codes: https://github.com/CERBON-MODS/Bosses-of-Mass-Destruction-FORGE/blob/master/Common/src/main/java/com/cerbon/bosses_of_mass_destruction/util/BMDUtils.java#L28
    public static BlockPos findGroundBelow(Level level, BlockPos pos, Function<BlockPos, Boolean> isOpenBlock) {
        int bottomY = level.getMinBuildHeight();
        for (int i = pos.getY(); i >= bottomY + 1; i--) {
            BlockPos tempPos = new BlockPos(pos.getX(), i, pos.getZ());

            if (level.getBlockState(tempPos).isFaceSturdy(level, tempPos, Direction.UP, SupportType.FULL) && isOpenBlock.apply(tempPos.above())) {
                return tempPos;
            }
        }
        return new BlockPos(pos.getX(), bottomY, pos.getZ());
    }
}
