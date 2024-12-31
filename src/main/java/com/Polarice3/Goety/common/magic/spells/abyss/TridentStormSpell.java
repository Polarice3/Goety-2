package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TridentStormSpell extends Spell {

    public int defaultSoulCost() {
        return SpellConfig.TridentStormCost.get();
    }

    public int defaultCastDuration() {
        return 100;
    }

    public int castDuration(LivingEntity caster) {
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
    public void startSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int warmUp = MathHelper.secondsToTicks(2);
        if (this.rightStaff(staff)) {
            int i = caster.getRandom().nextInt(4);
            if (i == 0) {
                WandUtil.summonTridentSurround(caster, warmUp);
            } else if (i == 1) {
                WandUtil.summonTridentSquare(caster, warmUp);
            } else if (i == 2) {
                WandUtil.summonTridentWideCircle(caster, warmUp);
            } else {
                WandUtil.summonTridentCross(caster, warmUp);
            }
        } else {
            WandUtil.summonTridentMinor(caster, warmUp);
        }
    }
}
