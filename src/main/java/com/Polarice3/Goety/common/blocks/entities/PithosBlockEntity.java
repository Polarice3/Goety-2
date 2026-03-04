package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.CryptChestBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.PithosBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PithosBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level p_155062_, BlockPos p_155063_, BlockState p_155064_) {
            PithosBlockEntity.this.playSound(SoundEvents.GRINDSTONE_USE, 1.0F);
            PithosBlockEntity.this.updateBlockState(p_155064_, true);
        }

        protected void onClose(Level p_155072_, BlockPos p_155073_, BlockState p_155074_) {
            PithosBlockEntity.this.playSound(SoundEvents.GRINDSTONE_USE, 0.25F);
            PithosBlockEntity.this.updateBlockState(p_155074_, false);
        }

        protected void openerCountChanged(Level p_155066_, BlockPos p_155067_, BlockState p_155068_, int p_155069_, int p_155070_) {
        }

        protected boolean isOwnContainer(Player p_155060_) {
            if (p_155060_.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu)p_155060_.containerMenu).getContainer();
                return container == PithosBlockEntity.this;
            } else {
                return false;
            }
        }
    };
    public boolean hasCustomSLName = false;
    public Component skullLordName = Component.empty();

    public PithosBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PITHOS.get(), blockPos, blockState);
    }

    protected void saveAdditional(CompoundTag p_187459_) {
        super.saveAdditional(p_187459_);
        if (!this.trySaveLootTable(p_187459_)) {
            ContainerHelper.saveAllItems(p_187459_, this.items);
        }
        if (this.hasCustomSLName()) {
            p_187459_.putString("SkullLordName", Component.Serializer.toJson(this.skullLordName));
        }
    }

    public void load(CompoundTag p_155055_) {
        super.load(p_155055_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(p_155055_)) {
            ContainerHelper.loadAllItems(p_155055_, this.items);
        }
        if (p_155055_.contains("SkullLordName")) {
            this.skullLordName = Component.Serializer.fromJson(p_155055_.getString("SkullLordName"));
            this.hasCustomSLName = true;
        }
    }

    public int getContainerSize() {
        return 27;
    }

    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    protected void setItems(NonNullList<ItemStack> pItems) {
        this.items = pItems;
    }

    protected Component getDefaultName() {
        return Component.translatable("container.goety.pithos");
    }

    protected AbstractContainerMenu createMenu(int pId, Inventory pPlayer) {
        return ChestMenu.threeRows(pId, pPlayer, this);
    }

    public void startOpen(Player p_58616_) {
        if (this.getLevel() == null) {
            return;
        }
        if (!this.remove && !p_58616_.isSpectator()) {
            this.openersCounter.incrementOpeners(p_58616_, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void stopOpen(Player p_58614_) {
        if (this.getLevel() == null) {
            return;
        }
        if (!this.remove && !p_58614_.isSpectator()) {
            this.openersCounter.decrementOpeners(p_58614_, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void recheckOpen() {
        if (this.getLevel() == null) {
            return;
        }
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    private void updateBlockState(BlockState pState, boolean pOpen) {
        if (this.level == null) {
            return;
        }
        this.level.setBlock(this.getBlockPos(), pState.setValue(PithosBlock.OPEN, pOpen), 3);
    }

    public void lock(){
        if (this.level == null) {
            return;
        }
        this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(PithosBlock.TRIGGERED, Boolean.FALSE), 3);
        this.playSound(SoundEvents.IRON_TRAPDOOR_CLOSE, 1.0F);
    }

    public void unlock(){
        if (this.level == null) {
            return;
        }
        if (this.getBlockState().getValue(PithosBlock.LOCKED)){
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(PithosBlock.LOCKED, Boolean.FALSE), 3);
            this.playSound(SoundEvents.IRON_TRAPDOOR_OPEN, 1.0F);
            int range = 24;
            for (int i = -range; i < range; ++i){
                for (int j = -range; j < range; ++j){
                    for (int k = -range; k < range; ++k){
                        BlockPos blockPos = this.getBlockPos().offset(i, j, k);
                        BlockState blockState = this.level.getBlockState(blockPos);
                        if (blockState.is(ModBlocks.CRYPT_CHEST.get())){
                            if (blockState.hasProperty(CryptChestBlock.LOCKED) && blockState.getValue(CryptChestBlock.LOCKED)) {
                                this.level.setBlock(blockPos, blockState.setValue(CryptChestBlock.LOCKED, false), 3);
                                if (this.level instanceof ServerLevel serverLevel){
                                    for (int i1 = 0; i1 < serverLevel.random.nextInt(10) + 10; ++i1) {
                                        serverLevel.sendParticles(ModParticleTypes.SUMMON.get(), this.getRandomX(1.5D, serverLevel.random), this.getRandomY(serverLevel.random), this.getRandomZ(1.5D, serverLevel.random), 0, 0.0F, 0.0F, 0.0F, 1.0F);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Component getSkullLordName() {
        return this.skullLordName;
    }

    public void setSkullLordName(@Nullable Component skullLordName) {
        if (skullLordName != null) {
            this.skullLordName = skullLordName;
            this.hasCustomSLName = true;
        } else {
            this.skullLordName = Component.empty();
            this.hasCustomSLName = false;
        }
    }

    public boolean hasCustomSLName() {
        return hasCustomSLName;
    }

    public double getRandomX(double p_20209_, RandomSource randomSource) {
        return this.getBlockPos().getX() + ((2.0D * randomSource.nextDouble() - 1.0D) * p_20209_);
    }

    public double getRandomY(RandomSource randomSource) {
        return this.getBlockPos().getY() + (randomSource.nextDouble());
    }

    public double getRandomZ(double p_20263_, RandomSource randomSource) {
        return this.getBlockPos().getZ() + ((2.0D * randomSource.nextDouble() - 1.0D) * p_20263_);
    }

    private void playSound(SoundEvent pSound, float pitch) {
        if (this.level == null) {
            return;
        }
        double d0 = (double)this.worldPosition.getX() + 0.5D;
        double d1 = (double)this.worldPosition.getY() + 0.5D;
        double d2 = (double)this.worldPosition.getZ() + 0.5D;
        this.level.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 10.0F, pitch);
    }
}
