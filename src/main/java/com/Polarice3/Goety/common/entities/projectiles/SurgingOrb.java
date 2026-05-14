package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.TrailEffect;
import com.Polarice3.Goety.utils.WandUtil;
import com.mojang.math.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class SurgingOrb extends SpellHurtingProjectile{
    public static final EntityDataAccessor<Boolean> DATA_ORANGE = SynchedEntityData.defineId(SurgingOrb.class, EntityDataSerializers.BOOLEAN);
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
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("staff")) {
            this.staff = compound.getBoolean("staff");
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("staff", this.isStaff());
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

    public void tick() {
        super.tick();
        if (this.tickCount >= MathHelper.secondsToTicks(10)){
            this.discard();
        }
        if (this.level.isClientSide) {
            this.prevClientScale = this.clientScale;
            this.clientScale += 0.2F;
            this.clientScale = Mth.clamp(this.clientScale, 0, 1);
            if (tickCount > 5) {
                Vec3 oldPos = new Vec3(xOld, yOld, zOld);
                Matrix4f transform = new Matrix4f();
                transform.rotate(Axis.YP.rotationDegrees(-MathHelper.positionToYaw(this.getDeltaMovement()) - 90));
                transform.rotate(Axis.XP.rotationDegrees(-MathHelper.positionToPitch(this.getDeltaMovement())));
                Vector4f a = transform.transform(new Vector4f(Mth.cos(tickCount / 4.0F) * 0.15F, Mth.sin(tickCount / 4.0F) * 0.15F, 0.0F, 1.0F));
                Vector4f b = transform.transform(new Vector4f(Mth.cos(tickCount / 4.0F + Mth.TWO_PI / 3) * 0.15F, Mth.sin(tickCount / 4.0F + Mth.TWO_PI / 3) * 0.15F, 0.0F, 1.0F));
                Vector4f c = transform.transform(new Vector4f(Mth.cos(tickCount / 4.0F + 2 * Mth.TWO_PI / 3) * 0.15F, Mth.sin(tickCount / 4.0F + 2 * Mth.TWO_PI / 3) * 0.15F, 0.0F, 1.0F));
                trailA.update(oldPos.add(a.x(), a.y() + this.getBbHeight() / 2, a.z()));
                trailB.update(oldPos.add(b.x(), b.y() + this.getBbHeight() / 2, b.z()));
                trailC.update(oldPos.add(c.x(), c.y() + this.getBbHeight() / 2, c.z()));
            }
        }
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
