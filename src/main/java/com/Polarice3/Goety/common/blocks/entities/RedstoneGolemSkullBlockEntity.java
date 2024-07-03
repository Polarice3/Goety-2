package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class RedstoneGolemSkullBlockEntity extends SkullBlockEntity {
    private UUID ownerUUID;
    private String customName;

    public RedstoneGolemSkullBlockEntity(BlockPos p_155731_, BlockState p_155732_) {
        super(p_155731_, p_155732_);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.REDSTONE_GOLEM_SKULL.get();
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

    @Nullable
    public ResourceLocation getNoteBlockSound() {
        return ModSounds.REDSTONE_GOLEM_AMBIENT.get().getLocation();
    }

    public void setCustomName(String customName){
        this.customName = customName;
    }

    @Nullable
    public String getCustomName(){
        return this.customName;
    }

    public void readNetwork(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.setOwnerId(tag.getUUID("Owner"));
        }
        if (tag.contains("mod_custom_name")){
            this.setCustomName(tag.getString("mod_custom_name"));
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        if (this.getOwnerId() != null) {
            tag.putUUID("Owner", this.getOwnerId());
        }
        if (this.getCustomName() != null && !this.getCustomName().isEmpty()){
            tag.putString("mod_custom_name", this.getCustomName());
        }
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nullable
    public UUID getOwnerId() {
        return this.ownerUUID;
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.ownerUUID = p_184754_1_;
    }

    public void setOwner(LivingEntity livingEntity){
        this.setOwnerId(livingEntity.getUUID());
    }

    public LivingEntity getTrueOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    public Player getPlayer(){
        return (Player) this.getTrueOwner();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            this.readNetwork(pkt.getTag());
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.load(tag);
        this.readNetwork(tag);
    }
}
