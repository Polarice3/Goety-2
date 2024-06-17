package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class EvilEyeEffect extends BrewMobEffect {

    public EvilEyeEffect(MobEffectCategory p_19451_, int p_19452_, boolean curable) {
        super(p_19451_, p_19452_, curable);
    }

    public void applyEffectTick(LivingEntity living, int amplify) {
        if (living.level instanceof ServerLevel serverLevel){
            int i = 64 >> amplify;
            if (i == 0){
                i = 2;
            }
            if (serverLevel.random.nextInt(i) == 0) {
                int j = serverLevel.getNearbyEntities(Mob.class, TargetingConditions.DEFAULT, living, living.getBoundingBox().inflate(16.0D, 8.0D, 16.0D)).size();
                if (j < 16) {
                    WeightedRandomList<MobSpawnSettings.SpawnerData> spawners = MobUtil.mobsAt(serverLevel, serverLevel.structureManager(), serverLevel.getChunkSource().getGenerator(), MobCategory.MONSTER, living.blockPosition(), serverLevel.getBiome(living.blockPosition()));
                    if (!spawners.isEmpty()) {
                        MobSpawnSettings.SpawnerData spawner = spawners.getRandom(serverLevel.random).orElse(null);
                        if (spawner != null) {
                            if (SpawnPlacements.checkSpawnRules(spawner.type, serverLevel, MobSpawnType.SPAWNER, living.blockPosition(), serverLevel.random)) {
                                Entity entity = spawner.type.create(serverLevel);
                                if (entity instanceof Mob mob) {
                                    BlockPos blockPos = BlockFinder.SummonRadius(living.blockPosition(), mob, serverLevel, 16);
                                    mob.setPos(blockPos.getX() + 0.5F, blockPos.getY(), blockPos.getZ() + 0.5F);
                                    mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPos), MobSpawnType.SPAWNER, (SpawnGroupData) null, (CompoundTag) null);
                                    if (serverLevel.addFreshEntity(mob)) {
                                        ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.FLAME, mob);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isDurationEffectTick(int tick, int amplify) {
        return true;
    }
}
