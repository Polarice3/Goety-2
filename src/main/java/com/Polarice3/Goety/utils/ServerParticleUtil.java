package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.client.particles.*;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
        if (world instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(pParticleData, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    public static void addParticles(ParticleOptions pParticleData, double x, double y, double z, double pXOffset, double pYOffset, double pZOffset, Level world){
        if (world instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(pParticleData, x, y, z, 0, pXOffset, pYOffset, pZOffset, 0.5F);
        }
    }

    public static void spawnRedstoneParticles(ServerLevel pLevel, BlockPos pPos) {
        double d0 = 0.5625D;
        RandomSource random = pLevel.getRandom();

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
            double d0 = (double)(serverWorld.getRandom().nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(serverWorld.getRandom().nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(serverWorld.getRandom().nextFloat() * 2.0F - 1.0F);
            if (!(d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)) {
                double d3 = entity.getX(d0 / 4.0D);
                double d4 = entity.getY(0.5D + d1 / 4.0D);
                double d5 = entity.getZ(d2 / 4.0D);
                serverWorld.sendParticles(particleData, d3, d4, d5, 0, d0, d1 + 0.2D, d2, 0.5F);
            }
        }
    }

    public static void gatheringParticles(ParticleOptions pParticleData, Entity entity, ServerLevel serverWorld) {
        gatheringParticles(pParticleData, entity, serverWorld, 2);
    }

    public static void gatheringParticles(ParticleOptions pParticleData, Entity entity, ServerLevel serverWorld, int range){
        List<BlockPos> positions = Lists.newArrayList();
        if (serverWorld != null) {
            for(int j1 = -range; j1 <= range; ++j1) {
                for(int k1 = -range; k1 <= range; ++k1) {
                    for(int l1 = -range; l1 <= range; ++l1) {
                        int i2 = Math.abs(j1);
                        int l = Math.abs(k1);
                        int i1 = Math.abs(l1);
                        if ((j1 == 0 && (l == range || i1 == range) || k1 == 0 && (i2 == range || i1 == range) || l1 == 0 && (i2 == range || l == range))) {
                            BlockPos blockpos1 = entity.blockPosition().offset(j1, k1, l1);
                            positions.add(blockpos1);
                        }
                    }
                }
            }
            Vec3 vector3d = new Vec3(entity.position().x, entity.getEyeY(), entity.position().z);
            if (pParticleData instanceof GatherTrailParticle.Option option) {
                vector3d = new Vec3(option.getEndX(), option.getEndY(), option.getEndZ());
            }
            for(BlockPos blockpos : positions) {
                if (serverWorld.getRandom().nextInt(25) == 0) {
                    float f = -0.5F + serverWorld.getRandom().nextFloat();
                    float f1 = -2.0F + serverWorld.getRandom().nextFloat();
                    float f2 = -0.5F + serverWorld.getRandom().nextFloat();
                    BlockPos blockpos1 = blockpos.subtract(entity.blockPosition());
                    Vec3 vector3d1 = (new Vec3(f, f1, f2)).add(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
                    serverWorld.sendParticles(pParticleData, vector3d.x, vector3d.y, vector3d.z, 1, vector3d1.x, vector3d1.y, vector3d1.z, 0.5F);
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
                if (serverWorld.getRandom().nextInt(50) == 0) {
                    float f = -0.5F + serverWorld.getRandom().nextFloat();
                    float f1 = -2.0F + serverWorld.getRandom().nextFloat();
                    float f2 = -0.5F + serverWorld.getRandom().nextFloat();
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
            double d0 = serverLevel.getRandom().nextGaussian() * 0.02D;
            double d1 = serverLevel.getRandom().nextGaussian() * 0.02D;
            double d2 = serverLevel.getRandom().nextGaussian() * 0.02D;
            serverLevel.sendParticles(particleOptions, entity.getRandomX(1.0D), entity.getRandomY() + 1.0D, entity.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
        }
    }

    public static void addParticlesAroundMiddleSelf(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity){
        for(int i = 0; i < 5; ++i) {
            double d0 = serverLevel.getRandom().nextGaussian() * 0.02D;
            double d1 = serverLevel.getRandom().nextGaussian() * 0.02D;
            double d2 = serverLevel.getRandom().nextGaussian() * 0.02D;
            serverLevel.sendParticles(particleOptions, entity.getRandomX(1.0D), entity.getRandomY(), entity.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
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

    public static void addAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Vec3 vec3, float radius){
        addAuraParticles(serverLevel, particleOptions, vec3.x, vec3.y, vec3.z, 0.0F, 0.0F, 0.0F, radius);
    }

    public static void addAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, double x, double y, double z, float radius){
        addAuraParticles(serverLevel, particleOptions, x, y, z, 0.0F, 0.0F, 0.0F, radius);
    }

    public static void addAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float radius){
        serverLevel.sendParticles(particleOptions, x + Math.cos(serverLevel.getGameTime() * 0.25) * radius, y, z + Math.sin(serverLevel.getGameTime() * 0.25) * radius, 0, xSpeed, ySpeed, zSpeed, 0.5F);
        serverLevel.sendParticles(particleOptions, x + Math.cos(serverLevel.getGameTime() * 0.25 + Math.PI) * radius, y, z + Math.sin(serverLevel.getGameTime() * 0.25 + Math.PI) * radius, 0, xSpeed, ySpeed, zSpeed, 0.5F);
    }

    public static void addReverseAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity, float radius){
        serverLevel.sendParticles(particleOptions, entity.getX() + Math.sin(entity.tickCount * 0.25) * radius, entity.getY() + 0.5, entity.getZ() + Math.cos(entity.tickCount * 0.25) * radius, 0, 0, 0, 0, 0.5F);
        serverLevel.sendParticles(particleOptions, entity.getX() + Math.sin(entity.tickCount * 0.25 + Math.PI) * radius, entity.getY() + 0.5, entity.getZ() + Math.cos(entity.tickCount * 0.25 + Math.PI) * radius, 0, 0, 0, 0, 0.5F);
    }

    public static void addReverseAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Vec3 vec3, float radius){
        addReverseAuraParticles(serverLevel, particleOptions, vec3.x, vec3.y, vec3.z, 0.0F, 0.0F, 0.0F, radius);
    }

    public static void addReverseAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, double x, double y, double z, float radius){
        addReverseAuraParticles(serverLevel, particleOptions, x, y, z, 0.0F, 0.0F, 0.0F, radius);
    }

    public static void addReverseAuraParticles(ServerLevel serverLevel, ParticleOptions particleOptions, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float radius){
        serverLevel.sendParticles(particleOptions, x + Math.sin(serverLevel.getGameTime() * 0.25) * radius, y, z + Math.cos(serverLevel.getGameTime() * 0.25) * radius, 0, xSpeed, ySpeed, zSpeed, 0.5F);
        serverLevel.sendParticles(particleOptions, x + Math.sin(serverLevel.getGameTime() * 0.25 + Math.PI) * radius, y, z + Math.cos(serverLevel.getGameTime() * 0.25 + Math.PI) * radius, 0, xSpeed, ySpeed, zSpeed, 0.5F);
    }

    public static void outerCircleParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity, float radius){
        float f6 = serverLevel.getRandom().nextFloat() * ((float) Math.PI * 2F);
        float f7 = Mth.sqrt(serverLevel.getRandom().nextFloat()) * radius;
        float f8 = Mth.cos(f6) * f7;
        float f9 = Mth.sin(f6) * f7;
        serverLevel.sendParticles(particleOptions, entity.getX() + (double) f8, entity.getY() + 0.5, entity.getZ() + (double) f9, 0, 0, 0, 0, 0.5F);
    }

    public static void circularParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity, float radius){
        circularParticles(serverLevel, particleOptions, entity.getX(), entity.getY(), entity.getZ(), radius);
    }

    public static void circularParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity, double xSpeed, double ySpeed, double zSpeed, float radius){
        circularParticles(serverLevel, particleOptions, entity.getX(), entity.getY(), entity.getZ(), xSpeed, ySpeed, zSpeed, radius);
    }

    public static void circularParticles(ServerLevel serverLevel, ParticleOptions particleOptions, double x, double y, double z, float radius){
        circularParticles(serverLevel, particleOptions, x, y, z, 0, 0, 0, radius);
    }

    public static void circularParticles(ServerLevel serverLevel, ParticleOptions particleOptions, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float radius){
        float f5 = (float) Math.PI * radius * radius;
        for (int k1 = 0; (float) k1 < f5; ++k1) {
            float f6 = serverLevel.getRandom().nextFloat() * ((float) Math.PI * 2F);
            float f7 = Mth.sqrt(serverLevel.getRandom().nextFloat()) * radius;
            float f8 = Mth.cos(f6) * f7;
            float f9 = Mth.sin(f6) * f7;
            serverLevel.sendParticles(particleOptions, x + (double) f8, y, z + (double) f9, 0, xSpeed, ySpeed, zSpeed, 0.5F);
        }
    }

    public static void circularColoredParticles(ServerLevel serverLevel, ParticleOptions particleOptions, Entity entity, float radius, ColorUtil colorUtil){
        circularColoredParticles(serverLevel, particleOptions, entity.getX(), entity.getY(), entity.getZ(), radius, colorUtil);
    }

    public static void circularColoredParticles(ServerLevel serverLevel, ParticleOptions particleOptions, double x, double y, double z, float radius, ColorUtil colorUtil){
        float f5 = (float) Math.PI * radius * radius;
        for (int k1 = 0; (float) k1 < f5; ++k1) {
            float f6 = serverLevel.getRandom().nextFloat() * ((float) Math.PI * 2F);
            float f7 = Mth.sqrt(serverLevel.getRandom().nextFloat()) * radius;
            float f8 = Mth.cos(f6) * f7;
            float f9 = Mth.sin(f6) * f7;
            serverLevel.sendParticles(particleOptions, x + (double) f8, y, z + (double) f9, 1, colorUtil.red, colorUtil.green, colorUtil.blue, 0);
        }
    }

    public static void createParticleBall(ParticleOptions pParticleData, Entity entity, ServerLevel serverLevel, int radius) {
        createParticleBall(pParticleData, entity.getX(), entity.getY(), entity.getZ(), serverLevel, radius);
    }

    public static void createParticleBall(ParticleOptions pParticleData, double x, double y, double z, ServerLevel serverLevel, int radius) {
        for(int i = -radius; i <= radius; ++i) {
            for(int j = -radius; j <= radius; ++j) {
                for(int k = -radius; k <= radius; ++k) {
                    double d3 = (double)j + (serverLevel.getRandom().nextDouble() - serverLevel.getRandom().nextDouble()) * 0.5D;
                    double d4 = (double)i + (serverLevel.getRandom().nextDouble() - serverLevel.getRandom().nextDouble()) * 0.5D;
                    double d5 = (double)k + (serverLevel.getRandom().nextDouble() - serverLevel.getRandom().nextDouble()) * 0.5D;
                    double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / 0.5 + serverLevel.getRandom().nextGaussian() * 0.05D;
                    serverLevel.sendParticles(pParticleData, x, y, z, 0, d3 / d6, d4 / d6, d5 / d6, 0.5F);
                    if (i != -radius && i != radius && j != -radius && j != radius) {
                        k += radius * 2 - 1;
                    }
                }
            }
        }
    }

    public static void windParticle(ServerLevel serverLevel, ColorUtil color, float width, float height, int id, Vec3 vec3){
        windParticle(serverLevel, color, width, height, 0, id, vec3);
    }

    public static void windParticle(ServerLevel serverLevel, ColorUtil color, float width, float height, int life, int id, Vec3 vec3){
        serverLevel.sendParticles(new WindParticleOption(color, width, height, life, id), vec3.x(), vec3.y(), vec3.z(), 1, 0.0D, 0.0D, 0.0D, 0.0F);
    }

    public static void windShockwaveParticle(ServerLevel serverLevel, ColorUtil color, float width, float height, int id, Vec3 vec3){
        windShockwaveParticle(serverLevel, color, width, height, 0.25F, id, vec3);
    }

    public static void windShockwaveParticle(ServerLevel serverLevel, ColorUtil color, float width, float height, int life, int id, Vec3 vec3){
        windShockwaveParticle(serverLevel, color, width, height, 0.25F, life, id, vec3);
    }

    public static void windShockwaveParticle(ServerLevel serverLevel, ColorUtil color, float width, float height, float increase, int id, Vec3 vec3){
        windShockwaveParticle(serverLevel, color, width, height, increase, 0, id, vec3);
    }

    public static void windShockwaveParticle(ServerLevel serverLevel, ColorUtil color, float width, float height, float increase, int life, int id, Vec3 vec3){
        for (int i = 0; i < 8; ++i) {
            serverLevel.sendParticles(new WindShockwaveParticle.Option(color, width, height, increase, i * 0.125F, life, id), vec3.x(), vec3.y(), vec3.z(), 1, 0.0D, 0.0D, 0.0D, 0.0F);
        }
    }

    public static void sendGodRay(ServerLevel serverLevel, Entity entity, ColorUtil colorUtil) {
        sendGodRay(serverLevel, entity.getX(), entity.getY(), entity.getZ(), colorUtil.red(), colorUtil.green(), colorUtil.blue());
    }

    public static void sendGodRay(ServerLevel serverLevel, double x, double y, double z, ColorUtil colorUtil) {
        sendGodRay(serverLevel, x, y, z, colorUtil.red(), colorUtil.green(), colorUtil.blue());
    }

    public static void sendGodRay(ServerLevel serverLevel, double x, double y, double z, double red, double green, double blue) {
        sendAlwaysVisibleParticles(serverLevel, ModParticleTypes.GOD_RAY.get(), x, y, z, 0, red, green, blue, 1.0F);
    }

    public static void sendStretchedGodRay(ServerLevel serverLevel, Entity entity, ColorUtil colorUtil) {
        sendStretchedGodRay(serverLevel, entity.getX(), entity.getY(), entity.getZ(), colorUtil.red(), colorUtil.green(), colorUtil.blue());
    }

    public static void sendStretchedGodRay(ServerLevel serverLevel, double x, double y, double z, ColorUtil colorUtil) {
        sendStretchedGodRay(serverLevel, x, y, z, colorUtil.red(), colorUtil.green(), colorUtil.blue());
    }

    public static void sendStretchedGodRay(ServerLevel serverLevel, double x, double y, double z, double red, double green, double blue) {
        sendAlwaysVisibleParticles(serverLevel, ModParticleTypes.STRETCHED_GOD_RAY.get(), x, y, z, 0, red, green, blue, 1.0F);
    }

    public static <T extends ParticleOptions> void sendAlwaysVisibleParticles(ServerLevel serverLevel, T p_8768_, double p_8769_, double p_8770_, double p_8771_, int p_8772_, double p_8773_, double p_8774_, double p_8775_, double p_8776_) {
        ClientboundLevelParticlesPacket clientboundlevelparticlespacket = new ClientboundLevelParticlesPacket(p_8768_, false, p_8769_, p_8770_, p_8771_, (float)p_8773_, (float)p_8774_, (float)p_8775_, (float)p_8776_, p_8772_);

        for(int j = 0; j < serverLevel.getPlayers(serverPlayer -> true).size(); ++j) {
            ServerPlayer serverplayer = serverLevel.getPlayers(serverPlayer -> true).get(j);
            sendParticles(serverLevel, serverplayer, true, p_8769_, p_8770_, p_8771_, clientboundlevelparticlespacket);
        }

    }

    public static void summonUndeadParticles(ServerLevel serverLevel, Entity entity) {
        ColorUtil waveColor = new ColorUtil(0x8FE6DF);
        summonUndeadParticles(serverLevel, entity, waveColor, 0x17b0e0, 0xffffff);
    }

    public static void summonPowerfulUndeadParticles(ServerLevel serverLevel, Entity entity) {
        ColorUtil waveColor = new ColorUtil(0xa7fc3e);
        summonUndeadParticles(serverLevel, entity, waveColor, 0xa7fc3e, 0xcffc97);
    }

    public static void summonUndeadParticles(ServerLevel serverLevel, Entity entity, ColorUtil waveColor, int from, int to) {
        ServerParticleUtil.sendGodRay(serverLevel, entity, waveColor);
//        ServerParticleUtil.windShockwaveParticle(serverLevel, waveColor, 0.1F, 0.1F, 0.1F, -1, entity.position());
        for (int i2 = 0; i2 < serverLevel.getRandom().nextInt(10) + 10; ++i2) {
            sendAlwaysVisibleParticles(serverLevel, new MagicSmokeParticle.Option(from, to, 10 + serverLevel.getRandom().nextInt(10), 0.2F), entity.getRandomX(1.5D), entity.getRandomY(), entity.getRandomZ(1.5D), 0, 0.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    public static void summonedParticles(ServerLevel serverLevel, Entity entity, ColorUtil waveColor, int from, int to) {
        ServerParticleUtil.sendGodRay(serverLevel, entity, waveColor);
        for (int i2 = 0; i2 < serverLevel.getRandom().nextInt(10) + 10; ++i2) {
            sendAlwaysVisibleParticles(serverLevel, new MagicSmokeParticle.Option(from, to, 10 + serverLevel.getRandom().nextInt(10), 0.2F), entity.getRandomX(1.5D), entity.getRandomY(), entity.getRandomZ(1.5D), 0, 0.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    public static boolean sendParticles(ServerLevel serverLevel, ServerPlayer p_8637_, boolean p_8638_, double p_8639_, double p_8640_, double p_8641_, Packet<?> p_8642_) {
        if (p_8637_.level() != serverLevel) {
            return false;
        } else {
            BlockPos blockpos = p_8637_.blockPosition();
            if (blockpos.closerToCenterThan(new Vec3(p_8639_, p_8640_, p_8641_), p_8638_ ? 512.0D : 32.0D)) {
                p_8637_.connection.send(p_8642_);
                return true;
            } else {
                return false;
            }
        }
    }
}
