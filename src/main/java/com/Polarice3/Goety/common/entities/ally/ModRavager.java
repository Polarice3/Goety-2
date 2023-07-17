package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.hostile.ArmoredRavager;
import com.Polarice3.Goety.common.entities.neutral.IRavager;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.RavagerArmorItem;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public class ModRavager extends Summoned implements PlayerRideable, ContainerListener, IRavager {
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("d404309f-25d3-4837-8828-e2b7b0ea79fd");
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(ModRavager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(ModRavager.class, EntityDataSerializers.BOOLEAN);
    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = (p_33346_) -> p_33346_.isAlive() && !(p_33346_ instanceof ModRavager);
    protected SimpleContainer inventory;
    private int attackTick;
    private int stunnedTick;
    private int roarTick;
    private int roarCool;

    public ModRavager(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.createInventory();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new ModRavager.RavagerMeleeAttackGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public void targetSelectGoal(){
        super.targetSelectGoal();
        this.targetSelector.addGoal(1, new NaturalAttackGoal<>(this, AbstractVillager.class));
        this.targetSelector.addGoal(1, new NaturalAttackGoal<>(this, IronGolem.class));
    }

    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
        boolean flag1 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, flag);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SADDLE_ID, false);
        this.entityData.define(AUTO_MODE, false);
    }

    public float getStepHeight() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 75.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void addAdditionalSaveData(CompoundTag p_33353_) {
        super.addAdditionalSaveData(p_33353_);
        p_33353_.putInt("AttackTick", this.attackTick);
        p_33353_.putInt("StunTick", this.stunnedTick);
        p_33353_.putInt("RoarTick", this.roarTick);
        p_33353_.putInt("RoarCool", this.roarCool);
        p_33353_.putBoolean("Saddle", this.hasSaddle());
        p_33353_.putBoolean("AutoMode", this.isAutonomous());
        if (!this.getArmorSlot().isEmpty()) {
            p_33353_.put("ArmorItem", this.getArmorSlot().save(new CompoundTag()));
        }
    }

    public void readAdditionalSaveData(CompoundTag p_33344_) {
        super.readAdditionalSaveData(p_33344_);
        this.attackTick = p_33344_.getInt("AttackTick");
        this.stunnedTick = p_33344_.getInt("StunTick");
        this.roarTick = p_33344_.getInt("RoarTick");
        this.roarCool = p_33344_.getInt("RoarCool");
        this.setSaddle(p_33344_.getBoolean("Saddle"));
        this.setAutonomous(p_33344_.getBoolean("AutoMode"));
        if (p_33344_.contains("ArmorItem", 10)) {
            ItemStack itemstack = ItemStack.of(p_33344_.getCompound("ArmorItem"));
            if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
                this.setArmorSlot(itemstack);
            }
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.isNatural()){
            this.setHostile(true);
        }
        return pSpawnData;
    }

    public void setSaddle(boolean p_20850_) {
        this.entityData.set(DATA_SADDLE_ID, p_20850_);
    }

    public boolean hasSaddle() {
        return this.entityData.get(DATA_SADDLE_ID);
    }

    public void setAutonomous(boolean p_20850_) {
        this.entityData.set(AUTO_MODE, p_20850_);
        this.playSound(SoundEvents.ARROW_HIT_PLAYER);
        if (!this.isWandering()){
            this.setWandering(true);
            this.setStaying(false);
        }
    }

    public boolean isAutonomous() {
        return this.entityData.get(AUTO_MODE);
    }

    protected PathNavigation createNavigation(Level p_33348_) {
        return new ModRavager.RavagerNavigation(this, p_33348_);
    }

    public int getMaxHeadYRot() {
        return 45;
    }

    public double getPassengersRidingOffset() {
        return 2.1D;
    }

    @Nullable
    public Entity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity != null && this.canBeControlledBy(entity) ? entity : null;
    }

    public boolean isControlledByLocalInstance() {
        return super.isControlledByLocalInstance() && !this.isAutonomous();
    }

    private boolean canBeControlledBy(Entity p_219063_) {
        return !this.isNoAi() && p_219063_ instanceof LivingEntity;
    }

    public ItemStack getArmorSlot(){
        return this.inventory.getItem(0);
    }

    public void setArmorSlot(ItemStack itemStack){
        this.inventory.setItem(0, itemStack);
    }

    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(1);
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            ItemStack itemstack = simplecontainer.getItem(0);
            if (!itemstack.isEmpty()) {
                this.inventory.setItem(0, itemstack.copy());
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
    }

    protected void updateContainerEquipment() {
        if (!this.level.isClientSide) {
            this.setArmorEquipment(this.getArmorSlot());
            this.setDropChance(EquipmentSlot.CHEST, 0.0F);
        }
    }

    public void containerChanged(Container p_30696_) {
        ItemStack itemstack = this.getArmor();
        this.updateContainerEquipment();
        ItemStack itemstack1 = this.getArmor();
        if (this.tickCount > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
            this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
        }

    }

    public ItemStack getArmor() {
        return this.getItemBySlot(EquipmentSlot.CHEST);
    }

    private void setArmor(ItemStack p_30733_) {
        this.setItemSlot(EquipmentSlot.CHEST, p_30733_);
        this.setDropChance(EquipmentSlot.CHEST, 0.0F);
    }

    private void setArmorEquipment(ItemStack p_30735_) {
        this.setArmor(p_30735_);
        if (!this.level.isClientSide) {
            AttributeInstance attribute = this.getAttribute(Attributes.ARMOR);
            if (attribute != null) {
                attribute.removeModifier(ARMOR_MODIFIER_UUID);
                if (this.isArmor(p_30735_)) {
                    int i = ((RavagerArmorItem) p_30735_.getItem()).getProtection();
                    if (i != 0) {
                        attribute.addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Ravager armor bonus", (double) i, AttributeModifier.Operation.ADDITION));
                    }
                }
            }
        }

    }

    public boolean isArmor(ItemStack p_30731_) {
        return p_30731_.getItem() instanceof RavagerArmorItem;
    }

    public void aiStep() {
        super.aiStep();
        if (this.tickCount % 20 == 0) {
            if (this.isHostile()) {
                if (this.hasSaddle()) {
                    if (!this.getArmor().isEmpty()) {
                        ArmoredRavager armoredRavager = this.convertTo(ModEntityType.ARMORED_RAVAGER.get(), false);
                        if (armoredRavager != null) {
                            armoredRavager.setArmorSlot(this.getArmorSlot());
                        }
                    } else {
                        this.convertTo(EntityType.RAVAGER, false);
                    }
                }
            }
        }
        if (this.isAlive()) {
            AttributeInstance attribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attribute != null) {
                if (this.isImmobile()) {
                    attribute.setBaseValue(0.0D);
                } else {
                    double d0 = this.getTarget() != null ? 0.35D : 0.3D;
                    double d1 = attribute.getBaseValue();
                    attribute.setBaseValue(Mth.lerp(0.1D, d1, d0));
                }
            }

            if (!this.level.isClientSide) {
                if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
                    boolean flag = false;
                    AABB aabb = this.getBoundingBox().inflate(0.2D);

                    for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                        BlockState blockstate = this.level.getBlockState(blockpos);
                        Block block = blockstate.getBlock();
                        if (block instanceof LeavesBlock) {
                            flag = this.level.destroyBlock(blockpos, true, this) || flag;
                        }
                    }
                }
            }

            if (this.roarCool > 0){
                --this.roarCool;
            }

            if (this.roarTick > 0) {
                --this.roarTick;
                if (this.roarTick == 10) {
                    this.roar();
                }
            }

            if (this.attackTick > 0) {
                --this.attackTick;
            }

            if (this.stunnedTick > 0) {
                --this.stunnedTick;
                this.stunEffect();
                if (this.stunnedTick == 0) {
                    this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
                    this.roarTick = 20;
                }
            }

        }
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasSaddle()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.getX() - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            double d1 = this.getY() + (double)this.getBbHeight() - 0.3D;
            double d2 = this.getZ() + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * ((float)Math.PI / 180F))) + (this.random.nextDouble() * 0.6D - 0.3D);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
        }

    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return this.stunnedTick <= 0 && this.roarTick <= 0 && super.hasLineOfSight(p_149755_);
    }

    protected void blockedByShield(LivingEntity p_33361_) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5D) {
                this.stunnedTick = 40;
                this.roarCool = getRoarCoolMax();
                this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 1.0F);
                this.level.broadcastEntityEvent(this, (byte)39);
                p_33361_.push(this);
            } else {
                this.strongKnockback(p_33361_);
            }

            p_33361_.hurtMarked = true;
        }
    }

    public void forceRoar(){
        if (this.roarCool <= 0) {
            this.roarTick = 20;
            this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
            this.roarCool = getRoarCoolMax();
            this.level.broadcastEntityEvent(this, (byte) 40);
        }
    }

    private void roar() {
        if (this.isAlive()) {
            for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), NO_RAVAGER_AND_ALIVE)) {
                if (livingentity != this.getTrueOwner() && !livingentity.isAlliedTo(this.getTrueOwner())) {
                    livingentity.hurt(DamageSource.mobAttack(this), 6.0F);
                }

                this.strongKnockback(livingentity);
            }

            Vec3 vec3 = this.getBoundingBox().getCenter();

            for(int i = 0; i < 40; ++i) {
                double d0 = this.random.nextGaussian() * 0.2D;
                double d1 = this.random.nextGaussian() * 0.2D;
                double d2 = this.random.nextGaussian() * 0.2D;
                this.level.addParticle(ParticleTypes.POOF, vec3.x, vec3.y, vec3.z, d0, d1, d2);
            }

            this.gameEvent(GameEvent.ENTITY_ROAR);
        }

    }

    private void strongKnockback(Entity p_33340_) {
        double d0 = p_33340_.getX() - this.getX();
        double d1 = p_33340_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        p_33340_.push(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
    }

    public void handleEntityEvent(byte p_33335_) {
        if (p_33335_ == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
        } else if (p_33335_ == 39) {
            this.stunnedTick = 40;
            this.roarCool = getRoarCoolMax();
        } else if (p_33335_ == 40) {
            this.roarTick = 20;
            this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
            this.roarCool = getRoarCoolMax();
        }

        super.handleEntityEvent(p_33335_);
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    public int getStunnedTick() {
        return this.stunnedTick;
    }

    public int getRoarTick() {
        return this.roarTick;
    }

    public int getRoarCool(){
        return this.roarCool;
    }

    public static int getRoarCoolMax(){
        return MathHelper.secondsToTicks(SpellConfig.RavagerRoarCooldown.get());
    }

    public boolean doHurtTarget(Entity p_33328_) {
        this.attackTick = 10;
        this.level.broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.doHurtTarget(p_33328_);
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_33359_) {
        return SoundEvents.RAVAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    protected void playStepSound(BlockPos p_33350_, BlockState p_33351_) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.15F, 1.0F);
        if (!this.getArmor().isEmpty()){
            this.playSound(SoundEvents.CHAIN_STEP, 0.15F, 1.0F);
        }
    }

    public boolean checkSpawnObstruction(LevelReader p_33342_) {
        return !p_33342_.containsAnyLiquid(this.getBoundingBox());
    }

    protected void doPlayerRide(Player player) {
        if (!this.level.isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    public void travel(Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
            if (this.isVehicle() && this.hasSaddle() && livingentity != null && !this.isAutonomous()) {
                this.setYRot(livingentity.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(livingentity.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float f = livingentity.xxa * 0.5F;
                float f1 = livingentity.zza;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                if (this.isInWater() && this.getFluidTypeHeight(ForgeMod.WATER_TYPE.get()) > this.getFluidJumpThreshold() || this.isInLava() || this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType) && height > this.getFluidJumpThreshold())){
                    Vec3 vector3d = this.getDeltaMovement();
                    this.setDeltaMovement(vector3d.x, 0.04F, vector3d.z);
                    this.hasImpulse = true;
                    if (f1 > 0.0F) {
                        float f2 = Mth.sin(this.getYRot() * ((float)Math.PI / 180F));
                        float f3 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F));
                        this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f2 * 0.04F), 0.0D, (double) (0.4F * f3 * 0.04F)));
                    }
                }

                this.flyingSpeed = this.getSpeed() * 0.1F;
                if (this.isControlledByLocalInstance() || livingentity instanceof Player) {
                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    super.travel(new Vec3(f, pTravelVector.y, f1));
                    this.lerpSteps = 0;
                }

                this.calculateEntityAnimation(this, false);
            } else {
                super.travel(pTravelVector);
            }
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!pPlayer.level.isClientSide) {
            if (pPlayer == this.getTrueOwner()) {
                if (this.hasSaddle() && !pPlayer.isCrouching()) {
                    this.doPlayerRide(pPlayer);
                    this.setStaying(false);
                    this.setWandering(false);
                } else if (pPlayer.getItemInHand(pHand).is(ModItems.OMINOUS_SADDLE.get()) && !this.hasSaddle()) {
                    pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
                    this.equipSaddle();
                    return InteractionResult.SUCCESS;
                } else if (this.isArmor(pPlayer.getMainHandItem()) && this.hasSaddle()) {
                    if (!this.getArmorSlot().isEmpty()) {
                        if (this.spawnAtLocation(this.getArmorSlot()) != null) {
                            this.setArmorSlot(pPlayer.getMainHandItem().copy());
                            if (!pPlayer.getAbilities().instabuild) {
                                pPlayer.getMainHandItem().shrink(1);
                            }
                        }
                    } else {
                        this.setArmorSlot(pPlayer.getMainHandItem().copy());
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getMainHandItem().shrink(1);
                        }
                    }
                    EntityFinder.sendEntityUpdatePacket(pPlayer, this);
                    return InteractionResult.SUCCESS;
                } else if (this.isFood(pPlayer.getMainHandItem()) && this.getHealth() < this.getMaxHealth()) {
                    FoodProperties foodProperties = pPlayer.getMainHandItem().getFoodProperties(this);
                    if (foodProperties != null) {
                        this.heal((float) foodProperties.getNutrition());
                        if (!pPlayer.getAbilities().instabuild) {
                            pPlayer.getMainHandItem().shrink(1);
                        }
                        this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), 0.5F);
                        this.gameEvent(GameEvent.EAT, this);
                        return InteractionResult.SUCCESS;
                    } else {
                        return InteractionResult.PASS;
                    }
                } else {
                    this.updateMoveMode(pPlayer);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    public boolean isFood(ItemStack p_30440_) {
        Item item = p_30440_.getItem();
        FoodProperties foodProperties = p_30440_.getFoodProperties(this);
        return item.isEdible() && foodProperties != null && foodProperties.isMeat();
    }

    public void equipSaddle() {
        this.level.playSound((Player)null, this, SoundEvents.HORSE_SADDLE, SoundSource.PLAYERS, 1.0F, 1.0F);
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(100.0D);
        }
        this.setSaddle(true);
    }

    class RavagerMeleeAttackGoal extends MeleeAttackGoal {
        public RavagerMeleeAttackGoal() {
            super(ModRavager.this, 1.0D, true);
        }

        protected double getAttackReachSqr(LivingEntity p_33377_) {
            float f = ModRavager.this.getBbWidth() - 0.1F;
            return (double)(f * 2.0F * f * 2.0F + p_33377_.getBbWidth());
        }
    }

    static class RavagerNavigation extends GroundPathNavigation {
        public RavagerNavigation(Mob p_33379_, Level p_33380_) {
            super(p_33379_, p_33380_);
        }

        protected PathFinder createPathFinder(int p_33382_) {
            this.nodeEvaluator = new ModRavager.RavagerNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, p_33382_);
        }
    }

    static class RavagerNodeEvaluator extends WalkNodeEvaluator {
        protected BlockPathTypes evaluateBlockPathType(BlockGetter p_33387_, boolean p_33388_, boolean p_33389_, BlockPos p_33390_, BlockPathTypes p_33391_) {
            return p_33391_ == BlockPathTypes.LEAVES ? BlockPathTypes.OPEN : super.evaluateBlockPathType(p_33387_, p_33388_, p_33389_, p_33390_, p_33391_);
        }
    }

    public static class NaturalAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        protected ModRavager ravaged;

        public NaturalAttackGoal(ModRavager ravager, Class<T> p_26061_) {
            super(ravager, p_26061_, true);
            this.ravaged = ravager;
        }

        public boolean canUse() {
            return super.canUse() && this.ravaged.isNatural() && (this.ravaged.getTrueOwner() == null || this.ravaged.getTrueOwner() instanceof AbstractIllager) && this.target != null && !this.target.isBaby();
        }
    }

    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (this.isAlive() && capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && itemHandler != null)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (itemHandler != null) {
            net.minecraftforge.common.util.LazyOptional<?> oldHandler = itemHandler;
            itemHandler = null;
            oldHandler.invalidate();
        }
    }
}
