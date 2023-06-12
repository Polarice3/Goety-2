package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.NecroBrazierBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SoulCandlestickBlockEntity extends BlockEntity{
    private CursedCageBlockEntity cursedCageTile;

    public SoulCandlestickBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.SOUL_CANDLESTICK.get(), p_155229_, p_155230_);
    }

    public void tick(){
        if (this.level != null){
            boolean flag = this.checkCage() && this.cursedCageTile.getSouls() > 0;
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(NecroBrazierBlock.LIT, flag), 3);
        }
    }

    public void drainSouls(int amount, BlockPos blockPos){
        if (this.level != null){
            if (this.checkCage()) {
                if (this.cursedCageTile.getSouls() > amount) {
                    this.cursedCageTile.decreaseSouls(amount);
                    double d0 = 0.1D * (blockPos.getX() - this.getBlockPos().getX());
                    double d1 = 0.1D * (blockPos.getY() - this.getBlockPos().getY());
                    double d2 = 0.1D * (blockPos.getZ() - this.getBlockPos().getZ());
                    if (this.level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ModParticleTypes.SOUL_EXPLODE_BITS.get(), this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.75D, this.getBlockPos().getZ() + 0.5D, 0, d0, d1, d2, 0.5D);
                    }
                }
            }
        }
    }

    public int getSouls(){
        if (this.level != null){
            if (this.checkCage()){
                return this.cursedCageTile.getSouls();
            }
        }
        return 0;
    }

    private boolean checkCage() {
        assert this.level != null;
        BlockPos pos = new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() - 1, this.getBlockPos().getZ());
        BlockState blockState = this.level.getBlockState(pos);
        if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())){
            BlockEntity tileentity = this.level.getBlockEntity(pos);
            if (tileentity instanceof CursedCageBlockEntity){
                this.cursedCageTile = (CursedCageBlockEntity) tileentity;
                return !cursedCageTile.getItem().isEmpty();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
