package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

public class EarthFist extends SpellEntity {
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 30;
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState retreatAnimationState = new AnimationState();

    public EarthFist(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.refreshDimensions();
    }

    public EarthFist(Level pLevel, LivingEntity pOwner){
        this(ModEntityType.EARTH_FIST.get(), pLevel);
        this.setOwner(pOwner);
    }

    public EarthFist(Level pLevel, double x, double y, double z, LivingEntity pOwner) {
        this(ModEntityType.EARTH_FIST.get(), pLevel);
        this.setOwner(pOwner);
        this.setPos(x, y, z);
    }

    public EarthFist(Level pLevel, Vec3 vec3, LivingEntity pOwner) {
        this(ModEntityType.EARTH_FIST.get(), pLevel);
        this.setOwner(pOwner);
        this.setPos(vec3.x, vec3.y, vec3.z);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public void setWarmupDelayTicks(int warmupDelayTicks) {
        this.warmupDelayTicks = warmupDelayTicks;
    }

    public boolean isSentSpikeEvent() {
        return this.sentSpikeEvent;
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            MobUtil.moveDownToGround(this);
            --this.warmupDelayTicks;
            if (this.warmupDelayTicks < 0) {
                if (!this.sentSpikeEvent) {
                    this.level.broadcastEntityEvent(this, (byte)4);
                    this.sentSpikeEvent = true;
                    this.refreshDimensions();
                    for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.25D))) {
                        this.dealDamageTo(livingentity);
                    }
                    if (this.level instanceof ServerLevel serverLevel) {
                        BlockPos blockPos = BlockPos.containing(this.getX(), this.getY() - 1.0F, this.getZ());
                        BlockState blockState = serverLevel.getBlockState(blockPos);
                        for (int i = 0; i < 8; ++i) {
                            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                            ServerParticleUtil.circularParticles(serverLevel, option, this, 0.0F, 1.0F, 0.0F, 1.5F);
                        }
                        Vector3f vector3f = new Vector3f(Vec3.fromRGB24(0x7f7d7d).toVector3f());
                        DustCloudParticleOption option = new DustCloudParticleOption(vector3f, 1.0F);
                        ServerParticleUtil.circularParticles(serverLevel, option, this, 1.5F);
                    }
                }

                --this.lifeTicks;
                if (this.lifeTicks == 0) {
                    this.level.broadcastEntityEvent(this, (byte)5);
                }

                if (this.lifeTicks <= -8) {
                    this.discard();
                }
            } else {
                if (this.level instanceof ServerLevel serverLevel) {
                    BlockPos blockPos = BlockPos.containing(this.getX(), this.getY() - 1.0F, this.getZ());
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    ServerParticleUtil.circularParticles(serverLevel, option, this, 1.5F);
                }
            }
        }
    }

    private void dealDamageTo(LivingEntity target) {
        LivingEntity livingentity = this.getOwner();
        float baseDamage = SpellConfig.EarthFistDamage.get().floatValue() * WandUtil.damageMultiply();
        baseDamage += this.getExtraDamage();
        if (target.isAlive() && !target.isInvulnerable()) {
            boolean hurt;
            if (livingentity == null) {
                hurt = target.hurt(this.damageSources().mobProjectile(this, null), baseDamage);
            } else {
                if (target == livingentity){
                    return;
                }
                if (MobUtil.areAllies(target, livingentity)){
                    return;
                }
                hurt = target.hurt(this.damageSources().mobProjectile(this, livingentity), baseDamage);
            }
            if (hurt) {
                MobUtil.push(target, 0, 1, 0);
            } else {
                target.move(MoverType.SHULKER_BOX, new Vec3(0, this.getBbHeight(), 0));
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose p_19975_) {
        if (!this.sentSpikeEvent) {
            return super.getDimensions(p_19975_).scale(0.0F);
        }
        return super.getDimensions(p_19975_);
    }

    public void handleEntityEvent(byte p_36935_) {
        super.handleEntityEvent(p_36935_);
        if (p_36935_ == 4) {
            this.attackAnimationState.start(this.tickCount);
            this.sentSpikeEvent = true;
            this.refreshDimensions();
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.WALL_ERUPT.get(), this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        } else if (p_36935_ == 5) {
            this.attackAnimationState.stop();
            this.retreatAnimationState.start(this.tickCount);
        }

    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
