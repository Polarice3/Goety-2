package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.common.entities.projectiles.Pyroclast;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class Volcano extends AbstractMonolith{
    public float explosionPower = 1.5F;
    public int potency = 0;
    public int flaming = 0;
    private int attackStep;
    private int attackTime = MathHelper.secondsToTicks(2);

    public Volcano(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.canSpawn(pLevel.getLevel())){
            this.playSound(ModSounds.RUMBLE.get(), 2.0F, 1.0F);
            this.playSound(ModSounds.WALL_ERUPT.get(), 1.0F, 1.0F);
        }
        return pSpawnData;
    }

    public void setExplosionPower(float pExplosionPower) {
        this.explosionPower = pExplosionPower;
    }

    public float getExplosionPower() {
        return this.explosionPower;
    }

    public void setPotency(int potency) {
        this.potency = potency;
    }

    public int getPotency() {
        return this.potency;
    }

    public void setFlaming(int flaming) {
        this.flaming = flaming;
    }

    public int getFlaming() {
        return this.flaming;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("ExplosionPower", this.getExplosionPower());
        pCompound.putInt("Potency",this.getPotency());
        pCompound.putInt("Flaming",this.getFlaming());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExplosionPower")) {
            this.setExplosionPower(pCompound.getFloat("ExplosionPower"));
        }
        if (pCompound.contains("Potency")){
            this.setPotency(pCompound.getInt("Potency"));
        }
        if (pCompound.contains("Flaming")){
            this.setFlaming(pCompound.getInt("Flaming"));
        }
    }

    protected void doPush(Entity p_20971_) {
    }

    public boolean canCollideWith(Entity p_20303_) {
        return false;
    }

    @Override
    public BlockState getState() {
        return Blocks.BLACKSTONE.defaultBlockState();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return pSource.isBypassInvul();
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public int getAgeSpeed() {
        return 5;
    }

    public boolean canSpawn(Level level){
        return true;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isEmerging()){
            if (!this.isActivate()){
                this.setActivate(true);
            }
            if (!this.level.isClientSide) {
                --this.attackTime;
                if (this.attackTime <= 0) {
                    ++this.attackStep;
                    if (this.attackStep <= 8) {
                        this.attackTime = 5;
                    } else {
                        this.attackTime = 80;
                        this.attackStep = 0;
                    }
                    if (this.attackStep > 1) {
                        if (!this.isSilent()) {
                            this.level.levelEvent((Player)null, 1018, this.blockPosition(), 0);
                        }

                        if (this.level instanceof ServerLevel serverLevel){
                            double d0 = (double)this.getX() + this.random.nextDouble();
                            double d1 = (double)this.getY() + 1.0D;
                            double d2 = (double)this.getZ() + this.random.nextDouble();
                            serverLevel.sendParticles(ParticleTypes.LAVA, d0, d1, d2, 0, 0.0D, 0.0D, 0.0D, 0.5F);
                        }

                        Pyroclast pyroclast = new Pyroclast(this, this.level);
                        if (this.getTrueOwner() != null){
                            pyroclast.setOwner(this.getTrueOwner());
                        } else {
                            pyroclast.setOwner(this);
                        }
                        pyroclast.setExplosionPower(this.getExplosionPower());
                        pyroclast.setPotency(this.getPotency());
                        pyroclast.setFlaming(this.getFlaming());
                        MobUtil.shootUp(pyroclast, this, MobUtil.ceilingVelocity(this, 1.5F));
                    }
                } else {
                    if (this.level instanceof ServerLevel serverLevel){
                        ServerParticleUtil.smokeParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), serverLevel);
                        if (this.random.nextInt(100) == 0) {
                            double d0 = (double)this.getX() + this.random.nextDouble();
                            double d1 = (double)this.getY() + 1.0D;
                            double d2 = (double)this.getZ() + this.random.nextDouble();
                            serverLevel.sendParticles(ParticleTypes.LAVA, d0, d1, d2, 0, 0.0D, 0.0D, 0.0D, 0.5F);
                            this.playSound(SoundEvents.LAVA_POP, 0.2F + this.random.nextFloat() * 0.2F, 0.9F + this.random.nextFloat() * 0.15F);
                        }

                        if (this.random.nextInt(200) == 0) {
                            this.playSound(SoundEvents.LAVA_AMBIENT, 0.2F + this.random.nextFloat() * 0.2F, 0.9F + this.random.nextFloat() * 0.15F);
                        }
                    }
                }
                if (this.tickCount >= MathHelper.secondsToTicks(this.getLifeSpan())){
                    this.setAge(this.getAge() - this.getAgeSpeed());
                    this.level.broadcastEntityEvent(this, (byte) 5);
                }
                if (this.getAge() <= 0){
                    this.discard();
                }
            }
            this.setOnGround(true);
            if (this.tickCount == MathHelper.secondsToTicks(this.getLifeSpan())){
                this.playSound(ModSounds.RUMBLE.get(), 2.0F, 1.0F);
            }
        }
    }
}
