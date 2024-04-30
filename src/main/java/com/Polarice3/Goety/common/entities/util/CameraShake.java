package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/**
 * Ripped from @BobMowzie's codes:<a href="https://github.com/BobMowzie/MowziesMobs/blob/1.19.2/src/main/java/com/bobmowzie/mowziesmobs/server/entity/effects/EntityCameraShake.java">...</a>
 */
public class CameraShake extends Entity {
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(CameraShake.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAGNITUDE = SynchedEntityData.defineId(CameraShake.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(CameraShake.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FADE_DURATION = SynchedEntityData.defineId(CameraShake.class, EntityDataSerializers.INT);

    public CameraShake(EntityType<?> type, Level world) {
        super(type, world);
    }

    public CameraShake(Level world, Vec3 position, float radius, float magnitude, int duration, int fadeDuration) {
        super(ModEntityType.CAMERA_SHAKE.get(), world);
        this.setRadius(radius);
        this.setMagnitude(magnitude);
        this.setDuration(duration);
        this.setFadeDuration(fadeDuration);
        this.setPos(position.x(), position.y(), position.z());
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(RADIUS, 10.0F);
        getEntityData().define(MAGNITUDE, 1.0F);
        getEntityData().define(DURATION, 0);
        getEntityData().define(FADE_DURATION, 5);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Radius")) {
            this.setRadius(compound.getFloat("Radius"));
        }
        if (compound.contains("Magnitude")) {
            this.setMagnitude(compound.getFloat("Magnitude"));
        }
        if (compound.contains("Duration")) {
            this.setDuration(compound.getInt("Duration"));
        }
        if (compound.contains("Fade")) {
            this.setFadeDuration(compound.getInt("Fade"));
        }
        if (compound.contains("TickAmount")) {
            this.tickCount = compound.getInt("TickAmount");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("Radius", this.getRadius());
        compound.putFloat("Magnitude", this.getMagnitude());
        compound.putInt("Duration", this.getDuration());
        compound.putInt("Fade", this.getFadeDuration());
        compound.putInt("TickCount", this.tickCount);
    }

    public float getShakeAmount(Player player, float delta) {
        float ticksDelta = this.tickCount + delta;
        float timeFrac = 1.0F - (ticksDelta - this.getDuration()) / (this.getFadeDuration() + 1.0f);
        float baseAmount = ticksDelta < this.getDuration() ? this.getMagnitude() : timeFrac * timeFrac * this.getMagnitude();
        Vec3 playerPos = player.getEyePosition(delta);
        float distFrac = (float) (1.0F - Mth.clamp(this.position().distanceTo(playerPos) / this.getRadius(), 0, 1));
        return baseAmount * distFrac * distFrac;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > (this.getDuration() + this.getFadeDuration())) {
            this.discard();
        }
    }

    public float getRadius() {
        return this.getEntityData().get(RADIUS);
    }

    public void setRadius(float radius) {
        this.getEntityData().set(RADIUS, radius);
    }

    public float getMagnitude() {
        return this.getEntityData().get(MAGNITUDE);
    }

    public void setMagnitude(float magnitude) {
        this.getEntityData().set(MAGNITUDE, magnitude);
    }

    public int getDuration() {
        return this.getEntityData().get(DURATION);
    }

    public void setDuration(int duration) {
        this.getEntityData().set(DURATION, duration);
    }

    public int getFadeDuration() {
        return this.getEntityData().get(FADE_DURATION);
    }

    public void setFadeDuration(int fadeDuration) {
        this.getEntityData().set(FADE_DURATION, fadeDuration);
    }

    public static void cameraShake(Level world, Vec3 position, float radius, float magnitude, int duration, int fadeDuration) {
        if (!world.isClientSide) {
            CameraShake cameraShake = new CameraShake(world, position, radius, magnitude, duration, fadeDuration);
            world.addFreshEntity(cameraShake);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
