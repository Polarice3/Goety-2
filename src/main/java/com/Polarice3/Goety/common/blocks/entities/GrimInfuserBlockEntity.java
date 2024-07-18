package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.CursedInfuserBlock;
import com.Polarice3.Goety.common.crafting.CursedInfuserRecipes;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

public class GrimInfuserBlockEntity extends ModBlockEntity implements Clearable, WorldlyContainer {
    private static final int[] SLOTS = new int[]{0};
    private final NonNullList<ItemStack> items = NonNullList.withSize(64, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[64];
    private final int[] cookingTime = new int[64];

    public GrimInfuserBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.GRIM_INFUSER.get(), blockPos, blockState);
    }

    public void tick() {
        if (this.level != null) {
            boolean flag = checkSpawner();
            if (!this.level.isClientSide) {
                if (flag) {
                    this.makeParticles();
                    if (!this.isEmpty()) {
                        this.makeWorkParticles();
                    }
                    this.work();
                } else {
                    for (int i = 0; i < this.items.size(); ++i) {
                        if (this.cookingProgress[i] > 0) {
                            this.cookingProgress[i] = Mth.clamp(this.cookingProgress[i] - 2, 0, this.cookingTime[i]);
                        }
                    }
                }
            }
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(CursedInfuserBlock.LIT, this.checkSpawner()), 3);
        }
    }

    private void work() {
        if (this.level != null) {
            for (int i = 0; i < this.items.size(); ++i) {
                ItemStack itemstack = this.items.get(i);
                if (!itemstack.isEmpty()) {
                    Container iinventory = new SimpleContainer(itemstack);
                    ItemStack itemstack1 = this.level.getRecipeManager()
                            .getRecipeFor(ModRecipeSerializer.CURSED_INFUSER.get(), iinventory, this.level)
                            .map((recipes) -> recipes.assemble(iinventory)).orElse(itemstack);
                    if (itemstack != itemstack1) {
                        this.cookingProgress[i]++;
                    }
                    if (this.cookingProgress[i] % 20 == 0) {
                        this.level.playSound(null, this.getBlockPos(), SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    if (this.cookingProgress[i] >= this.cookingTime[i]) {
                        this.items.set(i, ItemStack.EMPTY);
                        BlockPos blockpos = this.getBlockPos();
                        Containers.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
                        this.level.playSound(null, this.getBlockPos(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        this.markUpdated();
                        this.cookingProgress[i] = 0;
                    }
                }
            }
        }

    }

    public boolean placeItem(ItemStack pStack, int pCookTime) {
        if (this.level != null) {
            for (int i = 0; i < this.items.size(); ++i) {
                ItemStack itemstack = this.items.get(i);
                if (itemstack.isEmpty()) {
                    this.cookingTime[i] = pCookTime;
                    this.cookingProgress[i] = 0;
                    float volume = 0.25F;
                    if (this.isEmpty()) {
                        volume = 1.0F;
                    }
                    this.level.playSound(null, this.getBlockPos(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, volume, 1.0F);
                    this.items.set(i, pStack.split(1));
                    this.markUpdated();
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public ItemStack getItem(int pIndex) {
        return this.items.get(pIndex);
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        return ContainerHelper.removeItem(this.items, pIndex, pCount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        return ContainerHelper.takeItem(this.items, pIndex);
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        Optional<CursedInfuserRecipes> optional = this.getRecipes(pStack);
        optional.ifPresent(furnaceRecipe -> this.placeItem(pStack, furnaceRecipe.getCookingTime()));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    private void makeParticles() {
        BlockPos blockpos = this.getBlockPos();
        ServerLevel serverLevel = (ServerLevel) this.level;

        if (serverLevel != null) {
            long t = serverLevel.getGameTime();
            double d0 = (double)blockpos.getX() + serverLevel.random.nextDouble();
            double d1 = (double)blockpos.getY() + serverLevel.random.nextDouble();
            double d2 = (double)blockpos.getZ() + serverLevel.random.nextDouble();
            if (this.getBlockState().getValue(CursedInfuserBlock.WATERLOGGED)){
                if (t % 20L == 0L) {
                    for (int p = 0; p < 4; ++p) {
                        serverLevel.sendParticles(ParticleTypes.BUBBLE, d0, d1, d2, 1, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, d0, d1, d2, 0, 0.0D, 0.04D, 0.0D, 0.5F);
                    }
                }
            } else {
                if (t % 20L == 0L) {
                    for (int p = 0; p < 4; ++p) {
                        serverLevel.sendParticles(ParticleTypes.FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 0, 0.0D, 5.0E-4D, 0.0D, 0.5F);
                    }
                }
            }
        }
    }

    private void makeWorkParticles() {
        BlockPos blockpos = this.getBlockPos();
        ServerLevel serverLevel = (ServerLevel) this.level;

        if (serverLevel != null) {
            double d0 = (double)blockpos.getX() + serverLevel.random.nextDouble();
            double d1 = (double)blockpos.getY() + serverLevel.random.nextDouble();
            double d2 = (double)blockpos.getZ() + serverLevel.random.nextDouble();
            if (this.getBlockState().getValue(CursedInfuserBlock.WATERLOGGED)){
                for (int p = 0; p < 4; ++p) {
                    serverLevel.sendParticles(ParticleTypes.BUBBLE, d0, d1, d2, 1, 0, 0, 0, 0);
                    serverLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, d0, d1, d2, 0, 0.0D, 0.04D, 0.0D, 0.5F);
                }
            } else {
                for (int p = 0; p < 6; ++p) {
                    serverLevel.sendParticles(ParticleTypes.FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                    serverLevel.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 0, 0.0D, 5.0E-4D, 0.0D, 0.5F);
                }
            }
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public void readNetwork(CompoundTag compoundNBT) {
        this.items.clear();
        ContainerHelper.loadAllItems(compoundNBT, this.items);
        if (compoundNBT.contains("CookingTimes", 11)) {
            int[] aint = compoundNBT.getIntArray("CookingTimes");
            System.arraycopy(aint, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, aint.length));
        }

        if (compoundNBT.contains("CookingTotalTimes", 11)) {
            int[] aint1 = compoundNBT.getIntArray("CookingTotalTimes");
            System.arraycopy(aint1, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, aint1.length));
        }

    }

    public CompoundTag writeNetwork(CompoundTag pCompound) {
        this.saveMetadataAndItems(pCompound);
        pCompound.putIntArray("CookingTimes", this.cookingProgress);
        pCompound.putIntArray("CookingTotalTimes", this.cookingTime);
        return pCompound;
    }

    private CompoundTag saveMetadataAndItems(CompoundTag pCompound) {
        ContainerHelper.saveAllItems(pCompound, this.items, true);
        return pCompound;
    }

    private boolean checkSpawner() {
        return this.level != null && this.level.getBlockState(new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() - 1, this.getBlockPos().getZ())).is(Blocks.SPAWNER);
    }

    public Optional<CursedInfuserRecipes> getRecipes(ItemStack pStack) {
        return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.level.getRecipeManager().getRecipeFor(ModRecipeSerializer.CURSED_INFUSER.get(), new SimpleContainer(pStack), this.level);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        Optional<CursedInfuserRecipes> optional = this.getRecipes(pItemStack);
        if (optional.isEmpty()) return false;
        if (!this.checkSpawner()) return false;
        return this.level != null && !this.level.isClientSide && this.placeItem(pItemStack, optional.get().getCookingTime());
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }
}
