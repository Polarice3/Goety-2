package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.DarkAltarBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DarkAltarBlockEntity extends PedestalBlockEntity implements GameEventListener {
    public long lastChangeTime;
    public LazyOptional<ItemStackHandler> itemStackHandler = LazyOptional.of(
            () -> new ItemStackHandler(1) {
                @Override
                public int getSlotLimit(int slot) {
                    return 64;
                }

                @Override
                protected void onContentsChanged(int slot) {
                    assert DarkAltarBlockEntity.this.level != null;
                    if (!DarkAltarBlockEntity.this.level.isClientSide) {
                        DarkAltarBlockEntity.this.lastChangeTime = DarkAltarBlockEntity.this.level
                                .getGameTime();
                        boolean flag = !this.stacks.get(0).isEmpty();
                        DarkAltarBlockEntity.this.level.setBlockAndUpdate(DarkAltarBlockEntity.this.getBlockPos(),
                                DarkAltarBlockEntity.this.getBlockState().setValue(DarkAltarBlock.OCCUPIED, flag));
                        DarkAltarBlockEntity.this.markNetworkDirty();
                    }
                }
            });
    private CursedCageBlockEntity cursedCageTile;
    private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);
    public RitualRecipe currentRitualRecipe;
    public ResourceLocation currentRitualRecipeId;
    public UUID castingPlayerId;
    public Player castingPlayer;
    public List<Ingredient> remainingAdditionalIngredients = new ArrayList<>();
    public List<ItemStack> consumedIngredients = new ArrayList<>();
    public boolean sacrificeProvided;
    public Mob getConvertEntity;
    public int currentTime;
    public int structureTime;
    public int convertTime;
    public boolean showArea;

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
        this.itemStackHandler.ifPresent((handler) -> handler.deserializeNBT(compound.getCompound("inventory")));
        this.lastChangeTime = compound.getLong("lastChangeTime");
        if (compound.contains("currentRitual")) {
            this.currentRitualRecipeId = new ResourceLocation(compound.getString("currentRitual"));
        }

        if (compound.contains("castingPlayerId")) {
            this.castingPlayerId = compound.getUUID("castingPlayerId");
        }

        this.currentTime = compound.getInt("currentTime");
        this.structureTime = compound.getInt("structureTime");
        this.convertTime = compound.getInt("convertTime");
        if (compound.contains("showArea")) {
            this.showArea = compound.getBoolean("showArea");
        }
    }

    @Override
    public CompoundTag writeNetwork(CompoundTag compound) {
        this.itemStackHandler.ifPresent(handler -> compound.put("inventory", handler.serializeNBT()));
        compound.putLong("lastChangeTime", this.lastChangeTime);
        RitualRecipe recipe = this.getCurrentRitualRecipe();
        if (recipe != null) {
            compound.putString("currentRitual", recipe.getId().toString());
        }
        if (this.castingPlayerId != null) {
            compound.putUUID("castingPlayerId", this.castingPlayerId);
        }
        compound.putInt("currentTime", this.currentTime);
        compound.putInt("structureTime", this.structureTime);
        compound.putInt("convertTime", this.convertTime);
        compound.putBoolean("showArea", this.showArea);
        return compound;
    }

    public boolean isShowArea(){
        return this.showArea;
    }

    public void setShowArea(boolean showArea){
        this.showArea = showArea;
        this.setChanged();
        this.markNetworkDirty();
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
                            if (this.cursedCageTile.getSouls() >= recipe.getSoulCost()){
                                this.cursedCageTile.decreaseSouls(recipe.getSoulCost());
                                serverWorld.playSound(null, this.worldPosition, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F);
                                serverWorld.sendParticles(ParticleTypes.SCULK_SOUL, (double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 1.15D, (double)this.worldPosition.getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
                                this.currentTime++;
                            } else {
                                this.castingPlayer.displayClientMessage(Component.translatable("info.goety.ritual.noSouls.fail"), true);
                                this.stopRitual(false);
                                return;
                            }
                        }

                        recipe.getRitual().update(this.level, this.worldPosition, this, this.castingPlayer, handler.getStackInSlot(0),
                                this.currentTime, recipe.getDuration());

                        if (!recipe.getRitual()
                                .consumeAdditionalIngredients(this.level, this.worldPosition, this.castingPlayer, this.remainingAdditionalIngredients,
                                        this.currentTime, this.consumedIngredients)) {
                            this.stopRitual(false);
                            return;
                        }

                        if (recipe.getDuration() >= 0 && this.currentTime >= recipe.getDuration()) {
                            if (!recipe.isConversion()) {
                                this.stopRitual(true);
                            } else {
                                if (this.getConvertEntity != null){
                                    this.stopRitual(true);
                                } else {
                                    this.castingPlayer.displayClientMessage(Component.translatable("info.goety.ritual.convert.fail"), true);
                                    this.stopRitual(false);
                                }
                            }
                        }

                        int totalTime = 60;

                        if (!RitualRequirements.getProperStructure(recipe.getCraftType(), this, this.worldPosition, this.level)){
                            ++this.structureTime;
                            if (this.structureTime >= totalTime) {
                                this.castingPlayer.displayClientMessage(Component.translatable("info.goety.ritual.structure.fail"), true);
                                this.stopRitual(false);
                            }
                        } else {
                            this.structureTime = 0;
                        }
                        if (recipe.isConversion()){
                            if (RitualRequirements.noConvertEntity(recipe.getEntityToConvert(), this.worldPosition, this.level)){
                                ++this.convertTime;
                                if (this.getConvertEntity != null){
                                    this.getConvertEntity = null;
                                }
                                if (this.convertTime >= totalTime) {
                                    this.castingPlayer.displayClientMessage(Component.translatable("info.goety.ritual.convert.fail"), true);
                                    this.stopRitual(false);
                                }
                            } else {
                                this.getConvertEntity = RitualRequirements.getConvertEntity(recipe.getEntityToConvert(), this.worldPosition, this.level);
                                this.convertTime = 0;
                            }
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
        this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(DarkAltarBlock.LIT, flag), 3);
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
                    this.removeItem();
                }

                if (this.getCurrentRitualRecipe() == null) {

                    RitualRecipe ritualRecipe = world.getRecipeManager().getAllRecipesFor(ModRecipeSerializer.RITUAL_TYPE.get()).stream().filter(
                            r -> r.matches(world, pos, player, activationItem)
                    ).findFirst().orElse(null);

                    if (ritualRecipe != null) {
                        if (ritualRecipe.getRitual().isValid(world, pos, this, player, activationItem, ritualRecipe.getIngredients())) {
                            if (!RitualRequirements.getProperStructure(ritualRecipe.getCraftType(), this, pos, world)){
                                player.displayClientMessage(Component.translatable("info.goety.ritual.structure.fail"), true);
                                return false;
                            } else if (ritualRecipe.getCraftType().contains("lich")){
                                if (MainConfig.LichScrollRequirement.get()) {
                                    if (SEHelper.hasResearch(player, ResearchList.FORBIDDEN)) {
                                        this.startRitual(player, activationItem, ritualRecipe);
                                    } else {
                                        player.displayClientMessage(Component.translatable("info.goety.ritual.fail"), true);
                                        return false;
                                    }
                                } else {
                                    this.startRitual(player, activationItem, ritualRecipe);
                                }
                            } else if (ritualRecipe.isConversion() && RitualRequirements.noConvertEntity(ritualRecipe.getEntityToConvert(), pos, world)){
                                player.displayClientMessage(Component.translatable("info.goety.ritual.convert.fail"), true);
                                return false;
                            } else if (ritualRecipe.isSummoning() && !RitualRequirements.canSummon(world, player, ritualRecipe.getEntityToSummon())){
                                return false;
                            } else if (ritualRecipe.getResearch() != null
                                    && ResearchList.getResearch(ritualRecipe.getResearch()) != null){
                                if (SEHelper.hasResearch(player, ResearchList.getResearch(ritualRecipe.getResearch()))){
                                    this.startRitual(player, activationItem, ritualRecipe);
                                } else {
                                    player.displayClientMessage(Component.translatable("info.goety.ritual.fail"), true);
                                    return false;
                                }
                            } else if (world.dimension() == Level.NETHER && ritualRecipe.getEntityToSummon() == ModEntityType.SUMMON_APOSTLE.get()){
                                if (SEHelper.apostleWarned(player)){
                                    SEHelper.setApostleWarned(player, false);
                                    this.startRitual(player, activationItem, ritualRecipe);
                                } else {
                                    SEHelper.setApostleWarned(player, true);
                                    player.displayClientMessage(Component.translatable("info.goety.ritual.apostleWarn"), true);
                                    return false;
                                }
                            } else {
                                this.startRitual(player, activationItem, ritualRecipe);
                            }
                        } else {
                            player.displayClientMessage(Component.translatable("info.goety.ritual.itemProblem.fail"), true);
                            return false;
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("info.goety.ritual.itemProblem.fail"), true);
                        return false;
                    }
                } else {
                    this.stopRitual(false);
                }
            } else {
                this.removeItem();
            }
        }
        return true;
    }

    public void removeItem(){
        IItemHandler handler = this.itemStackHandler.orElseThrow(RuntimeException::new);
        ItemStack itemStack = handler.getStackInSlot(0);
        if (itemStack != ItemStack.EMPTY){
            Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(),
                    handler.extractItem(0, itemStack.getCount(), false));
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
                    if (recipe.getEntityToSummon() == ModEntityType.SUMMON_APOSTLE.get()){
                        if (this.level instanceof ServerLevel serverLevel) {
                            ModNetwork.sendToALL(new SPlayWorldSoundPacket(this.worldPosition, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.get(), 1.0F, 1.0F));
                            Warden.applyDarknessAround(serverLevel, Vec3.atCenterOf(this.worldPosition), (Entity)null, 32);
                        }
                        for (int i = -8; i <= 8; ++i) {
                            for (int j = -8; j <= 8; ++j) {
                                for (int k = -8; k <= 8; ++k) {
                                    BlockPos blockpos1 = this.worldPosition.offset(i, j, k);
                                    BlockState blockstate = this.level.getBlockState(blockpos1);
                                    if (blockstate.getBlock() instanceof SoulFireBlock){
                                        this.level.destroyBlock(blockpos1, false);
                                        this.level.levelEvent((Player)null, 1009, blockpos1, 0);
                                    }
                                }
                            }
                        }
                    }
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
            if (this.remainingAdditionalIngredients != null) {
                this.remainingAdditionalIngredients.clear();
            }
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

    public PositionSource getListenerSource() {
        return this.blockPosSource;
    }

    public int getListenerRadius() {
        return Ritual.SACRIFICE_DETECTION_RANGE;
    }

    public GameEventListener.DeliveryMode getDeliveryMode() {
        return GameEventListener.DeliveryMode.BY_DISTANCE;
    }

    public boolean handleGameEvent(ServerLevel serverLevel, GameEvent p_282184_, GameEvent.Context p_283014_, Vec3 p_282350_) {
        if (!this.isRemoved()) {
            if (p_282184_ == GameEvent.ENTITY_DIE) {
                Entity sourceEntity = p_283014_.sourceEntity();
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
                return !cursedCageTile.getItem().isEmpty() && cursedCageTile.getSouls() > 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemStackHandler.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.itemStackHandler.invalidate();
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