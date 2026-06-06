package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.*;

public class SurgingOrb extends SpellHurtingProjectile{
    public static final EntityDataAccessor<Boolean> DATA_ORANGE = SynchedEntityData.defineId(SurgingOrb.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> TARGET_UNIQUE_ID = SynchedEntityData.defineId(SurgingOrb.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> TARGET_CLIENT_ID = SynchedEntityData.defineId(SurgingOrb.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> TURN_RATE = SynchedEntityData.defineId(SurgingOrb.class, EntityDataSerializers.FLOAT);
    public boolean staff = false;
    public float clientScale, prevClientScale;

    public TrailEffect trailA = new TrailEffect(0.05F, 1.75F);
    public TrailEffect trailB = new TrailEffect(0.05F, 1.75F);
    public TrailEffect trailC = new TrailEffect(0.05F, 1.75F);

    public SurgingOrb(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public SurgingOrb(double p_36818_, double p_36819_, double p_36820_, double p_36821_, double p_36822_, double p_36823_, Level p_36824_) {
        super(ModEntityType.SURGING_ORB.get(), p_36818_, p_36819_, p_36820_, p_36821_, p_36822_, p_36823_, p_36824_);
    }

    public SurgingOrb(LivingEntity p_36827_, double p_36828_, double p_36829_, double p_36830_, Level p_36831_) {
        super(ModEntityType.SURGING_ORB.get(), p_36827_, p_36828_, p_36829_, p_36830_, p_36831_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ORANGE, false);
        this.entityData.define(TARGET_UNIQUE_ID, Optional.empty());
        this.entityData.define(TARGET_CLIENT_ID, -1);
        this.entityData.define(TURN_RATE, 0.1F);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("staff")) {
            this.staff = compound.getBoolean("staff");
        }
        if (compound.contains("TurnRate")){
            this.setTurnRate(compound.getFloat("TurnRate"));
        }
        if (compound.hasUUID("Target")) {
            this.setTargetId(compound.getUUID("Target"));
        }
        if (compound.contains("TargetClient")){
            this.setTargetClientId(compound.getInt("TargetClient"));
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("staff", this.isStaff());
        compound.putFloat("TurnRate", this.getTurnRate());
        if (this.getTargetId() != null) {
            compound.putUUID("Target", this.getTargetId());
        }
        if (this.getTargetClientId() > -1) {
            compound.putInt("TargetClient", this.getTargetClientId());
        }
    }

    public void setStaff(boolean staff){
        this.staff = staff;
    }

    public boolean isStaff(){
        return this.staff;
    }

    public void setOrange(boolean orange){
        this.entityData.set(DATA_ORANGE, orange);
    }

    public boolean isOrange(){
        return this.entityData.get(DATA_ORANGE);
    }

    @Nullable
    public LivingEntity getTarget() {
        if (!this.level.isClientSide){
            UUID uuid = this.getTargetId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } else {
            int id = this.getTargetClientId();
            return id <= -1 ? null : this.level.getEntity(this.getTargetClientId()) instanceof LivingEntity living ? living : null;
        }
    }

    @Nullable
    public UUID getTargetId() {
        return this.entityData.get(TARGET_UNIQUE_ID).orElse((UUID)null);
    }

    public void setTargetId(@Nullable UUID p_184754_1_) {
        this.entityData.set(TARGET_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public int getTargetClientId(){
        return this.entityData.get(TARGET_CLIENT_ID);
    }

    public void setTargetClientId(int id){
        this.entityData.set(TARGET_CLIENT_ID, id);
    }

    public void setTarget(@Nullable LivingEntity livingEntity) {
        if (livingEntity != null) {
            this.setTargetId(livingEntity.getUUID());
            this.setTargetClientId(livingEntity.getId());
        } else {
            this.setTargetId(null);
            this.setTargetClientId(-1);
        }
    }

    public float getTurnRate() {
        return this.entityData.get(TURN_RATE);
    }

    public void setTurnRate(float turnRate) {
        this.entityData.set(TURN_RATE, turnRate);
    }

    public void tick() {
        super.tick();
        if (this.tickCount >= MathHelper.secondsToTicks(10)){
            this.discard();
        }
        if (this.getTarget() != null && this.getTarget().isAlive() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.getTarget())) {
            if (this.tickCount > 2) {
                Vec3 toTarget = this.getTarget().position().add(0, 0.25F, 0).subtract(this.position());

                double distToTarget = toTarget.length();
                if (distToTarget > this.getTarget().getBbWidth()) {
                    Vec3 vec3 = this.getDeltaMovement();
                    double currentSpeed = vec3.length();

                    if (currentSpeed > 1.0E-6) {
                        Vec3 currentDir = vec3.normalize();
                        Vec3 targetDir = toTarget.normalize();

                        double turnStrength = this.getTurnRate() / this.getInertia();

                        Vec3 newDir = currentDir.lerp(targetDir, turnStrength).normalize();
                        this.setDeltaMovement(newDir.scale(currentSpeed));
                    }
                }
            }
        }
        if (this.level.isClientSide) {
            this.prevClientScale = this.clientScale;
            this.clientScale += 0.2F;
            this.clientScale = Mth.clamp(this.clientScale, 0, 1);
            if (this.tickCount > 5) {
                Vec3 oldPos = new Vec3(this.xOld, this.yOld, this.zOld);
                Matrix4f transform = new Matrix4f();
                transform.rotate(Axis.YP.rotationDegrees(-MathHelper.positionToYaw(this.getDeltaMovement()) - 90));
                transform.rotate(Axis.XP.rotationDegrees(-MathHelper.positionToPitch(this.getDeltaMovement())));
                Vector4f a = transform.transform(new Vector4f(Mth.cos(this.tickCount / 4.0F) * 0.15F, Mth.sin(this.tickCount / 4.0F) * 0.15F, 0.0F, 1.0F));
                Vector4f b = transform.transform(new Vector4f(Mth.cos(this.tickCount / 4.0F + Mth.TWO_PI / 3) * 0.15F, Mth.sin(this.tickCount / 4.0F + Mth.TWO_PI / 3) * 0.15F, 0.0F, 1.0F));
                Vector4f c = transform.transform(new Vector4f(Mth.cos(this.tickCount / 4.0F + 2 * Mth.TWO_PI / 3) * 0.15F, Mth.sin(this.tickCount / 4.0F + 2 * Mth.TWO_PI / 3) * 0.15F, 0.0F, 1.0F));
                this.trailA.update(oldPos.add(a.x(), a.y() + this.getBbHeight() / 2, a.z()));
                this.trailB.update(oldPos.add(b.x(), b.y() + this.getBbHeight() / 2, b.z()));
                this.trailC.update(oldPos.add(c.x(), c.y() + this.getBbHeight() / 2, c.z()));
            }
        } else {
            if (this.getTarget() == null) {
                if (this.getOwner() != null) {
                    if (this.getOwner() instanceof Mob mob) {
                        if (mob.getTarget() != null) {
                            this.setTarget(mob.getTarget());
                        }
                    } else {
                        List<LivingEntity> list = new ArrayList<>();
                        for (Entity entity1 : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(16.0F), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
                            LivingEntity livingEntity = null;
                            if (entity1 instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity living) {
                                livingEntity = living;
                            } else if (entity1 instanceof LivingEntity living) {
                                livingEntity = living;
                            }
                            if (livingEntity != null) {
                                if (MobUtil.ownedPredicate(this).test(livingEntity)) {
                                    list.add(livingEntity);
                                }
                            }
                        }
                        list.sort(Comparator.comparingDouble(this::distanceTo));
                        if (list.stream().findFirst().isPresent()) {
                            LivingEntity livingEntity = list.stream().findFirst().get();
                            this.setTarget(livingEntity);
                        }
                    }
                }
            }
        }
    }

    public void travel(){
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        float f = this.getInertia();
        this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale(f));
        this.setPos(d0, d1, d2);
    }

    protected void onHit(HitResult hitResult) {
        if (!this.level.isClientSide) {
            DamageSource damageSource = ModDamageSource.getDamageSource(this.level, ModDamageSource.SHOCK);
            float damage = SpellConfig.SurgingDamage.get().floatValue() * WandUtil.damageMultiply();
            if (this.getOwner() != null) {
                damageSource = ModDamageSource.indirectShock(this, this.getOwner());
            }
            damage += this.getExtraDamage();
            if (hitResult instanceof EntityHitResult result) {
                Entity entity = result.getEntity();
                if (entity.hurt(damageSource, damage)) {
                    float chance = this.isStaff() ? 0.25F : 0.05F;
                    if (this.level.isThundering() && this.level.isRainingAt(entity.blockPosition())) {
                        chance += 0.25F;
                    }
                    if (entity instanceof LivingEntity livingEntity) {
                        if (this.level.getRandom().nextFloat() <= chance) {
                            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                        }
                        if (this.isStaff()) {
                            float chainDamage = damage / 2.0F;
                            WandUtil.chainLightning(livingEntity, this.getOwner() instanceof LivingEntity living ? living : null, 2.0D, chainDamage);
                        }
                    }
                }
            }
            if (this.level instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = this.isOrange() ? new ColorUtil(ChatFormatting.GOLD) : new ColorUtil(0xa4ffff);
                serverLevel.sendParticles(new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F, 1), this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
                for (int i = 0; i < 8; ++i) {
                    Vec3 vec3 = this.position();
                    int random1 = this.level.getRandom().nextIntBetweenInclusive(-1, 1);
                    int random2 = this.level.getRandom().nextIntBetweenInclusive(-1, 1);
                    int random3 = this.level.getRandom().nextIntBetweenInclusive(-1, 1);
                    Vec3 vec31 = vec3.add(this.level.getRandom().nextDouble() * random1, this.level.getRandom().nextDouble() * random2, this.level.getRandom().nextDouble() * random3);
                    ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, colorUtil, 8));
                }
            }
            this.playSound(ModSounds.THUNDERBOLT.get(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.discard();
        }
    }

    public boolean isOnFire() {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NONE.get();
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
}
