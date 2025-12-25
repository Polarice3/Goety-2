package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class TridentStormSpell extends Spell {

    public int defaultSoulCost() {
        return SpellConfig.TridentStormCost.get();
    }

    public int defaultCastDuration() {
        return 100;
    }

    public int castDuration(LivingEntity caster, ItemStack staff) {
        return this.defaultCastDuration();
    }

    public SoundEvent CastingSound() {
        return ModSounds.TRIDENT_STORM_PRE.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.TridentStormCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        if (castTime >= MathHelper.secondsToTicks(2)){
            if (caster instanceof Player player && !focus.isEmpty()) {
                SEHelper.addCooldown(player, focus.getItem(), this.spellCooldown(caster));
                SEHelper.sendSEUpdatePacket(player);
            }
        }
    }

    @Override
    public void startSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
        }
        int warmUp = MathHelper.secondsToTicks(2);
        if (this.rightStaff(staff)) {
            int i = caster.getRandom().nextInt(4);
            if (i == 0) {
                WandUtil.summonTridentSurround(caster, warmUp, potency);
            } else if (i == 1) {
                WandUtil.summonTridentSquare(caster, warmUp, potency);
            } else if (i == 2) {
                WandUtil.summonTridentWideCircle(caster, warmUp, potency);
            } else {
                WandUtil.summonTridentCross(caster, warmUp, potency);
            }
        } else {
            WandUtil.summonTridentMinor(caster, warmUp, potency);
        }
    }
}
