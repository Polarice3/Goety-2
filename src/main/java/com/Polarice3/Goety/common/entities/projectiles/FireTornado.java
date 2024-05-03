package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FireTornado extends AbstractHurtingProjectile {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(FireTornado.class, EntityDataSerializers.OPTIONAL_UUID);
    private int lifespan;
    private int totalLife;
    private int spun = 0;

    public FireTornado(EntityType<? extends AbstractHurtingProjectile> p_i50173_1_, Level p_i50173_2_) {
        super(p_i50173_1_, p_i50173_2_);
        this.noPhysics = false;
        this.lifespan = 0;
        this.totalLife = 60;
    }

    public FireTornado(Level level, LivingEntity shooter, double xPower, double yPower, double zPower) {
        super(ModEntityType.FIRE_TORNADO.get(), shooter, xPower, yPower, zPower, level);
    }

    public FireTornado(Level p_i1795_1_, double p_i1795_2_, double p_i1795_4_, double p_i1795_6_, double p_i1795_8_, double p_i1795_10_, double p_i1795_12_) {
        super(ModEntityType.FIRE_TORNADO.get(), p_i1795_2_, p_i1795_4_, p_i1795_6_, p_i1795_8_, p_i1795_10_, p_i1795_12_, p_i1795_1_);
    }

    protected float getInertia() {
        return 0.68F;
    }

    public int getTotalLife() {
        return totalLife;
    }

    public void setTotalLife(int totalLife) {
        this.totalLife = totalLife;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public int getSpun(){
        return spun;
    }

    public void setSpun(int spun){
        this.spun = spun;
    }

    public LivingEntity getTrueOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public void tick() {
        super.tick();
        if (this.getLifespan() < getTotalLife()){
            ++this.lifespan;
        } else {
            this.trueRemove();
        }
        if (this.isInPowderSnow){
            this.lifespan += 10;
        }
        if (this.getTrueOwner() != null){
            if (this.getTrueOwner() instanceof Mob owner){
                if (owner.getTarget() != null){
                    LivingEntity livingentity = owner.getTarget();
                    double d1 = livingentity.getX() - this.getX();
                    double d2 = livingentity.getY(0.5D) - this.getY(0.5D);
                    double d3 = livingentity.getZ() - this.getZ();
                    if (this.tickCount % 50 == 0) {
                        this.fakeRemove(d1, d2, d3);
                    }
                } else {
                    this.trueRemove();
                }
            }
        }
        int maxSpun = 80;
        if (this.getSpun() >= maxSpun){
            this.trueRemove();
        }
        if (this.tickCount % 20 == 0){
            this.playSound(ModSounds.FIRE_TORNADO_AMBIENT.get(), 1.0F, 0.5F);
        }
        List<LivingEntity> targets = new ArrayList<>();
        for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(AreaOfEffect()))) {
            if (this.getTrueOwner() != null) {
                if (entity != this.getTrueOwner() && !entity.isAlliedTo(this.getTrueOwner()) && !this.getTrueOwner().isAlliedTo(entity)) {
                    targets.add(entity);
                }
            } else {
                targets.add(entity);
            }
        }
        if (!targets.isEmpty()){
            for (LivingEntity entity: targets){
                if (MobUtil.validEntity(entity)) {
                    entity.setSecondsOnFire(30);
                    if (entity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                        entity.removeEffectNoUpdate(MobEffects.FIRE_RESISTANCE);
                    }
                    this.suckInMobs(entity);
                }
            }
        }
    }

    public void trueRemove(){
        this.setLifespan(this.getTotalLife());
        this.remove();
    }

    public void fakeRemove(double x, double y, double z){
        FireTornado fireTornadoEntity = new FireTornado(this.level, this.getTrueOwner(), x, y, z);
        fireTornadoEntity.setOwnerId(this.getTrueOwner().getUUID());
        fireTornadoEntity.setLifespan(this.getLifespan());
        fireTornadoEntity.setTotalLife(this.getTotalLife());
        fireTornadoEntity.setSpun(this.getSpun());
        fireTornadoEntity.setPos(this.getX(), this.getY(), this.getZ());
        this.level.addFreshEntity(fireTornadoEntity);
        this.remove();
    }

    public void remove() {
        if (!this.level.isClientSide){
            if (this.getLifespan() >= this.getTotalLife()) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                for (int k = 0; k < 200; ++k) {
                    float f2 = random.nextFloat() * 4.0F;
                    float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                    double d1 = Mth.cos(f1) * f2;
                    double d2 = 0.01D + random.nextDouble() * 0.5D;
                    double d3 = Mth.sin(f1) * f2;
                    serverWorld.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                }
                if (this.getTrueOwner() instanceof Apostle apostle){
                    apostle.setTornadoCoolDown(apostle.getTornadoCoolDown() + MathHelper.secondsToTicks(45));
                }
            }
        }
        this.discard();
    }

    /**
     * Based on EntityDuster lift codes from @AlexModGuy's Alex's Mobs.
     */
    private void suckInMobs(LivingEntity livingEntity) {
        ++this.spun;
        float radius = 1.0F + (this.spun * 0.05F);
        float knockBack = (float) Mth.clamp((1.0D - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
        float angle = this.spun * -0.25F;
        double f0 = this.getX() + radius * Mth.sin((float) (Math.PI + angle));
        double f1 = this.getZ() + radius * Mth.cos(angle);
        double d0 = (f0 - livingEntity.getX()) * knockBack;
        double d1 = (f1 - livingEntity.getZ()) * knockBack;
        if (this.xPower != 0 || this.yPower != 0 || this.zPower != 0){
            if (this.getTrueOwner() != null) {
                this.fakeRemove(0, 0, 0);
            }
        }
        this.hurtMobs(livingEntity);

        MobUtil.twister(livingEntity, d0, 0.1 * knockBack, d1);
    }

    public void hurtMobs(LivingEntity living){
        if (this.getTrueOwner() != null) {
            if (this.getTrueOwner() instanceof Apostle) {
                if (living.hurt(DamageSource.indirectMagic(this, this.getTrueOwner()), AttributesConfig.ApostleMagicDamage.get().floatValue() / 1.5F)){
                    living.addEffect(new MobEffectInstance(GoetyEffects.BURN_HEX.get(), 1200));
                }
            } else {
                living.hurt(DamageSource.indirectMagic(this, this.getTrueOwner()), 4.0F);
            }
        } else {
            if (!living.fireImmune()) {
                living.hurt(DamageSource.IN_FIRE, 4.0F);
            }
        }
        if (living instanceof Player player){
            if (player.isBlocking()) {
                player.disableShield(true);
            }
        }
    }

    public double AreaOfEffect(){
        return 2.0D;
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerId(uuid);
            } catch (Throwable ignored) {
            }
        }
        if (compound.contains("Lifespan")) {
            this.setLifespan(compound.getInt("Lifespan"));
        }
        if (compound.contains("TotalLife")) {
            this.setTotalLife(compound.getInt("TotalLife"));
        }
        if (compound.contains("Spun")){
            this.setSpun(compound.getInt("Spun"));
        }

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }
        compound.putInt("Lifespan", this.getLifespan());
        compound.putInt("TotalLife", this.getTotalLife());
        compound.putInt("Spun", this.getSpun());
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.FLAME;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
