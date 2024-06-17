package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PartLiquidBlockEntity extends SaveBlockEntity{
    public int life = 0;
    public int lifespan = 120;

    public PartLiquidBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.PART_LIQUID.get(), p_155229_, p_155230_);
    }

    public void setStats(BlockState oldBlock, int initLife, int lifespan){
        this.oldBlock = oldBlock;
        this.life = initLife;
        this.lifespan = lifespan;
        this.setChanged();
    }

    public void tick() {
        if (this.level == null){
            return;
        }
        if (!this.level.isClientSide) {
            ++this.life;
            if (this.life % 20 == 0) {
                this.setChanged();
            }
            if (this.life >= this.lifespan) {
                if (this.oldBlock.is(ModBlocks.PART_LIQUID.get()) || this.oldBlock == null){
                    this.level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
                }
                this.level.setBlock(this.getBlockPos(), this.oldBlock, 3);
            }
        }
    }

    @Override
    public void readNetwork(CompoundTag compoundTag) {
        super.readNetwork(compoundTag);
        if (compoundTag.contains("Life")) {
            this.life = compoundTag.getInt("Life");
        }
        if (compoundTag.contains("Lifespan")) {
            this.lifespan = compoundTag.getInt("Lifespan");
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag compoundTag) {
        super.writeNetwork(compoundTag);
        compoundTag.putInt("Life", this.life);
        compoundTag.putInt("Lifespan", this.lifespan);
        return compoundTag;
    }
}
