package com.Polarice3.Goety.common.entities.ally.undead.skeleton;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.CreatureBowAttackGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.GhostArrow;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

public abstract class AbstractSkeletonServant extends Summoned implements RangedAttackMob {
    private final CreatureBowAttackGoal<AbstractSkeletonServant> bowGoal = new CreatureBowAttackGoal<>(this, 1.0D, 20, 15.0F);
    public final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false) {

        public void stop() {
            super.stop();
            AbstractSkeletonServant.this.setAggressive(false);
        }

        public void start() {
            super.start();
            AbstractSkeletonServant.this.setAggressive(true);
        }
    };
    private int arrowPower;

    public AbstractSkeletonServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.arrowPower = 0;
        this.reassessWeaponGoal();
    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkeletonServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.SkeletonServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkeletonServantDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SkeletonServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.SkeletonServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SkeletonServantDamage.get());
    }

    public void reassessWeaponGoal() {
        if (!this.level.isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);
            ItemStack itemstack = this.getMainHandItem();
            ItemStack itemstack2 = this.getOffhandItem();
            if (itemstack.getItem() instanceof BowItem || itemstack2.getItem() instanceof BowItem) {
                int i = 20;
                if (!this.isUpgraded()) {
                    i = 40;
                }

                this.bowGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, this.bowGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeGoal);
            }

        }
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setArrowPower(pCompound.getInt("arrowPower"));
        this.reassessWeaponGoal();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (pCompound.contains("arrowPower", 99)){
            pCompound.putInt("arrowPower", this.arrowPower);
        }
    }

    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
        super.setItemSlot(pSlot, pStack);
        if (!this.level.isClientSide) {
            this.reassessWeaponGoal();
        }

    }

    public void rideTick() {
        super.rideTick();
        if (this.getVehicle() instanceof PathfinderMob pathfindermob) {
            this.yBodyRot = pathfindermob.yBodyRot;
        }

    }

    protected abstract SoundEvent getStepSound();

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(randomSource, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public int getArrowPower() {
        return arrowPower;
    }

    public void setArrowPower(int arrowPower) {
        this.arrowPower = arrowPower;
    }

    public EntityType<?> getVariant(Level level, BlockPos blockPos){
        EntityType<?> entityType = ModEntityType.SKELETON_SERVANT.get();
        if (level instanceof ServerLevel serverLevel) {
            if (level.getBiome(blockPos).is(Tags.Biomes.IS_COLD_OVERWORLD) && level.canSeeSky(blockPos)) {
                entityType = ModEntityType.STRAY_SERVANT.get();
            } else if (BlockFinder.findStructure(serverLevel, blockPos, BuiltinStructures.PILLAGER_OUTPOST)) {
                entityType = ModEntityType.SKELETON_PILLAGER.get();
            } else if (level.getBiome(blockPos).is(BiomeTags.IS_JUNGLE) && level.random.nextBoolean()) {
                entityType = ModEntityType.MOSSY_SKELETON_SERVANT.get();
            } else if (level.isWaterAt(blockPos)) {
                entityType = ModEntityType.SUNKEN_SKELETON_SERVANT.get();
            }
        }
        return entityType;
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.reassessWeaponGoal();
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        if (this.isNatural()){
            this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));
        }
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
            }
        }
        return spawnDataIn;
    }

    public SoundEvent getShootSound(){
        return SoundEvents.SKELETON_SHOOT;
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        AbstractArrow abstractarrowentity = this.getMobArrow(itemstack, distanceFactor);
        if (this.getMainHandItem().getItem() instanceof BowItem) {
            abstractarrowentity = ((BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
            ItemHelper.hurtAndBreak(this.getMainHandItem(), 1, this);
        }
        abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + this.getArrowPower());
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - abstractarrowentity.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        abstractarrowentity.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
        if (this.getShootSound() != null) {
            this.playSound(this.getShootSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        }
        this.level.addFreshEntity(abstractarrowentity);
    }

    protected AbstractArrow getMobArrow(ItemStack arrowStack, float distanceFactor) {
        AbstractArrow abstractarrowentity = ProjectileUtil.getMobArrow(this, arrowStack, distanceFactor);
        if (this.isUpgraded()) {
            abstractarrowentity = getGhostArrow(this, arrowStack, distanceFactor);
        }

        return abstractarrowentity;
    }

    public static AbstractArrow getGhostArrow(LivingEntity living, ItemStack stack, float distanceFactor) {
        Arrow arrow = new GhostArrow(living.level, living);
        arrow.setEnchantmentEffectsFromEntity(living, distanceFactor);
        arrow.setEffectsFromItem(stack);
        return arrow;
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
        return p_230280_1_ == Items.BOW;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.74F;
    }

    public double getMyRidingOffset() {
        return -0.6D;
    }

    public boolean isShaking() {
        return this.isFullyFrozen();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            ItemStack itemstack2 = this.getMainHandItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (item == Items.BONE && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.SKELETON_STEP, 1.0F, 1.25F);
                    this.heal(2.0F);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    return InteractionResult.CONSUME;
                }
                if (!(pPlayer.getOffhandItem().getItem() instanceof IWand)) {
                    if (item instanceof SwordItem) {
                        this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                        this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                        }
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        EntityFinder.sendEntityUpdatePacket(pPlayer, this);
                        return InteractionResult.CONSUME;
                    }
                    if (item instanceof BowItem) {
                        this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                        this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                        }
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        EntityFinder.sendEntityUpdatePacket(pPlayer, this);
                        return InteractionResult.CONSUME;
                    }
                }
                if (item instanceof ArmorItem) {
                    ItemStack helmet = this.getItemBySlot(EquipmentSlot.HEAD);
                    ItemStack chestplate = this.getItemBySlot(EquipmentSlot.CHEST);
                    ItemStack legging = this.getItemBySlot(EquipmentSlot.LEGS);
                    ItemStack boots = this.getItemBySlot(EquipmentSlot.FEET);
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    if (((ArmorItem) item).getSlot() == EquipmentSlot.HEAD) {
                        this.setItemSlot(EquipmentSlot.HEAD, itemstack.copy());
                        this.dropEquipment(EquipmentSlot.HEAD, helmet);
                        this.setGuaranteedDrop(EquipmentSlot.HEAD);
                    }
                    if (((ArmorItem) item).getSlot() == EquipmentSlot.CHEST) {
                        this.setItemSlot(EquipmentSlot.CHEST, itemstack.copy());
                        this.dropEquipment(EquipmentSlot.CHEST, chestplate);
                        this.setGuaranteedDrop(EquipmentSlot.CHEST);
                    }
                    if (((ArmorItem) item).getSlot() == EquipmentSlot.LEGS) {
                        this.setItemSlot(EquipmentSlot.LEGS, itemstack.copy());
                        this.dropEquipment(EquipmentSlot.LEGS, legging);
                        this.setGuaranteedDrop(EquipmentSlot.LEGS);
                    }
                    if (((ArmorItem) item).getSlot() == EquipmentSlot.FEET) {
                        this.setItemSlot(EquipmentSlot.FEET, itemstack.copy());
                        this.dropEquipment(EquipmentSlot.FEET, boots);
                        this.setGuaranteedDrop(EquipmentSlot.FEET);
                    }
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    EntityFinder.sendEntityUpdatePacket(pPlayer, this);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.mobInteract(pPlayer, p_230254_2_);
    }
}
