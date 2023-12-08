package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class LightningSpell extends Spells {

    public int SoulCost() {
        return SpellConfig.LightningCost.get();
    }

    public int CastDuration() {
        return SpellConfig.LightningDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int SpellCooldown() {
        return SpellConfig.LightningCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    public void commonResult(ServerLevel worldIn, LivingEntity entityLiving, int range, boolean staff){
        double radius = 2.0D;
        float damage = SpellConfig.LightningDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, range, radius);
        Optional<BlockPos> lightningRod = BlockFinder.findLightningRod(worldIn, BlockPos.containing(rayTraceResult.getLocation()));
        if (lightningRod.isPresent() && !staff){
            BlockPos blockPos = lightningRod.get();
            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, worldIn);
            lightningBolt.setDamage(damage);
            lightningBolt.moveTo(Vec3.atBottomCenterOf(blockPos));
            if (entityLiving instanceof ServerPlayer serverPlayer) {
                lightningBolt.setCause(serverPlayer);
            }
            worldIn.addFreshEntity(lightningBolt);
        } else {
            if (rayTraceResult instanceof EntityHitResult){
                Entity target = ((EntityHitResult) rayTraceResult).getEntity();
                if (target instanceof LivingEntity) {
                    LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, worldIn);
                    lightningBolt.setDamage(damage);
                    lightningBolt.setPos(target.position());
                    if (entityLiving instanceof ServerPlayer serverPlayer) {
                        lightningBolt.setCause(serverPlayer);
                    }
                    worldIn.addFreshEntity(lightningBolt);
                }
            } else if (rayTraceResult instanceof BlockHitResult){
                BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, worldIn);
                lightningBolt.setDamage(damage);
                lightningBolt.moveTo(Vec3.atBottomCenterOf(blockPos));
                if (entityLiving instanceof ServerPlayer serverPlayer) {
                    lightningBolt.setCause(serverPlayer);
                }
                worldIn.addFreshEntity(lightningBolt);
            }
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        commonResult(worldIn, entityLiving, 16, rightStaff(staff));
    }
}
