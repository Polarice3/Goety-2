package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Map;

public class WitherBolt extends SpellHurtingProjectile {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(WitherBolt.class, EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, Goety.location("textures/entity/projectiles/soul_bolt/wither_bolt_1.png"));
        map.put(1, Goety.location("textures/entity/projectiles/soul_bolt/wither_bolt_2.png"));
        map.put(2, Goety.location("textures/entity/projectiles/soul_bolt/wither_bolt_3.png"));
    });

    public WitherBolt(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public WitherBolt(double pX, double pY, double pZ, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.WITHER_BOLT.get(), pX, pY, pZ, pXPower, pYPower, pZPower, pLevel);
    }

    public WitherBolt(LivingEntity pShooter, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.WITHER_BOLT.get(), pShooter, pXPower, pYPower, pZPower, pLevel);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
    }

    protected float getInertia() {
        return 0.95F;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAnimation(compound.getInt("Animation"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Animation", this.getAnimation());
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getAnimation(), TEXTURE_BY_TYPE.get(0));
    }

    public void rotateToMatchMovement() {
        this.updateRotation();
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public boolean isOnFire() {
        return false;
    }

    protected void onHitEntity(EntityHitResult p_37626_) {
        super.onHitEntity(p_37626_);
        if (!this.level.isClientSide) {
            float baseDamage = SpellConfig.SoulBoltDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
            Entity entity = p_37626_.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;
            if (entity1 instanceof LivingEntity livingentity) {
                if (livingentity instanceof Mob mob){
                    if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null && mob.getAttributeValue(Attributes.ATTACK_DAMAGE) > 0){
                        baseDamage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    }
                }
                baseDamage += this.getExtraDamage();
                flag = entity.hurt(entity.damageSources().indirectMagic(this, livingentity), baseDamage);
                if (flag) {
                    if (entity.isAlive()) {
                        this.doEnchantDamageEffects(livingentity, entity);
                    } else {
                        livingentity.heal(2.0F);
                    }
                }
            } else {
                flag = entity.hurt(entity.damageSources().magic(), baseDamage);
            }

            if (flag && entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this.getEffectSource());
                double x = this.getX();
                double z = this.getZ();
                if (entity1 != null){
                    x = entity1.getX();
                    z = entity1.getZ();
                }
                livingEntity.knockback(1.0F, x - entity.getX(), z - entity.getZ());
            }

        }
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            Entity entity = this.getOwner();
            Vec3 vec3 = Vec3.atCenterOf(this.blockPosition());
            if (entity instanceof LivingEntity) {
                if (pResult instanceof BlockHitResult blockHitResult) {
                    BlockPos blockpos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                    if (BlockFinder.canBeReplaced(this.level, blockpos)) {
                        vec3 = Vec3.atCenterOf(blockpos);
                    }
                } else if (pResult instanceof EntityHitResult entityHitResult) {
                    Entity entity1 = entityHitResult.getEntity();
                    vec3 = Vec3.atCenterOf(entity1.blockPosition());
                }
            }
            if (this.level instanceof ServerLevel serverLevel) {
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.LARGE_SMOKE, this);
                ColorUtil colorUtil = new ColorUtil(0x1f1f1f);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 2, 1), vec3.x, BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                DustCloudParticleOption cloudParticleOptions = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0x1f1f1f).toVector3f()), 1.0F);
                DustCloudParticleOption cloudParticleOptions2 = new DustCloudParticleOption(new Vector3f(Vec3.fromRGB24(0x171717).toVector3f()), 1.0F);
                for (int i = 0; i < 2; ++i) {
                    ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, 1.0F);
                }
                ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions2, vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, 1.0F);
            }
            new SpellExplosion(this.level, this.getOwner() != null ? this.getOwner() : this, this.damageSources().indirectMagic(this, this.getOwner()), vec3.x, vec3.y, vec3.z, 1.0F, 0){
                @Override
                public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                    super.explodeHurt(target, damageSource, x, y, z, seen, actualDamage);
                    if (target instanceof LivingEntity livingEntity) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
                    }
                }
            };
            this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F);
            this.playSound(ModSounds.HELL_BOLT_IMPACT.get(), 1.0F, 1.0F);
            this.discard();
        }

    }

    public void tick() {
        super.tick();
        if (this.getAnimation() < 2) {
            this.setAnimation(this.getAnimation() + 1);
        } else {
            this.setAnimation(0);
        }
        Entity entity = this.getOwner();
        if (this.tickCount >= MathHelper.secondsToTicks(10)){
            this.discard();
        }
        if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() - vec3.x;
            double d1 = this.getY() - vec3.y;
            double d2 = this.getZ() - vec3.z;
            this.level.addParticle(ModParticleTypes.TOTEM_EFFECT.get(),
                    d0 + ((this.level.random.nextDouble() / 4) * (this.level.random.nextIntBetweenInclusive(-1, 1))),
                    d1 + 0.15D,
                    d2 + ((this.level.random.nextDouble() / 4) * (this.level.random.nextIntBetweenInclusive(-1, 1))),
                    0.0D, 0.0D, 0.0D);
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.TOTEM_EFFECT.get();
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
