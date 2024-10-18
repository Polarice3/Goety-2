package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.items.magic.TroopFocus;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TroopSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.TroopCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.TroopDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.VOID_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.TroopCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    public boolean conditionsMet(ServerLevel worldIn, LivingEntity entityLiving){
        if (entityLiving instanceof ServerPlayer player) {
            if (!TroopFocus.hasSummonType(WandUtil.findFocus(player))){
                player.displayClientMessage(Component.translatable("info.goety.focus.noSummonType"), true);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        if (entityLiving instanceof ServerPlayer player) {
            if (!TroopFocus.hasSummonType(WandUtil.findFocus(player))){
                player.displayClientMessage(Component.translatable("info.goety.focus.noSummonType"), true);
            } else {
                TroopFocus.call(player, WandUtil.findFocus(player));
            }
        }
    }
}
