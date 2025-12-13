package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrismaBeamSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.PrismaBeamCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.PrismaBeamDuration.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.PrismaBeamCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        return this.getTarget(caster) != null && super.conditionsMet(worldIn, caster);
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        LivingEntity target = this.getTarget(caster);
        if (target != null) {
            MiscCapHelper.setClientTarget(caster, target);
            double d5 = ((double) castTime / this.castDuration(caster, staff));
            double d0 = target.getX() - caster.getX();
            double d1 = target.getY(0.5D) - caster.getEyeY();
            double d2 = target.getZ() - caster.getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d0 /= d3;
            d1 /= d3;
            d2 /= d3;
            double d4 = worldIn.getRandom().nextDouble();

            while(d4 < d3) {
                d4 += 1.8D - d5 + worldIn.getRandom().nextDouble() * (1.7D - d5);
                worldIn.sendParticles(ParticleTypes.BUBBLE, caster.getX() + d0 * d4, caster.getEyeY() + d1 * d4, caster.getZ() + d2 * d4, 1, 0.0D, 0.0D, 0.0D, 0);
            }
        }
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        super.stopSpell(worldIn, caster, staff, focus, castTime, spellStat);
        MiscCapHelper.setClientTarget(caster, null);
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
        }
        LivingEntity target = this.getTarget(caster);
        if (target != null) {
            float damage = SpellConfig.PrismaBeamDamage.get().floatValue() * WandUtil.damageMultiply();
            float f = 1.0F;

            if (this.rightStaff(staff)) {
                f += 2.0F;
                damage += 2.0F;
            }

            target.hurt(caster.damageSources().indirectMagic(caster, caster), f + potency);
            target.hurt(caster.damageSources().mobAttack(caster), damage + potency);
        }
    }
}
