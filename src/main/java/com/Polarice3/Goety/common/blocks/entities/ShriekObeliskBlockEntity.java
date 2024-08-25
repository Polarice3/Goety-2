package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.ShriekObeliskBlock;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ShriekObeliskBlockEntity extends BlockEntity {
    public int power;
    private CursedCageBlockEntity cursedCageTile;

    public ShriekObeliskBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.SHRIEKING_OBELISK.get(), p_155229_, p_155230_);
    }

    public void tick(){
        if (this.level != null) {
            this.power = updateBase(this.level, this.getBlockPos());
            if (!this.level.isClientSide) {
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(ShriekObeliskBlock.POWERED, this.checkCage()), 3);
            }
        }
    }

    private static int updateBase(Level pLevel, BlockPos blockPos) {
        List<BlockPos> result = new ArrayList<>();

        for(int levels = 1; levels <= 4; levels++) {
            int lowerLevel = levels - 1;
            int newY = blockPos.getY() - levels;
            if (newY < pLevel.getMinBuildHeight()) {
                break;
            }

            for(int newX = blockPos.getX() - levels; newX <= blockPos.getX() + levels; ++newX) {
                for(int newZ = blockPos.getZ() - levels; newZ <= blockPos.getZ() + levels; ++newZ) {
                    BlockPos blockPos1 = new BlockPos(newX, newY, newZ);
                    if (pLevel.getBlockState(blockPos1)
                            .is(ModBlocks.TALL_SKULL_BLOCK.get())
                            || pLevel.getBlockState(blockPos1)
                            .is(ModBlocks.WALL_TALL_SKULL_BLOCK.get())) {
                        result.add(blockPos1);
                    }
                }
            }

            for(int newX = blockPos.getX() - lowerLevel; newX <= blockPos.getX() + lowerLevel; ++newX) {
                for(int newZ = blockPos.getZ() - lowerLevel; newZ <= blockPos.getZ() + lowerLevel; ++newZ) {
                    BlockPos blockPos1 = new BlockPos(newX, newY, newZ);
                    if (pLevel.getBlockState(blockPos1)
                            .is(ModBlocks.TALL_SKULL_BLOCK.get())
                            || pLevel.getBlockState(blockPos1)
                            .is(ModBlocks.WALL_TALL_SKULL_BLOCK.get())) {
                        result.remove(blockPos1);
                    }
                }
            }
        }

        return result.isEmpty() ? 8 : Mth.floor(result.size() * 1.5F) + 8;
    }

    public boolean shriek(ServerLevel serverLevel, @Nullable Player player, int soulEnergy) {
        BlockPos blockpos = this.getBlockPos();
        BlockState blockstate = this.getBlockState();
        int cost = (soulEnergy / MobsConfig.IllagerAssaultSEThreshold.get()) * MainConfig.ShriekObeliskCost.get();
        if (blockstate.getValue(ShriekObeliskBlock.POWERED)) {
            if (this.cursedCageTile != null) {
                if (this.cursedCageTile.getSouls() >= cost) {
                    if (!blockstate.getValue(ShriekObeliskBlock.SHRIEKING)) {
                        this.cursedCageTile.decreaseSouls(cost);
                        serverLevel.setBlock(blockpos, blockstate.setValue(ShriekObeliskBlock.SHRIEKING, Boolean.TRUE), 2);
                        serverLevel.scheduleTick(blockpos, blockstate.getBlock(), 90);
                        for (int j = 0; j < 10; ++j) {
                            serverLevel.sendParticles(new ShriekParticleOption(j * 5), (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 1.0D, (double) blockpos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.5F);
                        }
                        ModNetwork.sendToALL(new SPlayWorldSoundPacket(blockpos.above(), SoundEvents.SCULK_SHRIEKER_SHRIEK, 2.0F, 0.25F));
                        serverLevel.gameEvent(GameEvent.SHRIEK, blockpos, GameEvent.Context.of(player));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getPower(){
        return this.power;
    }

    private boolean checkCage() {
        if (this.level != null) {
            BlockPos pos = this.getBlockPos().below();
            BlockState blockState = this.level.getBlockState(pos);
            if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())) {
                BlockEntity tileentity = this.level.getBlockEntity(pos);
                if (tileentity instanceof CursedCageBlockEntity) {
                    this.cursedCageTile = (CursedCageBlockEntity) tileentity;
                    return !cursedCageTile.getItem().isEmpty();
                }
            }
        }
        return false;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
