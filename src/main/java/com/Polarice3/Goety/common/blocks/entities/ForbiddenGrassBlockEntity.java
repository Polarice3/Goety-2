package com.Polarice3.Goety.common.blocks.entities;

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
        if (this.getOriginalLevel() != null && !this.getOriginalLevel().isClientSide) {
            return (ServerLevel) this.getOriginalLevel();
        } else {
            return null;
        }
    }

    public void tick() {
        if (this.getLevel() != null) {
            if (this.getLevel().isLoaded(this.worldPosition)) {
                BlockPos above = this.getBlockPos().above();
                if (!this.getLevel().getBlockState(above).isSolidRender(this.getLevel(), above)) {
                    if (this.getLevel().isEmptyBlock(above)) {
                        float f = this.getLevel().getLightLevelDependentMagicValue(above);
                        if (f > 0.5F && this.getLevel().canSeeSky(above) && this.getLevel().isDay()) {
                            this.getLevel().setBlockAndUpdate(above, BaseFireBlock.getState(this.getLevel(), above));
                        }
                        for (int j1 = -2; j1 < 2; ++j1) {
                            for (int k1 = -2; k1 <= 2; ++k1) {
                                for (int l1 = -2; l1 < 2; ++l1) {
                                    BlockPos blockPos1 = this.getBlockPos().offset(j1, k1, l1);
                                    BlockPos blockPos2 = blockPos1.above();
                                    BlockState blockState = this.getLevel().getBlockState(blockPos2);
                                    if (blockState.getBlock() instanceof BaseFireBlock) {
                                        if (this.getLevel().random.nextFloat() <= 0.01F) {
                                            this.getLevel().setBlockAndUpdate(above, BaseFireBlock.getState(this.getLevel(), above));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    AABB alignedBB = new AABB(above.getX() - 1, above.getY(), above.getZ() - 1, above.getX() + 1, above.getY() + 1, above.getZ() + 1);
                    int k = this.getLevel().getEntitiesOfClass(LivingEntity.class, alignedBB.inflate(4)).size();
                    if (this.getLevel().random.nextFloat() <= 0.005F) {
                        if (this.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(above).inflate(1)).isEmpty()) {
                            WeightedRandomList<MobSpawnSettings.SpawnerData> spawners = MobUtil.mobsAt(this.getLevel(), this.getLevel().structureManager(), this.getLevel().getChunkSource().getGenerator(), MobCategory.MONSTER, this.getBlockPos(), this.getLevel().getBiome(this.getBlockPos()));
                            if (!spawners.isEmpty()) {
                                List<MobSpawnSettings.SpawnerData> spawnerData = spawners.unwrap();
                                EntityType<?> entityType = spawnerData.get(this.getLevel().random.nextInt(spawnerData.size())).type;
                                if (entityType != null) {
                                    if (k <= 16) {
                                        if (SpawnPlacements.checkSpawnRules(entityType, this.getLevel(), MobSpawnType.SPAWNER, above, this.getLevel().random)) {
                                            Entity entity = entityType.create(this.getLevel());
                                            if (entity instanceof Mob mob) {
                                                mob.setPos(above.getX() + 0.5F, above.getY(), above.getZ() + 0.5F);
                                                if (this.getLevel().noCollision(entity) && this.getLevel().isUnobstructed(entity, this.getLevel().getBlockState(above).getShape(this.getLevel(), above))) {
                                                    mob.finalizeSpawn(this.getLevel(), this.getLevel().getCurrentDifficultyAt(this.worldPosition), MobSpawnType.SPAWNER, null, null);
                                                    this.getLevel().addFreshEntity(mob);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
