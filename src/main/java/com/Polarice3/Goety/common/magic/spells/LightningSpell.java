package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

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

    public void WandResult(ServerLevel worldIn, LivingEntity entityLiving){
        int range = 16;
        double radius = 2.0D;
        float damage = 5.0F;
        if (entityLiving instanceof Player playerEntity) {
            if (WandUtil.enchantedFocus(playerEntity)) {
                range += WandUtil.getLevels(ModEnchantments.RANGE.get(), playerEntity);
                radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), playerEntity);
                damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), playerEntity);
            }
//            this.IncreaseInfamy(SpellConfig.IceChunkInfamyChance.get(), (PlayerEntity) entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, range, radius);
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
            lightningBolt.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (entityLiving instanceof ServerPlayer serverPlayer) {
                lightningBolt.setCause(serverPlayer);
            }
            worldIn.addFreshEntity(lightningBolt);
        }
    }

    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving) {
        this.WandResult(worldIn, entityLiving);
    }
}
