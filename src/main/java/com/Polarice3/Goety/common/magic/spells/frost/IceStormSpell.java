package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.IceStorm;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class IceStormSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(0);
    }

    public int defaultSoulCost() {
        return SpellConfig.IceStormCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.IceStormDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.FROST_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.IceStormCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        int range = spellStat.getRange();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            range += WandUtil.getRangeLevel(caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }
        Vec3 vector3d = caster.getViewVector( 1.0F);
        IceStorm iceStorm = new IceStorm(
                caster.getX() + vector3d.x / 2,
                caster.getEyeY() - 0.2,
                caster.getZ() + vector3d.z / 2,
                vector3d.x,
                vector3d.y,
                vector3d.z, worldIn);
        iceStorm.setExtraDamage(potency);
        iceStorm.setDuration(duration);
        iceStorm.setRange(range);
        iceStorm.setBoltSpeed((int) velocity);
        if (rightStaff(staff)){
            iceStorm.setSize(1.0F);
        }
        iceStorm.setOwner(caster);
        worldIn.addFreshEntity(iceStorm);
        this.playSound(worldIn, caster, ModSounds.WIND_BLAST.get(), 1.0F, 0.75F);
    }
}
