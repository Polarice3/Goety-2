package com.Polarice3.Goety.common.blocks.entities.void_vault;

import com.Polarice3.Goety.utils.ModUUIDUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class VoidVaultSharedData {
    static final String SHARED_DATA_KEY = "shared_data";
    public static Codec<VoidVaultSharedData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            VoidVaultConfig.createOptionalCodec("display_item").forGetter(data -> data.displayItem),
                            ModUUIDUtil.CODEC_LINKED_SET.optionalFieldOf("connected_players", Set.of()).forGetter(data -> data.connectedPlayers),
                            Codec.DOUBLE.optionalFieldOf("connected_particles_range", VoidVaultConfig.DEFAULT.deactivationRange()).forGetter(data -> data.connectedParticlesRange)
                    )
                    .apply(instance, VoidVaultSharedData::new)
    );
    private ItemStack displayItem = ItemStack.EMPTY;
    private Set<UUID> connectedPlayers = new ObjectLinkedOpenHashSet<>();
    private double connectedParticlesRange = VoidVaultConfig.DEFAULT.deactivationRange();
    public boolean dirty;

    VoidVaultSharedData(ItemStack displayItem, Set<UUID> connectedPlayers, double connectedParticlesRange) {
        this.displayItem = displayItem;
        this.connectedPlayers.addAll(connectedPlayers);
        this.connectedParticlesRange = connectedParticlesRange;
    }

    public VoidVaultSharedData() {
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public boolean hasDisplayItem() {
        return !this.displayItem.isEmpty();
    }

    public void setDisplayItem(ItemStack stack) {
        if (!ItemStack.isSameItem(this.displayItem, stack)) {
            this.displayItem = stack.copy();
            this.markDirty();
        }
    }

    public boolean hasConnectedPlayers() {
        return !this.connectedPlayers.isEmpty();
    }

    public Set<UUID> getConnectedPlayers() {
        return this.connectedPlayers;
    }

    public double getConnectedParticlesRange() {
        return this.connectedParticlesRange;
    }

    public void updateConnectedPlayers(ServerLevel world, BlockPos pos, VoidVaultServerData serverData, VoidVaultConfig config, double radius) {
        Set<UUID> set = config.playerDetector()
                .detect(world, config.entitySelector(), pos, radius, false)
                .stream()
                .filter(uuid -> !serverData.getRewardedPlayers().contains(uuid))
                .collect(Collectors.toSet());
        if (!this.connectedPlayers.equals(set)) {
            this.connectedPlayers = set;
            this.markDirty();
        }
    }

    private void markDirty() {
        this.dirty = true;
    }

    public void copyFrom(VoidVaultSharedData data) {
        this.displayItem = data.displayItem;
        this.connectedPlayers = data.connectedPlayers;
        this.connectedParticlesRange = data.connectedParticlesRange;
    }
}
