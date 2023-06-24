package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.blocks.DarkAltarBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SoulMenderBlockEntity extends ModBlockEntity implements Clearable, WorldlyContainer {
    private static final int[] SLOTS = new int[]{0};
    private ItemStack itemStack = ItemStack.EMPTY;
    private CursedCageBlockEntity cursedCageTile;

    public SoulMenderBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.SOUL_MENDER.get(), p_155229_, p_155230_);
    }

    public void tick() {
        boolean flag = this.checkCage();
        assert this.level != null;
        if (!this.level.isClientSide) {
            if (flag) {
                if (!this.itemStack.isEmpty()) {
                    int i = 1;
                    if (!this.itemStack.getAllEnchantments().isEmpty()) {
                        i += this.itemStack.getAllEnchantments().size();
                    }
                    if (this.cursedCageTile.getSouls() > (MainConfig.SoulMenderCost.get() * i)) {
                        this.makeWorkParticles();
                    }
                }
                this.work();
            }
        }
        this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(DarkAltarBlock.LIT, flag), 3);
    }

    private void work() {
        if (this.level != null) {
            if (!this.itemStack.isEmpty()) {
                int i = 1;
                if (!this.itemStack.getAllEnchantments().isEmpty()) {
                    i += this.itemStack.getAllEnchantments().size();
                }
                if (this.cursedCageTile.getSouls() > (MainConfig.SoulMenderCost.get() * i)) {
                    if (this.itemStack.isDamaged()) {
                        if (this.level.getGameTime() % MathHelper.secondsToTicks(MainConfig.SoulMenderSeconds.get().floatValue()) == 0) {
                            this.itemStack.setDamageValue(this.itemStack.getDamageValue() - 1);
                            this.cursedCageTile.decreaseSouls(MainConfig.SoulMenderCost.get() * i);
                        }
                        if (this.level.random.nextInt(24) == 0) {
                            this.level.playSound(null, this.getBlockPos(), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + this.level.random.nextFloat(), this.level.random.nextFloat() * 0.7F + 0.3F);
                        }
                    } else {
                        BlockPos blockpos = this.getBlockPos();
                        Containers.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), this.itemStack);
                        this.itemStack.shrink(1);
                        this.finishParticles();
                        this.markUpdated();
                    }
                }
            }
        }
    }

    public boolean isEmpty() {
        return this.itemStack.isEmpty();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public ItemStack getItem(int pIndex) {
        return this.itemStack;
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        if (pStack.isDamaged() && pStack.isRepairable()){
            this.placeItem(pStack);
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    private void finishParticles() {
        BlockPos blockpos = this.getBlockPos();

        if (this.level != null) {
            if (!this.level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) this.level;
                serverWorld.sendParticles(ParticleTypes.LARGE_SMOKE, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D, 1, 0, 0, 0, 0);
                for (int p = 0; p < 6; ++p) {
                    double d0 = (double) blockpos.getX() + serverWorld.random.nextDouble();
                    double d1 = (double) blockpos.getY() + serverWorld.random.nextDouble();
                    double d2 = (double) blockpos.getZ() + serverWorld.random.nextDouble();
                    serverWorld.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                    serverWorld.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 1, 0, 0, 0, 0);
                }
            }
        }
    }

    private void makeWorkParticles() {
        BlockPos blockpos = this.getBlockPos();
        ServerLevel serverLevel = (ServerLevel) this.level;

        if (serverLevel != null) {
            long t = serverLevel.getGameTime();
            if (t % 20 == 0) {
                for (int p = 0; p < 6; ++p) {
                    double d0 = (double)blockpos.getX() + serverLevel.random.nextDouble();
                    double d1 = (double)blockpos.getY() + serverLevel.random.nextDouble();
                    double d2 = (double)blockpos.getZ() + serverLevel.random.nextDouble();
                    serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                }
            }
        }
    }

    public boolean placeItem(ItemStack pStack) {
        if (this.itemStack.isEmpty()) {
            this.itemStack = pStack.split(1);
            assert this.level != null;
            this.level.playSound(null, this.getBlockPos(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0F, 0.5F);
            this.markUpdated();
            return true;
        }

        return false;
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

    public void readNetwork(CompoundTag compoundNBT) {
        this.itemStack = ItemStack.of(compoundNBT.getCompound("Item"));
    }

    public CompoundTag writeNetwork(CompoundTag pCompound) {
        this.saveMetadataAndItems(pCompound);
        return pCompound;
    }

    private CompoundTag saveMetadataAndItems(CompoundTag pCompound) {
        pCompound.put("Item", this.itemStack.save(new CompoundTag()));
        return pCompound;
    }

    @Override
    public void clearContent() {
        this.itemStack.shrink(1);
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        if (!pItemStack.isDamaged() && !pItemStack.isRepairable()) return false;
        if (this.cursedCageTile == null) return false;
        assert this.level != null;
        return !this.level.isClientSide && this.placeItem(pItemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }
}
