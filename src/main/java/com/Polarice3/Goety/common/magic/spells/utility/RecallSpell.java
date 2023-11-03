package com.Polarice3.Goety.common.magic.spells.utility;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.items.magic.RecallFocus;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class RecallSpell extends Spells {
    @Override
    public int SoulCost() {
        return SpellConfig.RecallCost.get();
    }

    @Override
    public int CastDuration() {
        return SpellConfig.RecallDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.PORTAL_TRIGGER;
    }

    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving){
        return RecallFocus.isValid(worldIn, WandUtil.findFocus(entityLiving));
    }

    @Override
    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof ServerPlayer player) {
            if (RecallFocus.isValid(worldIn, WandUtil.findFocus(player))) {
                RecallFocus.recall(player, WandUtil.findFocus(player));
            } else if (!RecallFocus.hasRecall(WandUtil.findFocus(player))){
                player.displayClientMessage(Component.translatable("info.goety.focus.noPos"), true);
            } else {
                player.displayClientMessage(Component.translatable("info.goety.focus.PosInvalid"), true);
            }
        }
    }
}
