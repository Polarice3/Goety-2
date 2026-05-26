package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.entities.IChunkLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ChunkLoadBlockEntity extends BlockEntity implements IChunkLoader {
    public long ticketTime = 0;
    public boolean saveDataCheck = true;

    public ChunkLoadBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public long getTicketTime() {
        return this.ticketTime;
    }

    @Override
    public void setTicketTime(long ticketTime) {
        this.ticketTime = ticketTime;
    }

    public boolean saveDataCheck() {
        return this.saveDataCheck;
    }

    @Override
    public void saveDataChecked() {
        this.saveDataCheck = false;
    }
}
