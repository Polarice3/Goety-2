package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class SummonApostle extends Entity {
    public SummonApostle(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    public void tick() {
        super.tick();
        if (this.tickCount == 150) {
            this.playSound(SoundEvents.AMBIENT_NETHER_WASTES_MOOD.get(), 1.0F, 1.0F);
            for (Player player: this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(32))){
                player.displayClientMessage(Component.translatable("info.goety.apostle.summon"), true);
            }
            if (this.level instanceof ServerLevel serverLevel){
                Warden.applyDarknessAround(serverLevel, this.position(), (Entity)null, 32);
            }
        }
        if (this.tickCount == 300) {
            this.playSound(ModSounds.APOSTLE_AMBIENT.get(), 1.0F, 1.0F);
        }
        if (this.tickCount == 450){
            this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
        }
        if (!this.level.isClientSide) {
            ServerLevel serverWorld = (ServerLevel) this.level;
            if (serverWorld.getDifficulty() == Difficulty.PEACEFUL){
                this.discard();
            }
            for(int i = 0; i < 2; ++i) {
                serverWorld.sendParticles(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() + 1.0D, this.getRandomZ(0.5D), 0, (serverWorld.random.nextDouble() - 0.5D) * 2.0D, -serverWorld.random.nextDouble(), (serverWorld.random.nextDouble() - 0.5D) * 2.0D, 0.5D);
            }
            if (serverWorld.dimension() == Level.NETHER) {
                ServerParticleUtil.gatheringParticles(ParticleTypes.ENCHANT, this, serverWorld);
            }
            if (this.tickCount == 450){
                for(int k = 0; k < 200; ++k) {
                    float f2 = random.nextFloat() * 4.0F;
                    float f1 = random.nextFloat() * ((float)Math.PI * 2F);
                    double d1 = Mth.cos(f1) * f2;
                    double d2 = 0.01D + random.nextDouble() * 0.5D;
                    double d3 = Mth.sin(f1) * f2;
                    serverWorld.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                }
                serverWorld.setWeatherParameters(6000, 0, false, false);
                Apostle apostleEntity = new Apostle(ModEntityType.APOSTLE.get(), this.level);
                apostleEntity.setPos(this.getX(), this.getY(), this.getZ());
                apostleEntity.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                serverWorld.addFreshEntity(apostleEntity);
                this.discard();
            }
        }
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
