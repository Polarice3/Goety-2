package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.util.ModFallingBlock;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class QuakingSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(12).setRadius(4.0D);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.QuakingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.QuakingDuration.get();
    }

    @Override
    public int castDuration(LivingEntity caster, ItemStack staff) {
        int i = this.defaultStats().getRange();
        if (WandUtil.enchantedFocus(caster)) {
            i += WandUtil.getRangeLevel(caster);
        }
        return i + this.defaultCastDuration();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.RUMBLE.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.QuakingCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        int range = spellStat.getRange();
        int radius = (int) spellStat.getRadius();
        float damage = SpellConfig.QuakingDamage.get().floatValue() * WandUtil.damageMultiply();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
            damage += WandUtil.getPotencyLevel(caster);
        }
        damage += spellStat.getPotency();
        if (rightStaff(staff)) {
            radius += 1;
        }
        if (castTime > this.defaultCastDuration()) {
            if (!this.isShifting(caster)) {
                for (int i = 0; i <= range; ++i) {
                    if (castTime == i + this.defaultCastDuration()) {
                        if (rightStaff(staff)) {
                            int d = (i + 2) - 2;
                            int d2 = (i + 2) - 3;
                            float ds = (float) (d + d2) / 2;
                            arcTremor(caster, 0.35F, d, 5, 1.05F, -2.0F, 0, 80, damage);
                            arcTremor(caster, 0.35F, d2, 5, 1.05F, -2.0F, 0, 80, damage);
                            tremorSound(caster, ds, 0);
                        } else {
                            tremor(caster, i, 3, 0.0F, damage, 0.1F);
                            tremor(caster, i, 3, 1.5F, damage, 0.1F);
                            tremor(caster, i, 3, -1.5F, damage, 0.1F);
                        }
                    }
                }
            } else {
                for (int i = 0; i <= radius; ++i) {
                    if (castTime == i + this.defaultCastDuration()) {
                        surroundTremor(caster, i, 3, 0.0F, rightStaff(staff), damage, 0.1F);
                    }
                }
            }
        }
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        if (castTime > this.defaultCastDuration()){
            if (caster instanceof Player player && !focus.isEmpty()) {
                SEHelper.addCooldown(player, focus.getItem(), this.spellCooldown(caster));
                SEHelper.sendSEUpdatePacket(player);
            }
        }
    }

    //Based on @l_ender's codes: https://github.com/lender544/L_ender-s-Cataclysm-Backport-1.19.2-1.80/blob/7a1a4cea139685cd4fb11a482d4af893efa1f607/src/main/java/com/github/L_Ender/cataclysm/entity/BossMonsters/Ignis_Entity.java#L1853
    public static void tremor(LivingEntity livingEntity, int distance, double topY, float side, float damage, float airborne) {
        int hitY = Mth.floor(livingEntity.getBoundingBox().minY - 0.5D);
        double minY = livingEntity.getY() - 2.0D;
        double maxY = livingEntity.getY() + topY;
        float angle = (float) ((Math.PI / 180.0F) * livingEntity.yHeadRot);
        float f = Mth.cos(livingEntity.yHeadRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(livingEntity.yHeadRot * ((float) Math.PI / 180F));
        double extraX = distance * Mth.sin((float) (Math.PI + angle));
        double extraZ = distance * Mth.cos(angle);
        double px = livingEntity.getX() + extraX + f * side;
        double pz = livingEntity.getZ() + extraZ + f1 * side;
        int hitX = Mth.floor(px);
        int hitZ = Mth.floor(pz);
        BlockPos blockPos = new BlockPos(hitX, hitY, hitZ);
        BlockPos abovePos = new BlockPos(blockPos).above();
        BlockState blockState = livingEntity.level.getBlockState(blockPos);
        BlockState blockAbove = livingEntity.level.getBlockState(abovePos);

        if (blockState != Blocks.AIR.defaultBlockState() && !blockState.hasBlockEntity() && !blockAbove.blocksMotion()) {
            ModFallingBlock fallingBlock = new ModFallingBlock(livingEntity.level, Vec3.atCenterOf(blockPos.above()), blockState, (float) (0.2D + livingEntity.getRandom().nextGaussian() * 0.15D));
            livingEntity.level.addFreshEntity(fallingBlock);
        }
        AABB selection = new AABB(px - 0.5D, minY, pz - 0.5D, px + 0.5D, maxY, pz + 0.5D);
        List<LivingEntity> entities = livingEntity.level.getEntitiesOfClass(LivingEntity.class, selection);
        for (LivingEntity target : entities) {
            if (!MobUtil.areAllies(target, livingEntity) && target != livingEntity) {
                boolean flag = target.hurt(livingEntity.damageSources().mobAttack(livingEntity), damage);
                if (flag) {
                    MobUtil.push(target, 0.0D, (double) (airborne * (float) distance) + livingEntity.getRandom().nextDouble() * 0.15D, 0.0D);
                }
            }
        }
        if (distance % 4 == 0) {
            livingEntity.level.playSound(null, blockPos.above(), ModSounds.WALL_ERUPT.get(), SoundSource.PLAYERS, 1.0F, 0.8F + livingEntity.level.random.nextFloat() * 0.4F);
            livingEntity.level.playSound(null, blockPos.above(), ModSounds.DIRT_DEBRIS.get(), SoundSource.PLAYERS, 1.0F, 0.8F + livingEntity.level.random.nextFloat() * 0.4F);
            livingEntity.level.playSound(null, blockPos.above(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 0.8F + livingEntity.level.random.nextFloat() * 0.4F);
        }
    }

    //Based on @l_ender's codes: https://github.com/lender544/L_ender-s-Cataclysm-Backport-1.19.2-1.80/blob/7a1a4cea139685cd4fb11a482d4af893efa1f607/src/main/java/com/github/L_Ender/cataclysm/entity/BossMonsters/Ignis_Entity.java#L1788
    public static void surroundTremor(LivingEntity livingEntity, int distance, double topY, float side, boolean grab, float damage, float airborne) {
        int hitY = Mth.floor(livingEntity.getBoundingBox().minY - 0.5D);
        double spread = Math.PI * (double)2.0F;
        int arcLen = Mth.ceil((double)distance * spread);
        double minY = livingEntity.getY() - 1.0D;
        double maxY = livingEntity.getY() + topY;

        for(int i = 0; i < arcLen; ++i) {
            double theta = ((double)i / ((double)arcLen - 1.0D) - 0.5D) * spread;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = livingEntity.getX() + vx * (double)distance + (double)side * Math.cos((double)(livingEntity.yBodyRot + 90.0F) * Math.PI / 180.0D);
            double pz = livingEntity.getZ() + vz * (double)distance + (double)side * Math.sin((double)(livingEntity.yBodyRot + 90.0F) * Math.PI / 180.0D);
            float factor = 1.0F - (float)distance / 12.0F;
            int hitX = Mth.floor(px);
            int hitZ = Mth.floor(pz);
            BlockPos blockPos = new BlockPos(hitX, hitY, hitZ);

            BlockState blockState;
            for(blockState = livingEntity.level.getBlockState(blockPos); blockState.getRenderShape() != RenderShape.MODEL; blockState = livingEntity.level.getBlockState(blockPos)) {
                blockPos = blockPos.below();
            }
            BlockState blockAbove = livingEntity.level.getBlockState(blockPos.above());

            if (blockState != Blocks.AIR.defaultBlockState() && !blockState.hasBlockEntity() && !blockAbove.blocksMotion()) {
                ModFallingBlock fallingBlock = new ModFallingBlock(livingEntity.level, Vec3.atCenterOf(blockPos.above()), blockState, (float) (0.2D + livingEntity.getRandom().nextGaussian() * 0.15D));
                livingEntity.level.addFreshEntity(fallingBlock);
            }

            if (distance % 4 == 0) {
                livingEntity.level.playSound(null, blockPos.above(), ModSounds.WALL_ERUPT.get(), SoundSource.PLAYERS, 1.0F, 0.8F + livingEntity.level.random.nextFloat() * 0.4F);
                livingEntity.level.playSound(null, blockPos.above(), ModSounds.DIRT_DEBRIS.get(), SoundSource.PLAYERS, 1.0F, 0.8F + livingEntity.level.random.nextFloat() * 0.4F);
                livingEntity.level.playSound(null, blockPos.above(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 0.8F + livingEntity.level.random.nextFloat() * 0.4F);
            }

            AABB selection = new AABB(px - 0.5D, minY, pz - 0.5D, px + 0.5D, maxY, pz + 0.5D);
            List<LivingEntity> entities = livingEntity.level.getEntitiesOfClass(LivingEntity.class, selection);
            for (LivingEntity target : entities) {
                if (!MobUtil.areAllies(target, livingEntity) && target != livingEntity) {
                    boolean flag = target.hurt(livingEntity.damageSources().mobAttack(livingEntity), damage);
                    if (flag) {
                        if (grab) {
                            if (target.isDamageSourceBlocked(livingEntity.damageSources().mobAttack(livingEntity))) {
                                MobUtil.disableShield(target);
                            }
                        } else {
                            MobUtil.push(target, 0.0D, (double) (airborne * (float) distance) + livingEntity.getRandom().nextDouble() * 0.15D, 0.0D);
                        }
                    }
                }
            }
        }

    }

    //Based on @l_ender's codes: https://github.com/lender544/new1.20.1/blob/master/src/main/java/com/github/L_Ender/cataclysm/entity/InternalAnimationMonster/IABossMonsters/Ancient_Remnant/Ancient_Remnant_Entity.java#L1037
    public void arcTremor(LivingEntity caster, float spreadarc, int distance, int height, float mxy, float vec, float math, int shieldbreakticks, float damage) {
        double perpFacing = caster.getYRot() * (Math.PI / 180);
        double facingAngle = perpFacing + Math.PI / 2;
        int hitY = Mth.floor(caster.getBoundingBox().minY - 0.5);
        double spread = Math.PI * spreadarc;
        int arcLen = Mth.ceil(distance * spread);
        float f = Mth.cos(caster.getYRot() * ((float)Math.PI / 180F)) ;
        float f1 = Mth.sin(caster.getYRot() * ((float)Math.PI / 180F)) ;
        for (int i = 0; i < arcLen; i++) {
            double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = caster.getX() + vx * distance + vec * Math.cos((caster.getYRot() + 90) * Math.PI / 180) + f * math;
            double pz = caster.getZ() + vz * distance + vec * Math.sin((caster.getYRot() + 90) * Math.PI / 180) + f1 * math;
            int hitX = Mth.floor(px);
            int hitZ = Mth.floor(pz);
            BlockPos pos = new BlockPos(hitX, hitY + height, hitZ);
            BlockState block = caster.level.getBlockState(pos);

            int maxDepth = 30;
            for (int depthCount = 0; depthCount < maxDepth; depthCount++) {
                if (block.getRenderShape() == RenderShape.MODEL) {
                    break;
                }
                pos = pos.below();
                block = caster.level.getBlockState(pos);
            }

            if (block.getRenderShape() != RenderShape.MODEL) {
                block = Blocks.AIR.defaultBlockState();
            }
            if (!caster.level.isClientSide) {
                spawnBlocks(caster, hitX, hitY + height, hitZ, (int) (caster.getY() - height), block, px, pz, mxy, distance, shieldbreakticks, damage);
            }
        }
    }


    private void spawnBlocks(LivingEntity caster, int hitX, int hitY, int hitZ, int lowestYCheck, BlockState blockState, double px, double pz, float mxy, float distance, int shieldbreakticks, float damage) {
        BlockPos blockpos = new BlockPos(hitX, hitY, hitZ);
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = caster.level.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(caster.level, blockpos1, Direction.UP)) {
                if (!caster.level.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = caster.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(caster.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                break;
            }

            blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(lowestYCheck) - 1);

        ModFallingBlock fallingBlock = new ModFallingBlock(caster.level, Vec3.atCenterOf(blockpos.above()), blockState, (float) (0.2D + caster.getRandom().nextGaussian() * 0.04D));
        caster.level.addFreshEntity(fallingBlock);

        AABB selection = new AABB(px - 0.5, (double)blockpos.getY() + d0 -1, pz - 0.5, px + 0.5, (double)blockpos.getY() + d0 + mxy, pz + 0.5);
        List<LivingEntity> hitbox = caster.level.getEntitiesOfClass(LivingEntity.class, selection);

        for (LivingEntity entity : hitbox) {
            if (!MobUtil.areAllies(caster, entity)) {
                boolean flag = entity.hurt(caster.damageSources().mobAttack(entity), damage);
                if (entity.isDamageSourceBlocked(caster.damageSources().mobAttack(entity)) && shieldbreakticks > 0) {
                    MobUtil.disableShield(entity);
                }
                if (flag) {
                    MobUtil.push(entity, 0.0D, (double) (0.1F * distance) + entity.getRandom().nextDouble() * 0.15D, 0.0D);
                }
            }
        }

    }

    private void tremorSound(LivingEntity livingEntity, float distance, float math) {
        double theta = (livingEntity.getYRot()) * (Math.PI / 180);
        theta += Math.PI / 2;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float f = Mth.cos(livingEntity.getYRot() * ((float)Math.PI / 180F)) ;
        float f1 = Mth.sin(livingEntity.getYRot() * ((float)Math.PI / 180F)) ;
        livingEntity.level.playSound(null, livingEntity.getX() + distance * vecX + f * math, livingEntity.getY(), livingEntity.getZ() + distance * vecZ + f1 * math, ModSounds.WALL_ERUPT.get(), SoundSource.PLAYERS, 1.0F, 0.8F + livingEntity.level.random.nextFloat() * 0.4F);
        livingEntity.level.playSound(null, livingEntity.getX() + distance * vecX + f * math, livingEntity.getY(), livingEntity.getZ() + distance * vecZ + f1 * math, ModSounds.DIRT_DEBRIS.get(), SoundSource.PLAYERS, 1.0F, 0.8F + livingEntity.level.random.nextFloat() * 0.4F);
        livingEntity.level.playSound(null, livingEntity.getX() + distance * vecX + f * math, livingEntity.getY(), livingEntity.getZ() + distance * vecZ + f1 * math, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 0.8F + livingEntity.level.random.nextFloat() * 0.4F);
    }
}
