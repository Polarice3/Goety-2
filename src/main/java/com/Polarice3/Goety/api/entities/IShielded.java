package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.function.BooleanSupplier;

public interface IShielded {
    default boolean hasShield(){
        return false;
    }

    default void setShield(boolean shield){
    }

    default int getShieldHealth(){
        return 0;
    }

    default void setShieldHealth(int shieldHealth){
    }

    default int getMaxShieldHealth() {
        return 1;
    }

    default boolean isMeleeAttacking() {
        return false;
    }

    default void setMeleeAttacking(boolean attacking) {
    }

    default int getAttackTick() {
        return 0;
    }

    default void setAttackTick(int tick) {
    }

    default ItemStack getShieldMaterial() {
        return new ItemStack(Items.ANVIL);
    }

    default void destroyShield(){
        Mob mob = this instanceof Mob mob1 ? mob1 : null;
        if (this.hasShield()) {
            if (this.getShieldHealth() > 1){
                this.setShieldHealth(this.getShieldHealth() - 1);
                if (mob != null) {
                    mob.playSound(SoundEvents.SHIELD_BLOCK);
                }
            } else {
                this.setShieldHealth(0);
                this.setShield(false);
                if (mob != null) {
                    mob.playSound(ModSounds.SHIELD_BREAK.get(), 1.5F, 1.0F);
                    if (!this.getShieldMaterial().isEmpty()) {
                        if (mob.level instanceof ServerLevel serverLevel) {
                            ServerParticleUtil.addParticlesAroundSelf(serverLevel, new ItemParticleOption(ParticleTypes.ITEM, this.getShieldMaterial()), mob);
                        }
                    }
                }
            }
        }
    }

    default boolean hurtShielded(DamageSource source, float amount, BooleanSupplier defaultHurt) {
        if (this instanceof Mob mob) {
            if (!mob.level.isClientSide) {
                if (this.hasShield() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    if (amount > 0.0F) {
                        this.destroyShield();
                    }
                    return false;
                } else {
                    if (mob.getTarget() != null && mob instanceof OwnableEntity ownable) {
                        if (source.getEntity() instanceof LivingEntity livingEntity) {
                            double d0 = mob.distanceTo(mob.getTarget());
                            double d1 = mob.distanceTo(livingEntity);
                            if (MobUtil.ownedCanAttack(ownable, livingEntity) && livingEntity != ownable.getOwner()) {
                                if (d0 > d1) {
                                    mob.setTarget(livingEntity);
                                }
                            }
                        }
                    }
                }
            }
        }
        return defaultHurt.getAsBoolean();
    }

    default void handleShieldedEvent(byte event) {
        if (event == 4){
            this.animateAttack();
        } else if (event == 5){
            this.setAttackTick(0);
        } else if (event == 6){
            this.setShield(true);
            this.setShieldHealth(this.getMaxShieldHealth());
        }
    }

    default void animateAttack() {

    }

    default double getShieldedAttackReachSqr(LivingEntity enemy) {
        if (this instanceof Entity entity) {
            return entity.getBbWidth() * 6.0F * entity.getBbWidth() * 6.0F + enemy.getBbWidth();
        }
        return 0.0D;
    }

    default boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        if (this instanceof Entity entity) {
            return distToEnemySqr <= this.getShieldedAttackReachSqr(enemy) || entity.getBoundingBox().intersects(enemy.getBoundingBox());
        }
        return false;
    }

    default Vec3 getHorizontalLookAngle() {
        if (this instanceof Entity entity) {
            return this.shieldedViewVector(0, entity.getYRot());
        }
        return Vec3.ZERO;
    }

    default Vec3 shieldedViewVector(float p_20172_, float p_20173_) {
        float f = p_20172_ * ((float)Math.PI / 180F);
        float f1 = -p_20173_ * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    default boolean isShieldRepair(ItemStack itemStack) {
        return itemStack.is(Tags.Items.INGOTS_IRON);
    }

    default InteractionResult repairShield(LivingEntity livingEntity, ItemStack itemStack) {
        if (this instanceof Mob mob) {
            if (!mob.level.isClientSide) {
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                    itemStack.shrink(1);
                }
                this.setShield(true);
                this.setShieldHealth(this.getMaxShieldHealth());
                mob.level.broadcastEntityEvent(mob, (byte) 6);
                mob.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    default void readShieldedData(CompoundTag pCompound) {
        if (pCompound.contains("hasShield")){
            this.setShield(pCompound.getBoolean("hasShield"));
        }
        if (pCompound.contains("ShieldHeath")){
            this.setShieldHealth(pCompound.getInt("ShieldHeath"));
        }
    }

    default void addShieldedData(CompoundTag pCompound) {
        pCompound.putBoolean("hasShield", this.hasShield());
        pCompound.putInt("ShieldHeath", this.getShieldHealth());
    }
}
