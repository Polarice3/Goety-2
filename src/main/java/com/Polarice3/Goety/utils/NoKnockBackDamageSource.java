package com.Polarice3.Goety.utils;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
        super(pDamageType);
        this.entity = pSource;
        this.owner = pIndirectEntity;
        this.damageSourcePosition = this.sourcePositionRaw();
    }

    @Nullable
    @Override
    public Entity getDirectEntity() {
        return this.entity;
    }

    @Nullable
    public Entity getOwner() {
        if (this.owner != null) {
            return this.owner;
        } else {
            return this.getDirectEntity();
        }
    }

    @Nullable
    public Vec3 getSourcePosition() {
        if (this.damageSourcePosition != null) {
            return this.damageSourcePosition;
        } else {
            return this.getDirectEntity() != null ? this.getDirectEntity().position() : null;
        }
    }

    public Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
        String s = "death.attack." + this.type().msgId();
        if (this.entity != null){
            Component itextcomponent = this.owner == null ? this.entity.getDisplayName() : this.owner.getDisplayName();
            ItemStack itemstack = this.owner instanceof LivingEntity ? ((LivingEntity)this.owner).getMainHandItem() : ItemStack.EMPTY;
            String s1 = s + ".item";
            return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? Component.translatable(s1, pLivingEntity.getDisplayName(), itextcomponent, itemstack.getDisplayName()) : Component.translatable(s, pLivingEntity.getDisplayName(), itextcomponent);
        } else {
            String s1 = s + ".player";
            return Component.translatable(s1, pLivingEntity.getDisplayName(), pLivingEntity.getDisplayName());
        }
    }
}
