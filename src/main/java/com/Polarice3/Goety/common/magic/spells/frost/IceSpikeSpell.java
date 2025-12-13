package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.IceSpear;
import com.Polarice3.Goety.common.entities.projectiles.IceSpike;
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

import java.util.ArrayList;
import java.util.List;

public class IceSpikeSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setVelocity(1.6F);
    }

    public int defaultSoulCost() {
        return SpellConfig.IceSpikeCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.IceSpikeDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.FROST_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.IceSpikeCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat){
        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 3.0F;
        }
        IceSpike iceSpike = new IceSpike(caster, worldIn);
        if (rightStaff(staff)){
            iceSpike = new IceSpear(caster, worldIn);
            velocity *= 1.5F;
        }
        iceSpike.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 1.0F);
        iceSpike.setOwner(caster);
        iceSpike.setExtraDamage(potency);
        worldIn.addFreshEntity(iceSpike);
        this.playSound(worldIn, caster, ModSounds.ICE_SPIKE_CAST.get(), 1.0F, this.projPitch(worldIn.getRandom()));
    }
}
