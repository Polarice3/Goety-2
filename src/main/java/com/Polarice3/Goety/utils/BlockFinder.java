package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.blocks.entities.ShriekObeliskBlockEntity;
import com.Polarice3.Goety.common.blocks.fluids.ModFluids;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModTags;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class BlockFinder {

    public static boolean isScytheBreak(BlockState blockState){
        return (blockState.is(BlockTags.CROPS) || blockState.getBlock() instanceof BushBlock) && !(blockState.getBlock() instanceof StemBlock);
    }

    public static boolean samePos(BlockPos blockPos1, BlockPos blockPos2){
        if (blockPos1 == null || blockPos2 == null){
            return false;
        }
        return blockPos1.getX() == blockPos2.getX() && blockPos1.getY() == blockPos2.getY() && blockPos1.getZ() == blockPos2.getZ();
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

    public static boolean canSeeBlock(Entity looker, Vec3 vec31) {
        Vec3 vec3 = new Vec3(looker.getX(), looker.getEyeY(), looker.getZ());
        if (vec31.distanceTo(vec3) > 128.0D) {
            return false;
        } else {
            return looker.level.clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, looker)).getType() == HitResult.Type.MISS;
        }
    }

    public static boolean canSeeBlock(Entity looker, BlockPos location) {
        return canSeeBlock(looker, Vec3.atBottomCenterOf(location));
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

    public static double findNearestGroundY(Level level, BlockPos pos, int maxSearchDown) {
        BlockPos.MutableBlockPos mutable = pos.mutable();

        for (int i = 0; i < maxSearchDown; i++) {
            mutable.move(Direction.DOWN);
            BlockState state = level.getBlockState(mutable);

            if (state.isFaceSturdy(level, mutable, Direction.UP) ||
                    !state.getCollisionShape(level, mutable).isEmpty()) {
                return mutable.getY() + 1.0D;
            }
        }

        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY();
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

    public static BlockPos SummonPosition(Entity entity, BlockPos blockPos){
        return SummonPosition(entity.level, entity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos SummonPosition(Level level, Entity entity, BlockPos blockPos){
        return SummonPosition(level, entity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos SummonPosition(Level level, Entity entity, double x, double y, double z){
        double d3 = y;
        boolean flag = false;
        BlockPos blockpos = BlockPos.containing(x, y, z);
        if (level.isLoaded(blockpos)) {
            boolean flag1 = false;

            while(!flag1 && blockpos.getY() > level.getMinBuildHeight()) {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = level.getBlockState(blockpos1);
                if (blockstate.blocksMotion()) {
                    flag1 = true;
                } else {
                    --d3;
                    blockpos = blockpos1;
                }
            }

            if (flag1) {
                AABB aabb = entity.getBoundingBox().move(blockpos);
                if (level.noCollision(entity, aabb) && !level.containsAnyLiquid(aabb)) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            return blockpos;
        } else {
            return BlockPos.containing(x, d3, z);
        }
    }

    public static Vec3 SummonPosition(Entity entity, Vec3 vec3){
        return SummonPosition(entity.level, entity, vec3);
    }

    public static Vec3 SummonPosition(Level level, Entity entity, Vec3 vec3){
        double d3 = vec3.y;
        boolean flag = false;
        Vec3 vec31 = new Vec3(vec3.x, vec3.y, vec3.z);
        if (level.isLoaded(BlockPos.containing(vec31))) {
            boolean flag1 = false;

            while(!flag1 && vec31.y > level.getMinBuildHeight()) {
                BlockPos blockpos1 = BlockPos.containing(vec31).below();
                BlockState blockstate = level.getBlockState(blockpos1);
                if (blockstate.blocksMotion()) {
                    flag1 = true;
                } else {
                    --d3;
                    vec31 = blockpos1.getCenter();
                }
            }

            if (flag1) {
                AABB aabb = entity.getBoundingBox().move(vec31);
                if (level.noCollision(entity, aabb) && !level.containsAnyLiquid(aabb)) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            return vec31;
        } else {
            return new Vec3(vec3.x, d3, vec3.z);
        }
    }

    public static Vec3 SummonPosition(Level level, Vec3 vec3){
        double d3 = vec3.y;
        boolean flag = false;
        Vec3 vec31 = new Vec3(vec3.x, vec3.y, vec3.z);
        if (level.isLoaded(BlockPos.containing(vec31))) {
            boolean flag1 = false;

            while(!flag1 && vec31.y > level.getMinBuildHeight()) {
                BlockPos blockpos1 = BlockPos.containing(vec31).below();
                BlockState blockstate = level.getBlockState(blockpos1);
                if (blockstate.blocksMotion()) {
                    flag1 = true;
                } else {
                    --d3;
                    vec31 = blockpos1.getCenter();
                }
            }

            if (flag1) {
                flag = true;
            }
        }
        if (!flag) {
            return vec31;
        } else {
            return new Vec3(vec3.x, d3, vec3.z);
        }
    }

    public static BlockPos SummonRadius(BlockPos blockPos, Entity entity, Level world){
        return SummonRadius(blockPos, entity, world, 5);
    }

    public static BlockPos SummonRadius(BlockPos blockPos, Entity entity, Level world, int radius){
        return SummonRadius(blockPos, entity, world, 64, radius);
    }

    public static BlockPos SummonRadius(BlockPos blockPos, Entity entity, Level world, int attempts, int radius){
        for (int i = 0; i < attempts; ++i) {
            BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable().move(0, 0, 0);
            blockpos$mutable.setX(blockpos$mutable.getX() + world.random.nextInt(radius) - world.random.nextInt(radius));
            blockpos$mutable.setY(blockPos.getY());
            blockpos$mutable.setZ(blockpos$mutable.getZ() + world.random.nextInt(radius) - world.random.nextInt(radius));
            if (world.noCollision(entity, entity.getBoundingBox().move(blockpos$mutable))
                    && !world.containsAnyLiquid(entity.getBoundingBox().move(blockpos$mutable))
            && blockpos$mutable.distToCenterSqr(blockPos.getCenter()) <= Mth.square(radius * 2)) {
                blockPos = SummonPosition(world, entity, blockpos$mutable);
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
                    && blockpos$mutable.distToCenterSqr(blockPos.getCenter()) >= Mth.square(radius / 2)) {
                blockPos = SummonPosition(world, livingEntity, blockpos$mutable);
                break;
            }
        }
        return blockPos;
    }

    public static BlockPos SummonWaterAwayRadius(BlockPos blockPos, LivingEntity livingEntity, Level world, int attempts, int radius){
        BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable();
        for (int i = 0; i < attempts; ++i) {
            int xOffset = world.random.nextIntBetweenInclusive(-radius, radius);
            int yOffset = world.random.nextIntBetweenInclusive(-radius / 2, radius / 2);
            int zOffset = world.random.nextIntBetweenInclusive(-radius, radius);
            blockpos$mutable.setX(blockpos$mutable.getX() + xOffset);
            blockpos$mutable.setY(blockpos$mutable.getY() + yOffset);
            blockpos$mutable.setZ(blockpos$mutable.getZ() + zOffset);
            if (world.noCollision(livingEntity, livingEntity.getBoundingBox().move(blockpos$mutable))
                    && canSeeBlock(livingEntity, blockpos$mutable)
                    && blockpos$mutable.distToCenterSqr(blockPos.getCenter()) >= Mth.square(radius / 2)) {
                blockPos = blockpos$mutable;
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
                    && blockpos$mutable.distToCenterSqr(blockPos.getCenter()) <= Mth.square(radius * 2)
                    && canSeeBlock(looker, blockpos$mutable)) {
                blockPos = SummonPosition(world, summoned, blockpos$mutable);
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
                blockPos = SummonPosition(world, livingEntity, blockpos$mutable);
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

    public static BlockPos SummonWaterRadius(LivingEntity entity, Level world){
        return SummonWaterRadius(entity.blockPosition(), entity, world);
    }

    public static BlockPos SummonWaterRadius(BlockPos blockPos, LivingEntity entity, Level world){
        return SummonWaterRadius(blockPos, entity, world, 5);
    }

    public static BlockPos SummonWaterRadius(BlockPos blockPos, LivingEntity entity, Level world, int radius){
        return SummonWaterRadius(blockPos, entity, world, 16, radius);
    }

    public static BlockPos SummonWaterRadius(BlockPos blockPos, LivingEntity livingEntity, Level world, int attempts, int radius){
        BlockPos.MutableBlockPos blockpos$mutable = blockPos.mutable();
        for (int i = 0; i < attempts; ++i) {
            int xOffset = world.random.nextIntBetweenInclusive(-radius, radius);
            int yOffset = world.random.nextIntBetweenInclusive(-radius / 2, radius / 2);
            int zOffset = world.random.nextIntBetweenInclusive(-radius, radius);
            blockpos$mutable.setX(blockpos$mutable.getX() + xOffset);
            blockpos$mutable.setY(blockpos$mutable.getY() + yOffset);
            blockpos$mutable.setZ(blockpos$mutable.getZ() + zOffset);
            if (hasChunksAt(livingEntity)
                    && isEmptyBlock(world, blockpos$mutable, world.getBlockState(blockpos$mutable), world.getFluidState(blockpos$mutable), ModEntityType.ZOMBIE_SERVANT.get(), true)){
                blockPos = blockpos$mutable;
                break;
            }
        }
        return blockPos;
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

        BlockPos blockPos = BlockPos.containing(origin.add(direction));
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

        return blockPos.getCenter();
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
        Structure structure = serverLevel.structureManager().registryAccess().registryOrThrow(Registries.STRUCTURE).get(resourceKey);
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

    public static boolean findVillageSize(ServerLevel serverLevel, BlockPos blockPos, int size){
        PoiManager poimanager = serverLevel.getPoiManager();
        List<BlockPos> list = poimanager.getInRange(
                (p_217747_) -> p_217747_.is(PoiTypeTags.VILLAGE),
                blockPos, 32, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos).toList();
        return list.size() >= size;
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
            while (blockpos$mutable.getY() < blockPos.getY() + distance && level.getBlockState(blockpos$mutable).getCollisionShape(level, blockpos$mutable).isEmpty()){
                blockpos$mutable.move(Direction.UP);
                flag = true;
            }
        } else {
            while (blockpos$mutable.getY() > blockPos.getY() - distance && level.getBlockState(blockpos$mutable).getCollisionShape(level, blockpos$mutable).isEmpty()){
                blockpos$mutable.move(Direction.DOWN);
                flag = true;
            }
        }
        if (!level.getBlockState(blockpos$mutable).getCollisionShape(level, blockpos$mutable ).isEmpty()){
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

    public static Optional<BlockPos> findNetherPortal(ServerLevel serverLevel, BlockPos blockPos, int range) {
        return serverLevel.getPoiManager().findClosest(
                (poiTypeHolder) -> poiTypeHolder.is(PoiTypes.NETHER_PORTAL),
                blockPos, range, PoiManager.Occupancy.ANY);
    }

    public static void preventCreativeDropFromBottomPart(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pPos.below();
            BlockState blockstate = pLevel.getBlockState(blockpos);
            if (blockstate.is(pState.getBlock()) && blockstate.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
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

    public static boolean findIllagerWard(ServerLevel serverLevel, BlockPos blockPos, int soulEnergy) {
        int maxRadius = 8 + (64 * MainConfig.ShriekObeliskIncrease.get());
        int chunkRadius = (maxRadius >> 4) + 1;
        int centerChunkX = SectionPos.blockToSectionCoord(blockPos.getX());
        int centerChunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());

        for (int i = -chunkRadius; i <= chunkRadius; i++) {
            for (int j = -chunkRadius; j <= chunkRadius; j++) {
                LevelChunk chunk = serverLevel.getChunkSource().getChunkNow(centerChunkX + i, centerChunkZ + j);
                if (chunk != null) {
                    for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                        if (blockEntity instanceof ShriekObeliskBlockEntity obelisk) {
                            int radius = obelisk.getPower();
                            BlockPos obeliskPos = obelisk.getBlockPos();
                            AABB initialBB = new AABB(obeliskPos);
                            AABB alignedBB = new AABB(obelisk.getBlockPos()).inflate(radius);
                            if (initialBB.intersects(alignedBB)) {
                                return obelisk.shriek(serverLevel, null, soulEnergy);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean findIllagerWard(ServerLevel serverLevel, Player player, int soulEnergy) {
        BlockPos blockPos = player.blockPosition();
        int maxRadius = 8 + (64 * MainConfig.ShriekObeliskIncrease.get());
        int chunkRadius = (maxRadius >> 4) + 1;
        int centerChunkX = SectionPos.blockToSectionCoord(blockPos.getX());
        int centerChunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());

        for (int i = -chunkRadius; i <= chunkRadius; i++) {
            for (int j = -chunkRadius; j <= chunkRadius; j++) {
                LevelChunk chunk = serverLevel.getChunkSource().getChunkNow(centerChunkX + i, centerChunkZ + j);
                if (chunk != null) {
                    for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                        if (blockEntity instanceof ShriekObeliskBlockEntity obelisk) {
                            int radius = obelisk.getPower();
                            BlockPos obeliskPos = obelisk.getBlockPos();
                            AABB initialBB = new AABB(obeliskPos);
                            AABB alignedBB = new AABB(obelisk.getBlockPos()).inflate(radius);
                            if (initialBB.intersects(alignedBB)) {
                                return obelisk.shriek(serverLevel, null, soulEnergy);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasSunlight(Level level, BlockPos blockPos){
        return level.canSeeSky(blockPos) && level.isDay();
    }

    public static boolean getNearbyLitCandles(Level pLevel, BlockPos pPos, int range, int totalCount) {
        return getNearbyLitCandles(pLevel, pPos, range, range, range, totalCount);
    }

    public static boolean getNearbyLitCandles(Level pLevel, BlockPos pPos, int xRange, int yRange, int zRange, int totalCount) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos blockpos1 = pPos.offset(i, j, k);
                    BlockState blockstate = pLevel.getBlockState(blockpos1);
                    if (blockstate.is(BlockTags.CANDLES)
                            && blockstate.hasProperty(CandleBlock.LIT)
                            && blockstate.getValue(CandleBlock.LIT)
                            && blockstate.hasProperty(CandleBlock.CANDLES)){
                        currentCount += blockstate.getValue(CandleBlock.CANDLES);
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    public static boolean getNearbyBlocks(Level pLevel, BlockPos pPos, Predicate<BlockState> pPredicate, int range, int totalCount) {
        return getNearbyBlocks(pLevel, pPos, pPredicate, range, range, range, totalCount);
    }

    public static boolean getNearbyBlocks(Level pLevel, BlockPos pPos, Predicate<BlockState> pPredicate, int xRange, int yRange, int zRange, int totalCount) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos blockpos1 = pPos.offset(i, j, k);
                    BlockState blockstate = pLevel.getBlockState(blockpos1);
                    if (pPredicate.test(blockstate)){
                        ++currentCount;
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    public static boolean getNearbyEnchantPower(Level pLevel, BlockPos pPos, int range, int enchantPower) {
        return getNearbyEnchantPower(pLevel, pPos, range, range, range, enchantPower);
    }

    public static boolean getNearbyEnchantPower(Level pLevel, BlockPos pPos, int xRange, int yRange, int zRange, int enchantPower) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos blockpos1 = pPos.offset(i, j, k);
                    BlockState blockstate = pLevel.getBlockState(blockpos1);
                    if (blockstate.getEnchantPowerBonus(pLevel, blockpos1) > 0) {
                        currentCount += (int) blockstate.getEnchantPowerBonus(pLevel, blockpos1);
                    }
                }
            }
        }

        return currentCount >= enchantPower;
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

    public static boolean breakBlock(Level level, BlockPos blockPos, ItemStack itemStack, @Nullable Entity entity) {
        BlockState blockstate = level.getBlockState(blockPos);
        if (blockstate.isAir()) {
            return false;
        } else {
            FluidState fluidstate = level.getFluidState(blockPos);
            if (!(blockstate.getBlock() instanceof BaseFireBlock)) {
                level.levelEvent(2001, blockPos, Block.getId(blockstate));
            }

            BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            Block.dropResources(blockstate, level, blockPos, blockentity, entity, itemStack);

            boolean flag = level.setBlock(blockPos, fluidstate.createLegacyBlock(), 3, 512);
            if (flag) {
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(entity, blockstate));
            }

            return flag;
        }
    }

    public static boolean isPassableBlock(Level level, BlockPos blockPos){
        return level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty();
    }

    public static void voidedEffect(Level pLevel, BlockState pState, Entity pEntity) {
        if (pEntity instanceof LivingEntity livingEntity
                && !CuriosFinder.hasVoidRobe(livingEntity)
                && !livingEntity.getType().is(ModTags.EntityTypes.VOID_TOUCHED_IMMUNE)
                && livingEntity.canBeAffected(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get()))) {
            livingEntity.makeStuckInBlock(pState, new Vec3(0.8D, 1.0D, 0.8D));
            voidedEffect(pLevel, livingEntity);
        }
    }

    public static void voidedEffect(Level pLevel, LivingEntity pEntity) {
        if (!CuriosFinder.hasVoidRobe(pEntity)
                && !pEntity.getType().is(ModTags.EntityTypes.VOID_TOUCHED_IMMUNE)) {
            if (pEntity.canBeAffected(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get()))) {
                if (!pLevel.isClientSide) {
                    MobEffectInstance instance = pEntity.getEffect(GoetyEffects.VOID_TOUCHED.get());
                    float damage = pEntity.getMaxHealth() * 0.05F;
                    boolean flag = pEntity.getType().is(Tags.EntityTypes.BOSSES) || pEntity.getType().is(ModTags.EntityTypes.MINI_BOSSES) || pEntity.getMaxHealth() >= 200.0D;
                    if (flag) {
                        damage = 1.0F;
                    }
                    if (instance == null) {
                        if (pEntity.hurt(ModDamageSource.getDamageSource(pLevel, ModDamageSource.VOIDED), damage)) {
                            pEntity.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(3), flag ? 0 : 2, false, true));
                        }
                    } else {
                        if (pEntity.tickCount % 20 == 0) {
                            if (pEntity.hurt(ModDamageSource.getDamageSource(pLevel, ModDamageSource.VOIDED), damage)) {
                                if (!flag) {
                                    EffectsUtil.increaseEffect(pEntity, GoetyEffects.VOID_TOUCHED.get(), 9, false, true);
                                } else {
                                    pEntity.addEffect(new MobEffectInstance(GoetyEffects.VOID_TOUCHED.get(), MathHelper.secondsToTicks(3), 0, false, true));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isInVoid(Entity entity) {
        return entity.tickCount > 1 && entity.getFluidTypeHeight(ModFluids.VOID_FLUID_TYPE.get()) > 0.0D;
    }

    public static boolean canTeleportTo(LivingEntity entity, double x, double y, double z) {
        double d3 = y;
        boolean flag = false;
        BlockPos blockpos = BlockPos.containing(x, y, z);
        Level level = entity.level();
        if (level.hasChunkAt(blockpos)) {
            boolean flag1 = false;

            while(!flag1 && blockpos.getY() > level.getMinBuildHeight()) {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = level.getBlockState(blockpos1);
                if (blockstate.blocksMotion()) {
                    flag1 = true;
                } else {
                    --d3;
                    blockpos = blockpos1;
                }
            }

            if (flag1) {
                AABB aabb = entity.getBoundingBox().move(x, d3, z);
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(entity, x, d3, z);
                if (event.isCanceled()) return false;
                if (level.noCollision(aabb) && !level.containsAnyLiquid(aabb)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    //Based on ChainsawTask by @Shadows-of-Fire: https://github.com/Shadows-of-Fire/Apotheosis/blob/1.20/src/main/java/dev/shadowsoffire/apotheosis/ench/enchantments/masterwork/ChainsawEnchant.java
    public static class ChopTreeTask implements EventTask {
        UUID owner;
        ItemStack axe;
        ServerLevel level;
        Int2ObjectMap<Queue<BlockPos>> hits = new Int2ObjectOpenHashMap<>();
        int ticks = 0;

        public ChopTreeTask(UUID owner, ItemStack axe, ServerLevel level, BlockPos pos) {
            this.owner = owner;
            this.axe = axe;
            this.level = level;
            this.hits.computeIfAbsent(pos.getY(), i -> new ArrayDeque<>()).add(pos);
        }

        @Override
        public boolean getAsBoolean() {
            if (++this.ticks % 2 != 0) {
                return false;
            }
            if (this.axe.isEmpty()) {
                return true;
            }
            int minY = this.hits.keySet().intStream().min().getAsInt();
            Queue<BlockPos> queue = this.hits.get(minY);
            int breaks = 0;
            while (!queue.isEmpty()) {
                BlockPos pos = queue.poll();
                for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                    if (blockPos.equals(pos)) {
                        continue;
                    }
                    BlockState state = this.level.getBlockState(blockPos);
                    if (state.is(BlockTags.LOGS)) {
                        breakExtraBlock(this.level, blockPos, this.axe, this.owner);
                        if (!this.level.getBlockState(blockPos).is(BlockTags.LOGS)) {
                            this.hits.computeIfAbsent(blockPos.getY(), i -> new ArrayDeque<>()).add(blockPos.immutable());
                            breaks++;
                        }
                    }
                }
                if (breaks > 5) {
                    break;
                }
            }
            if (queue.isEmpty()) {
                this.hits.remove(minY);
            }
            return this.hits.isEmpty();
        }

        public static boolean breakExtraBlock(ServerLevel world, BlockPos pos, ItemStack mainhand, @Nullable UUID source) {
            BlockState blockstate = world.getBlockState(pos);
            FakePlayer player;
            if (source != null) {
                player = FakePlayerFactory.get(world, new GameProfile(source, UsernameCache.getLastKnownUsername(source)));
                Player realPlayer = world.getPlayerByUUID(source);
                if (realPlayer != null) {
                    player.setPos(realPlayer.position());
                }
            } else {
                player = FakePlayerFactory.getMinecraft(world);
            }

            player.getInventory().items.set(player.getInventory().selected, mainhand);

            if (blockstate.getDestroySpeed(world, pos) < 0 || !blockstate.canHarvestBlock(world, pos, player)) {
                return false;
            }

            GameType type = player.getAbilities().instabuild ? GameType.CREATIVE : GameType.SURVIVAL;
            int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(world, type, player, pos);
            if (exp == -1) {
                return false;
            } else {
                BlockEntity tileentity = world.getBlockEntity(pos);
                Block block = blockstate.getBlock();
                if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !player.canUseGameMasterBlocks()) {
                    world.sendBlockUpdated(pos, blockstate, blockstate, 3);
                    return false;
                } else if (mainhand.onBlockStartBreak(pos, player)) {
                    return false;
                } else if (player.blockActionRestricted(world, pos, type)) {
                    return false;
                } else {
                    ItemStack itemstack1 = mainhand.copy();
                    boolean canHarvest = blockstate.canHarvestBlock(world, pos, player);
                    mainhand.mineBlock(world, blockstate, pos, player);
                    if (mainhand.isEmpty() && !itemstack1.isEmpty()) {
                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack1, InteractionHand.MAIN_HAND);
                    }

                    boolean removed = removeBlock(world, player, pos, canHarvest);

                    if (removed && canHarvest) {
                        block.playerDestroy(world, player, pos, blockstate, tileentity, itemstack1);
                    }

                    if (removed && exp > 0) {
                        blockstate.getBlock().popExperience(world, pos, exp);
                    }
                    return true;
                }
            }
        }

        public static boolean removeBlock(ServerLevel world, ServerPlayer player, BlockPos pos, boolean canHarvest) {
            BlockState state = world.getBlockState(pos);
            boolean removed = state.onDestroyedByPlayer(world, pos, player, canHarvest, world.getFluidState(pos));
            if (removed) {
                state.getBlock().destroy(world, pos, state);
            }
            return removed;
        }

    }
}
