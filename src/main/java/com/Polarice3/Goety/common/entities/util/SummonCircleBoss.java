package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class SummonCircleBoss extends Entity {
    public Entity entity;
    private int oldTick;
    public int lifeSpan = 50;
    public boolean playedEvent;

    public SummonCircleBoss(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public SummonCircleBoss(Level pLevel, Vec3 pPos, Entity pEntity){
        this(ModEntityType.SUMMON_CIRCLE_BOSS.get(), pLevel);
        this.setPos(pPos.x, pPos.y, pPos.z);
        this.entity = pEntity;
    }

    public SummonCircleBoss(Level pLevel, BlockPos pPos, Entity pEntity){
        this(ModEntityType.SUMMON_CIRCLE_BOSS.get(), pLevel);
        this.setPos(pPos.getX(), pPos.getY(), pPos.getZ());
        this.entity = pEntity;
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("LifeSpan")) {
            this.lifeSpan = pCompound.getInt("lifeSpan");
        }
        if (pCompound.contains("CurrentLife")) {
            this.tickCount = pCompound.getInt("CurrentLife");
        }
        if (pCompound.contains("PlayedEvent")) {
            this.playedEvent = pCompound.getBoolean("PlayedEvent");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("lifeSpan", this.lifeSpan);
        pCompound.putInt("CurrentLife", this.tickCount);
        pCompound.putBoolean("PlayedEvent", this.playedEvent);
    }

    public void setLifeSpan(int lifeSpan){
        this.lifeSpan = lifeSpan;
    }

    public int getLifeSpan() {
        if (this.lifeSpan == 0){
            return 50;
        }
        return this.lifeSpan;
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public void tick() {
        super.tick();
        this.oldTick = this.tickCount;
        if (!this.isNoGravity()) {
            MobUtil.moveDownToGround(this);
        }
        if (!this.playedEvent){
            this.playedEvent = true;
            this.playSound(ModSounds.BOSS_SUMMON.get(), 16.0F, 1.0F);
        }
        if (this.level instanceof ServerLevel serverWorld) {
            float f = 1.5F;
            float f5 = (float) Math.PI * f * f;
            if (this.tickCount % 5 == 0){
                serverWorld.sendParticles(new TeleportInShockwaveParticleOption(0), this.getX(), this.getY() + 0.25F, this.getZ(), 0, 0, 0, 0, 0.5F);
            }
            if (this.tickCount == this.getLifeSpan()){
                for (int j1 = 0; j1 < 16; ++j1) {
                    for (int k1 = 0; (float) k1 < f5; ++k1) {
                        float f6 = this.random.nextFloat() * ((float) Math.PI * 2F);
                        float f7 = Mth.sqrt(this.random.nextFloat()) * f;
                        float f8 = Mth.cos(f6) * f7;
                        float f9 = Mth.sin(f6) * f7;
                        serverWorld.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX() + (double) f8, this.getY(), this.getZ() + (double) f9, 0, 0, 0.5D, 0, 0.5F);
                    }
                }
                if (this.entity != null){
                    serverWorld.addFreshEntity(entity);
                }
            }
        }
        if (this.tickCount >= this.getLifeSpan()){
            this.discard();
        }
    }

    public float getSwelling(float p_32321_) {
        return Mth.lerp(p_32321_, (float)this.oldTick, (float)this.tickCount) / (float)(this.getLifeSpan() - 2);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
