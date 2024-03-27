package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PoisonQuill extends Arrow {
    private static final EntityDataAccessor<Boolean> AQUA = SynchedEntityData.defineId(PoisonQuill.class, EntityDataSerializers.BOOLEAN);

    public PoisonQuill(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
    }

    public PoisonQuill(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
        super(p_36861_, p_36862_, p_36863_, p_36864_);
    }

    public PoisonQuill(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.POISON_QUILL.get();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AQUA, false);
    }

    public void addAdditionalSaveData(CompoundTag p_36881_) {
        super.addAdditionalSaveData(p_36881_);
        p_36881_.putBoolean("Aqua", this.isAqua());
    }

    public void readAdditionalSaveData(CompoundTag p_36875_) {
        super.readAdditionalSaveData(p_36875_);
        if (p_36875_.contains("Aqua")){
            this.setAqua(p_36875_.getBoolean("Aqua"));
        }
    }

    protected float getWaterInertia() {
        if (this.isAqua()) {
            return 0.99F;
        }
        return super.getWaterInertia();
    }

    public boolean isAqua(){
        return this.entityData.get(AQUA);
    }

    public void setAqua(boolean aqua){
        this.entityData.set(AQUA, aqua);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.inGround) {
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            ColorUtil colorUtil = new ColorUtil(0xefec8b);
            for (int i = 0; i < 4; ++i) {
                this.level.addParticle(ModParticleTypes.TRAIL.get(), d0 + (this.random.nextGaussian() / 2), d1 + 0.5D + (this.random.nextGaussian() / 2), d2 + (this.random.nextGaussian() / 2), colorUtil.red(), colorUtil.green(), colorUtil.blue());
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            boolean flag = entity.getType() == EntityType.ENDERMAN;
            if (!flag) {
                Entity entity1 = this.getOwner();
                DamageSource damagesource;
                float damage = entity1 instanceof LivingEntity livingEntity ? (float) MobUtil.getAttributeValue(livingEntity, Attributes.ATTACK_DAMAGE, 2.0D) : 2.0F;
                if (entity1 == null) {
                    damagesource = DamageSource.arrow(this, this);
                } else {
                    damagesource = DamageSource.arrow(this, entity1);
                    if (entity1 instanceof LivingEntity) {
                        ((LivingEntity)entity1).setLastHurtMob(entity);
                    }
                }
                if (entity.hurt(damagesource, damage)) {
                    if (entity instanceof LivingEntity livingEntity) {
                        if (entity1 instanceof LivingEntity) {
                            EnchantmentHelper.doPostHurtEffects(livingEntity, entity1);
                            EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingEntity);
                        }

                        this.doPostHurtEffects(livingEntity);

                        MobEffect mobEffect = MobEffects.POISON;
                        if ((entity1 instanceof IOwned owned && CuriosFinder.hasWildRobe(owned.getMasterOwner()))
                        || (entity1 instanceof LivingEntity livingEntity1 && CuriosFinder.hasWildRobe(livingEntity1))) {
                            mobEffect = GoetyEffects.ACID_VENOM.get();
                        }
                        livingEntity.addEffect(new MobEffectInstance(mobEffect, MathHelper.secondsToTicks(2)));

                        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                    }
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult p_37260_) {
        super.onHit(p_37260_);
        if (!this.level.isClientSide) {
            this.discard();
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (pEntity == this.getOwner()){
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if(this.getOwner().isAlliedTo(pEntity) || pEntity.isAlliedTo(this.getOwner())){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        if (this.isAqua()){
            return ModSounds.POISON_QUILL_AQUA_IMPACT.get();
        }
        return ModSounds.POISON_QUILL_IMPACT.get();
    }
}
