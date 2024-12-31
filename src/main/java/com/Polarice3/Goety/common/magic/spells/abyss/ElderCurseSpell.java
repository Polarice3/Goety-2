package com.Polarice3.Goety.common.magic.spells.abyss;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.EffectsUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElderCurseSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return 0;
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public int defaultSpellCooldown() {
        return 1200;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 6000, 2);
        List<LivingEntity> list = EffectsUtil.addEffectToAllAround(worldIn, caster, caster.position(), 50.0D, mobeffectinstance, 1200);
        list.forEach((p_289459_) -> {
            worldIn.sendParticles(ParticleTypes.ELDER_GUARDIAN, p_289459_.getX(), p_289459_.getY(), p_289459_.getZ(), 1, 0.0D, 0.0D, 0.0D, 0);
            worldIn.playSound(null, p_289459_.getX(), p_289459_.getY(), p_289459_.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 1.0F);
        });
    }
}
