package com.Polarice3.Goety.common.blocks.entities.void_spawner;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.MagicAshSmokeParticle;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.PlayerDetector;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Optional;
import java.util.UUID;

//Based codes from deobfuscated codes by @mahtomedi:https://github.com/mahtomedi/minecraft/blob/main/src/main/java/net/minecraft/world/level/block/entity/trialspawner/TrialSpawner.java
public class VoidSpawner {
    public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
    private static final int MAX_MOB_TRACKING_DISTANCE = 47;
    private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.square(47);
    private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;
    private final VoidSpawnerConfig config;
    private final VoidSpawnerData data;
    private final StateAccessor stateAccessor;
    private PlayerDetector playerDetector;
    private final PlayerDetector.EntitySelector entitySelector;
    private boolean overridePeacefulAndMobSpawnRule;

    public Codec<VoidSpawner> codec() {
        return RecordCodecBuilder.create(
                instance -> instance.group(VoidSpawnerConfig.MAP_CODEC.forGetter(VoidSpawner::getConfig), VoidSpawnerData.MAP_CODEC.forGetter(VoidSpawner::getData))
                        .apply(instance, (trialSpawnerConfig, trialSpawnerData) -> new VoidSpawner(trialSpawnerConfig, trialSpawnerData, this.stateAccessor, this.playerDetector, this.entitySelector))
        );
    }

    public VoidSpawner(StateAccessor stateAccessor, PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {
        this(VoidSpawnerConfig.DEFAULT, new VoidSpawnerData(), stateAccessor, playerDetector, entitySelector);
    }

    public VoidSpawner(VoidSpawnerConfig trialSpawnerConfig, VoidSpawnerData trialSpawnerData, StateAccessor stateAccessor, PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {
        this.config = trialSpawnerConfig;
        this.data = trialSpawnerData;
        this.data.setSpawnPotentialsFromConfig(trialSpawnerConfig);
        this.stateAccessor = stateAccessor;
        this.playerDetector = playerDetector;
        this.entitySelector = entitySelector;
    }

    public VoidSpawnerConfig getConfig() {
        return this.config;
    }

    public int getTargetCooldownLength() {
        return this.config.targetCooldownLength();
    }

    public int getRequiredPlayerRange() {
        return this.config.requiredPlayerRange();
    }

    public VoidSpawnerData getData() {
        return this.data;
    }

    public VoidSpawnerState getState() {
        return this.stateAccessor.getState();
    }

    public void setState(Level level, VoidSpawnerState trialSpawnerState) {
        this.stateAccessor.setState(level, trialSpawnerState);
    }

    public void markUpdated() {
        this.stateAccessor.markUpdated();
    }

    public PlayerDetector getPlayerDetector() {
        return this.playerDetector;
    }

    public PlayerDetector.EntitySelector getEntitySelector() {
        return this.entitySelector;
    }

    public boolean canSpawnInLevel(Level level) {
        if (this.overridePeacefulAndMobSpawnRule) {
            return true;
        } else {
            return level.getDifficulty() != Difficulty.PEACEFUL;
        }
    }

    public Optional<UUID> spawnMob(ServerLevel serverLevel, BlockPos blockPos) {
        RandomSource randomSource = serverLevel.getRandom();
        SpawnData spawnData = this.data.getOrCreateNextSpawnData(this, serverLevel.getRandom());
        CompoundTag compoundTag = spawnData.entityToSpawn();
        ListTag listTag = compoundTag.getList("Pos", 6);
        Optional<EntityType<?>> optional = EntityType.by(compoundTag);
        if (optional.isEmpty()) {
            return Optional.empty();
        } else {
            int i = listTag.size();
            double d = i >= 1 ? listTag.getDouble(0) : blockPos.getX() + (randomSource.nextDouble() - randomSource.nextDouble()) * this.config.spawnRange() + 0.5;
            double e = i >= 2 ? listTag.getDouble(1) : blockPos.getY() + randomSource.nextInt(3) - 1;
            double f = i >= 3 ? listTag.getDouble(2) : blockPos.getZ() + (randomSource.nextDouble() - randomSource.nextDouble()) * this.config.spawnRange() + 0.5;
            if (!serverLevel.noCollision(optional.get().getAABB(d, e, f))) {
                return Optional.empty();
            } else {
                Vec3 vec3 = new Vec3(d, e, f);
                if (!inLineOfSight(serverLevel, blockPos.getCenter(), vec3)) {
                    return Optional.empty();
                } else {
                    BlockPos blockPos2 = BlockPos.containing(vec3);
                    /*if (!SpawnPlacements.checkSpawnRules(optional.get(), serverLevel, MobSpawnType.SPAWNER, blockPos2, serverLevel.getRandom())) {
                        return Optional.empty();
                    } else {

                    }*/
                    Entity entity = EntityType.loadEntityRecursive(compoundTag, serverLevel, entityx -> {
                        entityx.moveTo(d, e, f, randomSource.nextFloat() * 360.0F, 0.0F);
                        return entityx;
                    });
                    if (entity == null) {
                        return Optional.empty();
                    } else {
                        if (entity instanceof Mob mob) {
                            if (!mob.checkSpawnObstruction(serverLevel)) {
                                return Optional.empty();
                            }

                            if (spawnData.getEntityToSpawn().size() == 1 && spawnData.getEntityToSpawn().contains("id", 8)) {
                                ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.SPAWNER, null, null);
                                mob.setPersistenceRequired();
                                if (mob instanceof IServant servant) {
                                    servant.setBoundPos(blockPos);
                                }
                                mob.restrictTo(blockPos, MobsConfig.ServantGuardingRange.get());
                            }
                        }

                        if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) {
                            return Optional.empty();
                        } else {
                            addSpawnParticles(serverLevel, blockPos, serverLevel.getRandom());
                            serverLevel.playSound(null, blockPos2, ModSounds.SUMMON_SPELL_FIERY.get(), SoundSource.BLOCKS, 1.0F, (serverLevel.getRandom().nextFloat() - serverLevel.getRandom().nextFloat()) * 0.2F + 1.0F);
                            addSpawnParticles(serverLevel, blockPos2, serverLevel.getRandom());
                            serverLevel.gameEvent(entity, GameEvent.ENTITY_PLACE, blockPos2);
                            return Optional.of(entity.getUUID());
                        }
                    }
                }
            }
        }
    }

    public void ejectReward(ServerLevel serverLevel, BlockPos blockPos, ResourceLocation resourceLocation) {
        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(resourceLocation);
        LootParams lootParams = new LootParams.Builder(serverLevel).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> objectArrayList = lootTable.getRandomItems(lootParams);
        if (!objectArrayList.isEmpty()) {
            for (ItemStack itemStack : objectArrayList) {
                DefaultDispenseItemBehavior.spawnItem(serverLevel, itemStack, 2, Direction.UP, Vec3.atBottomCenterOf(blockPos).relative(Direction.UP, 1.2));
            }
            serverLevel.playSound(null, blockPos, ModSounds.VOID_SPAWNER_EJECT_ITEM.get(), SoundSource.BLOCKS, 1.0F, (serverLevel.getRandom().nextFloat() - serverLevel.getRandom().nextFloat()) * 0.2F + 1.0F);
            addEjectItemParticles(serverLevel, blockPos, serverLevel.getRandom());
        }
    }

    public void tickClient(Level level, BlockPos blockPos) {
        if (!this.canSpawnInLevel(level)) {
            this.data.oSpin = this.data.spin;
        } else {
            VoidSpawnerState trialSpawnerState = this.getState();
            trialSpawnerState.emitParticles(level, blockPos);
            if (trialSpawnerState.hasSpinningMob()) {
                double d = Math.max(0L, this.data.nextMobSpawnsAt - level.getGameTime());
                this.data.oSpin = this.data.spin;
                this.data.spin = (this.data.spin + trialSpawnerState.spinningMobSpeed() / (d + 200.0)) % 360.0;
            }

            if (trialSpawnerState.isCapableOfSpawning()) {
                RandomSource randomSource = level.getRandom();
                if (randomSource.nextFloat() <= 0.02F) {
                    level.playLocalSound(blockPos, ModSounds.VOID_SPAWNER_AMBIENT.get(), SoundSource.BLOCKS, randomSource.nextFloat() * 0.25F + 0.75F, randomSource.nextFloat() + 0.5F, false);
                }
            }
        }
    }

    public void tickServer(ServerLevel serverLevel, BlockPos blockPos) {
        VoidSpawnerState trialSpawnerState = this.getState();
        if (!this.canSpawnInLevel(serverLevel)) {
            if (trialSpawnerState.isCapableOfSpawning()) {
                this.data.reset();
                this.setState(serverLevel, VoidSpawnerState.INACTIVE);
            }
        } else {
            if (this.data.currentMobs.removeIf(uUID -> shouldMobBeUntracked(serverLevel, blockPos, uUID))) {
                this.data.nextMobSpawnsAt = serverLevel.getGameTime() + this.config.ticksBetweenSpawn();
            }

            VoidSpawnerState trialSpawnerState2 = trialSpawnerState.tickAndGetNext(blockPos, this, serverLevel);
            if (trialSpawnerState2 != trialSpawnerState) {
                this.setState(serverLevel, trialSpawnerState2);
            }
        }
    }

    private static boolean shouldMobBeUntracked(ServerLevel serverLevel, BlockPos blockPos, UUID uUID) {
        Entity entity = serverLevel.getEntity(uUID);
        return entity == null
                || !entity.isAlive()
                || !entity.level().dimension().equals(serverLevel.dimension())
                || entity.blockPosition().distSqr(blockPos) > MAX_MOB_TRACKING_DISTANCE_SQR;
    }

    private static boolean inLineOfSight(Level level, Vec3 vec3, Vec3 vec32) {
        BlockHitResult blockHitResult = level.clip(new ClipContext(vec32, vec3, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
        return blockHitResult.getBlockPos().equals(BlockPos.containing(vec3)) || blockHitResult.getType() == HitResult.Type.MISS;
    }

    public static void addSpawnParticles(ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        for (int i = 0; i < 20; i++) {
            double d = blockPos.getX() + 0.5D + (randomSource.nextDouble() - 0.5D) * 2.0D;
            double e = blockPos.getY() + 0.5D + (randomSource.nextDouble() - 0.5D) * 2.0D;
            double f = blockPos.getZ() + 0.5D + (randomSource.nextDouble() - 0.5D) * 2.0D;
            level.sendParticles(new MagicAshSmokeParticle.Option(0x3a0637, 0x691575), d, e, f, 1, 0.0D, 0.0D, 0.0D, 0.0F);
            level.sendParticles(ModParticleTypes.END_FIRE.get(), d, e, f, 1, 0.0D, 0.0D, 0.0D, 0.0F);
            level.sendParticles(ModParticleTypes.END_FIRE_DROP.get(), d, e, f, 1, 0.0D, 0.0D, 0.0D, 0.0F);
        }
    }

    public static void addDetectPlayerParticles(ServerLevel level, BlockPos blockPos, RandomSource randomSource, int i) {
        for (int j = 0; j < 30 + Math.min(i, 10) * 5; j++) {
            double d = (2.0F * randomSource.nextFloat() - 1.0F) * 0.65D;
            double e = (2.0F * randomSource.nextFloat() - 1.0F) * 0.65D;
            double f = blockPos.getX() + 0.5D + d;
            double g = blockPos.getY() + 0.1D + randomSource.nextFloat() * 0.8D;
            double h = blockPos.getZ() + 0.5D + e;
            level.sendParticles(ModParticleTypes.VOID_SPAWNER_DETECTION.get(), f, g, h, 1, 0.0D, 0.0D, 0.0D, 0.0F);
        }
    }

    public static void addEjectItemParticles(ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        for (int i = 0; i < 20; i++) {
            double d = blockPos.getX() + 0.4D + randomSource.nextDouble() * 0.2D;
            double e = blockPos.getY() + 0.4D + randomSource.nextDouble() * 0.2D;
            double f = blockPos.getZ() + 0.4D + randomSource.nextDouble() * 0.2D;
            double g = randomSource.nextGaussian() * 0.02D;
            double h = randomSource.nextGaussian() * 0.02D;
            double j = randomSource.nextGaussian() * 0.02D;
            level.sendParticles(ModParticleTypes.SMALL_END_FIRE.get(), d, e, f, 0, g, h, j * 0.25D, 0.5F);
            level.sendParticles(new MagicAshSmokeParticle.Option(0x3a0637, 0x691575), d, e, f, 0, g, h, j, 0.5F);
        }
    }

    public interface StateAccessor {
        void setState(Level level, VoidSpawnerState trialSpawnerState);

        VoidSpawnerState getState();

        void markUpdated();
    }
}
