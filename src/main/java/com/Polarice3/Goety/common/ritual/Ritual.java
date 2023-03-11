package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.PedestalBlockEntity;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Ritual Codes based from @klikli-dev
 */
public abstract class Ritual {

    public static final int PEDESTAL_RANGE = 8;

    public static final int SACRIFICE_DETECTION_RANGE = 8;

    public static final int ITEM_USE_DETECTION_RANGE = 16;

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
                this.areAdditionalIngredientsFulfilled(world, darkAltarPos, remainingAdditionalIngredients);
    }

    public void start(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                      Player castingPlayer, ItemStack activationItem) {
        world.playSound(null, darkAltarPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1, 1);
    }

    public void finish(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
/*        if (activationItem.getItem() == ModItems.FILLED_ILL_CAGE.get()){
            world.playSound(null, darkAltarPos, SoundEvents.VILLAGER_DEATH, SoundSource.BLOCKS, 1.2F,
                    0.5f);
            world.playSound(null, darkAltarPos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS, 1.5F,
                    1.0f);
        }*/
        world.playSound(null, darkAltarPos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.7f,
                0.7f);
    }

    public void interrupt(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                          Player castingPlayer, ItemStack activationItem) {
        world.playSound(null, darkAltarPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.7f, 0.7f);
    }

    public void update(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem,
                       List<Ingredient> remainingAdditionalIngredients, int time) {
    }

    public void update(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem, int time) {
        this.update(world, darkAltarPos, tileEntity, castingPlayer, activationItem, new ArrayList<Ingredient>(),
                time);
    }

    public boolean identify(Level world, BlockPos darkAltarPos, ItemStack activationItem) {
        return this.recipe.getActivationItem().test(activationItem) &&
                this.areAdditionalIngredientsFulfilled(world, darkAltarPos, this.recipe.getIngredients());
    }

    public boolean consumeAdditionalIngredients(Level world, BlockPos darkAltarPos,
                                                List<Ingredient> remainingAdditionalIngredients, int time,
                                                List<ItemStack> consumedIngredients, boolean villager) {
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
                    consumedIngredients, villager)) {
                it.remove();
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean consumeAdditionalIngredient(Level world, BlockPos darkAltarPos,
                                               List<PedestalBlockEntity> pedestals,
                                               Ingredient ingredient, List<ItemStack> consumedIngredients, boolean villager) {
        for (PedestalBlockEntity pedestal : pedestals) {
            if (pedestal.itemStackHandler.map(handler -> {
                ItemStack stack = handler.extractItem(0, 1, true);
                if (ingredient.test(stack)) {
                    ItemStack extracted = handler.extractItem(0, 1, false);

                    consumedIngredients.add(extracted);
                    ((ServerLevel) world).sendParticles(ParticleTypes.LARGE_SMOKE, pedestal.getBlockPos().getX() + 0.5D, pedestal.getBlockPos().getY() + 1.5D, pedestal.getBlockPos().getZ() + 0.5D, 1, 0, 0, 0, 0);

                    if (villager){
                        world.playSound(null, darkAltarPos, SoundEvents.VILLAGER_HURT, SoundSource.BLOCKS, 1.2F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
                    }
                    world.playSound(null, pedestal.getBlockPos(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS,
                            0.7f, 0.7f);
                    return true;
                }
                return false;
            }).orElse(false))
                return true;

        }
        return false;
    }

    public boolean areAdditionalIngredientsFulfilled(Level world, BlockPos darkAltarPos,
                                                     List<Ingredient> additionalIngredients) {
        return this.matchesAdditionalIngredients(additionalIngredients,
                this.getItemsOnPedestals(world, darkAltarPos));
    }

    public boolean matchesAdditionalIngredients(List<Ingredient> additionalIngredients, List<ItemStack> items) {

        if (additionalIngredients.size() != items.size())
            return false;

        if (additionalIngredients.isEmpty())
            return true;

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
            if (!isMatched)
                return false;
        }

        return true;
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
        if (setTamed && livingEntity instanceof TamableAnimal) {
            ((TamableAnimal) livingEntity).tame(castingPlayer);
        }
        if (setTamed && livingEntity instanceof Owned summonedEntity) {
            summonedEntity.setPersistenceRequired();
            summonedEntity.setOwnerId(castingPlayer.getUUID());
            if (summonedEntity instanceof Summoned){
                ((Summoned) summonedEntity).setWandering(false);
            }
        }
/*        if (livingEntity instanceof ShadeEntity){
            ((ShadeEntity) livingEntity).setBoundOrigin(darkAltarPos);
        }*/
        livingEntity.absMoveTo(darkAltarPos.getX(), darkAltarPos.getY(), darkAltarPos.getZ(),
                world.random.nextInt(360), 0);
        if (livingEntity instanceof Mob)
            ((Mob) livingEntity).finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(darkAltarPos),
                    MobSpawnType.MOB_SUMMONED, null,
                    null);
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