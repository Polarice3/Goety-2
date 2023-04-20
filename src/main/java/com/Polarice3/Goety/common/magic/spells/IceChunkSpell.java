package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.IceChunk;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class IceChunkSpell extends Spells {

    public int SoulCost() {
        return SpellConfig.IceChunkCost.get();
    }

    public int CastDuration() {
        return SpellConfig.IceChunkDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving){
        int range = 16;
        double radius = 2.0D;
        float damage = 0.0F;
        if (entityLiving instanceof Player) {
            Player playerEntity = (Player) entityLiving;
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
                IceChunk iceChunkEntity = new IceChunk(worldIn, entityLiving, (LivingEntity) target);
                iceChunkEntity.setExtraDamage(damage);
                worldIn.addFreshEntity(iceChunkEntity);
            }
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.ICE_CHUNK_SUMMON.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else if (rayTraceResult instanceof BlockHitResult){
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            IceChunk iceChunkEntity = new IceChunk(worldIn, entityLiving, null);
            iceChunkEntity.setExtraDamage(damage);
            iceChunkEntity.setPos(blockPos.getX(), blockPos.getY() + 4, blockPos.getZ());
            worldIn.addFreshEntity(iceChunkEntity);
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.ICE_CHUNK_SUMMON.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }
}
