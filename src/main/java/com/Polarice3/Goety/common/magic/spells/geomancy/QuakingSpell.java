package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.util.ModFallingBlock;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class QuakingSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.QuakingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.QuakingDuration.get();
    }

    @Override
    public int castDuration(LivingEntity entityLiving) {
        return 72000;
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
    public void useSpell(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff, int castTime) {
        int range = 12;
        int radius = 4;
        float damage = SpellConfig.QuakingDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        if (castTime > this.defaultCastDuration()) {
            if (!this.isShifting(entityLiving)) {
                for (int i = 0; i <= range; ++i) {
                    if (castTime == i + this.defaultCastDuration()) {
                        tremor(entityLiving, i, 3, 0.0F, damage, 0.1F);
                        tremor(entityLiving, i, 3, 1.5F, damage, 0.1F);
                        tremor(entityLiving, i, 3, -1.5F, damage, 0.1F);
                    }
                }
                if (castTime >= range + this.defaultCastDuration()){
                    entityLiving.stopUsingItem();
                    this.stopSpell(worldIn, entityLiving, staff, castTime);
                }
            } else {
                for (int i = 0; i <= radius; ++i) {
                    if (castTime == i + this.defaultCastDuration()) {
                        surroundTremor(entityLiving, i, 3, 0.0F, false, damage, 0.1F);
                    }
                }
                if (castTime >= radius + this.defaultCastDuration()){
                    entityLiving.stopUsingItem();
                    this.stopSpell(worldIn, entityLiving, staff, castTime);
                }
            }
        }
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff, int useTimeRemaining) {
        if (useTimeRemaining > this.defaultCastDuration()){
            if (entityLiving instanceof Player player) {
                SEHelper.addCooldown(player, ModItems.QUAKING_FOCUS.get(), this.spellCooldown());
                SEHelper.sendSEUpdatePacket(player);
            }
        }
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
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

        if (blockState != Blocks.AIR.defaultBlockState() && !blockState.hasBlockEntity() && !blockAbove.getMaterial().blocksMotion()) {
            ModFallingBlock fallingBlock = new ModFallingBlock(livingEntity.level, Vec3.atCenterOf(blockPos.above()), blockState, (float) (0.2D + livingEntity.getRandom().nextGaussian() * 0.15D));
            livingEntity.level.addFreshEntity(fallingBlock);
        }
        AABB selection = new AABB(px - 0.5D, minY, pz - 0.5D, px + 0.5D, maxY, pz + 0.5D);
        List<LivingEntity> entities = livingEntity.level.getEntitiesOfClass(LivingEntity.class, selection);
        for (LivingEntity target : entities) {
            if (!MobUtil.areAllies(target, livingEntity) && target != livingEntity) {
                boolean flag = target.hurt(DamageSource.mobAttack(livingEntity), damage);
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

            if (blockState != Blocks.AIR.defaultBlockState() && !blockState.hasBlockEntity() && !blockAbove.getMaterial().blocksMotion()) {
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
                    boolean flag = target.hurt(DamageSource.mobAttack(livingEntity), damage);
                    if (flag) {
                        if (grab) {
                            double magnitude = -4.0D;
                            double x = vx * (double) (1.0F - factor) * magnitude;
                            double y = 0.0D;
                            if (target.isOnGround()) {
                                y += 0.15D;
                            }

                            double z = vz * (double) (1.0F - factor) * magnitude;
                            MobUtil.push(target, x, y, z);
                        } else {
                            MobUtil.push(target, 0.0D, (double) (airborne * (float) distance) + livingEntity.getRandom().nextDouble() * 0.15D, 0.0D);
                        }
                    }
                }
            }
        }

    }
}
