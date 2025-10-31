package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ai.IllagerChestGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class VindicatorChefServant extends VindicatorServant{
    protected static final EntityDataAccessor<Optional<BlockPos>> FURNACE_POS = SynchedEntityData.defineId(VindicatorChefServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<Boolean> COOKING = SynchedEntityData.defineId(VindicatorChefServant.class, EntityDataSerializers.BOOLEAN);

    public VindicatorChefServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new CookingGoal(this));
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
        this.entityData.define(COOKING, false);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getFurnacePos().isPresent()) {
            compound.put("FurnacePos", NbtUtils.writeBlockPos(this.getFurnacePos().get()));
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("FurnacePos")){
            this.setFurnacePos(NbtUtils.readBlockPos(compound.getCompound("FurnacePos")));
        }
    }

    public Optional<BlockPos> getFurnacePos() {
        return this.entityData.get(FURNACE_POS);
    }

    public void setFurnacePos(@Nullable BlockPos trainPos) {
        this.entityData.set(FURNACE_POS, Optional.ofNullable(trainPos));
    }

    public void setCooking(boolean cooking) {
        this.entityData.set(COOKING, cooking);
    }

    public boolean isCooking() {
        return this.entityData.get(COOKING);
    }

    public IllagerServantArmPose getArmPose() {
        if (this.isAggressive() || this.isCooking()) {
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

    public static class CookingGoal extends Goal {
        public static int COOK_TIME = 100;
        public VindicatorChefServant illager;
        protected int tryTicks;
        @Nullable
        public BlockPos furnace;
        public int workTick;
        public boolean playSound = false;

        public CookingGoal(VindicatorChefServant illager){
            this.illager = illager;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.illager.level instanceof ServerLevel serverLevel) {
                if (!this.illager.itemsInInv(itemStack -> canCook(itemStack, serverLevel).isEdible()).isEmpty()) {
                    this.furnace = this.findFurnace();
                    if (this.furnace != null) {
                        this.illager.setFurnacePos(this.furnace);
                    }
                    return this.furnace != null && this.illager.getTarget() == null;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.illager.level instanceof ServerLevel serverLevel) {
                if (!this.illager.itemsInInv(itemStack -> canCook(itemStack, serverLevel).isEdible()).isEmpty()) {
                    if (this.furnace != null) {
                        if (this.getNearbyChefs(serverLevel, new AABB(this.furnace).inflate(4.0D), this.furnace).isEmpty()) {
                            if (this.tryTicks <= 1200) {
                                BlockState blockState = this.illager.level.getBlockState(this.furnace);
                                return (blockState.getBlock() instanceof FurnaceBlock || blockState.getBlock() instanceof SmokerBlock) && super.canContinueToUse();
                            }
                        }
                    }
                }
            }
            return false;
        }

        public void start() {
            this.moveMobToBlock();
            this.tryTicks = 0;
        }

        protected void moveMobToBlock() {
            if (this.furnace == null) {
                this.stop();
                return;
            }
            this.illager.getNavigation().moveTo((double)((float)this.furnace.getX()) + 0.5D, (double)(this.furnace.getY() + 1), (double)((float)this.furnace.getZ()) + 0.5D, 0.75F);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            this.furnace = null;
            this.illager.setFurnacePos(null);
            this.tryTicks = 0;
            this.workTick = 0;
            this.illager.setCooking(false);
        }

        public void tick(){
            if (this.furnace == null) {
                this.stop();
                return;
            }
            if (this.illager.level instanceof ServerLevel serverLevel) {
                if (this.illager.distanceToSqr(Vec3.atCenterOf(this.furnace)) > Mth.square(2)) {
                    ++this.tryTicks;
                    if (this.shouldRecalculatePath()) {
                        this.illager.getNavigation().moveTo((double)((float)this.furnace.getX()) + 0.5D, (double)this.furnace.getY(), (double)((float)this.furnace.getZ()) + 0.5D, 0.75F);
                    }
                    if (this.illager.isCooking()) {
                        this.illager.setCooking(false);
                    }
                } else {
                    this.illager.getNavigation().stop();
                    this.illager.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(this.furnace));
                    this.tryTicks = 0;
                    ++this.workTick;
                    if (!this.illager.isCooking()) {
                        this.illager.setCooking(true);
                    }
                    if (!this.playSound) {
                        try {
                            BlockState blockState = this.illager.level.getBlockState(this.furnace);
                            if (blockState.getBlock() instanceof FurnaceBlock) {
                                this.illager.level.playSound(null, this.furnace.getX() + 0.5F, this.furnace.getY(), this.furnace.getZ() + 0.5F, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F);
                            } else if (blockState.getBlock() instanceof SmokerBlock) {
                                this.illager.level.playSound(null, this.furnace.getX() + 0.5F, this.furnace.getY(), this.furnace.getZ() + 0.5F, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F);
                            }
                        } catch (NullPointerException exception) {
                            this.stop();
                            return;
                        }
                        this.playSound = true;
                    }
                    if (this.workTick > COOK_TIME) {
                        Optional<ItemStack> optional = this.illager.itemsInInv(itemStack -> canCook(itemStack, serverLevel).isEdible()).stream().findFirst();
                        if (optional.isPresent()){
                            ItemStack itemStack = smelt(optional.get().split(1), serverLevel);
                            if (this.illager.getInventory().canAddItem(itemStack)) {
                                this.illager.getInventory().addItem(itemStack);
                            }
                        }
                        this.playSound = false;
                        this.workTick = 0;
                    }
                }
            }

        }

        public boolean shouldRecalculatePath() {
            return this.tryTicks % 40 == 0;
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

        public List<VindicatorChefServant> getNearbyChefs(Level level, AABB aabb, BlockPos blockPos) {
            return level.getEntitiesOfClass(VindicatorChefServant.class, aabb, chef -> chef.getFurnacePos().isPresent()
                    && BlockFinder.samePos(chef.getFurnacePos().get(), blockPos)
                    && chef.isCooking()
                    && chef != this.illager);
        }

        public BlockPos findFurnace(){
            int i = 8;
            int j = 1;
            for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                for(int l = 0; l < i; ++l) {
                    for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                        for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                            BlockPos blockPos1 = this.illager.blockPosition().offset(i1, k - 1, j1);
                            BlockState blockState = this.illager.level.getBlockState(blockPos1);
                            if (blockState.getBlock() instanceof FurnaceBlock || blockState.getBlock() instanceof SmokerBlock){
                                if (this.getNearbyChefs(this.illager.level, new AABB(blockPos1).inflate(4.0D), blockPos1).isEmpty()) {
                                    return blockPos1;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    public static class ChefThrowExcessFoodGoal extends GiveExcessFoodGoal {

        public ChefThrowExcessFoodGoal(AbstractIllagerServant illager){
            super(illager);
            this.predicate = itemStack ->
                    illager.level instanceof ServerLevel serverLevel
                            && !canCook(itemStack, serverLevel).isEdible();
        }
    }

    public static class LootUncookedFoodGoal<T extends AbstractIllagerServant> extends IllagerChestGoal<T> {

        public LootUncookedFoodGoal(T illager) {
            super(illager);
            this.chestPredicate = itemStack ->
                    illager.level instanceof ServerLevel serverLevel
                    && canCook(itemStack, serverLevel).isEdible();
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
                            && canCook(itemStack, serverLevel).isEdible()).size() >= 24){
                return false;
            }
            return super.canUse();
        }

        @Override
        public void chestInteract(Container container) {
            for (ItemStack itemStack : this.getItems(container)) {
                if (this.illager.getInventory().canAddItem(itemStack)){
                    this.illager.getInventory().addItem(itemStack.copyAndClear());
                    container.setChanged();
                    break;
                }
            }
        }
    }
}
