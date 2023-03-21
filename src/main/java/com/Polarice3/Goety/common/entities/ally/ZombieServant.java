package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.NeutralZombieAttackGoal;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.utils.EntityFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.UUID;

public class ZombieServant extends Summoned{
    private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(ZombieServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DROWNED_CONVERSION_ID = SynchedEntityData.defineId(ZombieServant.class, EntityDataSerializers.BOOLEAN);
    private int inWaterTime;
    private int conversionTime;

    public ZombieServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new NeutralZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(8, new WanderGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ZombieServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ZombieServantDamage.get())
                .add(Attributes.ARMOR, 2.0D);
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_BABY_ID, false);
        this.getEntityData().define(DATA_DROWNED_CONVERSION_ID, false);
    }

    public boolean isUnderWaterConverting() {
        return this.getEntityData().get(DATA_DROWNED_CONVERSION_ID);
    }

    public boolean isBaby() {
        return this.getEntityData().get(DATA_BABY_ID);
    }

    public void setBaby(boolean pChildZombie) {
        this.getEntityData().set(DATA_BABY_ID, pChildZombie);
        if (this.level != null && !this.level.isClientSide) {
            AttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            modifiableattributeinstance.removeModifier(SPEED_MODIFIER_BABY);
            if (pChildZombie) {
                modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER_BABY);
            }
        }

    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_BABY_ID.equals(pKey)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(pKey);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsBaby", this.isBaby());
        pCompound.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
        pCompound.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setBaby(pCompound.getBoolean("IsBaby"));
        this.inWaterTime = pCompound.getInt("InWaterTime");
        if (pCompound.contains("DrownedConversionTime", 99) && pCompound.getInt("DrownedConversionTime") > -1) {
            this.startUnderWaterConversion(pCompound.getInt("DrownedConversionTime"));
        }
    }

    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    public double getMyRidingOffset() {
        return this.isBaby() ? 0.0D : -0.45D;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ZOMBIE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected boolean convertsInWater() {
        return true;
    }

    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
            if (this.isUnderWaterConverting()) {
                --this.conversionTime;

                if (this.conversionTime < 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, ModEntityType.ZOMBIE_SERVANT.get(), (timer) -> this.conversionTime = timer)) {
                    this.doUnderWaterConversion();
                }
            } else if (this.convertsInWater()) {
                if (this.isEyeInFluid(FluidTags.WATER)) {
                    ++this.inWaterTime;
                    if (this.inWaterTime >= 600) {
                        this.startUnderWaterConversion(300);
                    }
                } else {
                    this.inWaterTime = -1;
                }
            }
        }

        super.tick();
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(p_217055_, difficulty);
        if (this.random.nextFloat() < (this.isUpgraded() ? 0.05F : 0.01F)) {
            int i = this.random.nextInt(3);
            if (i == 0) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }

    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        float f = difficultyIn.getSpecialMultiplier();
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
            }
        }
        this.handleAttributes(f);
        return spawnDataIn;
    }

    protected void handleAttributes(float difficulty) {
        Objects.requireNonNull(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).addPermanentModifier(new AttributeModifier("random spawn bonus", this.random.nextDouble() * (double)0.05F, AttributeModifier.Operation.ADDITION));
        double d0 = this.random.nextDouble() * 1.5D * (double)difficulty;
        if (d0 > 1.0D) {
            Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE)).addPermanentModifier(new AttributeModifier("random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

    }

    public boolean wasKilled(ServerLevel world, LivingEntity killedEntity) {
        boolean flag = super.wasKilled(world, killedEntity);
        float random = this.level.random.nextFloat();
        if (this.isUpgraded() && killedEntity instanceof Zombie && random <= 0.5F && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(killedEntity, ModEntityType.ZOMBIE_SERVANT.get(), (timer) -> {})) {
            Zombie zombieEntity = (Zombie)killedEntity;
            EntityType<? extends Mob> entityType = ModEntityType.ZOMBIE_SERVANT.get();
            if (zombieEntity instanceof Husk){
                entityType = ModEntityType.HUSK_SERVANT.get();
            } else if (zombieEntity instanceof Drowned){
                entityType = ModEntityType.DROWNED_SERVANT.get();
            }
            ZombieServant zombieMinionEntity = (ZombieServant) zombieEntity.convertTo(entityType, false);
            if (zombieMinionEntity != null) {
                zombieMinionEntity.finalizeSpawn(world, level.getCurrentDifficultyAt(zombieMinionEntity.blockPosition()), MobSpawnType.CONVERSION, null, null);
                zombieMinionEntity.setLimitedLife(10 * (15 + this.level.random.nextInt(45)));
                if (this.getTrueOwner() != null){
                    zombieMinionEntity.setTrueOwner(this.getTrueOwner());
                }
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(killedEntity, zombieMinionEntity);
                if (!this.isSilent()) {
                    world.levelEvent((Player) null, 1026, this.blockPosition(), 0);
                }
            }
        }
        return flag;
    }

    private void startUnderWaterConversion(int p_204704_1_) {
        this.conversionTime = p_204704_1_;
        this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, true);
    }

    protected void doUnderWaterConversion() {
        this.convertToZombieType(ModEntityType.DROWNED_SERVANT.get());
        if (!this.isSilent()) {
            this.level.levelEvent((Player) null, 1040, this.blockPosition(), 0);
        }

    }

    protected void convertToZombieType(EntityType<? extends ZombieServant> p_234341_1_) {
        ZombieServant zombieentity = this.convertTo(p_234341_1_, true);
        if (zombieentity != null) {
            zombieentity.handleAttributes(zombieentity.level.getCurrentDifficultyAt(zombieentity.blockPosition()).getSpecialMultiplier());
            if (this.getTrueOwner() != null) {
                zombieentity.setTrueOwner(this.getTrueOwner());
            }
            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, zombieentity);
        }

    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide){
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            ItemStack itemstack2 = this.getMainHandItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner() && !pPlayer.isShiftKeyDown() && !pPlayer.isCrouching()) {
                if (item == Items.ROTTEN_FLESH && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    this.heal(2.0F);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    return InteractionResult.CONSUME;
                }
                if (!(pPlayer.getOffhandItem().getItem() instanceof DarkWand)) {
                    if (item instanceof SwordItem) {
                        this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                        this.spawnAtLocation(itemstack2);
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
                    if (item instanceof AxeItem) {
                        this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                        this.spawnAtLocation(itemstack2);
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
                    if (item instanceof TridentItem && this instanceof DrownedServant) {
                        this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                        this.spawnAtLocation(itemstack2);
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
                        this.setGuaranteedDrop(EquipmentSlot.HEAD);
                        this.spawnAtLocation(helmet);
                    }
                    if (((ArmorItem) item).getSlot() == EquipmentSlot.CHEST) {
                        this.setItemSlot(EquipmentSlot.CHEST, itemstack.copy());
                        this.setGuaranteedDrop(EquipmentSlot.CHEST);
                        this.spawnAtLocation(chestplate);
                    }
                    if (((ArmorItem) item).getSlot() == EquipmentSlot.LEGS) {
                        this.setItemSlot(EquipmentSlot.LEGS, itemstack.copy());
                        this.setGuaranteedDrop(EquipmentSlot.LEGS);
                        this.spawnAtLocation(legging);
                    }
                    if (((ArmorItem) item).getSlot() == EquipmentSlot.FEET) {
                        this.setItemSlot(EquipmentSlot.FEET, itemstack.copy());
                        this.setGuaranteedDrop(EquipmentSlot.FEET);
                        this.spawnAtLocation(boots);
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
                } else {
                    return InteractionResult.PASS;
                }
            } else {
                return InteractionResult.PASS;
            }
        } else {
            return InteractionResult.PASS;
        }
    }
}
