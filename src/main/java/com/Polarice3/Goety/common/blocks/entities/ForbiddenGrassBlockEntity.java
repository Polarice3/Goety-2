package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ConcurrentModificationException;
import java.util.List;

public class ForbiddenGrassBlockEntity extends BlockEntity {

    public ForbiddenGrassBlockEntity(BlockPos p_155301_, BlockState p_155302_) {
        super(ModBlockEntities.FORBIDDEN_GRASS.get(), p_155301_, p_155302_);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }

    public Level getOriginalLevel(){
        return this.level;
    }

    public ServerLevel getLevel() {
        if (this.getOriginalLevel() != null && this.getOriginalLevel() instanceof ServerLevel serverLevel) {
            return serverLevel;
        } else {
            return null;
        }
    }

    public void tick() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (serverLevel.isLoaded(this.worldPosition)) {
                BlockPos above = this.getBlockPos().above();
                if (!serverLevel.getBlockState(above).isSolidRender(serverLevel, above)) {
                    if (serverLevel.isEmptyBlock(above)) {
                        float f = serverLevel.getLightLevelDependentMagicValue(above);
                        if (f > 0.5F && serverLevel.canSeeSky(above) && serverLevel.isDay()) {
                            serverLevel.setBlockAndUpdate(above, BaseFireBlock.getState(serverLevel, above));
                        }
                        for (int j1 = -2; j1 < 2; ++j1) {
                            for (int k1 = -2; k1 <= 2; ++k1) {
                                for (int l1 = -2; l1 < 2; ++l1) {
                                    BlockPos blockPos1 = this.getBlockPos().offset(j1, k1, l1);
                                    BlockPos blockPos2 = blockPos1.above();
                                    BlockState blockState = serverLevel.getBlockState(blockPos2);
                                    if (blockState.getBlock() instanceof BaseFireBlock) {
                                        if (serverLevel.getRandom().nextFloat() <= 0.01F) {
                                            serverLevel.setBlockAndUpdate(above, BaseFireBlock.getState(serverLevel, above));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    AABB alignedBB = new AABB(above.getX() - 1, above.getY(), above.getZ() - 1, above.getX() + 1, above.getY() + 1, above.getZ() + 1);
                    try {
                        int k = serverLevel.getEntitiesOfClass(LivingEntity.class, alignedBB.inflate(4)).size();
                        if (serverLevel.getRandom().nextFloat() <= 0.005F) {
                            if (serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(above).inflate(1)).isEmpty()) {
                                WeightedRandomList<MobSpawnSettings.SpawnerData> spawners = MobUtil.mobsAt(serverLevel, serverLevel.structureManager(), serverLevel.getChunkSource().getGenerator(), MobCategory.MONSTER, this.getBlockPos(), serverLevel.getBiome(this.getBlockPos()));
                                if (!spawners.isEmpty()) {
                                    List<MobSpawnSettings.SpawnerData> spawnerData = spawners.unwrap();
                                    EntityType<?> entityType = spawnerData.get(serverLevel.getRandom().nextInt(spawnerData.size())).type;
                                    if (entityType != null) {
                                        if (!MobUtil.hasEntityTypesConfig(MainConfig.ForbiddenGrassBlackList.get(), entityType)) {
                                            if (k <= 16) {
                                                if (SpawnPlacements.checkSpawnRules(entityType, serverLevel, MobSpawnType.SPAWNER, above, serverLevel.getRandom())) {
                                                    Entity entity = entityType.create(serverLevel);
                                                    if (entity instanceof Mob mob) {
                                                        mob.setPos(above.getX() + 0.5F, above.getY(), above.getZ() + 0.5F);
                                                        if (serverLevel.noCollision(entity) && serverLevel.isUnobstructed(entity, serverLevel.getBlockState(above).getShape(serverLevel, above))) {
                                                            ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(this.worldPosition), MobSpawnType.SPAWNER, null, null);
                                                            serverLevel.addFreshEntity(mob);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (ConcurrentModificationException ignored) {

                    }
                }
            }
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
