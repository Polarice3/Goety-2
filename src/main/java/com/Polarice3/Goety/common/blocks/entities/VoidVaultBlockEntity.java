package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.MagicAshSmokeParticle;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.VoidVaultBlock;
import com.Polarice3.Goety.common.blocks.entities.void_vault.*;
import com.Polarice3.Goety.common.blocks.properties.ModStateProperties;
import com.Polarice3.Goety.init.ModSounds;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VoidVaultBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    public final VoidVaultServerData serverData = new VoidVaultServerData();
    public final VoidVaultSharedData sharedData = new VoidVaultSharedData();
    public final VoidVaultClientData clientData = new VoidVaultClientData();
    private VoidVaultConfig config = VoidVaultConfig.DEFAULT;

    public VoidVaultBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.VOID_VAULT.get(), p_155229_, p_155230_);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.put("config", encodeValue(VoidVaultConfig.CODEC, this.config));
        compoundTag.put("shared_data", encodeValue(VoidVaultSharedData.CODEC, this.sharedData));
        compoundTag.put("server_data", encodeValue(VoidVaultServerData.CODEC, this.serverData));
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("server_data")) {
            VoidVaultServerData.CODEC.parse(NbtOps.INSTANCE, compoundTag.get("server_data")).resultOrPartial(LOGGER::error).ifPresent(this.serverData::copyFrom);
        }

        if (compoundTag.contains("config")) {
            VoidVaultConfig.CODEC.parse(NbtOps.INSTANCE, compoundTag.get("config")).resultOrPartial(LOGGER::error).ifPresent(config -> this.config = config);
        }

        if (compoundTag.contains("shared_data")) {
            VoidVaultSharedData.CODEC.parse(NbtOps.INSTANCE, compoundTag.get("shared_data")).resultOrPartial(LOGGER::error).ifPresent(this.sharedData::copyFrom);
        }
    }

    private static <T> Tag encodeValue(Codec<T> codec, T value) {
        return Util.getOrThrow(codec.encodeStart(NbtOps.INSTANCE, value), IllegalStateException::new);
    }

    @Nullable
    public VoidVaultServerData getServerData() {
        return this.level != null && !this.level.isClientSide ? this.serverData : null;
    }

    public VoidVaultSharedData getSharedData() {
        return this.sharedData;
    }

    public VoidVaultClientData getClientData() {
        return this.clientData;
    }

    public VoidVaultConfig getConfig() {
        return this.config;
    }

    @VisibleForTesting
    public void setConfig(VoidVaultConfig config) {
        this.config = config;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return Util.make(new CompoundTag(), nbt -> nbt.put("shared_data", encodeValue(VoidVaultSharedData.CODEC, this.sharedData)));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static final class Client {

        public static void tick(Level world, BlockPos pos, BlockState state, VoidVaultClientData clientData, VoidVaultSharedData sharedData) {
            clientData.rotateDisplay();
            if (world.getGameTime() % 20L == 0L) {
                spawnConnectedParticles(world, pos, state, sharedData);
            }

            spawnAmbientParticles(world, pos, sharedData);
            playAmbientSound(world, pos, sharedData);
        }

        public static void spawnActivateParticles(Level world, BlockPos pos, BlockState state, VoidVaultSharedData sharedData) {
            spawnConnectedParticles(world, pos, state, sharedData);
            RandomSource random = world.random;

            for (int i = 0; i < 20; i++) {
                Vec3 vec3d = getRegularParticlesPos(pos, random);
                if (world instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new MagicAshSmokeParticle.Option(0x3a0637, 0x691575), vec3d.x(), vec3d.y(), vec3d.z(), 1, 0.0, 0.0, 0.0, 0.0F);
                    serverLevel.sendParticles(ModParticleTypes.SMALL_END_FIRE.get(), vec3d.x(), vec3d.y(), vec3d.z(), 1, 0.0, 0.0, 0.0, 0.0F);
                } else {
                    world.addParticle(new MagicAshSmokeParticle.Option(0x3a0637, 0x691575), vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
                    world.addParticle(ModParticleTypes.SMALL_END_FIRE.get(), vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
                }
            }
        }

        public static void spawnDeactivateParticles(Level world, BlockPos pos) {
            RandomSource random = world.random;
            for (int i = 0; i < 20; i++) {
                Vec3 vec3d = getDeactivateParticlesPos(pos, random);
                Vec3 vec3d2 = new Vec3(random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02);
                if (world instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticleTypes.SMALL_END_FIRE.get(), vec3d.x(), vec3d.y(), vec3d.z(), 0, vec3d2.x(), vec3d2.y(), vec3d2.z(), 0.5F);
                } else {
                    world.addParticle(ModParticleTypes.SMALL_END_FIRE.get(), vec3d.x(), vec3d.y(), vec3d.z(), vec3d2.x(), vec3d2.y(), vec3d2.z());
                }
            }
        }

        private static void spawnAmbientParticles(Level world, BlockPos pos, VoidVaultSharedData sharedData) {
            RandomSource random = world.getRandom();
            if (random.nextFloat() <= 0.5F) {
                Vec3 vec3d = getRegularParticlesPos(pos, random);
                if (world instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new MagicAshSmokeParticle.Option(0x3a0637, 0x691575), vec3d.x(), vec3d.y(), vec3d.z(), 1, 0.0, 0.0, 0.0, 0.0F);
                    if (hasDisplayItem(sharedData)) {
                        serverLevel.sendParticles(ModParticleTypes.SMALL_END_FIRE.get(), vec3d.x(), vec3d.y(), vec3d.z(), 1, 0.0, 0.0, 0.0, 0.0F);
                    }
                } else {
                    world.addParticle(new MagicAshSmokeParticle.Option(0x3a0637, 0x691575), vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
                    if (hasDisplayItem(sharedData)) {
                        world.addParticle(ModParticleTypes.SMALL_END_FIRE.get(), vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
                    }
                }
            }
        }

        private static void spawnConnectedParticlesFor(Level world, Vec3 pos, Player player) {
            RandomSource random = world.random;
            Vec3 vec3d = pos.vectorTo(player.position().add(0.0, player.getBbHeight() / 2.0F, 0.0));
            int i = Mth.nextInt(random, 2, 5);

            for (int j = 0; j < i; j++) {
                Vec3 vec3d2 = vec3d.offsetRandom(random, 1.0F);
                world.addParticle(ModParticleTypes.VOID_VAULT_CONNECT.get(), pos.x(), pos.y(), pos.z(), vec3d2.x(), vec3d2.y(), vec3d2.z());
            }
        }

        private static void spawnConnectedParticles(Level world, BlockPos pos, BlockState state, VoidVaultSharedData sharedData) {
            Set<UUID> set = sharedData.getConnectedPlayers();
            if (!set.isEmpty()) {
                Vec3 vec3d = getConnectedParticlesOrigin(pos, state.getValue(VoidVaultBlock.FACING));

                for (UUID uUID : set) {
                    Player playerEntity = world.getPlayerByUUID(uUID);
                    if (playerEntity != null && isPlayerWithinConnectedParticlesRange(pos, sharedData, playerEntity)) {
                        spawnConnectedParticlesFor(world, vec3d, playerEntity);
                    }
                }
            }
        }

        private static boolean isPlayerWithinConnectedParticlesRange(BlockPos pos, VoidVaultSharedData sharedData, Player player) {
            return player.blockPosition().distSqr(pos) <= Mth.square(sharedData.getConnectedParticlesRange());
        }

        private static void playAmbientSound(Level world, BlockPos pos, VoidVaultSharedData sharedData) {
            if (hasDisplayItem(sharedData)) {
                RandomSource random = world.getRandom();
                if (random.nextFloat() <= 0.02F) {
                    world.playLocalSound(pos, ModSounds.VOID_VAULT_AMBIENT.get(), SoundSource.BLOCKS, (random.nextFloat() * 0.25F) + 0.75F, random.nextFloat() + 0.5F, false);
                }
            }
        }

        public static boolean hasDisplayItem(VoidVaultSharedData sharedData) {
            return sharedData.hasDisplayItem();
        }

        private static Vec3 getDeactivateParticlesPos(BlockPos pos, RandomSource random) {
            return Vec3.atLowerCornerOf(pos).add(Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6));
        }

        private static Vec3 getRegularParticlesPos(BlockPos pos, RandomSource random) {
            return Vec3.atLowerCornerOf(pos).add(Mth.nextDouble(random, 0.1, 0.9), Mth.nextDouble(random, 0.25, 0.75), Mth.nextDouble(random, 0.1, 0.9));
        }

        private static Vec3 getConnectedParticlesOrigin(BlockPos pos, Direction direction) {
            return Vec3.atBottomCenterOf(pos).add(direction.getStepX() * 0.5, 1.75, direction.getStepZ() * 0.5);
        }
    }

    public static final class Server {

        public static void tick(ServerLevel world, BlockPos pos, BlockState state, VoidVaultConfig config, VoidVaultServerData serverData, VoidVaultSharedData sharedData) {
            VoidVaultState vaultState = state.getValue(VoidVaultBlock.STATE);
            if (shouldUpdateDisplayItem(world.getGameTime(), vaultState)) {
                updateDisplayItem(world, vaultState, config, sharedData, pos);
            }

            BlockState blockState = state;
            if (world.getGameTime() >= serverData.getStateUpdatingResumeTime()) {
                blockState = state.setValue(VoidVaultBlock.STATE, vaultState.update(world, pos, config, serverData, sharedData));
                if (!state.equals(blockState)) {
                    changeVaultState(world, pos, state, blockState, config, sharedData);
                }
            }

            if (serverData.dirty || sharedData.dirty) {
                VoidVaultBlockEntity.setChanged(world, pos, state);
                if (sharedData.dirty) {
                    world.sendBlockUpdated(pos, state, blockState, 2);
                }

                serverData.dirty = false;
                sharedData.dirty = false;
            }
        }

        public static void tryUnlock(
                ServerLevel world,
                BlockPos pos,
                BlockState state,
                VoidVaultConfig config,
                VoidVaultServerData serverData,
                VoidVaultSharedData sharedData,
                Player player,
                ItemStack stack
        ) {
            VoidVaultState vaultState = state.getValue(VoidVaultBlock.STATE);
            if (canBeUnlocked(config, vaultState)) {
                if (!isValidKey(config, stack)) {
                    playFailedUnlockSound(world, serverData, pos, ModSounds.VOID_VAULT_INSERT_FAIL.get());
                } else if (serverData.hasRewardedPlayer(player)) {
                    playFailedUnlockSound(world, serverData, pos, ModSounds.VOID_VAULT_REJECT.get());
                } else {
                    List<ItemStack> list = generateLoot(world, config, pos, player);
                    if (!list.isEmpty()) {
                        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                        if (!player.isCreative()) {
                            stack.shrink(config.keyItem().getCount());
                        }

                        unlock(world, state, pos, config, serverData, sharedData, list);
                        serverData.markPlayerAsRewarded(player);
                        sharedData.updateConnectedPlayers(world, pos, serverData, config, config.deactivationRange());
                    }
                }
            }
        }

        static void changeVaultState(ServerLevel world, BlockPos pos, BlockState oldState, BlockState newState, VoidVaultConfig config, VoidVaultSharedData sharedData) {
            VoidVaultState vaultState = oldState.getValue(VoidVaultBlock.STATE);
            VoidVaultState vaultState2 = newState.getValue(VoidVaultBlock.STATE);
            world.setBlock(pos, newState, Block.UPDATE_ALL);
            vaultState.onStateChange(world, pos, vaultState2, config, sharedData);
        }

        public static void updateDisplayItem(ServerLevel world, VoidVaultState state, VoidVaultConfig config, VoidVaultSharedData sharedData, BlockPos pos) {
            if (!canBeUnlocked(config, state)) {
                sharedData.setDisplayItem(ItemStack.EMPTY);
            } else {
                ItemStack itemStack = generateDisplayItem(world, pos, config.overrideLootTableToDisplay().orElse(config.lootTable()));
                sharedData.setDisplayItem(itemStack);
            }
        }

        private static ItemStack generateDisplayItem(ServerLevel world, BlockPos pos, ResourceLocation lootTable) {
            LootTable lootTable2 = world.getServer().getLootData().getLootTable(lootTable);
            LootParams.Builder lootContextParameterSet = new LootParams.Builder(world).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos));
            LootParams lootParams = lootContextParameterSet.create(LootContextParamSets.CHEST);
            List<ItemStack> list = lootTable2.getRandomItems(lootParams);
            return list.isEmpty() ? ItemStack.EMPTY : Util.getRandom(list, world.getRandom());
        }

        private static void unlock(ServerLevel world, BlockState state, BlockPos pos, VoidVaultConfig config, VoidVaultServerData serverData, VoidVaultSharedData sharedData, List<ItemStack> itemsToEject) {
            serverData.setItemsToEject(itemsToEject);
            sharedData.setDisplayItem(serverData.getItemToDisplay());
            serverData.setStateUpdatingResumeTime(world.getGameTime() + 14L);
            changeVaultState(world, pos, state, state.setValue(ModStateProperties.VOID_VAULT_STATE, VoidVaultState.UNLOCKING), config, sharedData);
        }

        private static List<ItemStack> generateLoot(ServerLevel world, VoidVaultConfig config, BlockPos pos, Player player) {
            LootTable lootTable = world.getServer().getLootData().getLootTable(config.lootTable());
            LootParams.Builder lootContextParameterSet = new LootParams.Builder(world)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withLuck(player.getLuck())
                    .withParameter(LootContextParams.THIS_ENTITY, player);
            LootParams lootParams = lootContextParameterSet.create(LootContextParamSets.CHEST);
            return lootTable.getRandomItems(lootParams);
        }

        private static boolean canBeUnlocked(VoidVaultConfig config, VoidVaultState state) {
            return config.lootTable() != LootTable.EMPTY.getLootTableId() && !config.keyItem().isEmpty() && state != VoidVaultState.INACTIVE;
        }

        private static boolean isValidKey(VoidVaultConfig config, ItemStack stack) {
            return ItemStack.isSameItemSameTags(stack, config.keyItem()) && stack.getCount() >= config.keyItem().getCount();
        }

        private static boolean shouldUpdateDisplayItem(long time, VoidVaultState state) {
            return time % 20L == 0L && state == VoidVaultState.ACTIVE;
        }

        private static void playFailedUnlockSound(ServerLevel world, VoidVaultServerData serverData, BlockPos pos, SoundEvent soundEvent) {
            if (world.getGameTime() >= serverData.getLastFailedUnlockTime() + 15L) {
                world.playSound(null, pos, soundEvent, SoundSource.BLOCKS);
                serverData.setLastFailedUnlockTime(world.getGameTime());
            }
        }
    }
}
