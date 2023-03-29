package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

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

    public static BlockPos SummonRadius(LivingEntity livingEntity, Level world){
        BlockPos.MutableBlockPos blockpos$mutable = livingEntity.blockPosition().mutable().move(0, 0, 0);
        blockpos$mutable.setX(blockpos$mutable.getX() + world.random.nextInt(5) - world.random.nextInt(5));
        blockpos$mutable.setY((int) BlockFinder.moveDownToGround(livingEntity));
        blockpos$mutable.setZ(blockpos$mutable.getZ() + world.random.nextInt(5) - world.random.nextInt(5));
        if (hasChunksAt(livingEntity)
                && isEmptyBlock(world, blockpos$mutable, world.getBlockState(blockpos$mutable), world.getFluidState(blockpos$mutable), ModEntityType.DOPPELGANGER.get(), false)){
            return blockpos$mutable;
        } else {
            return livingEntity.blockPosition().mutable().move(0, (int) BlockFinder.moveDownToGround(livingEntity), 0);
        }
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

    public static boolean findStructure(ServerLevel serverLevel, LivingEntity livingEntity, ResourceKey<Structure> resourceKey){
        Structure structure = serverLevel.structureManager().registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).get(resourceKey);
        if (structure != null) {
            StructureStart structureStart = serverLevel.structureManager().getStructureWithPieceAt(livingEntity.blockPosition(), structure);
            if (!structureStart.getPieces().isEmpty()) {
                return structureStart.getBoundingBox().isInside(livingEntity.blockPosition());
            }
        }
        return false;
    }

    public static boolean isEmptyBox(Level level, BlockPos p_46860_){
        return level.isEmptyBlock(p_46860_)
                && level.isEmptyBlock(p_46860_.below())
                && level.isEmptyBlock(p_46860_.above())
                && level.isEmptyBlock(p_46860_.west())
                && level.isEmptyBlock(p_46860_.east())
                && level.isEmptyBlock(p_46860_.north())
                && level.isEmptyBlock(p_46860_.south());
    }

}
