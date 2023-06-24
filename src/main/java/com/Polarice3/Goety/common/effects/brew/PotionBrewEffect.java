package com.Polarice3.Goety.common.effects.brew;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionBrewEffect extends BrewEffect{
    public MobEffect mobEffect;
    public int duration;

    public PotionBrewEffect(MobEffect effect, int soulCost, int cap, int duration) {
        super(effect, soulCost, cap, effect.getCategory(), effect.getColor());
        this.mobEffect = effect;
        this.duration = duration;
    }

    public PotionBrewEffect(MobEffect effect, int soulCost, int duration) {
        super(effect, soulCost, effect.getCategory(), effect.getColor());
        this.mobEffect = effect;
        this.duration = duration;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity p_19467_, int p_19468_) {
        mobEffect.applyEffectTick(p_19467_, p_19468_);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity pLivingEntity, int pAmplifier, double pHealth) {
        mobEffect.applyInstantenousEffect(pSource, pIndirectSource, pLivingEntity, pAmplifier, pHealth);
    }

    @Override
    public boolean isInstantenous() {
        return mobEffect.isInstantenous();
    }

    @Override
    public boolean canLinger() {
        return true;
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return this.mobEffect.getDescriptionId();
    }

    public int getDuration() {
        return duration;
    }


}
