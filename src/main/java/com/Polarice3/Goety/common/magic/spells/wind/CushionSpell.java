package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.util.Cushion;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class CushionSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.CushionCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.CushionDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.CushionCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        int range = 64;
        int duration = 0;
        int radius = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), entityLiving);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, entityLiving, range, 3.0D);
        LivingEntity target = this.getTarget(entityLiving, range);
        if (target != null){
            Cushion cushion = new Cushion(ModEntityType.CUSHION.get(), worldIn);
            cushion.setRadius(radius);
            cushion.increaseLifeSpan(duration);
            cushion.setPos(target.position());
            worldIn.addFreshEntity(cushion);
        } else if (rayTraceResult instanceof BlockHitResult){
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            Cushion cushion = new Cushion(ModEntityType.CUSHION.get(), worldIn);
            cushion.setRadius(radius);
            cushion.increaseLifeSpan(duration);
            cushion.setPos(blockPos.getX() + 0.5F, blockPos.getY() + 1.0F, blockPos.getZ() + 0.5F);
            worldIn.addFreshEntity(cushion);
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
    }
}