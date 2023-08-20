package com.Polarice3.Goety.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class RedstoneGolemSkullBlockEntity extends OwnedBlockEntity {
    private String customName;

    public RedstoneGolemSkullBlockEntity(BlockPos p_155731_, BlockState p_155732_) {
        super(ModBlockEntities.REDSTONE_GOLEM_SKULL.get(), p_155731_, p_155732_);
    }

    @Override
    public boolean screenView() {
        return false;
    }

    public void setCustomName(String customName){
        this.customName = customName;
    }

    @Nullable
    public String getCustomName(){
        return this.customName;
    }

    public void readNetwork(CompoundTag tag) {
        super.readNetwork(tag);
        if (tag.contains("mod_custom_name")){
            this.setCustomName(tag.getString("mod_custom_name"));
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        CompoundTag compoundTag = super.writeNetwork(tag);
        if (this.getCustomName() != null && !this.getCustomName().isEmpty()){
            compoundTag.putString("mod_custom_name", this.getCustomName());
        }
        return compoundTag;
    }
}
