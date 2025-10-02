package com.Polarice3.Goety.common.blocks.entities.void_vault;

import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModUUIDUtil;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class VoidVaultServerData {
    static final String SERVER_DATA_KEY = "server_data";
    public static Codec<VoidVaultServerData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ModUUIDUtil.CODEC_LINKED_SET.optionalFieldOf("rewarded_players", Set.of()).forGetter(data -> data.rewardedPlayers),
                            Codec.LONG.optionalFieldOf("state_updating_resumes_at", 0L).forGetter(data -> data.stateUpdatingResumesAt),
                            ItemStack.CODEC.listOf().optionalFieldOf("items_to_eject", List.of()).forGetter(data -> data.itemsToEject),
                            Codec.INT.optionalFieldOf("total_ejections_needed", 0).forGetter(data -> data.totalEjectionsNeeded)
                    )
                    .apply(instance, VoidVaultServerData::new)
    );
    private static final int MAX_STORED_REWARDED_PLAYERS = 128;
    private final Set<UUID> rewardedPlayers = new ObjectLinkedOpenHashSet<>();
    private long stateUpdatingResumesAt;
    private final List<ItemStack> itemsToEject = new ObjectArrayList<>();
    private long lastFailedUnlockTime;
    private int totalEjectionsNeeded;
    public boolean dirty;

    public VoidVaultServerData(Set<UUID> rewardedPlayers, long stateUpdatingResumesAt, List<ItemStack> itemsToEject, int totalEjectionsNeeded) {
        this.rewardedPlayers.addAll(rewardedPlayers);
        this.stateUpdatingResumesAt = stateUpdatingResumesAt;
        this.itemsToEject.addAll(itemsToEject);
        this.totalEjectionsNeeded = totalEjectionsNeeded;
    }

    public VoidVaultServerData() {
    }

    public void setLastFailedUnlockTime(long lastFailedUnlockTime) {
        this.lastFailedUnlockTime = lastFailedUnlockTime;
    }

    public long getLastFailedUnlockTime() {
        return this.lastFailedUnlockTime;
    }

    public Set<UUID> getRewardedPlayers() {
        return this.rewardedPlayers;
    }

    public boolean hasRewardedPlayer(Player player) {
        return this.rewardedPlayers.contains(player.getUUID());
    }

    @VisibleForTesting
    public void markPlayerAsRewarded(Player player) {
        this.rewardedPlayers.add(player.getUUID());
        if (this.rewardedPlayers.size() > 128) {
            Iterator<UUID> iterator = this.rewardedPlayers.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }

        this.markDirty();
    }

    public long getStateUpdatingResumeTime() {
        return this.stateUpdatingResumesAt;
    }

    public void setStateUpdatingResumeTime(long stateUpdatingResumesAt) {
        this.stateUpdatingResumesAt = stateUpdatingResumesAt;
        this.markDirty();
    }

    public List<ItemStack> getItemsToEject() {
        return this.itemsToEject;
    }

    public void finishEjecting() {
        this.totalEjectionsNeeded = 0;
        this.markDirty();
    }

    public void setItemsToEject(List<ItemStack> itemsToEject) {
        this.itemsToEject.clear();
        this.itemsToEject.addAll(itemsToEject);
        this.totalEjectionsNeeded = this.itemsToEject.size();
        this.markDirty();
    }

    public ItemStack getItemToDisplay() {
        return this.itemsToEject.isEmpty()
                ? ItemStack.EMPTY
                : Objects.requireNonNullElse(this.itemsToEject.get(this.itemsToEject.size() - 1), ItemStack.EMPTY);
    }

    public ItemStack getItemToEject() {
        if (this.itemsToEject.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.markDirty();
            return Objects.requireNonNullElse(this.itemsToEject.remove(this.itemsToEject.size() - 1), ItemStack.EMPTY);
        }
    }

    public void copyFrom(VoidVaultServerData data) {
        this.stateUpdatingResumesAt = data.getStateUpdatingResumeTime();
        this.itemsToEject.clear();
        this.itemsToEject.addAll(data.itemsToEject);
        this.rewardedPlayers.clear();
        this.rewardedPlayers.addAll(data.rewardedPlayers);
    }

    private void markDirty() {
        this.dirty = true;
    }

    public float getEjectSoundPitchModifier() {
        return this.totalEjectionsNeeded == 1 ? 1.0F : 1.0F - MathHelper.getLerpProgress((float)this.getItemsToEject().size(), 1.0F, (float)this.totalEjectionsNeeded);
    }
}
