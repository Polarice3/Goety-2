package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TotemicWall extends AbstractMonolith{
    public TotemicWall(EntityType<? extends Owned> type, Level worldIn) {
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
            this.playSound(ModSounds.WALL_SPAWN.get(), 2.0F, 1.0F);
            this.playSound(ModSounds.WALL_ERUPT.get(), 1.0F, 1.0F);
            ServerLevel serverLevel = pLevel.getLevel();
            for (int i = 0; i < serverLevel.random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), serverLevel);
            }
        }
        return pSpawnData;
    }

    @Override
    public BlockState getState() {
        return Blocks.STONE.defaultBlockState();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!pSource.is(DamageTypeTags.BYPASSES_ARMOR)) {
            this.playSound(ModSounds.WALL_HIT.get());
        }
        return pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public int getAgeSpeed() {
        return 5;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isEmerging()){
            if (!this.isActivate()){
                this.setActivate(true);
            }
            if (!this.level.isClientSide) {
                if (this.tickCount >= MathHelper.secondsToTicks(this.getLifeSpan())){
                    this.setAge(this.getAge() - this.getAgeSpeed());
                    this.level.broadcastEntityEvent(this, (byte) 5);
                }
                if (this.getAge() <= 0){
                    this.discard();
                }
            }
            if (this.tickCount == MathHelper.secondsToTicks(this.getLifeSpan())){
                this.playSound(ModSounds.WALL_DISAPPEAR.get(), 2.0F, 1.0F);
            }
        }
    }
}
