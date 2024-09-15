package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.PedestalBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Ritual Codes based from @klikli-dev
 */
public abstract class Ritual {

    public static final int RANGE = 8;

    public static final int PEDESTAL_RANGE = 8;

    public static final int SACRIFICE_DETECTION_RANGE = 8;

    public RitualRecipe recipe;

    public ResourceLocation factoryId;

    public Ritual(RitualRecipe recipe) {
        this.recipe = recipe;
    }

    public static List<Ingredient> getRemainingAdditionalIngredients(List<Ingredient> additionalIngredients, List<ItemStack> consumedIngredients) {
        List<ItemStack> consumedIngredientsCopy = new ArrayList<>(consumedIngredients);
        List<Ingredient> remainingAdditionalIngredients = new ArrayList<>();
        for (Ingredient ingredient : additionalIngredients) {
            Optional<ItemStack> matchedStack = consumedIngredientsCopy.stream().filter(ingredient::test).findFirst();
            if (matchedStack.isPresent()) {
                consumedIngredientsCopy.remove(matchedStack.get());
            } else {
                remainingAdditionalIngredients.add(ingredient);
            }
        }
        return remainingAdditionalIngredients;
    }

    public ResourceLocation getFactoryID() {
        return this.factoryId;
    }

    public void setFactoryId(ResourceLocation factoryId) {
        this.factoryId = factoryId;
    }

    public RitualRecipe getRecipe() {
        return this.recipe;
    }

    public boolean isValid(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                           Player castingPlayer, ItemStack activationItem,
                           List<Ingredient> remainingAdditionalIngredients) {
        return this.recipe.getActivationItem().test(activationItem) &&
                this.areAdditionalIngredientsFulfilled(world, darkAltarPos, castingPlayer, remainingAdditionalIngredients);
    }

    public void start(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                      Player castingPlayer, ItemStack activationItem) {
        world.playSound(null, darkAltarPos, ModSounds.ALTAR_START.get(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
    }

    public void finish(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        if (tileEntity.getCurrentRitualRecipe().getCraftType().contains("forge")){
            world.playSound(null, darkAltarPos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
        }
        if (tileEntity.getCurrentRitualRecipe().getCraftType().contains("storm")){
            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, world);
            lightningBolt.setVisualOnly(true);
            lightningBolt.setPos(Vec3.atCenterOf(darkAltarPos));
            world.addFreshEntity(lightningBolt);
        }
        if (tileEntity.getCurrentRitualRecipe().getRitual() instanceof EnchantItemRitual){
            world.playSound(null, darkAltarPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
        }
        world.playSound(null, darkAltarPos, ModSounds.ALTAR_FINISH.get(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
    }

    public void interrupt(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                          Player castingPlayer, ItemStack activationItem) {
        world.playSound(null, darkAltarPos, ModSounds.SPELL_FAIL.get(), SoundSource.BLOCKS, 0.7f, 0.7f);
    }

    public void update(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem,
                       List<Ingredient> remainingAdditionalIngredients, int time, int totalTime) {
        if (tileEntity.getConvertEntity != null){
            tileEntity.getConvertEntity.setTicksFrozen(140);
            if (tileEntity.getConvertEntity.tickCount % 19 == 0) {
                if (world instanceof ServerLevel serverLevel){
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.ENCHANT, tileEntity.getConvertEntity);
                }
                tileEntity.getConvertEntity.tickCount -= 9;
            }
        }

        int progress = totalTime - time;

        if (world.getGameTime() % MathHelper.secondsToTicks(4) == 0 && MathHelper.secondsToTicks(4) > progress) {
            world.playSound(null, darkAltarPos, ModSounds.ALTAR_LOOP.get(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
        }

        List<PedestalBlockEntity> pedestals = this.getPedestals(world, darkAltarPos);
        for (PedestalBlockEntity pedestal : pedestals) {
            pedestal.itemStackHandler.map(handler -> {
                ItemStack stack = handler.extractItem(0, 1, true);
                if (!stack.isEmpty()) {
                    addItemParticles((ServerLevel) world, pedestal.getBlockPos(), darkAltarPos, stack);
                }
                return true;
            });
        }

    }

    public void update(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem, int time, int totalTime) {
        this.update(world, darkAltarPos, tileEntity, castingPlayer, activationItem, new ArrayList<Ingredient>(),
                time, totalTime);
    }

    public boolean identify(Level world, BlockPos darkAltarPos, Player player, ItemStack activationItem) {
        return this.recipe.getActivationItem().test(activationItem) &&
                this.areAdditionalIngredientsFulfilled(world, darkAltarPos, player, this.recipe.getIngredients());
    }

    public boolean consumeAdditionalIngredients(Level world, BlockPos darkAltarPos, Player player,
                                                List<Ingredient> remainingAdditionalIngredients, int time,
                                                List<ItemStack> consumedIngredients) {
        if (remainingAdditionalIngredients.isEmpty())
            return true;

        int totalIngredientsToConsume = (int) Math.floor(time / this.recipe.getDurationPerIngredient());
        int ingredientsConsumed = consumedIngredients.size();

        int ingredientsToConsume = totalIngredientsToConsume - ingredientsConsumed;
        if (ingredientsToConsume == 0)
            return true;

        List<PedestalBlockEntity> pedestals = this.getPedestals(world, darkAltarPos);
        int consumed = 0;
        for (Iterator<Ingredient> it = remainingAdditionalIngredients.iterator();
             it.hasNext() && consumed < ingredientsToConsume; consumed++) {
            Ingredient ingredient = it.next();
            if (this.consumeAdditionalIngredient(world, darkAltarPos, pedestals, ingredient,
                    consumedIngredients)) {
                it.remove();
            } else {
                player.displayClientMessage(Component.translatable("info.goety.ritual.cannotConsume.fail"), true);
                return false;
            }
        }
        return true;
    }

    public boolean consumeAdditionalIngredient(Level world, BlockPos darkAltarPos,
                                               List<PedestalBlockEntity> pedestals,
                                               Ingredient ingredient, List<ItemStack> consumedIngredients) {
        for (PedestalBlockEntity pedestal : pedestals) {
            if (pedestal.itemStackHandler.map(handler -> {
                ItemStack stack = handler.extractItem(0, 1, true);
                if (ingredient.test(stack)) {
                    ItemStack extracted = handler.extractItem(0, 1, false);

                    consumedIngredients.add(extracted);

                    if (extracted.getItem() instanceof BucketItem bucketItem && !bucketItem.getFluid().defaultFluidState().isEmpty()){
                        ItemHelper.addItemEntity(world, pedestal.getBlockPos().above(), new ItemStack(Items.BUCKET));
                        world.playSound(null, pedestal.getBlockPos(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS,
                                0.7F, 0.7F);
                    } else if (extracted.hasCraftingRemainingItem()){
                        ItemHelper.addItemEntity(world, pedestal.getBlockPos().above(), extracted.getCraftingRemainingItem());
                    }

                    handler.setStackInSlot(0, ItemStack.EMPTY);

                    world.playSound(null, pedestal.getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS,
                            0.7F, 0.7F);
                    return true;
                }
                return false;
            }).orElse(false))
                return true;

        }
        return false;
    }

    private void addItemParticles(ServerLevel world, BlockPos pedestalPos, BlockPos darkAltarPos, ItemStack stack) {
        double d0 = 0.1D * (darkAltarPos.getX() - pedestalPos.getX());
        double d1 = 0.3D;
        double d2 = 0.1D * (darkAltarPos.getZ() - pedestalPos.getZ());
        world.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), pedestalPos.getX() + 0.5D, pedestalPos.getY() + 1.5D, pedestalPos.getZ() + 0.5D, 0, d0, d1, d2, 1.0D);
    }

    public boolean areAdditionalIngredientsFulfilled(Level world, BlockPos darkAltarPos,
                                                     Player player, List<Ingredient> additionalIngredients) {
        return this.matchesAdditionalIngredients(player, additionalIngredients,
                this.getItemsOnPedestals(world, darkAltarPos));
    }

    public boolean matchesAdditionalIngredients(Player player, List<Ingredient> additionalIngredients, List<ItemStack> items) {

        if (additionalIngredients.size() != items.size()) {
            return false;
        }

        if (additionalIngredients.isEmpty()) {
            return true;
        }

        List<ItemStack> remainingItems = new ArrayList<>(items);

        for (Ingredient ingredient : additionalIngredients) {
            boolean isMatched = false;
            for (int i = 0; i < remainingItems.size(); i++) {
                ItemStack stack = remainingItems.get(i);
                if (ingredient.test(stack)) {
                    isMatched = true;
                    remainingItems.remove(i);
                    break;
                }
            }
            if (!isMatched) {
                return false;
            }
        }

        return true;
    }

    public List<ItemStack> getItemsOnPedestals(Level world, BlockPos darkAltarPos, List<Ingredient> additionalIngredients) {
        List<ItemStack> result = new ArrayList<>();

        List<PedestalBlockEntity> pedestals = this.getPedestals(world, darkAltarPos);
        for (PedestalBlockEntity pedestalTile : pedestals) {
            pedestalTile.itemStackHandler.ifPresent(handler -> {
                ItemStack stack = handler.getStackInSlot(0);
                for (Ingredient ingredient : additionalIngredients) {
                    if (ingredient.test(stack) && !result.contains(stack)) {
                        result.add(stack);
                    }
                }
            });
        }

        return result;
    }

    public List<ItemStack> getItemsOnPedestals(Level world, BlockPos darkAltarPos) {
        List<ItemStack> result = new ArrayList<>();

        List<PedestalBlockEntity> pedestals = this.getPedestals(world, darkAltarPos);
        for (PedestalBlockEntity pedestalTile : pedestals) {
            pedestalTile.itemStackHandler.ifPresent(handler -> {
                ItemStack stack = handler.getStackInSlot(0);
                if (!stack.isEmpty()) {
                    result.add(stack);
                }
            });
        }

        return result;
    }

    public List<PedestalBlockEntity> getPedestals(Level world, BlockPos darkAltarPos) {
        List<PedestalBlockEntity> result = new ArrayList<>();
        Iterable<BlockPos> blocksToCheck = BlockPos.betweenClosed(
                darkAltarPos.offset(-PEDESTAL_RANGE, -PEDESTAL_RANGE, -PEDESTAL_RANGE),
                darkAltarPos.offset(PEDESTAL_RANGE, PEDESTAL_RANGE, PEDESTAL_RANGE));
        for (BlockPos blockToCheck : blocksToCheck) {
            BlockEntity tileEntity = world.getBlockEntity(blockToCheck);
            if (tileEntity instanceof PedestalBlockEntity &&
                    !(tileEntity instanceof DarkAltarBlockEntity)) {
                result.add((PedestalBlockEntity) tileEntity);
            }
        }
        return result;
    }

    public void prepareLivingEntityForSpawn(LivingEntity livingEntity, Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                                            Player castingPlayer, boolean setTamed) {
        if (setTamed){
            MobUtil.summonTame(livingEntity, castingPlayer);
        }

        if (livingEntity instanceof Mob mob) {
            mob.absMoveTo(darkAltarPos.getX() + 0.5F, darkAltarPos.getY() + 1.0F, darkAltarPos.getZ() + 0.5F,
                    world.random.nextInt(360), 0);
            mob.finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(darkAltarPos),
                    MobSpawnType.MOB_SUMMONED, null,
                    null);
        } else {
            livingEntity.setPos(darkAltarPos.getX() + 0.5F, darkAltarPos.getY() + 1, darkAltarPos.getZ() + 0.5F);
        }
    }

    public boolean isValidSacrifice(LivingEntity entity) {
        return entity != null && this.recipe.requiresSacrifice() && entity.getType().is(this.recipe.getEntityToSacrifice());
    }

    public boolean requiresSacrifice() {
        return this.recipe.requiresSacrifice();
    }

    public void dropResult(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                           Player castingPlayer, ItemStack stack) {
        ItemEntity entity = new ItemEntity(world, darkAltarPos.getX(), darkAltarPos.getY() + 1.0F,
                darkAltarPos.getZ(), stack);
        entity.setPickUpDelay(10);
        world.addFreshEntity(entity);
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