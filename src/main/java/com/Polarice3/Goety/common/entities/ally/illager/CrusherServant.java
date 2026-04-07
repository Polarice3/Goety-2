package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.api.entities.IMobCrafter;
import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.blocks.DarkAnvilBlock;
import com.Polarice3.Goety.common.entities.ai.IllagerChestGoal;
import com.Polarice3.Goety.common.entities.ai.MobCraftingGoal;
import com.Polarice3.Goety.common.entities.ai.MobFurnaceGoal;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningBoltPacket;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class CrusherServant extends AbstractIllagerServant implements IMobCrafter {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(CrusherServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(CrusherServant.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> STORM = SynchedEntityData.defineId(CrusherServant.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<BlockPos>> FURNACE_POS = SynchedEntityData.defineId(CrusherServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<Boolean> SMELTING = SynchedEntityData.defineId(CrusherServant.class, EntityDataSerializers.BOOLEAN);
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public int attackTick;
    public boolean isRunning = false;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public CrusherServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeGoal());
        this.goalSelector.addGoal(1, new ForgeArmorGoal<>(this));
        this.goalSelector.addGoal(2, new ForgeWeaponGoal<>(this));
        this.goalSelector.addGoal(3, new SmeltingGoal<>(this));
        this.goalSelector.addGoal(4, new EquipArmorGoal(this));
        this.goalSelector.addGoal(5, new EquipWeaponGoal(this));
        this.goalSelector.addGoal(6, new AttackGoal(1.0D));
    }

    public void chestGoal() {
        super.chestGoal();
        this.goalSelector.addGoal(6, new LootOreGoal<>(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CrusherHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.CrusherArmor.get())
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CrusherDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CrusherHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.CrusherArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.CrusherDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(STORM, false);
        this.entityData.define(FURNACE_POS, Optional.empty());
        this.entityData.define(SMELTING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Storm", this.isStorm());
        this.saveCrafterData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Storm")){
            this.setStorm(pCompound.getBoolean("Storm"));
        }
        this.readCrafterData(pCompound);
    }

    @Override
    public void die(DamageSource pCause) {
        if (!this.isFurnaceActuallyCooking()) {
            this.setFurnaceLit(false);
        }
        super.die(pCause);
    }

    @Override
    public boolean canHaveWeapon() {
        return false;
    }

    public Optional<BlockPos> getFurnacePos() {
        return this.entityData.get(FURNACE_POS);
    }

    public void setFurnacePos(@Nullable BlockPos trainPos) {
        this.entityData.set(FURNACE_POS, Optional.ofNullable(trainPos));
    }

    public void setUsingFurnace(boolean cooking) {
        this.entityData.set(SMELTING, cooking);
    }

    public boolean isUsingFurnace() {
        return this.entityData.get(SMELTING);
    }

    @Override
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
        return blockState.getBlock() instanceof AnvilBlock || blockState.getBlock() instanceof DarkAnvilBlock;
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)){
            return 1;
        } else if (Objects.equals(animation, ATTACK)){
            return 2;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.attackAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAllAnimations()){
            animationState.stop();
        }
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                }
            }
        }
    }

    @Override
    public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
        if (!this.isStorm()) {
            this.setStorm(true);
        } else {
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600));
        }
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.isAlive()){
                if (this.getCurrentAnimation() != this.getAnimationState(ATTACK)) {
                    this.setAnimationState(IDLE);
                    this.isRunning = this.isAggressive();
                }
            }
        }
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
        }
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlag(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlag(1, attacking);
        this.attackTick = 0;
        this.level.broadcastEntityEvent(this, (byte) 5);
    }

    public boolean isStorm(){
        return this.entityData.get(STORM);
    }

    public void setStorm(boolean storm){
        this.entityData.set(STORM, storm);
    }

    @Override
    public float getVoicePitch() {
        return 0.75F;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.VINDICATOR_HURT;
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 5){
            this.attackTick = 0;
        } else if (p_21375_ == 6){
            this.setAggressive(true);
        } else if (p_21375_ == 7){
            this.setAggressive(false);
        } else if (p_21375_ == 8){
            this.setMeleeAttacking(true);
        } else if (p_21375_ == 9){
            this.setMeleeAttacking(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double)(this.getBbWidth() * 4.0F * this.getBbWidth() * 4.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr){
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach || this.getBoundingBox().intersects(enemy.getBoundingBox());
    }

    @Override
    public int xpReward() {
        if (this.isStorm()){
            return super.xpReward() * 3;
        }
        return super.xpReward();
    }

    public static ItemStack canSmelt(ItemStack stack, ServerLevel level) {
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level)
                .map(smeltingRecipe -> smeltingRecipe.getResultItem(level.registryAccess()))
                .filter(itemStack -> !itemStack.isEmpty())
                .orElse(ItemStack.EMPTY);
    }

    public static boolean canBeSmelted(ItemStack stack, ServerLevel level) {
        if (stack.is(ModTags.Items.CRUSHER_CANNOT_SMELT)) {
            return false;
        }
        return !canSmelt(stack, level).isEmpty() && (stack.is(Tags.Items.RAW_MATERIALS) || stack.is(Tags.Items.ORES) || stack.is(ModTags.Items.CRUSHER_CAN_SMELT));
    }

    public boolean validLootToStore(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ArmorItem) {
            return false;
        }
        if (validWeapon(itemStack)) {
            return false;
        }
        boolean flag = super.validLootToStore(itemStack);
        if (!itemStack.is(Tags.Items.RAW_MATERIALS) && !itemStack.is(Tags.Items.ORES)) {
            if (this.level instanceof ServerLevel serverLevel) {
                return flag && !isCraftingIngredient(itemStack, serverLevel);
            }
            return flag;
        }
        return false;
    }

    @Nullable
    public static CraftingRecipe findSatisfiableRecipe(ItemStack stack, ServerLevel level, Container inventory, @Nullable EquipmentSlot targetSlot) {
        List<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        return recipes.stream()
                .filter(recipe -> {
                    ItemStack result = recipe.getResultItem(level.registryAccess());
                    if (!validCraft(result)) {
                        return false;
                    }
                    if (targetSlot != null) {
                        if (!(result.getItem() instanceof ArmorItem armorItem)) {
                            return false;
                        }
                        if (armorItem.getEquipmentSlot() != targetSlot) {
                            return false;
                        }
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
                            ItemStack s = inventory.getItem(i);
                            if (s.getItem() == entry.getKey()) have += s.getCount();
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
        if (itemStack.is(ModTags.Items.CRUSHER_CANNOT_CRAFT)) {
            return false;
        }
        return validWeapon(itemStack) || itemStack.getItem() instanceof ArmorItem || itemStack.is(Tags.Items.ARMORS);
    }

    @Nullable
    public static CraftingRecipe findSatisfiableWeaponRecipe(ItemStack stack, ServerLevel level, Container inventory, @Nullable Predicate<ItemStack> resultFilter) {
        List<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        return recipes.stream()
                .filter(recipe -> {
                    ItemStack result = recipe.getResultItem(level.registryAccess());
                    if (!validWeapon(result)) {
                        return false;
                    }
                    if (resultFilter != null && !resultFilter.test(result)) {
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
                            ItemStack s = inventory.getItem(i);
                            if (s.getItem() == entry.getKey()) {
                                have += s.getCount();
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

    public static boolean validWeapon(ItemStack itemStack) {
        if (itemStack.is(ModTags.Items.CRUSHER_CANNOT_CRAFT)) {
            return false;
        }
        return itemStack.getItem() instanceof TieredItem
                || itemStack.getItem() instanceof CrossbowItem
                || itemStack.is(Tags.Items.TOOLS_CROSSBOWS)
                || itemStack.is(ModTags.Items.PILLAGER_WEAPONS)
                || itemStack.is(ItemTags.AXES)
                || itemStack.is(ModTags.Items.VINDICATOR_WEAPONS)
                || itemStack.is(ModTags.Items.MOUNTAINEER_WEAPONS);
    }

    @Nullable
    public static Tier getItemTier(ItemStack stack) {
        if (stack.getItem() instanceof TieredItem tiered) {
            return tiered.getTier();
        }
        return null;
    }

    public static boolean isBetterTier(ItemStack currentWeapon, ItemStack newWeapon) {
        Tier current = getItemTier(currentWeapon);
        Tier next = getItemTier(newWeapon);
        if (current == null || next == null) {
            return false;
        }
        return next.getAttackDamageBonus() > current.getAttackDamageBonus();
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (pHand == InteractionHand.MAIN_HAND && pPlayer.getMainHandItem().isEmpty() && (pPlayer.isShiftKeyDown() || pPlayer.isCrouching())) {
                if (this.level instanceof ServerLevel serverLevel) {
                    List<ItemStack> craftOrTool = new ArrayList<>();
                    if (!this.itemsInInv(CrusherServant::validCraft).isEmpty()) {
                        craftOrTool = this.itemsInInv(CrusherServant::validCraft);
                    } else if (!this.itemsInInv(itemStack -> isCraftingIngredient(itemStack, serverLevel)).isEmpty()) {
                        craftOrTool = this.itemsInInv(itemStack -> isCraftingIngredient(itemStack, serverLevel));
                    }
                    if (!craftOrTool.isEmpty()) {
                        Optional<ItemStack> optional = craftOrTool.stream().findFirst();
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

    class AttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(double moveSpeed) {
            super(CrusherServant.this, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return CrusherServant.this.getTarget() != null
                    && CrusherServant.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            CrusherServant.this.setAggressive(true);
            CrusherServant.this.level.broadcastEntityEvent(CrusherServant.this, (byte) 6);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            CrusherServant.this.getNavigation().stop();
            CrusherServant.this.setAggressive(false);
            CrusherServant.this.level.broadcastEntityEvent(CrusherServant.this, (byte) 7);
        }

        @Override
        public void tick() {
            LivingEntity livingentity = CrusherServant.this.getTarget();
            if (livingentity == null) {
                return;
            }

            CrusherServant.this.getLookControl().setLookAt(livingentity, CrusherServant.this.getMaxHeadYRot(), CrusherServant.this.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                CrusherServant.this.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity, CrusherServant.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity enemy, double distToEnemySqr) {
            if (CrusherServant.this.targetClose(enemy, distToEnemySqr)) {
                if (!CrusherServant.this.isMeleeAttacking()) {
                    CrusherServant.this.setMeleeAttacking(true);
                    CrusherServant.this.level.broadcastEntityEvent(CrusherServant.this, (byte) 8);
                }
            }
        }

    }

    class MeleeGoal extends Goal {
        private float yRot;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return CrusherServant.this.getTarget() != null
                    && CrusherServant.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return CrusherServant.this.attackTick < MathHelper.secondsToTicks(1.3333F);
        }

        @Override
        public void start() {
            CrusherServant.this.setMeleeAttacking(true);
            CrusherServant.this.level.broadcastEntityEvent(CrusherServant.this, (byte) 8);
            if (CrusherServant.this.getTarget() != null){
                MobUtil.instaLook(CrusherServant.this, CrusherServant.this.getTarget());
            }
            this.yRot = CrusherServant.this.yBodyRot;
        }

        @Override
        public void stop() {
            CrusherServant.this.setAnimationState(IDLE);
            CrusherServant.this.setMeleeAttacking(false);
            CrusherServant.this.level.broadcastEntityEvent(CrusherServant.this, (byte) 9);
        }

        @Override
        public void tick() {
            CrusherServant.this.setYRot(this.yRot);
            CrusherServant.this.yBodyRot = this.yRot;
            CrusherServant.this.getNavigation().stop();
            if (CrusherServant.this.attackTick == 1) {
                CrusherServant.this.playSound(SoundEvents.VINDICATOR_AMBIENT, 1.0F, CrusherServant.this.isStorm() ? 0.75F : 1.25F);
                CrusherServant.this.setAnimationState(ATTACK);
            }
            if (CrusherServant.this.attackTick == 11){
                CrusherServant.this.playSound(ModSounds.HAMMER_SWING.get());
            }
            if (CrusherServant.this.attackTick == 13) {
                AABB aabb = MobUtil.makeAttackRange(CrusherServant.this.getX() + CrusherServant.this.getHorizontalLookAngle().x * 2,
                        CrusherServant.this.getY(),
                        CrusherServant.this.getZ() + CrusherServant.this.getHorizontalLookAngle().z * 2, 3, 3, 3);
                for (LivingEntity target : CrusherServant.this.level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != CrusherServant.this && !MobUtil.areAllies(CrusherServant.this, target)) {
                        this.hurtTarget(target);
                    }
                }
                CrusherServant.this.playSound(ModSounds.HAMMER_IMPACT.get());
                CrusherServant.this.playSound(ModSounds.DIRT_DEBRIS.get());
                if (CrusherServant.this.isStorm()){
                    CrusherServant.this.playSound(ModSounds.THUNDER_STRIKE_FAST.get());
                }
                if (CrusherServant.this.level instanceof ServerLevel serverLevel){
                    BlockPos blockPos = BlockPos.containing(CrusherServant.this.getX() + CrusherServant.this.getHorizontalLookAngle().x * 2, CrusherServant.this.getY() - 1.0F, CrusherServant.this.getZ() + CrusherServant.this.getHorizontalLookAngle().z * 2);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPos));
                    Vec3 vec3 = new Vec3(CrusherServant.this.getX() + CrusherServant.this.getHorizontalLookAngle().x * 2, CrusherServant.this.getY() + 0.25D, CrusherServant.this.getZ() + CrusherServant.this.getHorizontalLookAngle().z * 2);
                    for (int i = 0; i < 8; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option, vec3.x, vec3.y, vec3.z, 1.5F);
                    }
                    int color = serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col;
                    ColorUtil colorUtil = color == 0 ? ColorUtil.WHITE : new ColorUtil(color);
                    serverLevel.sendParticles(new SmashParticleOption(colorUtil, 3, 5), vec3.x, vec3.y, vec3.z, 1, 0, 0, 0, 0);
                }
            }
        }

        public void hurtTarget(Entity target) {
            CrusherServant.this.doHurtTarget(target);
            if (CrusherServant.this.level instanceof ServerLevel serverLevel) {
                if (CrusherServant.this.isStorm() && target instanceof LivingEntity livingEntity) {
                    BlockHitResult rayTraceResult = this.blockResult(serverLevel, CrusherServant.this, 16);
                    Optional<BlockPos> lightningRod = BlockFinder.findLightningRod(serverLevel, BlockPos.containing(rayTraceResult.getLocation()), 16);
                    if (lightningRod.isPresent()) {
                        BlockPos blockPos1 = lightningRod.get();
                        ModNetwork.sendToALL(new SLightningBoltPacket(new Vec3(blockPos1.getX(), blockPos1.getY() + 250, blockPos1.getZ()), new Vec3(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()), 10));
                        serverLevel.playSound(null, CrusherServant.this.getX(), CrusherServant.this.getY(), CrusherServant.this.getZ(), ModSounds.THUNDERBOLT.get(), CrusherServant.this.getSoundSource(), 1.0F, 1.0F);
                    } else {
                        Vec3 vec31 = new Vec3(livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() / 2, livingEntity.getZ());
                        ModNetwork.sendToALL(new SLightningBoltPacket(new Vec3(livingEntity.getX(), livingEntity.getY() + 250, livingEntity.getZ()), vec31, 10));
                        chain(livingEntity, CrusherServant.this);
                        serverLevel.playSound(null, CrusherServant.this.getX(), CrusherServant.this.getY(), CrusherServant.this.getZ(), ModSounds.THUNDERBOLT.get(), CrusherServant.this.getSoundSource(), 1.0F, 1.0F);
                    }
                }
            }
        }

        public void chain(LivingEntity pTarget, LivingEntity pAttacker) {
            double range = 6;
            Level level = pAttacker.level;
            float oDamage = (float) pAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE);

            List<Entity> harmed = new ArrayList<>();
            Predicate<Entity> selector = entity -> entity instanceof LivingEntity livingEntity && livingEntity != pAttacker && !harmed.contains(livingEntity) && MobUtil.canAttack(CrusherServant.this, livingEntity);
            LivingEntity prevTarget = pTarget;

            float damage = level.isThundering() ? oDamage : oDamage / 2.0F;

            for (int i = 0; i < 4; i++) {
                AABB aabb = new AABB(Vec3Util.subtract(prevTarget.position(), range), Vec3Util.add(prevTarget.position(), range));
                List<Entity> entities = level.getEntities(prevTarget, aabb, selector);
                if (!entities.isEmpty()) {
                    LivingEntity target = (LivingEntity) entities.get(level.getRandom().nextInt(entities.size()));
                    if (target.hurt(ModDamageSource.directShock(pAttacker), damage)) {
                        if (prevTarget != target) {
                            Vec3 vec3 = prevTarget.getEyePosition();
                            Vec3 vec31 = target.getEyePosition();
                            ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, 8));
                        }
                    }

                    harmed.add(target);
                    prevTarget = target;
                    damage--;
                }
            }
        }

        public BlockHitResult blockResult(Level worldIn, Entity entity, double range) {
            float f = entity.getXRot();
            float f1 = entity.getYRot();
            Vec3 vector3d = entity.getEyePosition(1.0F);
            float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
            float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
            float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
            float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
            float f6 = f3 * f4;
            float f7 = f2 * f4;
            Vec3 vector3d1 = vector3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
            return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class SmeltingGoal<T extends CrusherServant> extends MobFurnaceGoal<T> {

        public SmeltingGoal(T illager){
            super(illager, 100, 0.75F);
        }

        @Override
        public boolean canStartFurnaceUsing() {
            if (this.mob.level instanceof ServerLevel serverLevel) {
                return !this.mob.itemsInInv(itemStack -> canBeSmelted(itemStack, serverLevel)).isEmpty();
            }
            return false;
        }

        @Override
        public void onSmelt(ServerLevel serverLevel) {
            Optional<ItemStack> optional = this.mob.itemsInInv(itemStack -> canBeSmelted(itemStack, serverLevel)).stream().findFirst();
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

    public static class ForgeArmorGoal<T extends CrusherServant> extends MobCraftingGoal<T> {

        public ForgeArmorGoal(T mob) {
            super(mob, 60, 0.75F);
        }

        public boolean canStartCrafting() {
            if (this.mob.level instanceof ServerLevel serverLevel) {
                return this.findCraftableStack(serverLevel) != null;
            }
            return false;
        }

        private static EquipmentSlot[] armorSlots() {
            return new EquipmentSlot[]{
                    EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS, EquipmentSlot.FEET
            };
        }

        @Nullable
        public ItemStack findCraftableStack(ServerLevel level) {
            SimpleContainer inv = this.mob.getInventory();

            List<RaiderServant> allies = this.mob.level.getEntitiesOfClass(RaiderServant.class, this.mob.getBoundingBox().inflate(16), ally -> ally != this.mob && ally.getTrueOwner() == this.mob.getTrueOwner() && ally.canWearArmor());

            for (EquipmentSlot slot : armorSlots()) {
                boolean crusherNeeds = this.mob.getItemBySlot(slot).isEmpty() && this.mob.itemsInInv(s -> s.getItem() instanceof ArmorItem a && a.getEquipmentSlot() == slot).isEmpty();
                boolean allyNeeds = allies.stream().anyMatch(ally -> ally.getItemBySlot(slot).isEmpty() && this.mob.itemsInInv(s -> s.getItem() instanceof ArmorItem a && a.getEquipmentSlot() == slot).isEmpty());

                if (!crusherNeeds && !allyNeeds) {
                    continue;
                }

                for (ItemStack stack : this.mob.itemsInInv(s -> !s.isEmpty())) {
                    CraftingRecipe recipe = findSatisfiableRecipe(stack, level, inv, slot);
                    if (recipe == null) {
                        continue;
                    }
                    int needed = (int) recipe.getIngredients().stream().filter(ing -> ing.test(stack)).count();
                    if (inv.countItem(stack.getItem()) >= needed) {
                        return stack;
                    }
                }
            }
            return null;
        }

        @Override
        public void onCraft(ServerLevel serverLevel) {
            SimpleContainer inv = this.mob.getInventory();
            List<RaiderServant> allies = this.mob.level.getEntitiesOfClass(RaiderServant.class, this.mob.getBoundingBox().inflate(16), ally -> ally != this.mob && ally.getTrueOwner() == this.mob.getTrueOwner() && ally.canWearArmor());
            EquipmentSlot targetSlot = null;
            ItemStack source = null;

            outer:
            for (EquipmentSlot slot : armorSlots()) {
                boolean crusherNeeds = this.mob.getItemBySlot(slot).isEmpty() && this.mob.itemsInInv(s -> s.getItem() instanceof ArmorItem a && a.getEquipmentSlot() == slot).isEmpty();
                boolean allyNeeds = allies.stream().anyMatch(ally -> ally.getItemBySlot(slot).isEmpty() && this.mob.itemsInInv(s -> s.getItem() instanceof ArmorItem a && a.getEquipmentSlot() == slot).isEmpty());

                if (!crusherNeeds && !allyNeeds) {
                    continue;
                }

                for (ItemStack stack : this.mob.itemsInInv(s -> !s.isEmpty())) {
                    CraftingRecipe recipe = findSatisfiableRecipe(stack, serverLevel, inv, slot);
                    if (recipe == null) {
                        continue;
                    }
                    int needed = (int) recipe.getIngredients().stream().filter(ing -> ing.test(stack)).count();
                    if (inv.countItem(stack.getItem()) >= needed) {
                        targetSlot = slot;
                        source = stack;
                        break outer;
                    }
                }
            }

            if (source == null || targetSlot == null) {
                return;
            }

            CraftingRecipe recipe = findSatisfiableRecipe(source, serverLevel, inv, targetSlot);
            if (recipe == null) {
                return;
            }

            ItemStack result = recipe.getResultItem(serverLevel.registryAccess()).copy();
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.isEmpty()) {
                    continue;
                }
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    ItemStack slot = inv.getItem(i);
                    if (!slot.isEmpty() && ingredient.test(slot)) {
                        ItemStack remainder = slot.getCraftingRemainingItem() != null ? slot.getCraftingRemainingItem() : ItemStack.EMPTY;
                        slot.shrink(1);
                        if (slot.isEmpty()) {
                            inv.setItem(i, remainder);
                        } else if (!remainder.isEmpty() && inv.canAddItem(remainder)) {
                            inv.addItem(remainder);
                        }
                        break;
                    }
                }
            }
            inv.setChanged();

            if (inv.canAddItem(result)) {
                inv.addItem(result);
                serverLevel.playSound(null, this.mob.blockPosition(), SoundEvents.ANVIL_USE, this.mob.getSoundSource(), 0.5F, 1.0F);
            }
        }
    }

    public static class ForgeWeaponGoal<T extends CrusherServant> extends MobCraftingGoal<T> {

        public ForgeWeaponGoal(T mob) {
            super(mob, 60, 0.75F);
        }

        @Override
        public boolean canStartCrafting() {
            if (this.mob.level instanceof ServerLevel serverLevel) {
                return this.findCraftableStack(serverLevel) != null;
            }
            return false;
        }

        private boolean allyNeedsWeapon(RaiderServant ally, ItemStack candidateResult) {
            ItemStack current = ally.getMainHandItem();
            if (!ally.isMainWeapon(candidateResult)) {
                return false;
            }
            if (current.isEmpty()) {
                return true;
            }
            return !current.isEnchanted() && isBetterTier(current, candidateResult);
        }

        @Nullable
        public ItemStack findCraftableStack(ServerLevel level) {
            SimpleContainer inv = this.mob.getInventory();

            List<RaiderServant> allies = this.mob.level.getEntitiesOfClass(RaiderServant.class, this.mob.getBoundingBox().inflate(16), ally -> ally != this.mob && ally.getTrueOwner() == this.mob.getTrueOwner() && ally.canHaveWeapon());

            for (ItemStack stack : this.mob.itemsInInv(s -> !s.isEmpty())) {
                CraftingRecipe recipe = findSatisfiableWeaponRecipe(stack, level, inv, null);
                Optional<RaiderServant> optional = allies.stream().findFirst();

                if (optional.isPresent()) {
                    RaiderServant raiderServant = optional.get();
                    recipe = findSatisfiableWeaponRecipe(stack, level, inv, raiderServant::isMainWeapon);
                }
                if (recipe == null) {
                    continue;
                }

                ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
                int needed = (int) recipe.getIngredients().stream().filter(ing -> ing.test(stack)).count();
                if (inv.countItem(stack.getItem()) < needed) {
                    continue;
                }

                boolean allyNeeds = allies.stream().anyMatch(ally -> allyNeedsWeapon(ally, result)
                        && (ally instanceof AbstractIllagerServant illager && illager.itemsInInv(s -> s.getItem() == result.getItem()).isEmpty()) || !(ally instanceof AbstractIllagerServant));

                if (allyNeeds) {
                    return stack;
                }
            }
            return null;
        }

        @Override
        public void onCraft(ServerLevel serverLevel) {
            SimpleContainer inv = this.mob.getInventory();

            List<RaiderServant> allies = this.mob.level.getEntitiesOfClass(RaiderServant.class, this.mob.getBoundingBox().inflate(16), ally -> ally != this.mob && ally.getTrueOwner() == this.mob.getTrueOwner() && ally.canHaveWeapon());

            ItemStack source = null;
            CraftingRecipe recipe = null;

            for (ItemStack stack : this.mob.itemsInInv(s -> !s.isEmpty())) {
                CraftingRecipe candidate = findSatisfiableWeaponRecipe(stack, serverLevel, inv, null);
                Optional<RaiderServant> optional = allies.stream().findFirst();

                if (optional.isPresent()) {
                    RaiderServant raiderServant = optional.get();
                    candidate = findSatisfiableWeaponRecipe(stack, serverLevel, inv, raiderServant::isMainWeapon);
                }
                if (candidate == null) {
                    continue;
                }
                ItemStack result = candidate.getResultItem(serverLevel.registryAccess()).copy();
                int needed = (int) candidate.getIngredients().stream().filter(ing -> ing.test(stack)).count();
                if (inv.countItem(stack.getItem()) < needed) {
                    continue;
                }

                boolean allyNeeds = allies.stream().anyMatch(ally -> allyNeedsWeapon(ally, result)
                        && (ally instanceof AbstractIllagerServant illager && illager.itemsInInv(s -> s.getItem() == result.getItem()).isEmpty()) || !(ally instanceof AbstractIllagerServant));

                if (allyNeeds) {
                    source = stack;
                    recipe = candidate;
                    break;
                }
            }

            if (source == null || recipe == null) {
                return;
            }

            ItemStack result = recipe.getResultItem(serverLevel.registryAccess()).copy();
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.isEmpty()) {
                    continue;
                }
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    ItemStack slot = inv.getItem(i);
                    if (!slot.isEmpty() && ingredient.test(slot)) {
                        ItemStack remainder = slot.getCraftingRemainingItem() != null ? slot.getCraftingRemainingItem() : ItemStack.EMPTY;
                        slot.shrink(1);
                        if (slot.isEmpty()) {
                            inv.setItem(i, remainder);
                        } else if (!remainder.isEmpty() && inv.canAddItem(remainder)) {
                            inv.addItem(remainder);
                        }
                        break;
                    }
                }
            }
            inv.setChanged();

            if (inv.canAddItem(result)) {
                inv.addItem(result);
                serverLevel.playSound(null, this.mob.blockPosition(), SoundEvents.ANVIL_USE, this.mob.getSoundSource(), 0.5F, 1.0F);
            }
        }
    }

    public static class EquipArmorGoal extends Goal {
        private static final int SEARCH_RADIUS = 16;
        private final CrusherServant crusher;

        public EquipArmorGoal(CrusherServant crusher) {
            this.crusher = crusher;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Nullable
        private ItemStack findArmorForSlot(EquipmentSlot slot) {
            return this.crusher.itemsInInv(stack -> {
                if (!(stack.getItem() instanceof ArmorItem armor)) {
                    return false;
                }
                return armor.getEquipmentSlot() == slot;
            }).stream().findFirst().orElse(null);
        }

        private boolean slotIsEmpty(LivingEntity entity, EquipmentSlot slot) {
            return entity.getItemBySlot(slot).isEmpty();
        }

        @Nullable
        private RaiderServant findUnequippedAlly() {
            return this.crusher.level.getEntitiesOfClass(
                    RaiderServant.class,
                    this.crusher.getBoundingBox().inflate(SEARCH_RADIUS), ally -> ally != this.crusher && ally.getTrueOwner() == this.crusher.getTrueOwner() && ally.canWearArmor() && hasArmorForAlly(ally)
            ).stream().findFirst().orElse(null);
        }

        private boolean hasArmorForAlly(LivingEntity ally) {
            for (EquipmentSlot slot : armorSlots()) {
                if (this.slotIsEmpty(ally, slot) && this.findArmorForSlot(slot) != null) {
                    return true;
                }
            }
            return false;
        }

        private boolean crusherNeedsArmor() {
            for (EquipmentSlot slot : armorSlots()) {
                if (slotIsEmpty(this.crusher, slot) && this.findArmorForSlot(slot) != null) {
                    return true;
                }
            }
            return false;
        }

        private static EquipmentSlot[] armorSlots() {
            return new EquipmentSlot[]{
                    EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS, EquipmentSlot.FEET
            };
        }

        @Override
        public boolean canUse() {
            if (this.crusher.getTarget() != null) {
                return false;
            }
            if (this.crusher.isStaying()) {
                return false;
            }
            if (!(this.crusher.level instanceof ServerLevel)) {
                return false;
            }
            RaiderServant ally = this.findUnequippedAlly();
            LivingEntity target = ally != null ? ally : this.crusher;
            if (target != this.crusher && !this.crusher.hasLineOfSight(target)) {
                return false;
            }
            return this.findUnequippedAlly() != null || this.crusherNeedsArmor();
        }

        @Override
        public boolean canContinueToUse() {
            RaiderServant ally = this.findUnequippedAlly();
            LivingEntity target = ally != null ? ally : this.crusher;
            if (target != this.crusher && !this.crusher.hasLineOfSight(target)) {
                return false;
            }
            return this.findUnequippedAlly() != null || this.crusherNeedsArmor();
        }

        @Override
        public void tick() {
            RaiderServant ally = this.findUnequippedAlly();
            LivingEntity target = ally != null ? ally : this.crusher;

            if (target != this.crusher && !this.crusher.isWithinDistance(target, 2.0D)) {
                this.crusher.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 0.75F);
            } else {
                this.crusher.getNavigation().stop();
                for (EquipmentSlot slot : armorSlots()) {
                    if (!slotIsEmpty(target, slot)) {
                        continue;
                    }
                    ItemStack armor = findArmorForSlot(slot);
                    if (armor == null) {
                        continue;
                    }

                    target.setItemSlot(slot, armor.copyAndClear());
                    this.crusher.getInventory().setChanged();
                    this.crusher.level.playSound(null, target.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC, crusher.getSoundSource(), 1.0F, 1.0F);
                }
            }
        }
    }

    public static class EquipWeaponGoal extends Goal {
        private static final int SEARCH_RADIUS = 16;
        private final CrusherServant crusher;

        public EquipWeaponGoal(CrusherServant crusher) {
            this.crusher = crusher;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Nullable
        private ItemStack findWeaponForAlly(RaiderServant ally) {
            return this.crusher.itemsInInv(stack -> {
                if (!validWeapon(stack)) {
                    return false;
                }
                if (!ally.isMainWeapon(stack)) {
                    return false;
                }
                ItemStack current = ally.getMainHandItem();
                if (current.isEmpty()) {
                    return true;
                }
                return !current.isEnchanted() && isBetterTier(current, stack);
            }).stream().findFirst().orElse(null);
        }

        @Nullable
        private RaiderServant findAllyNeedingWeapon() {
            return this.crusher.level.getEntitiesOfClass(RaiderServant.class, this.crusher.getBoundingBox().inflate(SEARCH_RADIUS), ally -> ally != this.crusher && ally.getTrueOwner() == this.crusher.getTrueOwner() && ally.canHaveWeapon() && this.findWeaponForAlly(ally) != null).stream().findFirst().orElse(null);
        }

        @Override
        public boolean canUse() {
            if (this.crusher.getTarget() != null) {
                return false;
            }
            if (this.crusher.isStaying()) {
                return false;
            }
            if (!(this.crusher.level instanceof ServerLevel)) {
                return false;
            }
            return this.findAllyNeedingWeapon() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.findAllyNeedingWeapon() != null;
        }

        @Override
        public void tick() {
            RaiderServant ally = this.findAllyNeedingWeapon();
            if (ally == null) {
                return;
            }

            if (!this.crusher.isWithinDistance(ally, 2.0D)) {
                this.crusher.getNavigation().moveTo(ally.getX(), ally.getY(), ally.getZ(), 0.75F);
            } else {
                this.crusher.getNavigation().stop();
                ItemStack weapon = this.findWeaponForAlly(ally);
                if (weapon != null) {
                    ItemStack oldWeapon = ally.getMainHandItem();
                    if (!oldWeapon.isEmpty() && this.crusher.getInventory().canAddItem(oldWeapon)) {
                        this.crusher.getInventory().addItem(oldWeapon.copy());
                    }
                    ally.setItemInHand(InteractionHand.MAIN_HAND, weapon.copyAndClear());
                    this.crusher.getInventory().setChanged();
                    this.crusher.level.playSound(null, ally.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC, this.crusher.getSoundSource(), 1.0F, 1.0F);
                }
            }
        }
    }

    public static class LootOreGoal<T extends AbstractIllagerServant> extends IllagerChestGoal<T> {

        public LootOreGoal(T illager) {
            super(illager);
            this.chestPredicate = itemStack -> {
                if (itemStack.isEmpty()) {
                    return false;
                }
                if (!(illager.level instanceof ServerLevel serverLevel)) {
                    return false;
                }
                if (canBeSmelted(itemStack, serverLevel)) {
                    return true;
                }
                return isCraftingIngredient(itemStack, serverLevel);
            };
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
            if (this.illager.level instanceof ServerLevel serverLevel) {
                int craftableCount = this.illager.itemsInInv(stack ->
                        canBeSmelted(stack, serverLevel) || isCraftingIngredient(stack, serverLevel)).size();
                if (craftableCount >= 24) {
                    return false;
                }
            }
            return super.canUse();
        }

        @Override
        public void chestInteract(Container container) {
            for (ItemStack itemStack : this.getItems(container)) {
                if (this.illager.getInventory().canAddItem(itemStack)){
                    this.illager.getInventory().addItem(itemStack.copyAndClear());
                    container.setChanged();
                }
            }
        }
    }
}
