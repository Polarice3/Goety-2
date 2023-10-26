package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.items.magic.CallFocus;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class CallSpell extends Spells {
    @Override
    public int SoulCost() {
        return SpellConfig.CallCost.get();
    }

    @Override
    public int CastDuration() {
        return SpellConfig.CallDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof ServerPlayer player) {
            if (!CallFocus.hasSummon(WandUtil.findFocus(player))){
                SEHelper.increaseSouls(player, this.SoulCost());
                player.displayClientMessage(Component.translatable("info.goety.focus.noSummon"), true);
                worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.FIRE_EXTINGUISH, this.getSoundSource(), 1.0F, 1.0F);
            } else {
                CallFocus.call(player, WandUtil.findFocus(player));
            }
        }
    }
}
