package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.items.magic.CallFocus;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

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
    public int SpellCooldown() {
        return SpellConfig.CallCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving){
        if (entityLiving instanceof ServerPlayer player) {
            if (!CallFocus.hasSummon(WandUtil.findFocus(player))){
                player.displayClientMessage(Component.translatable("info.goety.focus.noSummon"), true);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        if (entityLiving instanceof ServerPlayer player) {
            if (!CallFocus.hasSummon(WandUtil.findFocus(player))){
                player.displayClientMessage(Component.translatable("info.goety.focus.noSummon"), true);
            } else {
                CallFocus.call(player, WandUtil.findFocus(player));
            }
        }
    }
}
