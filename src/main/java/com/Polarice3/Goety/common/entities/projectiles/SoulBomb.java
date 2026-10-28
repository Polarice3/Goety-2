package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.NecroVFXPalette;
import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.client.particles.SoulSummonVFX;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SpellExplosion;
import com.Polarice3.Goety.utils.TrailEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class SoulBomb extends ThrowableProjectile {
    public static final EntityDataAccessor<Boolean> DATA_NECRO = SynchedEntityData.defineId(SoulBomb.class, EntityDataSerializers.BOOLEAN);
    public static ColorUtil SMASH_COLOR = new ColorUtil(0x2ac9cf);
    public double prevDeltaMovementX, prevDeltaMovementY, prevDeltaMovementZ;
    public float damage;
    public TrailEffect trail = new TrailEffect(0.2F, 6.0F);

    public SoulBomb(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public SoulBomb(double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(ModEntityType.SOUL_BOMB.get(), p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public SoulBomb(LivingEntity p_37463_, Level p_37464_) {
        super(ModEntityType.SOUL_BOMB.get(), p_37463_, p_37464_);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_NECRO, false);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_37265_) {
        super.addAdditionalSaveData(p_37265_);
        p_37265_.putBoolean("Necro", this.isNecro());
        p_37265_.putFloat("Damage", this.damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_37262_) {
        super.readAdditionalSaveData(p_37262_);
        if (p_37262_.contains("Necro")) {
            this.setNecro(p_37262_.getBoolean("Necro"));
        }
        if (p_37262_.contains("Damage")) {
            this.damage = p_37262_.getFloat("Damage");
        }
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public boolean isNecro() {
        return this.entityData.get(DATA_NECRO);
    }

    public void setNecro(boolean necro) {
        this.entityData.set(DATA_NECRO, necro);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevDeltaMovementX = this.getDeltaMovement().x;
        this.prevDeltaMovementY = this.getDeltaMovement().y;
        this.prevDeltaMovementZ = this.getDeltaMovement().z;

        this.setYRot(-((float) Mth.atan2(this.getDeltaMovement().x, this.getDeltaMovement().z)) * (180F / (float)Math.PI)) ;

        if (this.level.isClientSide) {
            Vec3 oldPos = new Vec3(this.xOld, this.yOld + getBbHeight() / 2, this.zOld);
            this.trail.update(oldPos);
        }
    }

    protected void onHit(HitResult p_37406_) {
        super.onHit(p_37406_);
        Vec3 vec3 = this.position();
        if (p_37406_ instanceof EntityHitResult entityHitResult) {
            vec3 = entityHitResult.getEntity().position();
        }
        this.playSound(ModSounds.SOUL_BOMB_EXPLODE.get());
        if (!this.level.isClientSide) {
            LivingEntity owner = null;
            if (this.getOwner() instanceof LivingEntity livingEntity) {
                owner = livingEntity;
            }
            ColorUtil colorUtil = SMASH_COLOR;
            Ripple.RippleStyle style = Ripple.RippleStyle.SOUL;
            if (this.isNecro()) {
                colorUtil = new ColorUtil(NecroVFXPalette.BRIGHT_HEX);
                style = Ripple.RippleStyle.NECRO;
            }
            if (this.level instanceof ServerLevel serverLevel) {
                SoulSummonVFX.emitter(serverLevel, vec3, SoulSummonVFX.Emitter.SOUL_BOMB, 0.0F, this.isNecro());
                serverLevel.sendParticles(new SmashParticleOption(colorUtil, 3, 3 * 0.06F, 2, 10), vec3.x(), vec3.y(), vec3.z(), 0, 0, 0, 0, 0);
                serverLevel.sendParticles(new SphereExplodeParticleOption(colorUtil, 3, 2), vec3.x(), vec3.y(), vec3.z(), 1, 0, 0, 0, 0);
            }
            new SpellExplosion(this.level, this, this.damageSources().explosion(this, owner), vec3.x(), vec3.y(), vec3.z(), 3.0F, this.getDamage()) {
                @Override
                public void postHurtEffect(Entity target, DamageSource damageSource, float actualDamage) {
                    super.postHurtEffect(target, damageSource, actualDamage);
                    if (target instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(GoetyEffects.SOUL_BURN.get(), MathHelper.secondsToTicks(5)));
                    }
                }
            };
            Ripple ripple = new Ripple(this.level, owner, vec3.x(), vec3.y(), vec3.z(), Ripple.Preset.REDSTONE);
            ripple = ripple.setStyle(style);
            ripple = ripple.setDamageType(Ripple.RippleDamage.SOUL);
            ripple = ripple.setDamage(this.getDamage());
            ripple = ripple.setFireSeconds(0);
            ripple = ripple.addEffect(new MobEffectInstance(GoetyEffects.SOUL_BURN.get(), MathHelper.secondsToTicks(5)));
            this.level.addFreshEntity(ripple);
            this.discard();
        }
    }

    @Override
    protected float getGravity() {
        return 0.15F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
