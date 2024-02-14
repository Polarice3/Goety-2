package com.Polarice3.Goety.utils;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ServerParticleUtil {
    public static void smokeParticles(ParticleOptions pParticleData, double x, double y, double z, Level world){
        ServerLevel serverWorld = (ServerLevel) world;
        serverWorld.sendParticles(pParticleData, x, y, z, 1, 0, 0, 0, 0);
    }

    public static void addParticles(ParticleOptions pParticleData, double x, double y, double z, double pXOffset, double pYOffset, double pZOffset, Level world){
        ServerLevel serverWorld = (ServerLevel) world;
        serverWorld.sendParticles(pParticleData, x, y, z, 0, pXOffset, pYOffset, pZOffset, 0.5F);
    }

    public static void spawnRedstoneParticles(ServerLevel pLevel, BlockPos pPos) {
        double d0 = 0.5625D;
        RandomSource random = pLevel.random;

        for(Direction direction : Direction.values()) {
            BlockPos blockpos = pPos.relative(direction);
            if (!pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5D + d0 * (double)direction.getStepX() : (double)random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + d0 * (double)direction.getStepY() : (double)random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + d0 * (double)direction.getStepZ() : (double)random.nextFloat();
                smokeParticles(DustParticleOptions.REDSTONE, (double)pPos.getX() + d1, (double)pPos.getY() + d2, (double)pPos.getZ() + d3, pLevel);
            }
        }

    }

    public static void emitterParticles(ServerLevel serverWorld, Entity entity, ParticleOptions particleData){
        for(int i = 0; i < 16; ++i) {
            double d0 = (double)(serverWorld.random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(serverWorld.random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(serverWorld.random.nextFloat() * 2.0F - 1.0F);
            if (!(d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)) {
                double d3 = entity.getX(d0 / 4.0D);
                double d4 = entity.getY(0.5D + d1 / 4.0D);
                double d5 = entity.getZ(d2 / 4.0D);
                serverWorld.sendParticles(particleData, d3, d4, d5, 0, d0, d1 + 0.2D, d2, 0.5F);
            }
        }
    }

    public static void gatheringParticles(ParticleOptions pParticleData, Entity livingEntity, ServerLevel serverWorld){
        List<BlockPos> positions = Lists.newArrayList();
        if (serverWorld != null) {
            for(int j1 = -2; j1 <= 2; ++j1) {
                for(int k1 = -2; k1 <= 2; ++k1) {
                    for(int l1 = -2; l1 <= 2; ++l1) {
                        int i2 = Math.abs(j1);
                        int l = Math.abs(k1);
                        int i1 = Math.abs(l1);
                        if ((j1 == 0 && (l == 2 || i1 == 2) || k1 == 0 && (i2 == 2 || i1 == 2) || l1 == 0 && (i2 == 2 || l == 2))) {
                            BlockPos blockpos1 = livingEntity.blockPosition().offset(j1, k1, l1);
                            positions.add(blockpos1);
                        }
                    }
                }
            }
            Vec3 vector3d = new Vec3(livingEntity.position().x, livingEntity.getEyeY(), livingEntity.position().z);
            for(BlockPos blockpos : positions) {
                if (serverWorld.random.nextInt(50) == 0) {
                    float f = -0.5F + serverWorld.random.nextFloat();
                    float f1 = -2.0F + serverWorld.random.nextFloat();
                    float f2 = -0.5F + serverWorld.random.nextFloat();
                    BlockPos blockpos1 = blockpos.subtract(livingEntity.blockPosition());
                    Vec3 vector3d1 = (new Vec3(f, f1, f2)).add(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
                    serverWorld.sendParticles(pParticleData, vector3d.x, vector3d.y, vector3d.z, 0, vector3d1.x, vector3d1.y, vector3d1.z, 0.5F);
                }
            }
        }
    }

    public static void gatheringBlockParticles(ParticleOptions pParticleData, BlockPos pBlockPos, ServerLevel serverWorld){
        List<BlockPos> positions = Lists.newArrayList();
        if (serverWorld != null) {
            for(int j1 = -2; j1 <= 2; ++j1) {
                for(int k1 = -2; k1 <= 2; ++k1) {
                    for(int l1 = -2; l1 <= 2; ++l1) {
                        int i2 = Math.abs(j1);
                        int l = Math.abs(k1);
                        int i1 = Math.abs(l1);
                        if ((j1 == 0 && (l == 2 || i1 == 2) || k1 == 0 && (i2 == 2 || i1 == 2) || l1 == 0 && (i2 == 2 || l == 2))) {
                            BlockPos blockpos1 = pBlockPos.offset(j1, k1, l1);
                            positions.add(blockpos1);
                        }
                    }
                }
            }
            Vec3 vector3d = new Vec3(pBlockPos.getX() + 0.5F, pBlockPos.getY() + 1.0F, pBlockPos.getZ() + 0.5F);
            for(BlockPos blockpos : positions) {
                if (serverWorld.random.nextInt(50) == 0) {
                    float f = -0.5F + serverWorld.random.nextFloat();
                    float f1 = -2.0F + serverWorld.random.nextFloat();
                    float f2 = -0.5F + serverWorld.random.nextFloat();
                    BlockPos blockpos1 = blockpos.subtract(pBlockPos);
                    Vec3 vector3d1 = (new Vec3(f, f1, f2)).add(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
                    serverWorld.sendParticles(pParticleData, vector3d.x, vector3d.y, vector3d.z, 0, vector3d1.x, vector3d1.y, vector3d1.z, 0.5F);
                }
            }
        }
    }

    public static void blockBreakParticles(ParticleOptions pParticleData, BlockPos pPos, BlockState pState, ServerLevel serverWorld) {
        if (serverWorld != null) {
            VoxelShape voxelshape = pState.getShape(serverWorld, pPos);
            voxelshape.forAllBoxes((p_228348_3_, p_228348_5_, p_228348_7_, p_228348_9_, p_228348_11_, p_228348_13_) -> {
                double d1 = Math.min(1.0D, p_228348_9_ - p_228348_3_);
                double d2 = Math.min(1.0D, p_228348_11_ - p_228348_5_);
                double d3 = Math.min(1.0D, p_228348_13_ - p_228348_7_);
                int i = Math.max(2, Mth.ceil(d1 / 0.25D));
                int j = Math.max(2, Mth.ceil(d2 / 0.25D));
                int k = Math.max(2, Mth.ceil(d3 / 0.25D));

                for (int l = 0; l < i; ++l) {
                    for (int i1 = 0; i1 < j; ++i1) {
                        for (int j1 = 0; j1 < k; ++j1) {
                            double d4 = ((double) l + 0.5D) / (double) i;
                            double d5 = ((double) i1 + 0.5D) / (double) j;
                            double d6 = ((double) j1 + 0.5D) / (double) k;
                            double d7 = d4 * d1 + p_228348_3_;
                            double d8 = d5 * d2 + p_228348_5_;
                            double d9 = d6 * d3 + p_228348_7_;
                            serverWorld.sendParticles(pParticleData, (double) pPos.getX() + d7, (double) pPos.getY() + d8, (double) pPos.getZ() + d9, 0, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, 0.5F);
                        }
                    }
                }
            });
        }
    }

    public static void addParticlesAroundSelf(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity){
        for(int i = 0; i < 5; ++i) {
            double d0 = serverLevel.random.nextGaussian() * 0.02D;
            double d1 = serverLevel.random.nextGaussian() * 0.02D;
            double d2 = serverLevel.random.nextGaussian() * 0.02D;
            serverLevel.sendParticles(particleOptions, entity.getRandomX(1.0D), entity.getRandomY() + 1.0D, entity.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
        }
    }

    public static void addGroundAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity, float radius){
        serverLevel.sendParticles(particleOptions, entity.getX() + Math.cos(entity.tickCount * 0.25) * radius, entity.getY(), entity.getZ() + Math.sin(entity.tickCount * 0.25) * radius, 0, 0, 0, 0, 0.5F);
        serverLevel.sendParticles(particleOptions, entity.getX() + Math.cos(entity.tickCount * 0.25 + Math.PI) * radius, entity.getY(), entity.getZ() + Math.sin(entity.tickCount * 0.25 + Math.PI) * radius, 0, 0, 0, 0, 0.5F);
    }

    public static void addAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity, float radius){
        serverLevel.sendParticles(particleOptions, entity.getX() + Math.cos(entity.tickCount * 0.25) * radius, entity.getY() + 0.5, entity.getZ() + Math.sin(entity.tickCount * 0.25) * radius, 0, 0, 0, 0, 0.5F);
        serverLevel.sendParticles(particleOptions, entity.getX() + Math.cos(entity.tickCount * 0.25 + Math.PI) * radius, entity.getY() + 0.5, entity.getZ() + Math.sin(entity.tickCount * 0.25 + Math.PI) * radius, 0, 0, 0, 0, 0.5F);
    }

    public static void addAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, double x, double y, double z, float radius){
        serverLevel.sendParticles(particleOptions, x + Math.cos(serverLevel.getGameTime() * 0.25) * radius, y, z + Math.sin(serverLevel.getGameTime() * 0.25) * radius, 0, 0, 0, 0, 0.5F);
        serverLevel.sendParticles(particleOptions, x + Math.cos(serverLevel.getGameTime() * 0.25 + Math.PI) * radius, y, z + Math.sin(serverLevel.getGameTime() * 0.25 + Math.PI) * radius, 0, 0, 0, 0, 0.5F);
    }

    public static void circularParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity, float radius){
        circularParticles(serverLevel, particleOptions, entity.getX(), entity.getY(), entity.getZ(), radius);
    }

    public static void circularParticles(ServerLevel serverLevel, ParticleOptions particleOptions, double x, double y, double z, float radius){
        float f5 = (float) Math.PI * radius * radius;
        for (int k1 = 0; (float) k1 < f5; ++k1) {
            float f6 = serverLevel.random.nextFloat() * ((float) Math.PI * 2F);
            float f7 = Mth.sqrt(serverLevel.random.nextFloat()) * radius;
            float f8 = Mth.cos(f6) * f7;
            float f9 = Mth.sin(f6) * f7;
            serverLevel.sendParticles(particleOptions, x + (double) f8, y, z + (double) f9, 1, 0, 0, 0, 0);
        }
    }
}
