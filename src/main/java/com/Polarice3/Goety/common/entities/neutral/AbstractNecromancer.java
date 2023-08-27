package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.projectiles.SoulBolt;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.SoulJar;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractNecromancer extends AbstractSkeletonServant implements RangedAttackMob {
    private static final EntityDataAccessor<Byte> SPELL = SynchedEntityData.defineId(AbstractNecromancer.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(AbstractNecromancer.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> DROP_JAR = SynchedEntityData.defineId(AbstractNecromancer.class, EntityDataSerializers.BOOLEAN);
    protected int spellCooldown;
    private SpellType activeSpell = SpellType.NONE;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState spellAnimationState = new AnimationState();

    public AbstractNecromancer(EntityType<? extends AbstractSkeletonServant> type, Level level) {
        super(type, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.summonSpells(1);
        this.goalSelector.addGoal(2, AvoidTargetGoal.newGoal(this, 4.0F, 1.0D, 1.2D));
        this.projectileGoal(3);
    }

    public void projectileGoal(int priority){
        this.goalSelector.addGoal(priority, new NecromancerRangedGoal(this, 1.0D, 20, 10.0F));
    }

    public void summonSpells(int priority){
        this.goalSelector.addGoal(priority, new SummonZombieSpell());
        this.goalSelector.addGoal(priority + 1, new SummonUndeadGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.NecromancerHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.NecromancerDamage.get())
                .add(Attributes.ARMOR, 2.0D);
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPELL, (byte)0);
        this.entityData.define(FLAGS, (byte)0);
        this.entityData.define(DROP_JAR, false);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.spellCooldown = compound.getInt("SpellCoolDown");
        this.setAlternateSummon(compound.getBoolean("HasAlternate"));
        this.setUndeadIdle(compound.getBoolean("SpawnUndeadIdle"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpellCoolDown", this.spellCooldown);
        compound.putBoolean("HasAlternate", this.hasAlternateSummon());
        compound.putBoolean("SpawnUndeadIdle", this.spawnUndeadIdle());
    }

    public void setDropJar(boolean dropJar){
        this.entityData.set(DROP_JAR, dropJar);
    }

    public boolean dropsJar(){
        return this.entityData.get(DROP_JAR);
    }

    public void setSpellCasting(boolean casting){
        this.setNecromancerFlags(1, casting);
    }

    public boolean isSpellCasting() {
        return this.getNecromancerFlags(1);
    }

    public void setAlternateSummon(boolean alternate){
        this.setNecromancerFlags(2, alternate);
    }

    public boolean hasAlternateSummon(){
        return this.getNecromancerFlags(2);
    }

    public void setUndeadIdle(boolean spawn){
        this.setNecromancerFlags(4, spawn);
    }

    public boolean spawnUndeadIdle(){
        return this.getNecromancerFlags(4);
    }

    public void setSpellType(SpellType spellType) {
        this.activeSpell = spellType;
        this.entityData.set(SPELL, (byte)spellType.id);
    }

    protected SpellType getSpellType() {
        return !this.level.isClientSide ? this.activeSpell : SpellType.getFromId(this.entityData.get(SPELL));
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 2.17F;
    }

    public void reassessWeaponGoal() {
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.NECROMANCER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.NECROMANCER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.NECROMANCER_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.NECROMANCER_STEP.get();
    }

    private boolean getNecromancerFlags(int mask) {
        int i = this.entityData.get(FLAGS);
        return (i & mask) != 0;
    }

    private void setNecromancerFlags(int mask, boolean value) {
        int i = this.entityData.get(FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(FLAGS, (byte)(i & 255));
    }

    protected int getSpellCooldown(){
        return this.spellCooldown;
    }

    protected void setSpellCooldown(int cooldown){
        this.spellCooldown = cooldown;
    }

    @Override
    public void die(DamageSource pCause) {
        if (this.getTrueOwner() != null){
            ItemStack itemStack = new ItemStack(ModItems.SOUL_JAR.get());
            SoulJar.setOwnerName(this.getTrueOwner(), itemStack);
            SoulJar.setNecromancer(this, itemStack);
            ItemEntity itemEntity = this.spawnAtLocation(itemStack);
            if (itemEntity != null){
                itemEntity.setExtendedLifetime();
            }
        }
        super.die(pCause);
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        return spawnDataIn;
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.NECRO_STAFF.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public List<AnimationState> getAnimations(){
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.spellAnimationState);
        return animationStates;
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    public void tick() {
        super.tick();
        if (this.spellCooldown > 0) {
            --this.spellCooldown;
        }
        if (this.level.isClientSide){
            if (this.isAlive()){
                if (!this.isAggressive() && !this.isSpellCasting()) {
                    if (!this.isMoving()) {
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.walkAnimationState.stop();
                    } else {
                        this.idleAnimationState.stop();
                        this.walkAnimationState.startIfStopped(this.tickCount);
                    }
                } else {
                    this.walkAnimationState.stop();
                    this.idleAnimationState.stop();
                }
                if (this.isSpellCasting()){
                    double d0 = MathHelper.rgbParticle(this.getSpellType().particleSpeed)[0];
                    double d1 = MathHelper.rgbParticle(this.getSpellType().particleSpeed)[1];
                    double d2 = MathHelper.rgbParticle(this.getSpellType().particleSpeed)[2];
                    for (int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                        this.level.addParticle(ModParticleTypes.CULT_SPELL.get(), this.getX(), this.getY(), this.getZ(), d0, d1, d2);
                    }
                }
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
        Vec3 vector3d = this.getViewVector( 1.0F);
        SoulBolt soulBolt = new SoulBolt(this, vector3d.x, vector3d.y, vector3d.z, this.level);
        soulBolt.setYRot(this.getYRot());
        soulBolt.setXRot(this.getXRot());
        soulBolt.setPos(this.getX() + vector3d.x / 2, this.getEyeY() - 0.2, this.getZ() + vector3d.z / 2);
        if (this.level.addFreshEntity(soulBolt)){
            this.playSound(ModSounds.CAST_SPELL.get());
            this.swing(InteractionHand.MAIN_HAND);
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner() && !pPlayer.isShiftKeyDown() && !pPlayer.isCrouching()) {
                if (item == Items.BONE && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(ModSounds.NECROMANCER_STEP.get(), 1.0F, 1.25F);
                    this.heal(2.0F);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    return InteractionResult.CONSUME;
                }
                if (!this.hasAlternateSummon() && item == ModItems.OSSEOUS_FOCUS.get()){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.setAlternateSummon(true);
                    this.playSound(ModSounds.NECROMANCER_LAUGH.get());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public class CastingSpellGoal extends Goal {
        public CastingSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return AbstractNecromancer.this.isSpellCasting();
        }

        public void start() {
            super.start();
            AbstractNecromancer.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            AbstractNecromancer.this.setSpellType(SpellType.NONE);
        }

        public void tick() {
            if (AbstractNecromancer.this.getTarget() != null) {
                AbstractNecromancer.this.getLookControl().setLookAt(AbstractNecromancer.this.getTarget(), (float) AbstractNecromancer.this.getMaxHeadYRot(), (float) AbstractNecromancer.this.getMaxHeadXRot());
            }
        }
    }

    public abstract class UseSpellGoal extends Goal {
        protected int spellTime;

        protected UseSpellGoal() {
        }

        public boolean canUse() {
            LivingEntity livingentity = AbstractNecromancer.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                if (AbstractNecromancer.this.isSpellCasting()) {
                    return false;
                } else {
                    return AbstractNecromancer.this.getSpellCooldown() <= 0;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = AbstractNecromancer.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.spellTime > 0;
        }

        public void start() {
            this.spellTime = this.getCastingTime();
            AbstractNecromancer.this.setSpellCooldown(this.getCastingInterval());
            if (this.getSpellPrepareSound() != null) {
                AbstractNecromancer.this.playSound(this.getSpellPrepareSound(), 1.0F, 1.0F);
            }
            AbstractNecromancer.this.level.broadcastEntityEvent(AbstractNecromancer.this, (byte) 5);
            AbstractNecromancer.this.setSpellCasting(true);
            AbstractNecromancer.this.setSpellType(this.getSpellType());
        }

        @Override
        public void stop() {
            super.stop();
            AbstractNecromancer.this.setSpellCasting(false);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 5) {
                if (this.getCastSound() != null) {
                    AbstractNecromancer.this.playSound(this.getCastSound(), 1.0F, 1.0F);
                }
                AbstractNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, AbstractNecromancer.this.getVoicePitch());
                this.castSpell();
                AbstractNecromancer.this.setSpellType(AbstractNecromancer.SpellType.NONE);
            }
        }

        protected abstract void castSpell();

        protected int getCastingTime() {
            return 12;
        }

        protected int getCastingInterval(){
            return 100;
        };

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        @Nullable
        protected SoundEvent getCastSound(){
            return ModSounds.SUMMON_SPELL.get();
        }

        protected abstract AbstractNecromancer.SpellType getSpellType();
    }

    public class SummonZombieSpell extends UseSpellGoal {

        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof Owned owned && owned.getTrueOwner() instanceof AbstractNecromancer;
            int i = AbstractNecromancer.this.level.getEntitiesOfClass(Owned.class, AbstractNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
            , predicate).size();
            return super.canUse() && i < 7;
        }

        protected void castSpell(){
            if (AbstractNecromancer.this.level instanceof ServerLevel serverLevel) {
                Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof Owned owned && owned.getTrueOwner() instanceof AbstractNecromancer;
                int i = AbstractNecromancer.this.level.getEntitiesOfClass(Owned.class, AbstractNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                        , predicate).size();
                for (int i1 = 0; i1 < 1 + serverLevel.random.nextInt(7 - i); ++i1) {
                    Summoned summonedentity = new ZombieServant(ModEntityType.ZOMBIE_SERVANT.get(), serverLevel);
                    if (AbstractNecromancer.this.hasAlternateSummon()){
                        if (serverLevel.random.nextBoolean()){
                            summonedentity = new SkeletonServant(ModEntityType.SKELETON_SERVANT.get(), serverLevel);
                        }
                    }
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractNecromancer.this, serverLevel);
                    summonedentity.setTrueOwner(AbstractNecromancer.this);
                    summonedentity.moveTo(blockPos, AbstractNecromancer.this.getYRot(), AbstractNecromancer.this.getXRot());
                    MobUtil.moveDownToGround(summonedentity);
                    summonedentity.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                    summonedentity.setPersistenceRequired();
                    summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(AbstractNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    this.populateDefaultEquipmentSlots(summonedentity, serverLevel.random);
                    if (serverLevel.addFreshEntity(summonedentity)){
                        summonedentity.playSound(ModSounds.SUMMON_SPELL.get(), 1.0F, 1.0F);
                    }
                }
            }
        }

        protected void populateDefaultEquipmentSlots(LivingEntity livingEntity, RandomSource p_217055_) {
            if (p_217055_.nextFloat() <= 0.15F) {
                int i = p_217055_.nextInt(2) + 2;
                if (p_217055_.nextFloat() < 0.095F) {
                    ++i;
                }

                boolean flag = true;

                for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                    if (equipmentslot.getType() == EquipmentSlot.Type.ARMOR) {
                        ItemStack itemstack = livingEntity.getItemBySlot(equipmentslot);
                        if (!flag && p_217055_.nextFloat() < 0.1F) {
                            break;
                        }

                        flag = false;
                        if (itemstack.isEmpty()) {
                            Item item = getEquipmentForSlot(equipmentslot, i);
                            if (item != null) {
                                livingEntity.setItemSlot(equipmentslot, new ItemStack(item));
                            }
                        }
                    }
                }
            }

        }

        protected SoundEvent getCastSound(){
            return null;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.ZOMBIE;
        }
    }

    public class SummonUndeadGoal extends Goal{
        protected int spellTime;

        @Override
        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof Owned owned && owned.getTrueOwner() instanceof AbstractNecromancer;
            int i = AbstractNecromancer.this.level.getEntitiesOfClass(Owned.class, AbstractNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                    , predicate).size();
            if (AbstractNecromancer.this.getTarget() != null && AbstractNecromancer.this.getTarget().isAlive()){
                return false;
            } else if (AbstractNecromancer.this.isAggressive()){
                return false;
            } else if (AbstractNecromancer.this.isSpellCasting()) {
                return false;
            } else if (AbstractNecromancer.this.isFollowing()){
                return false;
            } else if (!AbstractNecromancer.this.spawnUndeadIdle()){
                return false;
            } else {
                return AbstractNecromancer.this.getSpellCooldown() <= 0 && i < 4 && AbstractNecromancer.this.tickCount % 100 >= 90 && AbstractNecromancer.this.random.nextFloat() <= 0.05F;
            }
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0 && AbstractNecromancer.this.hurtTime <= 0;
        }

        public void start() {
            this.spellTime = 20;
            AbstractNecromancer.this.setSpellCooldown(100);
            AbstractNecromancer.this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 1.0F);
            AbstractNecromancer.this.setSpellCasting(true);
            AbstractNecromancer.this.setSpellType(SpellType.ZOMBIE);
            AbstractNecromancer.this.level.broadcastEntityEvent(AbstractNecromancer.this, (byte) 6);
        }

        @Override
        public void stop() {
            super.stop();
            AbstractNecromancer.this.setSpellCasting(false);
            AbstractNecromancer.this.level.broadcastEntityEvent(AbstractNecromancer.this, (byte) 7);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 0) {
                AbstractNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, AbstractNecromancer.this.getVoicePitch());
                AbstractNecromancer.this.setSpellType(AbstractNecromancer.SpellType.NONE);
                if (AbstractNecromancer.this.level instanceof ServerLevel serverLevel) {
                    Summoned summonedentity = new ZombieServant(ModEntityType.ZOMBIE_SERVANT.get(), serverLevel);
                    if (AbstractNecromancer.this.random.nextFloat() <= 0.25F){
                        summonedentity = new SkeletonServant(ModEntityType.SKELETON_SERVANT.get(), serverLevel);
                        if (serverLevel.getBiome(AbstractNecromancer.this.blockPosition()).is(Tags.Biomes.IS_COLD_OVERWORLD)){
                            summonedentity = new StrayServant(ModEntityType.STRAY_SERVANT.get(), serverLevel);
                        }
                    } else if (serverLevel.getBiome(AbstractNecromancer.this.blockPosition()).is(Tags.Biomes.IS_DESERT)){
                        summonedentity = new HuskServant(ModEntityType.HUSK_SERVANT.get(), serverLevel);
                    }
                    if (AbstractNecromancer.this.random.nextFloat() <= 0.025F){
                        summonedentity = new WraithServant(ModEntityType.WRAITH_SERVANT.get(), serverLevel);
                    }
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractNecromancer.this, serverLevel);
                    summonedentity.setTrueOwner(AbstractNecromancer.this);
                    summonedentity.moveTo(blockPos, AbstractNecromancer.this.getYRot(), AbstractNecromancer.this.getXRot());
                    MobUtil.moveDownToGround(summonedentity);
                    summonedentity.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                    summonedentity.setPersistenceRequired();
                    summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(AbstractNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    this.populateDefaultEquipmentSlots(summonedentity, serverLevel.random);
                    if (serverLevel.addFreshEntity(summonedentity)){
                        summonedentity.playSound(ModSounds.SUMMON_SPELL.get(), 1.0F, 1.0F);
                    }
                }
            }
        }

        protected void populateDefaultEquipmentSlots(LivingEntity livingEntity, RandomSource p_217055_) {
            if (p_217055_.nextFloat() <= 0.15F) {
                int i = p_217055_.nextInt(2) + 2;
                if (p_217055_.nextFloat() < 0.095F) {
                    ++i;
                }

                boolean flag = true;

                for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                    if (equipmentslot.getType() == EquipmentSlot.Type.ARMOR) {
                        ItemStack itemstack = livingEntity.getItemBySlot(equipmentslot);
                        if (!flag && p_217055_.nextFloat() < 0.1F) {
                            break;
                        }

                        flag = false;
                        if (itemstack.isEmpty()) {
                            Item item = getEquipmentForSlot(equipmentslot, i);
                            if (item != null) {
                                livingEntity.setItemSlot(equipmentslot, new ItemStack(item));
                            }
                        }
                    }
                }
            }

        }

    }

    public static class NecromancerRangedGoal extends Goal{
        private final AbstractNecromancer mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public NecromancerRangedGoal(AbstractNecromancer p_25768_, double speed, int attackInterval, float attackRadius) {
            this(p_25768_, speed, attackInterval, attackInterval, attackRadius);
        }

        public NecromancerRangedGoal(AbstractNecromancer mob, double speed, int attackMin, int attackMax, float attackRadius) {
            this.mob = mob;
            this.speedModifier = speed;
            this.attackIntervalMin = attackMin;
            this.attackIntervalMax = attackMax;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return !this.mob.isSpellCasting();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || (this.target != null && this.target.isAlive() && !this.mob.getNavigation().isDone() && !this.mob.isSpellCasting());
        }

        public void stop() {
            this.mob.setAggressive(false);
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null && !this.mob.isSpellCasting()) {
                double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
                if (flag) {
                    ++this.seeTime;
                } else {
                    this.seeTime = 0;
                }

                if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
                    this.mob.getNavigation().stop();
                } else {
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier);
                }

                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                --this.attackTime;
                if (this.attackTime == 5) {
                    this.mob.setAggressive(true);
                    this.mob.attackAnimationState.start(this.mob.tickCount);
                    this.mob.level.broadcastEntityEvent(this.mob, (byte) 4);
                } else if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.mob.performRangedAttack(this.target, f1);
                    this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
                } else if (this.attackTime < 0) {
                    this.mob.setAggressive(false);
                    this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4){
            this.stopAllAnimations();
            this.attackAnimationState.start(this.tickCount);
        } else if (p_21375_ == 5){
            this.stopAllAnimations();
            this.summonAnimationState.start(this.tickCount);
        } else if (p_21375_ == 6){
            this.stopAllAnimations();
            this.spellAnimationState.start(this.tickCount);
        } else if (p_21375_ == 7){
            this.stopAllAnimations();
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public enum SpellType {
        NONE(0, 0xffffff),
        ZOMBIE(1, 0x16feff),
        CLOUD(2, 0xf2ffff);

        private final int id;
        private final int particleSpeed;

        SpellType(int idIn, int color) {
            this.id = idIn;
            this.particleSpeed = color;
        }

        public static AbstractNecromancer.SpellType getFromId(int idIn) {
            for(AbstractNecromancer.SpellType spellType : values()) {
                if (idIn == spellType.id) {
                    return spellType;
                }
            }

            return NONE;
        }
    }
}
