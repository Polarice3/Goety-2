package com.Polarice3.Goety.common.blocks.entities.void_vault;

import com.Polarice3.Goety.common.blocks.entities.VoidVaultBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.void_spawner.VoidSpawner;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public enum VoidVaultState implements StringRepresentable {
    INACTIVE("inactive", VoidVaultState.Light.HALF_LIT) {
        @Override
        protected void onChangedTo(ServerLevel world, BlockPos pos, VoidVaultConfig config, VoidVaultSharedData sharedData) {
            sharedData.setDisplayItem(ItemStack.EMPTY);
            VoidVaultBlockEntity.Client.spawnDeactivateParticles(world, pos);
            world.playSound(null, pos, ModSounds.VOID_VAULT_DEACTIVATE.get(), SoundSource.BLOCKS, 1.0F, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 1.0F);
        }
    },
    ACTIVE("active", VoidVaultState.Light.LIT) {
        @Override
        protected void onChangedTo(ServerLevel world, BlockPos pos, VoidVaultConfig config, VoidVaultSharedData sharedData) {
            if (!sharedData.hasDisplayItem()) {
                VoidVaultBlockEntity.Server.updateDisplayItem(world, this, config, sharedData, pos);
            }

            if (world.getBlockEntity(pos) instanceof VoidVaultBlockEntity vaultBlockEntity) {
                VoidVaultBlockEntity.Client.spawnActivateParticles(
                        world,
                        vaultBlockEntity.getBlockPos(),
                        vaultBlockEntity.getBlockState(),
                        vaultBlockEntity.getSharedData()
                );
                world.playSound(null, pos, ModSounds.VOID_VAULT_ACTIVATE.get(), SoundSource.BLOCKS, 1.0F, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 1.0F);
            }

        }
    },
    UNLOCKING("unlocking", VoidVaultState.Light.LIT) {
        @Override
        protected void onChangedTo(ServerLevel world, BlockPos pos, VoidVaultConfig config, VoidVaultSharedData sharedData) {
            world.playSound(null, pos, ModSounds.VOID_VAULT_INSERT.get(), SoundSource.BLOCKS);
        }
    },
    EJECTING("ejecting", VoidVaultState.Light.LIT) {
        @Override
        protected void onChangedTo(ServerLevel world, BlockPos pos, VoidVaultConfig config, VoidVaultSharedData sharedData) {
            world.playSound(null, pos, ModSounds.VOID_VAULT_OPEN_SHUTTER.get(), SoundSource.BLOCKS);
        }

        @Override
        protected void onChangedFrom(ServerLevel world, BlockPos pos, VoidVaultConfig config, VoidVaultSharedData sharedData) {
            world.playSound(null, pos, ModSounds.VOID_VAULT_CLOSE_SHUTTER.get(), SoundSource.BLOCKS);
        }
    };
    private final String id;
    private final VoidVaultState.Light light;

    VoidVaultState(String id, VoidVaultState.Light light) {
        this.id = id;
        this.light = light;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    public int getLuminance() {
        return this.light.luminance;
    }

    public VoidVaultState update(ServerLevel world, BlockPos pos, VoidVaultConfig config, VoidVaultServerData serverData, VoidVaultSharedData sharedData) {
        return switch (this) {
            case INACTIVE -> updateActiveState(world, pos, config, serverData, sharedData, config.activationRange());
            case ACTIVE -> updateActiveState(world, pos, config, serverData, sharedData, config.deactivationRange());
            case UNLOCKING -> {
                serverData.setStateUpdatingResumeTime(world.getGameTime() + 20L);
                yield EJECTING;
            }
            case EJECTING -> {
                if (serverData.getItemsToEject().isEmpty()) {
                    serverData.finishEjecting();
                    yield updateActiveState(world, pos, config, serverData, sharedData, config.deactivationRange());
                } else {
                    float f = serverData.getEjectSoundPitchModifier();
                    this.ejectItem(world, pos, serverData.getItemToEject(), f);
                    sharedData.setDisplayItem(serverData.getItemToDisplay());
                    boolean bl = serverData.getItemsToEject().isEmpty();
                    int i = bl ? 20 : 20;
                    serverData.setStateUpdatingResumeTime(world.getGameTime() + i);
                    yield EJECTING;
                }
            }
        };
    }

    private static VoidVaultState updateActiveState(ServerLevel world, BlockPos pos, VoidVaultConfig config, VoidVaultServerData serverData, VoidVaultSharedData sharedData, double radius) {
        sharedData.updateConnectedPlayers(world, pos, serverData, config, radius);
        serverData.setStateUpdatingResumeTime(world.getGameTime() + 20L);
        return sharedData.hasConnectedPlayers() ? ACTIVE : INACTIVE;
    }

    public void onStateChange(ServerLevel world, BlockPos pos, VoidVaultState newState, VoidVaultConfig config, VoidVaultSharedData sharedData) {
        this.onChangedFrom(world, pos, config, sharedData);
        newState.onChangedTo(world, pos, config, sharedData);
    }

    protected void onChangedTo(ServerLevel world, BlockPos pos, VoidVaultConfig config, VoidVaultSharedData sharedData) {
    }

    protected void onChangedFrom(ServerLevel world, BlockPos pos, VoidVaultConfig config, VoidVaultSharedData sharedData) {
    }

    private void ejectItem(ServerLevel world, BlockPos pos, ItemStack stack, float pitchModifier) {
        DefaultDispenseItemBehavior.spawnItem(world, stack, 2, Direction.UP, Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2));
        VoidSpawner.addEjectItemParticles(world, pos, world.getRandom());
        world.playSound(null, pos, ModSounds.VOID_VAULT_EJECT_ITEM.get(), SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * pitchModifier);
    }

    static enum Light {
        HALF_LIT(6),
        LIT(12);

        final int luminance;

        private Light(int luminance) {
            this.luminance = luminance;
        }
    }
}
