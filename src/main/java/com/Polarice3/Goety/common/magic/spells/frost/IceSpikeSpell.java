package com.Polarice3.Goety.common.magic.spells.frost;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.IceSpear;
import com.Polarice3.Goety.common.entities.projectiles.IceSpike;
import com.Polarice3.Goety.common.magic.Spell;
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

    public int defaultSoulCost() {
        return SpellConfig.IceSpikeCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.IceSpikeDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.ICE_SPIKE_CAST.get();
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

    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff){
        float enchantment = 0;
        if (WandUtil.enchantedFocus(entityLiving)) {
            enchantment = WandUtil.getLevels(ModEnchantments.VELOCITY.get(), entityLiving) / 3.0F;
        }
        IceSpike iceSpike = new IceSpike(entityLiving, worldIn);
        if (rightStaff(staff)){
            iceSpike = new IceSpear(entityLiving, worldIn);
        }
        iceSpike.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, 1.6F + enchantment, 1.0F);
        iceSpike.setOwner(entityLiving);
        iceSpike.setExtraDamage(WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving));
        worldIn.addFreshEntity(iceSpike);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.CAST_SPELL.get(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
