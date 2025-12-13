package com.Polarice3.Goety.common.magic.spells.wind;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
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

public class LaunchSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.LaunchCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.LaunchDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WIND.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.LaunchCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
        }
        caster.hurtMarked = true;
        caster.setOnGround(false);
        Vec3 vector3d = caster.getLookAngle();
        double power = rightStaff(staff) ? 2.5D : 1.5D;
        double d0 = power + (potency / 2.0D);
        caster.setDeltaMovement(vector3d.x * d0, vector3d.y * d0, vector3d.z * d0);
        caster.hasImpulse = true;
        caster.fallDistance = 0;
        this.playSound(worldIn, caster, 2.0F, 1.0F);
    }
}
