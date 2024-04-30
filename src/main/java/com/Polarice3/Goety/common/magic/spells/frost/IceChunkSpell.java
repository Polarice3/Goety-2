package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.IceChunk;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class IceChunkSpell extends Spell {

    public int defaultSoulCost() {
        return SpellConfig.IceChunkCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.IceChunkDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.IceChunkCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        int range = 16;
        double radius = 2.0D;
        float damage = 0.0F;
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, range, radius);
        LivingEntity target = this.getTarget(entityLiving, range);
        if (target != null){
            IceChunk iceChunkEntity = new IceChunk(worldIn, entityLiving, (LivingEntity) target);
            iceChunkEntity.setExtraDamage(damage);
            iceChunkEntity.setStaff(rightStaff(staff));
            worldIn.addFreshEntity(iceChunkEntity);
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.ICE_CHUNK_SUMMON.get(), this.getSoundSource(), 1.0F, 1.0F);
        } else if (rayTraceResult instanceof BlockHitResult){
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            IceChunk iceChunkEntity = new IceChunk(worldIn, entityLiving, null);
            iceChunkEntity.setExtraDamage(damage);
            iceChunkEntity.setPos(blockPos.getX() + 0.5F, blockPos.getY() + 4, blockPos.getZ() + 0.5F);
            iceChunkEntity.setStaff(rightStaff(staff));
            worldIn.addFreshEntity(iceChunkEntity);
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.ICE_CHUNK_SUMMON.get(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
