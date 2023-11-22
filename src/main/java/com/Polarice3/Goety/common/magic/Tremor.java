package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.common.entities.util.ModFallingBlock;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Based on @Fuzss's Seismic Wave codes: <a href="https://github.com/Fuzss/mutantmonsters/blob/1.20/Common/src/main/java/fuzs/mutantmonsters/core/SeismicWave.java">...</a>
 */
public class Tremor extends BlockPos {
    private final boolean first;
    private final boolean playEffects;

    public Tremor(int x, int y, int z, boolean first, boolean playEffects, Level level, LivingEntity livingEntity, int extraDamage) {
        super(x, y, z);
        this.first = first;
        this.playEffects = playEffects;
        this.affectBlocks(level, livingEntity, extraDamage);
    }

    public Tremor(int x, int y, int z, boolean first, Level level, LivingEntity livingEntity, int extraDamage){
        this(x, y, z, first, true, level, livingEntity, extraDamage);
    }

    public boolean isFirst() {
        return this.first;
    }

    public static void createTremors(Level level, LivingEntity livingEntity, List<Tremor> tremors, int extraDamage, int x1, int z1, int x2, int z2, int y) {
        int deltaX = x2 - x1;
        int deltaZ = z2 - z1;
        int xStep = deltaX < 0 ? -1 : 1;
        int zStep = deltaZ < 0 ? -1 : 1;
        deltaX = Math.abs(deltaX);
        deltaZ = Math.abs(deltaZ);
        int x = x1;
        int z = z1;
        int deltaX2 = deltaX * 2;
        int deltaZ2 = deltaZ * 2;
        int firstY = getSuitableGround(level, x1, y, z1, 3);
        Tremor wave = new Tremor(x1, y, z1, true, level, livingEntity, extraDamage);
        if (firstY != -1) {
            wave = new Tremor(x1, firstY, z1, true, level, livingEntity, extraDamage);
        }

        tremors.add(wave);
        int error;
        int i;
        if (deltaX2 >= deltaZ2) {
            error = deltaX;

            for(i = 0; i < deltaX; ++i) {
                x += xStep;
                error += deltaZ2;
                if (error > deltaX2) {
                    z += zStep;
                    error -= deltaX2;
                }

                addTremor(level, livingEntity, tremors, x, y, z, extraDamage);
            }
        } else {
            error = deltaZ;

            for(i = 0; i < deltaZ; ++i) {
                z += zStep;
                error += deltaX2;
                if (error > deltaZ2) {
                    x += xStep;
                    error -= deltaZ2;
                }

                addTremor(level, livingEntity, tremors, x, y, z, extraDamage);
            }
        }

    }

    public static void quake(Level level, LivingEntity source, List<Tremor> tremors, int extraDamage, int y){
        int maxDistance = 8;
        float f5 = (float) Math.PI * maxDistance * maxDistance;
        for (int k1 = 0; (float) k1 < f5; ++k1) {
            float f6 = level.random.nextFloat() * ((float) Math.PI * 2F);
            float f7 = Mth.sqrt(level.random.nextFloat()) * maxDistance;
            float f8 = Mth.cos(f6) * f7;
            float f9 = Mth.sin(f6) * f7;
            addTremor(level, source, false, tremors, (int) (source.getX() + f8), y, (int) (source.getZ() + f9), extraDamage);
        }
    }

    public static void addTremor(Level level, LivingEntity livingEntity, boolean playEffects, List<Tremor> list, int extraDamage, int x, int y, int z) {
        y = getSuitableGround(level, x, y, z, 3);
        if (y != -1) {
            list.add(new Tremor(x, y, z, false, playEffects, level, livingEntity, extraDamage));
        }

        if (level.random.nextInt(2) == 0) {
            list.add(new Tremor(x, y + 1, z, false, playEffects, level, livingEntity, extraDamage));
        }

    }

    public static void addTremor(Level level, LivingEntity livingEntity, List<Tremor> list, int x, int y, int z, int extraDamage) {
        addTremor(level, livingEntity, true, list, x, y, z, extraDamage);
    }

    public void affectBlocks(Level level, LivingEntity source, int extraDamage) {
        BlockPos posAbove = this.above();
        BlockState blockstate = level.getBlockState(this);
        Block block = blockstate.getBlock();
        Player playerEntity = source instanceof Player ? (Player)source : null;
        if (block instanceof DoorBlock) {
            if (DoorBlock.isWoodenDoor(blockstate)) {
                level.levelEvent(LevelEvent.SOUND_ZOMBIE_WOODEN_DOOR, this, 0);
            } else {
                level.levelEvent(LevelEvent.SOUND_ZOMBIE_IRON_DOOR, this, 0);
            }
        }

        if (block instanceof BellBlock) {
            ((BellBlock)block).onHit(level, blockstate, new BlockHitResult(Vec3.atLowerCornerOf(this), source.getDirection(), this, false), playerEntity, true);
        }

        if (blockstate.is(Blocks.REDSTONE_ORE)) {
            block.stepOn(level, this, blockstate, source);
        }

        if (blockstate.getFluidState().isEmpty()) {
            if (this.playEffects) {
                level.levelEvent(2001, posAbove, Block.getId(blockstate));
            }
            ModFallingBlock fallingBlock = new ModFallingBlock(level, blockstate, 0.3F);
            fallingBlock.setPos(this.getX() + 0.5D, this.getY() + 1.0D, this.getZ() + 0.5D);
            level.addFreshEntity(fallingBlock);
        }

        AABB box = new AABB(this.getX(), (double) this.getY() + 1.0, this.getZ(), (double) this.getX() + 1.0, (double) this.getY() + 2.0, (double) this.getZ() + 1.0);

        for (LivingEntity livingEntity : source.level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (livingEntity != source && source.getVehicle() != livingEntity) {
                float damage = 4.0F;
                if (this.isFirst()){
                    damage = 6.0F;
                }
                damage += extraDamage;
                if (livingEntity.hurt(source.damageSources().mobAttack(source), damage)) {
                    MobUtil.knockBack(livingEntity, source, 0.5D, 0.2D, 0.5D);
                }
            }
        }
    }

    public static int getSuitableGround(Level world, int x, int y, int z, int range) {
        int i = y;

        while (true) {
            if (Math.abs(y - i) > range) {
                return -1;
            }

            BlockPos startPos = new BlockPos(x, i, z);
            BlockPos posUp = startPos.above();
            BlockState blockState = world.getBlockState(startPos);
            if (blockState.is(BlockTags.FIRE)) {
                return -1;
            }

            if (world.getFluidState(startPos).is(FluidTags.LAVA) && !world.getFluidState(startPos).isEmpty()) {
                break;
            }

            if (world.isEmptyBlock(startPos)) {
                --i;
            } else {
                if (!world.isEmptyBlock(startPos) && world.isEmptyBlock(posUp) && blockState.getCollisionShape(world, startPos).isEmpty()) {
                    --i;
                    break;
                }

                if (world.isEmptyBlock(startPos) || world.isEmptyBlock(posUp) || world.getBlockState(posUp).getCollisionShape(world, posUp).isEmpty()) {
                    break;
                }

                ++i;
            }
        }

        return i;
    }
}
