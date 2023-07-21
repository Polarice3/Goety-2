package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.nbt.CompoundTag;
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
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.playSound(ModSounds.WALL_SPAWN.get(), 2.0F, 1.0F);
        return pSpawnData;
    }

    @Override
    public BlockState getState() {
        return Blocks.STONE.defaultBlockState();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        this.playSound(ModSounds.WALL_HIT.get());
        return pSource.isBypassInvul();
    }

    @Override
    public int getAgeSpeed() {
        return 3;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isEmerging()){
            if (!this.isActivate()){
                this.setActivate(true);
            }
            if (this.tickCount >= MathHelper.secondsToTicks(6)){
                this.setAge(this.getAge() - this.getAgeSpeed());
            }
            if (this.tickCount == MathHelper.secondsToTicks(6)){
                this.playSound(ModSounds.WALL_ERUPT.get(), 2.0F, 1.0F);
            }
            if (this.getAge() <= 0){
                this.discard();
            }
        }
    }
}
