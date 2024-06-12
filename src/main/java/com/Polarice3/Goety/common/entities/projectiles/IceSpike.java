package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class IceSpike extends AbstractArrow {
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(IceSpike.class, EntityDataSerializers.FLOAT);

    public IceSpike(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
        this.pickup = Pickup.DISALLOWED;
    }

    public IceSpike(double p_36712_, double p_36713_, double p_36714_, Level p_36715_) {
        super(ModEntityType.ICE_SPIKE.get(), p_36712_, p_36713_, p_36714_, p_36715_);
        this.pickup = Pickup.DISALLOWED;
    }

    public IceSpike(LivingEntity p_36718_, Level p_36719_) {
        super(ModEntityType.ICE_SPIKE.get(), p_36718_, p_36719_);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
    }

    public void addAdditionalSaveData(CompoundTag p_36881_) {
        super.addAdditionalSaveData(p_36881_);
        p_36881_.putFloat("ExtraDamage", this.getExtraDamage());
    }

    public void readAdditionalSaveData(CompoundTag p_36875_) {
        super.readAdditionalSaveData(p_36875_);
        if (p_36875_.contains("ExtraDamage")) {
            this.setExtraDamage(p_36875_.getFloat("ExtraDamage"));
        }
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float pDamage) {
        this.entityData.set(DATA_EXTRA_DAMAGE, pDamage);
    }

    public void tick() {
        super.tick();
        if (this.isInLava() || this.isOnFire()){
            this.discard();
        }
        if (!this.inGround) {
            Vec3 vector3d = this.getDeltaMovement();
            double d3 = vector3d.x;
            double d4 = vector3d.y;
            double d0 = vector3d.z;
            double d5 = this.getX() + d3;
            double d1 = this.getY() + d4;
            double d2 = this.getZ() + d0;
            this.level.addParticle(ParticleTypes.SNOWFLAKE, d5 - d3 * 0.25D, d1 - d4 * 0.25D, d2 - d0 * 0.25D, d3, d4, d0);
        }
    }

    protected void onHitEntity(EntityHitResult p_37626_) {
        if (!this.level.isClientSide) {
            float baseDamage = SpellConfig.IceSpikeDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            Entity entity = p_37626_.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;
            baseDamage += this.getExtraDamage();
            if (entity1 instanceof LivingEntity livingentity) {
                flag = entity.hurt(ModDamageSource.indirectFreeze(this, livingentity), baseDamage);
                if (flag) {
                    if (entity.isAlive()) {
                        this.doEnchantDamageEffects(livingentity, entity);
                    }
                }
            } else {
                flag = entity.hurt(ModDamageSource.indirectFreeze(this, this), baseDamage);
            }

            if (flag && entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(3 + livingEntity.getRandom().nextInt(2))));
                this.playSound(ModSounds.ICE_SPIKE_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                if (livingEntity.level instanceof ServerLevel serverLevel){
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.PACKED_ICE.defaultBlockState()), livingEntity);
                }
                this.discard();
            }

        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null){
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity){
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)){
                    return false;
                }
                if (this.getOwner() instanceof Enemy && pEntity instanceof Enemy){
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1){
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    protected boolean tryPickup(Player p_150196_) {
        return false;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.GLASS_BREAK;
    }

    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
