package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.utils.BrewUtils;
import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

public class BrewArrow extends Arrow {
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(BrewArrow.class, EntityDataSerializers.INT);
    public List<MobEffectInstance> effects = Lists.newArrayList();
    public List<BrewEffectInstance> brewEffects = Lists.newArrayList();
    private boolean fixedColor;

    public BrewArrow(EntityType<? extends Arrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    public BrewArrow(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
        super(p_36861_, p_36862_, p_36863_, p_36864_);
    }

    public BrewArrow(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_EFFECT_COLOR, -1);
    }

    public void setEffectsFromItem(ItemStack p_36879_) {
        if (p_36879_.is(Items.TIPPED_ARROW)) {
            Collection<MobEffectInstance> collection0 = PotionUtils.getCustomEffects(p_36879_);
            if (!collection0.isEmpty()) {
                for(MobEffectInstance instance : collection0) {
                    this.effects.add(new MobEffectInstance(instance));
                }
            }
            Collection<BrewEffectInstance> collection = BrewUtils.getCustomEffects(p_36879_);
            if (!collection.isEmpty()) {
                for(BrewEffectInstance instance : collection) {
                    this.brewEffects.add(new BrewEffectInstance(instance));
                }
            }

            int i = getCustomColor(p_36879_);
            if (i == -1) {
                this.updateColor();
            } else {
                this.setFixedColor(i);
            }
        } else if (p_36879_.is(Items.ARROW)) {
            this.effects.clear();
            this.brewEffects.clear();
            this.entityData.set(ID_EFFECT_COLOR, -1);
        }

    }

    public static int getCustomColor(ItemStack p_36885_) {
        CompoundTag compoundtag = p_36885_.getTag();
        return compoundtag != null && compoundtag.contains("CustomPotionColor", 99) ? compoundtag.getInt("CustomPotionColor") : -1;
    }

    private void updateColor() {
        this.getEntityData().set(ID_EFFECT_COLOR, BrewUtils.getColor(effects, brewEffects));
    }

    public void addEffect(MobEffectInstance p_19717_) {
        this.effects.add(p_19717_);
        if (!this.fixedColor) {
            this.updateColor();
        }

    }

    public void addBrewEffect(BrewEffectInstance p_19717_) {
        this.brewEffects.add(p_19717_);
        if (!this.fixedColor) {
            this.updateColor();
        }
    }

    public int getColor() {
        return this.getEntityData().get(ID_EFFECT_COLOR);
    }

    public void setFixedColor(int p_19715_) {
        this.fixedColor = true;
        this.getEntityData().set(ID_EFFECT_COLOR, p_19715_);
    }

    public void addAdditionalSaveData(CompoundTag p_36881_) {
        super.addAdditionalSaveData(p_36881_);

        if (this.fixedColor) {
            p_36881_.putInt("Color", this.getColor());
        }

        if (!this.effects.isEmpty()) {
            ListTag listtag = new ListTag();

            for(MobEffectInstance mobeffectinstance : this.effects) {
                listtag.add(mobeffectinstance.save(new CompoundTag()));
            }

            p_36881_.put("Effects", listtag);
        }

        if (!this.brewEffects.isEmpty()) {
            ListTag listtag = new ListTag();

            for(BrewEffectInstance brewEffectInstance : this.brewEffects) {
                listtag.add(brewEffectInstance.save(new CompoundTag()));
            }

            p_36881_.put("BrewEffects", listtag);
        }

    }

    public void readAdditionalSaveData(CompoundTag p_36875_) {
        super.readAdditionalSaveData(p_36875_);

        if (p_36875_.contains("Effects", 9)) {
            ListTag listtag = p_36875_.getList("Effects", 10);

            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                MobEffectInstance instance = MobEffectInstance.load(compoundtag);
                if (instance != null) {
                    this.addEffect(instance);
                }
            }
        }

        if (p_36875_.contains("BrewEffects", 9)) {
            ListTag listtag = p_36875_.getList("BrewEffects", 10);

            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                BrewEffectInstance instance = BrewEffectInstance.load(compoundtag);
                if (instance != null) {
                    this.addBrewEffect(instance);
                }
            }
        }

        if (p_36875_.contains("Color", 99)) {
            this.setFixedColor(p_36875_.getInt("Color"));
        } else {
            this.updateColor();
        }

    }

    protected void doPostHurtEffects(LivingEntity p_36873_) {
        super.doPostHurtEffects(p_36873_);
        Entity entity = this.getEffectSource();

        if (!this.effects.isEmpty()) {
            for(MobEffectInstance mobeffectinstance1 : this.effects) {
                p_36873_.addEffect(mobeffectinstance1, entity);
            }
        }

        if (!this.brewEffects.isEmpty()) {
            for(BrewEffectInstance brewEffectInstance : this.brewEffects) {
                if (brewEffectInstance.getEffect().canLinger()) {
                    brewEffectInstance.getEffect().applyInstantenousEffect(this, this.getOwner(), p_36873_, brewEffectInstance.getAmplifier(), 0.5D);
                }
            }
        }

    }

    protected ItemStack getPickupItem() {
        if (this.effects.isEmpty() && this.brewEffects.isEmpty()) {
            return new ItemStack(Items.ARROW);
        } else {
            ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
            PotionUtils.setCustomEffects(itemstack, this.effects);
            if (this.fixedColor) {
                itemstack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
            }

            return itemstack;
        }
    }
}
