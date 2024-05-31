package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.entities.IOwnedBlock;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class OwnedBlockEntity extends BlockEntity implements IOwnedBlock {
    private UUID ownerUUID;
    private int ownerID;

    public OwnedBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.writeNetwork(super.getUpdateTag());
    }

    public void load(CompoundTag nbt) {
        this.readNetwork(nbt);
        super.load(nbt);
    }

    public void saveAdditional(CompoundTag compound) {
        this.writeNetwork(compound);
        super.saveAdditional(compound);
    }

    public void readNetwork(CompoundTag tag) {
        UUID uuid;
        if (tag.hasUUID("Owner")) {
            uuid = tag.getUUID("Owner");
        } else {
            String s = tag.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.level.getServer(), s);
        }
        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            } catch (Throwable ignored) {
            }
        }
        if (tag.contains("OwnerID")){
            this.setOwnerId(tag.getInt("OwnerID"));
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        if (this.getOwnerUUID() != null) {
            tag.putUUID("Owner", this.getOwnerUUID());
        }
        tag.putInt("OwnerID", this.getOwnerId());
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(@Nullable UUID p_184754_1_) {
        this.ownerUUID = p_184754_1_;
    }

    public int getOwnerId() {
        return this.ownerID;
    }

    public void setOwnerId(int p_184754_1_) {
        this.ownerID = p_184754_1_;
    }

    public void setOwner(LivingEntity livingEntity){
        this.setOwnerUUID(livingEntity.getUUID());
        this.setOwnerId(livingEntity.getId());
    }

    @Nullable
    public LivingEntity getTrueOwner() {
        if (this.level != null) {
            if (!this.level.isClientSide) {
                UUID uuid = this.getOwnerUUID();
                return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
            } else {
                return this.level.getEntity(this.getOwnerId()) instanceof LivingEntity living ? living : null;
            }
        } else {
            return null;
        }
    }

    @Nullable
    public Player getPlayer(){
        if (this.level != null) {
            if (!this.level.isClientSide) {
                if (this.getTrueOwner() instanceof Player player) {
                    return player;
                }
            } else {
                if (this.getOwnerUUID() != null) {
                    return this.level.getPlayerByUUID(this.getOwnerUUID());
                }
            }
        }
        return null;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            this.readNetwork(pkt.getTag());
        }
    }

    public boolean screenView(){
        return true;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.load(tag);
        this.readNetwork(tag);
    }
}
