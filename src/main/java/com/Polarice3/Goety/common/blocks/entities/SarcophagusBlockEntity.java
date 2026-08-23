package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.SarcophagusBlock;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SarcophagusBlockEntity extends ModBlockEntity {
    public final AnimationState openingState = new AnimationState();
    public final AnimationState closingState = new AnimationState();
    public final AnimationState closedState = new AnimationState();
    private boolean wasOccupied = false;
    private long closingStartTick = -1;
    private DyeColor color = DyeColor.WHITE;

    public SarcophagusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SARCOPHAGUS.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, SarcophagusBlockEntity blockEntity) {
        boolean occupied = state.getValue(SarcophagusBlock.OCCUPIED);

        if (blockEntity.closingStartTick > 0) {
            --blockEntity.closingStartTick;
        }

        if (occupied != blockEntity.wasOccupied) {
            if (occupied) {
                blockEntity.closingState.start((int) level.getGameTime());
                blockEntity.playSound(ModSounds.STONE_DRAG.get(), level.getRandom().nextFloat() * 0.25F + 0.75F);
                blockEntity.closingStartTick = 20;
                blockEntity.openingState.stop();
                blockEntity.closedState.stop();
            } else {
                blockEntity.closedState.stop();
                blockEntity.openingState.start((int) level.getGameTime());
                blockEntity.playSound(ModSounds.STONE_DRAG.get(), level.getRandom().nextFloat() * 0.25F + 0.75F);
                blockEntity.closingState.stop();
            }
            blockEntity.wasOccupied = occupied;
        }

        if (occupied && blockEntity.closingStartTick <= 0) {
            blockEntity.closingState.stop();
            blockEntity.closedState.start((int) level.getGameTime());
        }
    }

    private void playSound(SoundEvent pSound, float pitch) {
        if (this.level == null) {
            return;
        }
        double d0 = (double)this.worldPosition.getX() + 0.5D;
        double d1 = (double)this.worldPosition.getY() + 0.5D;
        double d2 = (double)this.worldPosition.getZ() + 0.5D;
        this.level.playLocalSound(d0, d1, d2, pSound, SoundSource.BLOCKS, 1.0F, pitch, true);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor p_58730_) {
        this.color = p_58730_;
        this.markUpdated();
    }

    @Override
    public void readNetwork(CompoundTag compoundNBT) {
        if (compoundNBT.contains("CushionColor")) {
            this.color = DyeColor.byName(compoundNBT.getString("CushionColor"), DyeColor.WHITE);
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag pCompound) {
        if (this.color != null) {
            pCompound.putString("CushionColor", this.color.getName());
        }
        return pCompound;
    }

    public static void dropItemStack(Level level, double pX, double pY, double pZ, ItemStack stack) {
        double d0 = EntityType.ITEM.getWidth();
        double d1 = 1.0D - d0;
        double d2 = d0 / 2.0D;

        while(!stack.isEmpty()) {
            ItemEntity itementity = new ItemEntity(level, pX, pY, pZ, stack.split(1));
            itementity.setDeltaMovement(0.0D, level.random.triangle(0.2D, 0.11485000171139836D), 0.0D);
            level.addFreshEntity(itementity);
        }

    }

    @Override
    public AABB getRenderBoundingBox() {
        return BlockEntity.INFINITE_EXTENT_AABB;
    }

}
