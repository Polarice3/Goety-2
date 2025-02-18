package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;

public class SpiderMotherDenBlockEntity extends ModBlockEntity{
    private int spawnDelay = 20;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;

    public SpiderMotherDenBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.SPIDER_MOTHER_DEN.get(), p_155229_, p_155230_);
    }

    private boolean isNearPlayer(Level pLevel, BlockPos pPos) {
        return pLevel.hasNearbyAlivePlayer((double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, 16.0D);
    }

    public void tick(){
        if (this.level == null){
            return;
        }
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.isNearPlayer(serverLevel, this.worldPosition) && serverLevel.isLoaded(this.worldPosition)) {
                if (this.spawnDelay == -1) {
                    this.delay(serverLevel);
                }
                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                } else {
                    boolean flag = false;
                    if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                        for (int i = 0; i < serverLevel.getRandom().nextInt(4) + 1; ++i) {
                            double d0 = (double) this.worldPosition.getX() + (serverLevel.getRandom().nextDouble() - serverLevel.getRandom().nextDouble()) * 4.0D + 0.5D;
                            double d1 = this.worldPosition.getY() + serverLevel.getRandom().nextInt(3) - 1;
                            double d2 = (double) this.worldPosition.getZ() + (serverLevel.getRandom().nextDouble() - serverLevel.getRandom().nextDouble()) * 4.0D + 0.5D;
                            if (serverLevel.noCollision(EntityType.SPIDER.getAABB(d0, d1, d2))) {
                                BlockPos blockpos = BlockPos.containing(d0, d1, d2);
                                if (SpawnPlacements.checkSpawnRules(EntityType.SPIDER, serverLevel, MobSpawnType.SPAWNER, blockpos, serverLevel.getRandom())) {
                                    Entity entity = EntityType.SPIDER.create(serverLevel);
                                    if (serverLevel.getRandom().nextInt(4) == 0) {
                                        entity = EntityType.CAVE_SPIDER.create(serverLevel);
                                    }
                                    if (entity == null) {
                                        this.delay(serverLevel);
                                        return;
                                    }
                                    int k = this.level.getEntitiesOfClass(entity.getClass(), (new AABB((double) this.worldPosition.getX(), (double) this.worldPosition.getY(), (double) this.worldPosition.getZ(), this.worldPosition.getX() + 1, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 1)).inflate(4.0D)).size();
                                    if (k >= 6) {
                                        this.delay(serverLevel);
                                        return;
                                    }
                                    entity.moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), serverLevel.getRandom().nextFloat() * 360.0F, 0.0F);
                                    if (entity instanceof Mob mob) {
                                        ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(this.worldPosition), MobSpawnType.SPAWNER, null, null);
                                        mob.spawnAnim();
                                    }
                                    if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) {
                                        this.delay(serverLevel);
                                        return;
                                    }
                                    serverLevel.levelEvent(2004, this.worldPosition, 0);
                                    serverLevel.gameEvent(entity, GameEvent.ENTITY_PLACE, blockpos);

                                    flag = true;
                                }
                            }
                        }
                        if (flag) {
                            this.delay(serverLevel);
                        }
                    }
                }
            }

        }
    }

    private void delay(Level p_151351_) {
        RandomSource randomsource = p_151351_.random;
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            this.spawnDelay = this.minSpawnDelay + randomsource.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }
    }

    @Override
    public void readNetwork(CompoundTag compoundNBT) {
        this.spawnDelay = compoundNBT.getShort("Delay");
        if (compoundNBT.contains("MinSpawnDelay")) {
            this.minSpawnDelay = compoundNBT.getShort("MinSpawnDelay");
        }
        if (compoundNBT.contains("MaxSpawnDelay")) {
            this.maxSpawnDelay = compoundNBT.getShort("MaxSpawnDelay");
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag pCompound) {
        pCompound.putShort("Delay", (short)this.spawnDelay);
        pCompound.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        pCompound.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        return pCompound;
    }
}
