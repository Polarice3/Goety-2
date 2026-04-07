package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.api.entities.IMobCrafter;
import com.Polarice3.Goety.common.entities.ai.IllagerChestGoal;
import com.Polarice3.Goety.common.entities.ai.MobCraftingGoal;
import com.Polarice3.Goety.common.entities.ai.MobFurnaceGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VindicatorChefServant extends VindicatorServant implements IMobCrafter {
    protected static final EntityDataAccessor<Optional<BlockPos>> FURNACE_POS = SynchedEntityData.defineId(VindicatorChefServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<Optional<BlockPos>> CRAFT_TABLE_POS = SynchedEntityData.defineId(VindicatorChefServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<Boolean> COOKING = SynchedEntityData.defineId(VindicatorChefServant.class, EntityDataSerializers.BOOLEAN);

    public VindicatorChefServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CraftingGoal<>(this));
        this.goalSelector.addGoal(2, new CookingGoal<>(this));
    }

    public void throwGoal() {
        this.goalSelector.addGoal(3, new ChefThrowExcessFoodGoal(this));
        this.goalSelector.addGoal(3, new ThrowRottenFleshGoal(this));
        this.goalSelector.addGoal(4, new FeedRottenFleshGoal(this));
        this.goalSelector.addGoal(5, new ThrowLootGoal(this));
    }

    public void chestGoal() {
        super.chestGoal();
        this.goalSelector.addGoal(6, new LootUncookedFoodGoal<>(this));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FURNACE_POS, Optional.empty());
        this.entityData.define(CRAFT_TABLE_POS, Optional.empty());
        this.entityData.define(COOKING, false);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.saveCrafterData(compound);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.readCrafterData(compound);
    }

    @Override
    public void die(DamageSource pCause) {
        if (!this.isFurnaceActuallyCooking()) {
            this.setFurnaceLit(false);
        }
        super.die(pCause);
    }

    public Optional<BlockPos> getFurnacePos() {
        return this.entityData.get(FURNACE_POS);
    }

    public void setFurnacePos(@Nullable BlockPos trainPos) {
        this.entityData.set(FURNACE_POS, Optional.ofNullable(trainPos));
    }

    public Optional<BlockPos> getCraftTablePos() {
        return this.entityData.get(CRAFT_TABLE_POS);
    }

    public void setCraftTablePos(@Nullable BlockPos trainPos) {
        this.entityData.set(CRAFT_TABLE_POS, Optional.ofNullable(trainPos));
    }

    public void setUsingFurnace(boolean cooking) {
        this.entityData.set(COOKING, cooking);
    }

    public boolean isUsingFurnace() {
        return this.entityData.get(COOKING);
    }

    public boolean isFurnace(BlockState blockState) {
        return blockState.getBlock() instanceof FurnaceBlock || blockState.getBlock() instanceof SmokerBlock;
    }

    public void setCrafting(boolean crafting) {
        this.setUsingFurnace(crafting);
    }

    public boolean isCrafting() {
        return this.isUsingFurnace();
    }

    public boolean isCraftTable(BlockState blockState) {
        return blockState.getBlock() instanceof CraftingTableBlock || blockState.is(ModTags.Blocks.CHEF_WORK_TABLES);
    }

    public IllagerServantArmPose getArmPose() {
        if (this.isAggressive() || this.isUsingFurnace()) {
            return IllagerServantArmPose.ATTACKING;
        } else {
            return this.isCelebrating() ? IllagerServantArmPose.CELEBRATING : IllagerServantArmPose.CROSSED;
        }
    }

    public boolean validFood(ItemStack itemStack) {
        FoodProperties foodProperties = itemStack.getFoodProperties(this);
        if (foodProperties == null) {
            return false;
        } else {
            return foodProperties.getEffects().isEmpty() || foodProperties.isMeat() || itemStack.is(Items.ROTTEN_FLESH);
        }
    }

    @Override
    public boolean canEat(ItemStack itemStack) {
        FoodProperties foodProperties = itemStack.getFoodProperties(this);
        if (foodProperties == null) {
            return false;
        } else if (foodProperties.isMeat() && foodProperties.getNutrition() <= 3) {
            return false;
        }
        return super.canEat(itemStack);
    }

    public static ItemStack canCook(ItemStack stack, ServerLevel level) {
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level)
                .map(smeltingRecipe -> smeltingRecipe.getResultItem(level.registryAccess()))
                .filter(itemStack -> !itemStack.isEmpty())
                .orElse(ItemStack.EMPTY);
    }

    public static boolean validCook(ItemStack stack, ServerLevel level) {
        return validCraft(canCook(stack, level));
    }

    public boolean validLootToStore(ItemStack itemStack) {
        boolean flag = super.validLootToStore(itemStack);
        if (this.level instanceof ServerLevel serverLevel) {
            return flag && !isCraftingIngredient(itemStack, serverLevel);
        }
        return flag;
    }

    @Nullable
    public static CraftingRecipe findSatisfiableRecipe(ItemStack stack, ServerLevel level, Container inventory) {
        List<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        return recipes.stream()
                .filter(recipe -> {
                    ItemStack result = recipe.getResultItem(level.registryAccess());
                    if (result.isEmpty() || !validCraft(result)) {
                        return false;
                    }

                    if (recipe.getIngredients().stream().noneMatch(ing -> ing.test(stack))) {
                        return false;
                    }

                    Map<Item, Integer> required = new HashMap<>();
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (ingredient.isEmpty()) {
                            continue;
                        }
                        boolean matched = false;
                        for (int i = 0; i < inventory.getContainerSize(); i++) {
                            ItemStack slot = inventory.getItem(i);
                            if (!slot.isEmpty() && ingredient.test(slot)) {
                                required.merge(slot.getItem(), 1, Integer::sum);
                                matched = true;
                                break;
                            }
                        }
                        if (!matched) {
                            return false;
                        }
                    }

                    for (Map.Entry<Item, Integer> entry : required.entrySet()) {
                        int have = 0;
                        for (int i = 0; i < inventory.getContainerSize(); i++) {
                            ItemStack slot = inventory.getItem(i);
                            if (slot.getItem() == entry.getKey()) {
                                have += slot.getCount();
                            }
                        }
                        if (have < entry.getValue()) {
                            return false;
                        }
                    }

                    return true;
                })
                .findFirst()
                .orElse(null);
    }

    public static ItemStack canCraft(ItemStack stack, ServerLevel level, Container inventory) {
        CraftingRecipe recipe = findSatisfiableRecipe(stack, level, inventory);
        return recipe != null ? recipe.getResultItem(level.registryAccess()).copy() : ItemStack.EMPTY;
    }

    public static int craftIngredientCount(ItemStack stack, ServerLevel level, SimpleContainer inventory) {
        CraftingRecipe recipe = findSatisfiableRecipe(stack, level, inventory);
        if (recipe == null) {
            return 0;
        }
        return (int) recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.test(stack))
                .count();
    }

    public static boolean isCraftingIngredient(ItemStack stack, ServerLevel level) {
        List<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        if (stack.isEmpty()) {
            return false;
        }
        return recipes.stream().anyMatch(recipe -> {
            ItemStack result = recipe.getResultItem(level.registryAccess());
            return validCraft(result) && recipe.getIngredients().stream()
                    .anyMatch(ingredient -> ingredient.test(stack));
        });
    }

    public static boolean validCraft(ItemStack itemStack) {
        return (itemStack.isEdible() || itemStack.is(ModTags.Items.CHEF_CAN_COOK)) && !itemStack.is(ModTags.Items.CHEF_CANNOT_COOK);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (this.countFoodPointsInInventory() != 0) {
                if (pHand == InteractionHand.MAIN_HAND && pPlayer.getMainHandItem().isEmpty() && (pPlayer.isShiftKeyDown() || pPlayer.isCrouching())) {
                    Optional<ItemStack> optional = this.itemsInInv(this::canEat).stream().findFirst();
                    if (optional.isPresent()) {
                        pPlayer.setItemInHand(pHand, optional.get().copyAndClear());
                        this.getInventory().setChanged();
                        if (this.getAmbientSound() != null) {
                            this.playSound(this.getAmbientSound(), 1.0F, 1.25F);
                        }
                        this.level.playSound(pPlayer, pPlayer, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public static class CookingGoal<T extends VindicatorChefServant> extends MobFurnaceGoal<T> {

        public CookingGoal(T illager){
            super(illager, 100, 0.75F);
        }

        @Override
        public boolean canStartFurnaceUsing() {
            if (this.mob.level instanceof ServerLevel serverLevel) {
                return !this.mob.itemsInInv(itemStack -> validCook(itemStack, serverLevel)).isEmpty();
            }
            return false;
        }

        @Override
        public void onSmelt(ServerLevel serverLevel) {
            Optional<ItemStack> optional = this.mob.itemsInInv(itemStack -> validCook(itemStack, serverLevel)).stream().findFirst();
            if (optional.isPresent()){
                ItemStack itemStack = smelt(optional.get().split(1), serverLevel);
                if (this.mob.getInventory().canAddItem(itemStack)) {
                    this.mob.getInventory().addItem(itemStack);
                }
            }
        }

        public static ItemStack smelt(ItemStack stack, ServerLevel level) {
            return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level)
                    .map(smeltingRecipe -> smeltingRecipe.getResultItem(level.registryAccess()))
                    .filter(itemStack -> !itemStack.isEmpty())
                    .map(itemStack -> {
                        ItemStack copy = itemStack.copy();
                        copy.setCount(stack.getCount() * itemStack.getCount());
                        return copy;
                    })
                    .orElse(stack);
        }
    }

    public static class CraftingGoal<T extends VindicatorChefServant> extends MobCraftingGoal<T> {

        public CraftingGoal(T mob) {
            super(mob, 60, 0.75F);
        }

        public boolean canStartCrafting() {
            if (this.mob.level instanceof ServerLevel serverLevel) {
                return this.findCraftableStack(serverLevel) != null;
            }
            return false;
        }

        @Nullable
        public ItemStack findCraftableStack(ServerLevel level) {
            SimpleContainer inv = this.mob.getInventory();
            return this.mob.itemsInInv(stack -> {
                ItemStack result = canCraft(stack, level, inv);
                if (result.isEmpty()) {
                    return false;
                }
                int needed = craftIngredientCount(stack, level, inv);
                return inv.countItem(stack.getItem()) >= needed;
            }).stream().findFirst().orElse(null);
        }

        @Override
        public void onCraft(ServerLevel serverLevel) {
            ItemStack source = this.findCraftableStack(serverLevel);
            if (source != null) {
                SimpleContainer inv = this.mob.getInventory();
                CraftingRecipe recipe = findSatisfiableRecipe(source, serverLevel, inv);
                if (recipe != null) {
                    ItemStack result = recipe.getResultItem(serverLevel.registryAccess()).copy();

                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (ingredient.isEmpty()) {
                            continue;
                        }
                        for (int i = 0; i < inv.getContainerSize(); i++) {
                            ItemStack slot = inv.getItem(i);
                            if (!slot.isEmpty() && ingredient.test(slot)) {
                                ItemStack remainder = slot.getCraftingRemainingItem() != null
                                        ? slot.getCraftingRemainingItem()
                                        : ItemStack.EMPTY;
                                slot.shrink(1);
                                if (slot.isEmpty()) {
                                    inv.setItem(i, remainder);
                                } else if (!remainder.isEmpty()) {
                                    if (inv.canAddItem(remainder)) {
                                        inv.addItem(remainder);
                                    }
                                }
                                break;
                            }
                        }
                    }
                    inv.setChanged();

                    if (inv.canAddItem(result)) {
                        inv.addItem(result);
                        serverLevel.playSound(null, this.mob.blockPosition(), SoundEvents.UI_STONECUTTER_TAKE_RESULT, this.mob.getSoundSource(), 0.5F, 1.0F);
                    }
                }
            }
        }
    }

    public static class ChefThrowExcessFoodGoal extends GiveExcessFoodGoal {

        public ChefThrowExcessFoodGoal(AbstractIllagerServant illager){
            super(illager);
            this.predicate = itemStack ->
                    illager.level instanceof ServerLevel serverLevel
                            && !validCook(itemStack, serverLevel) && !isCraftingIngredient(itemStack, serverLevel);
        }
    }

    public static class LootUncookedFoodGoal<T extends AbstractIllagerServant> extends IllagerChestGoal<T> {

        public LootUncookedFoodGoal(T illager) {
            super(illager);
            this.chestPredicate = itemStack ->
                    illager.level instanceof ServerLevel serverLevel
                            && (validCook(itemStack, serverLevel) || isCraftingIngredient(itemStack, serverLevel));
        }

        public boolean hasItemInInv() {
            return true;
        }

        @Override
        public boolean canUse() {
            if (this.illager.getChestPos() == null) {
                return false;
            }
            if (this.illager.getBoundPos() != null){
                if (this.illager.getChestPos() != null){
                    if (!this.illager.isWithinGuard(this.illager.getChestPos())){
                        return false;
                    }
                }
            }
            if (this.illager.getChestLevel() != this.illager.level.dimension()) {
                return false;
            }
            if (!this.isChestRaidable(this.illager.level, this.illager.getChestPos())){
                return false;
            }
            if (this.illager.itemsInInv(itemStack ->
                    this.illager.level instanceof ServerLevel serverLevel
                            && (validCook(itemStack, serverLevel) || isCraftingIngredient(itemStack, serverLevel))).size() >= 24) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public void chestInteract(Container container) {
            for (ItemStack itemStack : this.getItems(container)) {
                if (itemStack.isEmpty()) {
                    continue;
                }
                if (!this.illager.getInventory().canAddItem(itemStack)) {
                    break;
                }
                this.illager.getInventory().addItem(itemStack.copyAndClear());
                container.setChanged();
            }
        }
    }
}
