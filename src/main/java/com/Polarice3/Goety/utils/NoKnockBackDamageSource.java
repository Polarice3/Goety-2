package com.Polarice3.Goety.utils;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class NoKnockBackDamageSource extends DamageSource {
    @Nullable
    protected final Entity entity;
    @Nullable
    private final Entity owner;
    @Nullable
    private final Vec3 damageSourcePosition;

    public NoKnockBackDamageSource(Holder<DamageType> pDamageType, @Nullable Entity pSource, @Nullable Entity pIndirectEntity) {
        super(pDamageType, pSource, pIndirectEntity);
        this.entity = pSource;
        this.owner = pIndirectEntity;
        this.damageSourcePosition = this.sourcePositionRaw();
    }

    @Nullable
    @Override
    public Entity getDirectEntity() {
        return super.getDirectEntity();
    }

    @Nullable
    public Entity getOwner() {
        return super.getEntity();
    }

    @Nullable
    public Vec3 getSourcePosition() {
        return super.getSourcePosition();
    }

    public boolean isCreativePlayer() {
        return super.isCreativePlayer();
    }

    public Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
        return super.getLocalizedDeathMessage(pLivingEntity);
    }
}
