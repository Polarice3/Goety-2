package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.IceSpike;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class IceSpikeSpell extends Spells {

    public int SoulCost() {
        return SpellConfig.IceSpikeCost.get();
    }

    public int CastDuration() {
        return SpellConfig.IceSpikeDuration.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    @Override
    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        float enchantment = 0;
        if (entityLiving instanceof Player player) {
            if (WandUtil.enchantedFocus(player)) {
                enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player) / 3.0F;
            }
        }
        IceSpike iceSpike = new IceSpike(entityLiving, worldIn);
        iceSpike.shootFromRotation(entityLiving, entityLiving.getXRot(), entityLiving.getYRot(), 0.0F, 1.6F + enchantment, 1.0F);
        iceSpike.setOwner(entityLiving);
        worldIn.addFreshEntity(iceSpike);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), ModSounds.CAST_SPELL.get(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
