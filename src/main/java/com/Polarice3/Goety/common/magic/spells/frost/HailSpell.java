package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.HailCloud;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
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

public class HailSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(100).setRadius(2.0D);
    }

    public int defaultSoulCost() {
        return SpellConfig.HailCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.HailDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.FROST_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.HailCoolDown.get();
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
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int range = spellStat.getRange();
        int duration = spellStat.getDuration();
        double radius = spellStat.getRadius();
        float potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
            duration *= WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
            potency += WandUtil.getPotencyLevel(caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, caster, range, radius);
        LivingEntity target = this.getTarget(caster, range);
        if (target != null){
            if (target instanceof LivingEntity) {
                HailCloud hailCloud = new HailCloud(worldIn, caster, target);
                hailCloud.setExtraDamage(potency);
                hailCloud.setRadius((float) radius);
                hailCloud.setLifeSpan(duration);
                hailCloud.setStaff(rightStaff(staff));
                worldIn.addFreshEntity(hailCloud);
            }
            this.playSound(worldIn, caster, SoundEvents.PLAYER_HURT_FREEZE);
        } else if (rayTraceResult instanceof BlockHitResult){
            BlockPos blockPos = ((BlockHitResult) rayTraceResult).getBlockPos();
            HailCloud hailCloud = new HailCloud(worldIn, caster, null);
            hailCloud.setExtraDamage(potency);
            hailCloud.setRadius((float) radius);
            hailCloud.setLifeSpan(duration);
            hailCloud.setStaff(rightStaff(staff));
            hailCloud.setPos(blockPos.getX() + 0.5F, blockPos.getY() + 4, blockPos.getZ() + 0.5F);
            worldIn.addFreshEntity(hailCloud);
            this.playSound(worldIn, caster, SoundEvents.PLAYER_HURT_FREEZE);
        }
    }
}
