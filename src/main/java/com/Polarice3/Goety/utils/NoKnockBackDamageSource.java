package com.Polarice3.Goety.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class NoKnockBackDamageSource extends DamageSource {
    @Nullable
    protected final Entity entity;
    @Nullable
    private final Entity owner;

    public NoKnockBackDamageSource(String pMessageId, @Nullable Entity pSource, @Nullable Entity pIndirectEntity) {
        super(pMessageId);
        this.entity = pSource;
        this.owner = pIndirectEntity;
    }

    @Nullable
    public Entity getDirectAttacker() {
        return this.entity;
    }

    @Nullable
    public Entity getOwner() {
        if (this.owner != null) {
            return this.owner;
        } else {
            return this.getDirectAttacker();
        }
    }

    public Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
        String s = "death.attack." + this.msgId;
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
