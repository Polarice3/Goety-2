package com.Polarice3.Goety.common.magic;

import com.Polarice3.Goety.api.magic.ITouchSpell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class TouchSpells extends Spells implements ITouchSpell {

    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
    }

    public abstract void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target);
}
