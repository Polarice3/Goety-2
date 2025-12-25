package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningBoltPacket;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TridentStorm extends CastSpellTrap{
    public boolean instant;
    public int warmUp = MathHelper.secondsToTicks(2);
    public int tickTime;
    public int delay;
    public AnimationState mainAnimationState = new AnimationState();

    public TridentStorm(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.delay = worldIn.getRandom().nextInt(61);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Instant", this.instant);
        compound.putInt("WarmUp", this.warmUp);
        compound.putInt("TickTime", this.tickTime);
        compound.putInt("Delay", this.delay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Instant")){
            this.instant = compound.getBoolean("Instant");
        }
        if (compound.contains("WarmUp")) {
            this.warmUp = compound.getInt("WarmUp");
        }
        if (compound.contains("TickTime")){
            this.tickTime = compound.getInt("TickTime");
        }
        if (compound.contains("Delay")){
            this.delay = compound.getInt("Delay");
        }
    }

    public void setWarmUp(int warmUp){
        this.warmUp = warmUp;
    }

    public int getWarmUp(){
        return this.warmUp;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.tickTime;
        if (!this.level.isClientSide) {
            int time = this.instant ? 0 : this.getWarmUp();
            if (this.tickTime >= time + this.delay) {
                if (!this.isActivated()) {
                    this.level.broadcastEntityEvent(this, (byte) 4);
                    this.setActivated(true);
                }
            }
            if (this.tickTime == (time + this.delay + 5)) {
                float damage = SpellConfig.TridentStormDamage.get().floatValue() * WandUtil.damageMultiply();
                float radius = 2.0F;
                List<LivingEntity> targets = new ArrayList<>();
                AABB aabb = EntityType.TRIDENT.getAABB(this.position().x, this.position().y, this.position().z);
                for (Entity entity : this.level.getEntitiesOfClass(Entity.class, aabb.inflate(1, 16, 1))) {
                    LivingEntity livingEntity = MobUtil.getLivingTarget(entity);
                    if (livingEntity != null) {
                        if (livingEntity.getY() > this.getY() + 1) {
                            if (this.getOwner() != null) {
                                if (livingEntity != this.getOwner() && !MobUtil.areAllies(this.getOwner(), livingEntity)) {
                                    targets.add(livingEntity);
                                }
                            } else {
                                targets.add(livingEntity);
                            }
                        }
                    }
                }
                Optional<LivingEntity> optional = Optional.empty();
                if (!targets.isEmpty()) {
                    targets.sort(Comparator.comparingDouble(Entity::getY));
                    optional = Optional.ofNullable(targets.get(targets.size() - 1));
                }
                Vec3 vec3 = this.position();
                if (optional.isPresent()) {
                    vec3 = optional.get().position();
                }
                new SpellExplosion(this.level, this, ModDamageSource.lightning(this, this.getOwner()), vec3.x, vec3.y, vec3.z, radius, damage + this.getExtraDamage());
                if (this.level instanceof ServerLevel serverLevel){
                    ColorUtil colorUtil = ColorUtil.WHITE;
                    serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, radius, 1), vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                ModNetwork.sendToALL(new SLightningBoltPacket(vec3.add(0.0D, 250.0D, 0.0D), this.position(), 5));
                for (int i = 0; i < 16; ++i) {
                    Vec3 vec31 = vec3.add(this.random.nextDouble(), 1.0D, this.random.nextDouble());
                    ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, 2));
                }
            }
            if (this.tickTime >= time + MathHelper.secondsToTicks(2) + this.delay) {
                this.discard();
            }
        }
    }

    public void handleEntityEvent(byte p_36935_) {
        super.handleEntityEvent(p_36935_);
        if (p_36935_ == 4) {
            this.setActivated(true);
            this.mainAnimationState.start(this.tickCount);
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.TRIDENT_STORM_EXPLODE.get(), this.getSoundSource(), 2.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }

    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
