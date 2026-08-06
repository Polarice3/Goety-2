package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

public class GraveSoilBlock extends Block {

    public GraveSoilBlock(Properties p_49795_) {
        super(p_49795_.randomTicks());
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getBlockState(pPos.above()).canBeReplaced() && pLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && pRandom.nextInt(500) < pLevel.getDifficulty().getId()){
            if (pLevel.isLoaded(pPos)) {
                WeightedRandomList<MobSpawnSettings.SpawnerData> spawners = MobUtil.mobsAt(pLevel, pLevel.structureManager(), pLevel.getChunkSource().getGenerator(), MobCategory.MONSTER, pPos, pLevel.getBiome(pPos));
                if (!spawners.isEmpty()) {
                    List<MobSpawnSettings.SpawnerData> spawnerData = spawners.unwrap();
                    EntityType<?> entityType = spawnerData.get(pLevel.getRandom().nextInt(spawnerData.size())).type;
                    if (entityType != null) {
                        BlockPos above = pPos.above();
                        if (SpawnPlacements.checkSpawnRules(entityType, pLevel, MobSpawnType.SPAWNER, above, pRandom)) {
                            Entity entity = entityType.create(pLevel);
                            if (entity instanceof Mob mob && mob.getMobType() == MobType.UNDEAD) {
                                mob.setPos(above.getX() + 0.5F, above.getY(), above.getZ() + 0.5F);
                                if (pLevel.noCollision(entity) && pLevel.isUnobstructed(entity, pLevel.getBlockState(above).getShape(pLevel, above))) {
                                    ForgeEventFactory.onFinalizeSpawn(mob, pLevel, pLevel.getCurrentDifficultyAt(pPos), MobSpawnType.SPAWNER, null, null);
                                    if (pLevel.addFreshEntity(mob)) {
                                        pLevel.levelEvent(null, 2001, pPos, Block.getId(pState));
                                        pLevel.setBlockAndUpdate(pPos, ModBlocks.DARK_DIRT.get().defaultBlockState());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        if (pLevel.getBlockState(pPos.above()).canBeReplaced()) {
            if (pRand.nextFloat() <= 0.25F) {
                double d0 = (double) pPos.getX() + pRand.nextDouble();
                double d1 = (double) pPos.getY() + 1.1D;
                double d2 = (double) pPos.getZ() + pRand.nextDouble();
                pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
