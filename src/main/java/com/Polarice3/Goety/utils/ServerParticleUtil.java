package com.Polarice3.Goety.utils;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
            Vec3 vector3d = new Vec3(pBlockPos.getX(), pBlockPos.getY() + 1.5F, pBlockPos.getZ());
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
}
