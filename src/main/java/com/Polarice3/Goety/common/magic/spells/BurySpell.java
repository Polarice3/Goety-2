package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.common.magic.TouchSpells;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

public class BurySpell extends TouchSpells {
    @Override
    public int SoulCost() {
        return 0;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.RUMBLE.get();
    }

    @Override
    public void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target) {
        target.setPos(target.getX(), target.getY() - target.getBbHeight(), target.getZ());
        worldIn.playSound(null, target.getX(), target.getY(), target.getZ(), CastingSound(), this.getSoundSource(), 1.0F, 1.0F);
    }
}
