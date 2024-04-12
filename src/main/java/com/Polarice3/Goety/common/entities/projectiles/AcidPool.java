package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.FoggyCloudParticleOption;
import com.Polarice3.Goety.common.entities.util.AbstractTrap;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class AcidPool extends AbstractTrap {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(AcidPool.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(AcidPool.class, EntityDataSerializers.INT);

    public AcidPool(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(null);
        this.setDuration(50);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_COLOR, 0x44b529);
        this.getEntityData().define(DATA_RADIUS, 2.0F);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_19729_);
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        return EntityDimensions.scalable(this.radius() * 2.0F, 0.5F);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Radius", this.radius());
        compound.putInt("Color", this.getColor());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Radius")){
            this.setRadius(compound.getFloat("Radius"));
        }
        if (compound.contains("Color")){
            this.setColor(compound.getInt("Color"));
        }
    }

    public void setRadius(float p_19713_) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(p_19713_, 0.0F, 32.0F));
        }
    }

    public int getColor() {
        return this.getEntityData().get(DATA_COLOR);
    }

    public void setColor(int p_19715_) {
        this.getEntityData().set(DATA_COLOR, p_19715_);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public float radius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.tickCount % 5 == 0) {
                serverLevel.sendParticles(new FoggyCloudParticleOption(new ColorUtil(this.getColor()), this.radius() / 2.0F, 1), this.getX(), this.getY() + 0.25D, this.getZ(), 1, 0, 0, 0, 0);
            }
            List<LivingEntity> targets = new ArrayList<>();
            for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())){
                if (this.owner != null) {
                    if (livingEntity != this.owner && !livingEntity.isAlliedTo(this.owner) && !this.owner.isAlliedTo(livingEntity)) {
                        targets.add(livingEntity);
                    }
                } else {
                    targets.add(livingEntity);
                }
            }
            if (!targets.isEmpty()){
                for (LivingEntity livingEntity : targets) {
                    if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                        if (this.owner != null) {
                            float damage = 2.0F;
                            livingEntity.hurt(DamageSource.indirectMagic(this, this.owner), damage);
                        } else {
                            livingEntity.hurt(DamageSource.MAGIC, 2.0F);
                        }
                    }
                }
            }
            if (this.tickCount >= this.getDuration()) {
                this.discard();
            }
        }
    }
}
