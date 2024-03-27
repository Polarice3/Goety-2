package com.Polarice3.Goety.compat.iron;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.ModList;

public enum IronLoaded {
    IRON_SPELLBOOKS("irons_spellbooks");
    private final boolean loaded;

    IronLoaded(String modid) {
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean hasFirePower(LivingEntity livingEntity){
        return isCompat(livingEntity, IronAttributes.FIRE_SPELL_POWER);
    }

    public boolean hasIcePower(LivingEntity livingEntity){
        return isCompat(livingEntity, IronAttributes.ICE_SPELL_POWER);
    }

    public boolean hasLightningPower(LivingEntity livingEntity){
        return isCompat(livingEntity, IronAttributes.LIGHTNING_SPELL_POWER);
    }

    public boolean hasEnderPower(LivingEntity livingEntity){
        return isCompat(livingEntity, IronAttributes.ENDER_SPELL_POWER);
    }

    public boolean hasBloodPower(LivingEntity livingEntity){
        return isCompat(livingEntity, IronAttributes.BLOOD_SPELL_POWER);
    }

    public boolean hasEvocationPower(LivingEntity livingEntity){
        return isCompat(livingEntity, IronAttributes.EVOCATION_SPELL_POWER);
    }

    public boolean isCompat(LivingEntity livingEntity, Attribute attribute){
        return this.isLoaded() && livingEntity.getAttribute(attribute) != null;
    }
}
