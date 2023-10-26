package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.NecroBrazierBlock;
import com.Polarice3.Goety.common.crafting.BrazierRecipe;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ConstantPaths;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class NecroBrazierBlockEntity extends ModBlockEntity implements Clearable {
    private final List<SoulCandlestickBlockEntity> candlestickBlockEntityList = Lists.newArrayList();
    public BrazierRecipe recipe;
    public ResourceLocation recipeId;
    public final SimpleContainer inventory = new SimpleContainer(5){
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    public int currentTime;

    public NecroBrazierBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.NECRO_BRAZIER.get(), p_155229_, p_155230_);
    }

    public Container getContainer(){
        return this.inventory;
    }

    public void setItems(NonNullList<ItemStack> items){
        for (int i = 0; i < items.size(); i++) {
            this.getContainer().setItem(i, items.get(i));
        }
    }

    public NonNullList<ItemStack> getItems(){
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(this.getContainer().getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < this.getContainer().getContainerSize(); i++) {
            itemStacks.set(i, this.getContainer().getItem(i));
        }
        return itemStacks;
    }

    public void readNetwork(CompoundTag compoundNBT) {
        NonNullList<ItemStack> items = NonNullList.withSize(this.getContainer().getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundNBT, items);
        this.setItems(items);
        this.currentTime = compoundNBT.getInt("currentTime");
    }

    public CompoundTag writeNetwork(CompoundTag pCompound) {
        this.saveMetadataAndItems(pCompound);
        pCompound.putInt("currentTime", this.currentTime);
        return pCompound;
    }

    private CompoundTag saveMetadataAndItems(CompoundTag pCompound) {
        ContainerHelper.saveAllItems(pCompound, this.getItems(), true);
        return pCompound;
    }

    public boolean addItem(@Nullable Player player, ItemStack stack) {
        if (this.currentTime > 0){
            return false;
        }

        for (int i = 0; i < this.getContainer().getContainerSize(); i++) {
            if (this.getContainer().getItem(i).isEmpty()) {
                ItemStack stack1 = stack.copy();
                stack1.setCount(1);
                this.getContainer().setItem(i, stack1);

                if (player == null || !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                if (player != null && this.level != null){
                    this.level.playSound(null, this.getBlockPos(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                break;
            }
        }
        this.markUpdated();
        return true;
    }

    public void removeItem(Player player) {
        for (int i = this.getContainer().getContainerSize() - 1; i >= 0; i--) {
            ItemStack stackAt = this.getContainer().getItem(i);
            if (!stackAt.isEmpty()) {
                ItemStack copy = stackAt.copy();
                player.getInventory().placeItemBackInInventory(copy);
                this.getContainer().setItem(i, ItemStack.EMPTY);
                break;
            }
        }
    }

    public void removeAllItems(){
        for (int i = 0; i < this.getContainer().getContainerSize(); i++) {
            ItemStack stackAt = this.getContainer().getItem(i);
            if (!stackAt.isEmpty()) {
                this.getContainer().setItem(i, ItemStack.EMPTY);
            }
        }
    }

    public void updateRecipe(Level world){
        if (this.getRecipe() != null){
            if (!this.getRecipe().matches(this.getContainer(), world)){
                this.stopBrazier(false);
            }
        } else {
            this.stopBrazier(false);
        }
    }

    public boolean activate(Level world) {
        if (this.getRecipe() == null) {
            BrazierRecipe brazierRecipe = world.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.BRAZIER_TYPE.get()).stream().filter(
                    r -> r.matches(this.getContainer(), world)
            ).findFirst().orElse(null);
            if (brazierRecipe != null) {
                this.recipe = brazierRecipe;
            } else {
                return false;
            }
        }
        return true;
    }

    public BrazierRecipe getRecipe(){
        if(this.recipeId != null){
            if(this.level != null) {
                Optional<? extends Recipe<?>> recipe = this.level.getRecipeManager().byKey(this.recipeId);
                recipe.map(r -> (BrazierRecipe) r).ifPresent(r -> this.recipe = r);
                this.recipeId = null;
            }
        }
        return this.recipe;
    }

    public void tick() {
        if (this.level != null) {
            boolean flag = this.level.getBiome(this.getBlockPos()).is(Biomes.DEEP_DARK);
            if (flag) {
                this.findCandlesticks();
                if (!this.level.isClientSide) {
                    if (this.level.random.nextFloat() < 0.3F) {
                        if (this.level.random.nextFloat() < 0.17F) {
                            ModNetwork.sendToALL(new SPlayWorldSoundPacket(this.worldPosition, SoundEvents.FURNACE_FIRE_CRACKLE, 0.5F + this.level.random.nextFloat(), this.level.random.nextFloat() * 0.7F + 0.3F));
                        }
                    }
                }
                if (!this.candlestickBlockEntityList.isEmpty()){
                    BrazierRecipe recipe = this.getRecipe();
                    double d0 = (double)this.worldPosition.getX() + this.level.random.nextDouble();
                    double d1 = (double)this.worldPosition.getY() + 0.5D + this.level.random.nextDouble();
                    double d2 = (double)this.worldPosition.getZ() + this.level.random.nextDouble();
                    if (!this.level.isClientSide) {
                        ServerLevel serverWorld = (ServerLevel) this.level;
                        this.makeParticles();
                        if (this.activate(this.level)) {
                            if (recipe != null) {
                                for (int p = 0; p < 2; ++p) {
                                    serverWorld.sendParticles(ModParticleTypes.SMALL_NECRO_FIRE.get(), d0, this.worldPosition.getY() + 0.5F, d2, 1, 0, 0, 0, 0);
                                    serverWorld.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 0, 0.0D, 5.0E-4D, 0.0D, 0.5F);
                                    serverWorld.sendParticles(ModParticleTypes.NECRO_EFFECT.get(), d0, d1, d2, 1, 0.0F, 0.0F, 0.0F, 0.0F);
                                }
                                for (SoulCandlestickBlockEntity candlestickBlock : this.candlestickBlockEntityList){
                                    if (candlestickBlock.getSouls() > 0){
                                        candlestickBlock.drainSouls(1, this.getBlockPos());
                                        this.currentTime++;
                                    }
                                }
                                if (this.currentTime == 1) {
                                    ModNetwork.sendToALL(new SPlayWorldSoundPacket(this.worldPosition, SoundEvents.BLAZE_AMBIENT, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F));
                                }
                                if (this.level.getGameTime() % 20 == 0) {
                                    ModNetwork.sendToALL(new SPlayWorldSoundPacket(this.worldPosition, SoundEvents.FIRE_AMBIENT, 1.0F + this.level.random.nextFloat(), this.level.random.nextFloat() * 0.7F + 0.3F));
                                    ModNetwork.sendToALL(new SPlayWorldSoundPacket(this.worldPosition, SoundEvents.SCULK_CATALYST_BLOOM, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F));
                                    serverWorld.sendParticles(ParticleTypes.SCULK_SOUL, (double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 1.15D, (double) this.worldPosition.getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
                                }

                                if (this.currentTime >= recipe.getSoulCost()) {
                                    this.stopBrazier(true);
                                } else {
                                    this.updateRecipe(this.level);
                                }
                            }
                        }
                    }
                } else {
                    this.stopBrazier(false);
                }
            } else {
                this.stopBrazier(false);
            }
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(NecroBrazierBlock.LIT, flag), 3);
        }
    }

    public void stopBrazier(boolean finished) {
        if (this.level != null) {
            if (!this.level.isClientSide) {
                BrazierRecipe recipe = this.getRecipe();
                if (recipe != null) {
                    this.level.playSound(null, this.getBlockPos(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (finished) {
                        ItemStack itemstack1 = this.level.getRecipeManager()
                                .getRecipeFor(ModRecipeSerializer.BRAZIER_TYPE.get(), this.getContainer(), this.level)
                                .map((recipes) -> recipes.assemble(this.getContainer(), this.level.registryAccess())).orElse(ItemStack.EMPTY);
                        BlockPos blockpos = this.getBlockPos();
                        dropItemStack(this.level, blockpos.getX(), blockpos.getY() + 1, blockpos.getZ(), itemstack1);
                        this.level.playSound(null, this.getBlockPos(), ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 2.0F, 0.5F);
                    } else {
                        Containers.dropContents(this.level, this.getBlockPos(), this.getItems());
                        this.level.playSound(null, this.getBlockPos(), ModSounds.SPELL_FAIL.get(), SoundSource.BLOCKS, 2.0F, 0.5F);
                    }
                }
                this.removeAllItems();
                this.clearContent();
                this.recipe = null;
                this.currentTime = 0;
                this.markUpdated();
            }
        }
    }

    public static void dropItemStack(Level pLevel, double pX, double pY, double pZ, ItemStack pStack) {
        double d0 = (double) EntityType.ITEM.getWidth();
        double d1 = 1.0D - d0;
        double d2 = d0 / 2.0D;
        double d3 = Math.floor(pX) + pLevel.random.nextDouble() * d1 + d2;
        double d4 = Math.floor(pY) + pLevel.random.nextDouble() * d1;
        double d5 = Math.floor(pZ) + pLevel.random.nextDouble() * d1 + d2;

        while(!pStack.isEmpty()) {
            ItemEntity itementity = new ItemEntity(pLevel, d3, d4, d5, pStack.split(pLevel.random.nextInt(21) + 10));
            itementity.addTag(ConstantPaths.resultItem());
            itementity.setDeltaMovement(pLevel.random.triangle(0.0D, 0.11485000171139836D), pLevel.random.triangle(0.2D, 0.11485000171139836D), pLevel.random.triangle(0.0D, 0.11485000171139836D));
            pLevel.addFreshEntity(itementity);
        }

    }

    @Override
    public void clearContent() {
        this.getItems().clear();
    }

    private void makeParticles() {
        BlockPos blockpos = this.getBlockPos();
        ServerLevel serverLevel = (ServerLevel) this.level;

        if (serverLevel != null) {
            long t = serverLevel.getGameTime();
            double d0 = (double)blockpos.getX() + serverLevel.random.nextDouble();
            double d1 = (double)blockpos.getY() + serverLevel.random.nextDouble();
            double d2 = (double)blockpos.getZ() + serverLevel.random.nextDouble();
            if (t % 20L == 0L) {
                if (this.getBlockState().getValue(NecroBrazierBlock.WATERLOGGED)) {
                    if (serverLevel.getFluidState(blockpos.above()).isEmpty() && serverLevel.getBlockState(blockpos.above()).isAir()) {
                        serverLevel.sendParticles(ModParticleTypes.NECRO_FIRE.get(), blockpos.getX() + 0.5F, blockpos.getY() + 1.0F, blockpos.getZ() + 0.5F, 1, 0, 0, 0, 0);
                    } else {
                        for (int p = 0; p < 4; ++p) {
                            serverLevel.sendParticles(ParticleTypes.BUBBLE, d0, d1, d2, 1, 0, 0, 0, 0);
                            serverLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, d0, d1, d2, 0, 0.0D, 0.04D, 0.0D, 0.5F);
                        }
                    }
                } else {
                    if (serverLevel.getFluidState(blockpos.above()).isEmpty() && serverLevel.getBlockState(blockpos.above()).isAir()) {
                        for (int p = 0; p < 4; ++p) {
                            serverLevel.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 0, 0.0D, 5.0E-4D, 0.0D, 0.5F);
                        }
                        serverLevel.sendParticles(ModParticleTypes.NECRO_FIRE.get(), blockpos.getX() + 0.5F, blockpos.getY() + 1.0F, blockpos.getZ() + 0.5F, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }

    private void findCandlesticks(){
        if (this.level != null){
            this.candlestickBlockEntityList.clear();
            for (int i = -8; i <= 8; ++i) {
                for (int j = -8; j <= 8; ++j) {
                    for (int k = -8; k <= 8; ++k) {
                        BlockPos blockpos1 = this.getBlockPos().offset(i, j, k);
                        if (this.level.getBlockEntity(blockpos1) instanceof SoulCandlestickBlockEntity soulCandlestickBlockEntity) {
                            if (soulCandlestickBlockEntity.getSouls() > 0) {
                                this.candlestickBlockEntityList.add(soulCandlestickBlockEntity);
                            }
                        }
                    }
                }
            }
        }
    }
}
