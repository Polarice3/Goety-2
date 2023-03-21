package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.DarkAltarBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.Polarice3.Goety.common.ritual.RitualStructures;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DarkAltarBlockEntity extends PedestalBlockEntity implements GameEventListener {
    private CursedCageBlockEntity cursedCageTile;
    private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);
    public RitualRecipe currentRitualRecipe;
    public ResourceLocation currentRitualRecipeId;
    public UUID castingPlayerId;
    public Player castingPlayer;
    public List<Ingredient> remainingAdditionalIngredients = new ArrayList<>();
    public List<ItemStack> consumedIngredients = new ArrayList<>();
    public boolean sacrificeProvided;
    public int currentTime;
    public int structureTime;

    public DarkAltarBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.DARK_ALTAR.get(), blockPos, blockState);
    }

    public RitualRecipe getCurrentRitualRecipe(){
        if(this.currentRitualRecipeId != null){
            if(this.level != null) {
                Optional<? extends Recipe<?>> recipe = this.level.getRecipeManager().byKey(this.currentRitualRecipeId);
                recipe.map(r -> (RitualRecipe) r).ifPresent(r -> this.currentRitualRecipe = r);
                this.currentRitualRecipeId = null;
            }
        }
        return this.currentRitualRecipe;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        this.consumedIngredients.clear();
        if (this.currentRitualRecipeId != null || this.getCurrentRitualRecipe() != null) {
            if (compound.contains("consumedIngredients")) {
                ListTag list = compound.getList("consumedIngredients", Tag.TAG_COMPOUND);
                for (int i = 0; i < list.size(); i++) {
                    ItemStack stack = ItemStack.of(list.getCompound(i));
                    this.consumedIngredients.add(stack);
                }
            }
            this.restoreRemainingAdditionalIngredients();
        }
        if (compound.contains("sacrificeProvided")) {
            this.sacrificeProvided = compound.getBoolean("sacrificeProvided");
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        if (this.getCurrentRitualRecipe() != null) {
            if (this.consumedIngredients.size() > 0) {
                ListTag list = new ListTag();
                for (ItemStack stack : this.consumedIngredients) {
                    list.add(stack.serializeNBT());
                }
                compound.put("consumedIngredients", list);
            }
            compound.putBoolean("sacrificeProvided", this.sacrificeProvided);
        }
        super.saveAdditional(compound);
    }

    @Override
    public void readNetwork(CompoundTag compound) {
        super.readNetwork(compound);
        if (compound.contains("currentRitual")) {
            this.currentRitualRecipeId = new ResourceLocation(compound.getString("currentRitual"));
        }

        if (compound.contains("castingPlayerId")) {
            this.castingPlayerId = compound.getUUID("castingPlayerId");
        }

        this.currentTime = compound.getInt("currentTime");
        this.structureTime = compound.getInt("structureTime");
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag compound) {
        RitualRecipe recipe = this.getCurrentRitualRecipe();
        if (recipe != null) {
            compound.putString("currentRitual", recipe.getId().toString());
        }
        if (this.castingPlayerId != null) {
            compound.putUUID("castingPlayerId", this.castingPlayerId);
        }
        compound.putInt("currentTime", this.currentTime);
        compound.putInt("structureTime", this.structureTime);
        return super.writeNetwork(compound);
    }

    public void tick() {
        boolean flag = this.checkCage();
        assert this.level != null;
        if (flag) {
            if (this.cursedCageTile.getSouls() > 0){
                RitualRecipe recipe = this.getCurrentRitualRecipe();
                double d0 = (double)this.worldPosition.getX() + this.level.random.nextDouble();
                double d1 = (double)this.worldPosition.getY() + this.level.random.nextDouble();
                double d2 = (double)this.worldPosition.getZ() + this.level.random.nextDouble();
                if (!this.level.isClientSide) {
                    ServerLevel serverWorld = (ServerLevel) this.level;
                    if (recipe != null) {
                        this.cursedCageTile.generateParticles();

                        this.restoreCastingPlayer();

                        if (this.castingPlayer == null || !this.sacrificeFulfilled()) {
                            for (int p = 0; p < 4; ++p) {
                                serverWorld.sendParticles(ParticleTypes.FLAME, d0, d1, d2, 1, 0.0F, 0.0F, 0.0F, 0.0F);
                            }
                        }
                        for (int p = 0; p < 4; ++p) {
                            serverWorld.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(), d0, d1, d2, 0, 0.45, 0.45, 0.45, 1);
                        }

                        if (this.remainingAdditionalIngredients == null) {
                            this.restoreRemainingAdditionalIngredients();
                            if (this.remainingAdditionalIngredients == null) {
                                Goety.LOGGER
                                        .warn("Could not restore remainingAdditionalIngredients during tick - world seems to be null. Will attempt again next tick.");
                                return;
                            }
                        }

                        IItemHandler handler = this.itemStackHandler.orElseThrow(RuntimeException::new);
                        if (!recipe.getRitual().isValid(this.level, this.worldPosition, this, this.castingPlayer,
                                handler.getStackInSlot(0), this.remainingAdditionalIngredients)) {
                            this.stopRitual(false);
                            return;
                        }

                        if (this.castingPlayer == null || !this.sacrificeFulfilled()) {
                            return;
                        }

                        if (this.level.getGameTime() % 20 == 0) {
                            this.cursedCageTile.decreaseSouls(recipe.getSoulCost());
                            this.currentTime++;
                        }

                        recipe.getRitual().update(this.level, this.worldPosition, this, this.castingPlayer, handler.getStackInSlot(0),
                                this.currentTime);

                        boolean villager = /*recipe.getActivationItem().test(new ItemStack(ModItems.FILLED_ILL_CAGE.get()));*/ false;

                        if (!recipe.getRitual()
                                .consumeAdditionalIngredients(this.level, this.worldPosition, this.remainingAdditionalIngredients,
                                        this.currentTime, this.consumedIngredients, villager)) {
                            this.stopRitual(false);
                            return;
                        }

                        if (recipe.getDuration() >= 0 && this.currentTime >= recipe.getDuration()) {
                            this.stopRitual(true);
                        }

                        int totalSTime = 60;

                        if (!RitualStructures.getProperStructure(recipe.getCraftType(), this, this.worldPosition, this.level)){
                            ++this.structureTime;
                            if (this.structureTime >= totalSTime) {
                                this.castingPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.fail"), true);
                                this.stopRitual(false);
                            }
                        } else {
                            this.structureTime = 0;
                        }
                    } else {
                        if (this.level.getGameTime() % 20 == 0) {
                            for (int p = 0; p < 4; ++p) {
                                serverWorld.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(), d0, d1, d2, 0, 0.45, 0.45, 0.45, 1);
                            }
                        }
                    }
                }
            } else {
                this.stopRitual(false);
            }
        } else {
            this.stopRitual(false);
        }
        this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(DarkAltarBlock.LIT, flag), 3);
    }

    public void restoreCastingPlayer() {
        if (this.castingPlayer == null && this.castingPlayerId != null) {
            if (this.level != null) {
                if (this.level.getGameTime() % (20 * 30) == 0) {
                    this.castingPlayer = EntityFinder.getPlayerByUuiDGlobal(this.castingPlayerId).orElse(null);
                    this.setChanged();
                    this.markNetworkDirty();
                }
            }
        }
    }

    public boolean activate(Level world, BlockPos pos, Player player, InteractionHand hand, Direction face) {
        if (!world.isClientSide) {
            if (this.checkCage()){
                ItemStack activationItem = player.getItemInHand(hand);
                if (activationItem == ItemStack.EMPTY){
                    this.RemoveItem();
                }

                if (this.getCurrentRitualRecipe() == null) {

                    RitualRecipe ritualRecipe = this.level.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get()).stream().filter(
                            r -> r.matches(world, pos, activationItem)
                    ).findFirst().orElse(null);

                    if (ritualRecipe != null) {
                        if (ritualRecipe.getRitual().isValid(world, pos, this, player, activationItem, ritualRecipe.getIngredients())) {

                            if (!RitualStructures.getProperStructure(ritualRecipe.getCraftType(), this, pos, world)){
                                player.displayClientMessage(Component.translatable("info.goety.ritual.structure.fail"), true);
                                return false;
                            }/* else if (ritualRecipe.getCraftType().contains("adept_nether") || ritualRecipe.getCraftType().contains("sabbath") || ritualRecipe.getCraftType().contains("expert_nether")){
                                CompoundTag playerData = player.getPersistentData();
                                CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
                                if (data.getBoolean(ConstantPaths.readNetherBook())){
                                    this.startRitual(player, activationItem, ritualRecipe);
                                } else {
                                    player.displayClientMessage(Component.translatable("info.goety.ritual.fail"), true);
                                    return false;
                                }
                            } else if (ritualRecipe.getCraftType().contains("lich")){
                                CompoundTag playerData = player.getPersistentData();
                                CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
                                if (MainConfig.LichScrollRequirement.get()) {
                                    if (data.getBoolean(ConstantPaths.readScroll())) {
                                        this.startRitual(player, activationItem, ritualRecipe);
                                    } else {
                                        player.displayClientMessage(Component.translatable("info.goety.ritual.fail"), true);
                                        return false;
                                    }
                                } else {
                                    this.startRitual(player, activationItem, ritualRecipe);
                                }
                            } */else {
                                this.startRitual(player, activationItem, ritualRecipe);
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    this.stopRitual(false);
                }
            } else {
                this.RemoveItem();
            }
        }
        return true;
    }

    public void RemoveItem(){
        IItemHandler handler = this.itemStackHandler.orElseThrow(RuntimeException::new);
        ItemStack itemStack = handler.getStackInSlot(0);
        if (itemStack != ItemStack.EMPTY){
            Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(),
                    handler.extractItem(0, 1, false));
        }
        this.currentRitualRecipe = null;
        this.castingPlayerId = null;
        this.castingPlayer = null;
        this.currentTime = 0;
        this.sacrificeProvided = false;
        if (this.remainingAdditionalIngredients != null)
            this.remainingAdditionalIngredients.clear();
        this.consumedIngredients.clear();
        this.structureTime = 0;
        this.setChanged();
        this.markNetworkDirty();
    }

    public void startRitual(Player player, ItemStack activationItem, RitualRecipe ritualRecipe) {
        if (!this.level.isClientSide) {
            this.currentRitualRecipe = ritualRecipe;
            this.castingPlayerId = player.getUUID();
            this.castingPlayer = player;
            this.currentTime = 0;
            this.sacrificeProvided = false;
            this.consumedIngredients.clear();
            this.remainingAdditionalIngredients = new ArrayList<>(this.currentRitualRecipe.getIngredients());
            IItemHandler handler = this.itemStackHandler.orElseThrow(RuntimeException::new);
            handler.insertItem(0, activationItem.split(1), false);
            this.currentRitualRecipe.getRitual().start(this.level, this.worldPosition, this, player, handler.getStackInSlot(0));
            this.structureTime = 40;
            this.setChanged();
            this.markNetworkDirty();
        }
    }

    public void stopRitual(boolean finished) {
        if (!this.level.isClientSide) {
            RitualRecipe recipe = this.getCurrentRitualRecipe();
            if (recipe != null && this.castingPlayer != null) {
                IItemHandler handler = this.itemStackHandler.orElseThrow(RuntimeException::new);
                if (finished) {
                    ItemStack activationItem = handler.getStackInSlot(0);
                    recipe.getRitual().finish(this.level, this.worldPosition, this, this.castingPlayer, activationItem);
                } else {
                    recipe.getRitual().interrupt(this.level, this.worldPosition, this, this.castingPlayer,
                            handler.getStackInSlot(0));
                    Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(),
                            handler.extractItem(0, 1, false));
                }
            }
            this.currentRitualRecipe = null;
            this.castingPlayerId = null;
            this.castingPlayer = null;
            this.currentTime = 0;
            this.sacrificeProvided = false;
            if (this.remainingAdditionalIngredients != null)
                this.remainingAdditionalIngredients.clear();
            this.consumedIngredients.clear();
            this.structureTime = 0;
            this.setChanged();
            this.markNetworkDirty();
        }
    }

    public boolean sacrificeFulfilled() {
        return !this.getCurrentRitualRecipe().requiresSacrifice() || this.sacrificeProvided;
    }

    public void notifySacrifice(LivingEntity entityLivingBase) {
        this.sacrificeProvided = true;
    }

    public boolean handleEventsImmediately() {
        return true;
    }

    public PositionSource getListenerSource() {
        return this.blockPosSource;
    }

    public int getListenerRadius() {
        return Ritual.SACRIFICE_DETECTION_RANGE;
    }

    public boolean handleGameEvent(ServerLevel serverLevel, GameEvent.Message message) {
        if (!this.isRemoved()) {
            GameEvent.Context gameevent$context = message.context();
            if (message.gameEvent() == GameEvent.ENTITY_DIE) {
                Entity sourceEntity = gameevent$context.sourceEntity();
                if (sourceEntity instanceof LivingEntity livingEntity) {
                    if (this.getCurrentRitualRecipe() != null && this.getCurrentRitualRecipe().getRitual().isValidSacrifice(livingEntity)) {
                        this.notifySacrifice(livingEntity);
                    }
                    return true;
                }
            }

        }
        return false;
    }

    protected void restoreRemainingAdditionalIngredients() {
        if (this.level == null) {
            this.remainingAdditionalIngredients = null;
        } else {
            if (this.consumedIngredients.size() > 0) {
                this.remainingAdditionalIngredients = Ritual.getRemainingAdditionalIngredients(
                        this.getCurrentRitualRecipe().getIngredients(), this.consumedIngredients);
            } else {
                this.remainingAdditionalIngredients = new ArrayList<>(this.getCurrentRitualRecipe().getIngredients());
            }
        }

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

/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */