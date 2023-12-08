package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class Cushion extends Entity {
    public float radius = 2.0F;
    public int lifeTicks = MathHelper.secondsToTicks(10);

    public Cushion(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("LifeTicks")){
            this.lifeTicks = pCompound.getInt("LifeTicks");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("LifeTicks", this.lifeTicks);
    }

    @Override
    public void tick() {
        super.tick();
        --this.lifeTicks;
        if (!this.level.isClientSide) {
            if (!this.isNoGravity()) {
                MobUtil.moveDownToGround(this);
            }
            if (this.lifeTicks <= 0){
                this.discard();
            } else if (this.lifeTicks < MathHelper.secondsToTicks(9)){
                if (this.level instanceof ServerLevel serverLevel){
                    ServerParticleUtil.addGroundAuraParticles(serverLevel, ParticleTypes.ENCHANT, this, this.radius);
                    ServerParticleUtil.circularParticles(serverLevel, ParticleTypes.ENCHANT, this.getX(), this.getY(), this.getZ(), this.radius);
                }
                for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                    if (livingentity != null && livingentity.isAlive()) {
                        livingentity.resetFallDistance();
                    }
                }
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
