package com.Polarice3.Goety.common.entities.hostile.servants;

import com.Polarice3.Goety.common.entities.neutral.AbstractObsidianMonolith;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class ObsidianMonolith extends AbstractObsidianMonolith implements Enemy {

    public ObsidianMonolith(EntityType<? extends AbstractObsidianMonolith> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason != MobSpawnType.STRUCTURE) {
            if (this.canSpawn(pLevel.getLevel())) {
                if (pReason != MobSpawnType.MOB_SUMMONED && pReason != MobSpawnType.SPAWN_EGG){
                    this.shouldSpawnHeretics = this.getTrueOwner() == null;
                }
                this.playSound(ModSounds.RUMBLE.get(), 10.0F, 1.0F);
                this.playSound(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 10.0F, 0.25F);
            }
        } else {
            this.shouldSpawnHeretics = false;
        }
        return pSpawnData;
    }

    public static boolean checkOMSpawnRules(EntityType<? extends Owned> p_217058_, LevelAccessor p_217059_, MobSpawnType p_217060_, BlockPos p_217061_, RandomSource p_217062_) {
        List<AbstractObsidianMonolith> monoliths = p_217059_.getEntitiesOfClass(AbstractObsidianMonolith.class, new AABB(p_217061_).inflate(32.0D, 16.0D, 32.0D));
        if (!monoliths.isEmpty()){
            return false;
        }
        if (p_217059_ instanceof ServerLevelAccessor serverLevelAccessor) {
            return checkHostileSpawnRules(p_217058_, serverLevelAccessor, p_217060_, p_217061_, p_217062_);
        } else {
            return false;
        }
    }
}
