package com.Polarice3.Goety.common.magic.spells.utility;

import com.Polarice3.Goety.common.magic.Spells;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CommandSpell extends Spells {
    @Override
    public int SoulCost() {
        return 0;
    }

    @Override
    public int CastDuration() {
        return 0;
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.SNOW_GOLEM_AMBIENT;
    }

    @Override
    public int SpellCooldown() {
        return 0;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
    }
}
