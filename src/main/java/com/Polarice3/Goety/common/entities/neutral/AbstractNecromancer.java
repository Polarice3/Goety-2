package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.WraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.entities.projectiles.SoulBolt;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.SoulJar;
import com.Polarice3.Goety.common.magic.spells.SoulBoltSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class AbstractNecromancer extends AbstractSkeletonServant implements RangedAttackMob {
    private static final EntityDataAccessor<Byte> SPELL = SynchedEntityData.defineId(AbstractNecromancer.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(AbstractNecromancer.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(AbstractNecromancer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(AbstractNecromancer.class, EntityDataSerializers.INT);
    public static int INITIAL_LEVEL = 0;
    public static int MAX_LEVEL = 2;
    public static String IDLE = "idle";
    public static String WALK = "walk";
    public static String ATTACK = "attack";
    public static String SUMMON = "summon";
    public static String SPELL_ANIM = "spell";
    protected List<EntityType<?>> summonList = new ArrayList<>();
    protected int spellCooldown;
    protected int idleSpellCool;
    public int cantDo;
    protected float attackSpeed = 1.0F;
    private NecromancerSpellType activeSpell = NecromancerSpellType.NONE;
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
        this.avoidGoal(2);
        this.projectileGoal(3);
    }

    public void projectileGoal(int priority){
        this.goalSelector.addGoal(priority, new NecromancerRangedGoal(this, 1.0D, 20, 12.0F));
    }

    public void avoidGoal(int priority){
        this.goalSelector.addGoal(priority, AvoidTargetGoal.AvoidRadiusGoal.newGoal(this, 2, 4, 1.0D, 1.2D));
    }

    public void summonSpells(int priority){
        this.goalSelector.addGoal(priority, new SummonZombieSpell());
        this.goalSelector.addGoal(priority + 1, new SummonUndeadGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.NecromancerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.NecromancerArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.NecromancerFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.NecromancerDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.NecromancerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.NecromancerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.NecromancerFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.NecromancerDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPELL, (byte)0);
        this.entityData.define(FLAGS, (byte)0);
        this.entityData.define(LEVEL, 0);
        this.entityData.define(ANIM_STATE, 0);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33609_) {
        if (LEVEL.equals(p_33609_)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
        }
        if (ANIM_STATE.equals(p_33609_)) {
            if (this.level.isClientSide) {
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.walkAnimationState);
                        break;
                    case 3:
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 4:
                        this.summonAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case 5:
                        this.spellAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.spellAnimationState);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_33609_);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SpellCoolDown")) {
            this.spellCooldown = compound.getInt("SpellCoolDown");
        }
        if (compound.contains("IdleSpellCool")) {
            this.idleSpellCool = compound.getInt("IdleSpellCool");
        }
        if (compound.contains("MultiShot")) {
            this.setNecroLevel(compound.getInt("MultiShot"));
        } else if (compound.contains("NecroLevel")){
            this.setNecroLevel(compound.getInt("NecroLevel"));
        }
        if (compound.contains("AttackSpeed")){
            this.setAttackSpeed(compound.getFloat("AttackSpeed"));
        }
        if (compound.contains("HasAlternate") && compound.getBoolean("HasAlternate")) {
            this.addSummon(ModEntityType.SKELETON_SERVANT.get());
        }
        if (compound.contains("SpawnUndeadIdle")) {
            this.setUndeadIdle(compound.getBoolean("SpawnUndeadIdle"));
        }
        if (compound.contains("SummonList", Tag.TAG_LIST)) {
            ListTag listTag = compound.getList("SummonList", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i){
                String string = listTag.getCompound(i).getString("id");
                if (EntityType.byString(string).isPresent()){
                    this.addSummon(EntityType.byString(string).get());
                }
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpellCoolDown", this.spellCooldown);
        compound.putInt("IdleSpellCool", this.idleSpellCool);
        compound.putInt("NecroLevel", this.getNecroLevel());
        compound.putFloat("AttackSpeed", this.getAttackSpeed());
        compound.putBoolean("SpawnUndeadIdle", this.spawnUndeadIdle());
        ListTag listTag = new ListTag();
        if (!this.getSummonList().isEmpty()) {
            for (EntityType<?> entityType : this.getSummonList()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putString("id", EntityType.getKey(entityType).toString());
                listTag.add(compoundTag);
            }
        }
        if (!listTag.isEmpty()){
            compound.put("SummonList", listTag);
        }
    }

    @Override
    public int xpReward() {
        return 20;
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219154_, DifficultyInstance p_219155_) {
    }

    public void setSpellCasting(boolean casting){
        this.setNecromancerFlags(1, casting);
    }

    public boolean isSpellCasting() {
        return this.getNecromancerFlags(1);
    }

    public void setUndeadIdle(boolean spawn){
        this.setNecromancerFlags(4, spawn);
    }

    public boolean spawnUndeadIdle(){
        return this.getNecromancerFlags(4);
    }

    public void setShooting(boolean shooting){
        this.setNecromancerFlags(8, shooting);
    }

    public boolean isShooting(){
        return this.getNecromancerFlags(8);
    }

    public void setAttackSpeed(float speed){
        this.attackSpeed = speed;
    }

    public float getAttackSpeed(){
        return this.attackSpeed;
    }

    public void setNecroLevel(int shot){
        int i = Mth.clamp(shot, 0, 2);
        this.entityData.set(LEVEL, i);
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance != null){
            attributeInstance.setBaseValue(AttributesConfig.NecromancerHealth.get() * Math.max(i * 1.25F, 1));
        }
        this.reapplyPosition();
        this.refreshDimensions();
    }

    public int getNecroLevel(){
        return this.entityData.get(LEVEL);
    }

    public List<EntityType<?>> getSummonList() {
        return this.summonList;
    }

    public void addSummon(EntityType<?> entityType){
        if (!this.summonList.contains(entityType) && entityType != this.getDefaultSummon().getType()) {
            this.summonList.add(entityType);
        }
    }

    public void setNecromancerSpellType(NecromancerSpellType necromancerSpellType) {
        this.activeSpell = necromancerSpellType;
        this.entityData.set(SPELL, (byte) necromancerSpellType.id);
    }

    protected NecromancerSpellType getNecromancerSpellType() {
        return !this.level.isClientSide ? this.activeSpell : NecromancerSpellType.getFromId(this.entityData.get(SPELL));
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        float f1 = (float)this.getNecroLevel();
        float size = 1.0F + Math.max(f1 * 0.15F, 0);
        return 2.17F * size;
    }

    public void reassessWeaponGoal() {
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.NECROMANCER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
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

    public @NotNull EntityDimensions getDimensions(@NotNull Pose p_33597_) {
        if (this.getNecroLevel() > 0) {
            float f1 = (float)this.getNecroLevel();
            float size = 1.0F + Math.max(f1 * 0.15F, 0);
            return super.getDimensions(p_33597_).scale(size);
        } else {
            return super.getDimensions(p_33597_);
        }
    }

    @Override
    public void die(DamageSource pCause) {
        if (this.getTrueOwner() != null && MobsConfig.NecromancerSoulJar.get()){
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

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, "idle")){
            return 1;
        } else if (Objects.equals(animation, "walk")){
            return 2;
        } else if (Objects.equals(animation, "attack")){
            return 3;
        } else if (Objects.equals(animation, "summon")){
            return 4;
        } else if (Objects.equals(animation, "spell")){
            return 5;
        } else {
            return 0;
        }
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

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void stopAllAnimations(){
        for (AnimationState animationState : this.getAnimations()){
            animationState.stop();
        }
    }

    public void tick() {
        super.tick();
        if (this.spellCooldown > 0) {
            --this.spellCooldown;
        }
        if (this.idleSpellCool > 0) {
            --this.idleSpellCool;
        }
        if (this.cantDo > 0){
            --this.cantDo;
        }
        if (this.level.isClientSide){
            if (this.isAlive()){
                if (this.isSpellCasting()){
                    this.spellCastParticles();
                }
            }
        } else {
            if (!this.isShooting() && !this.isSpellCasting()) {
                if (!this.isMoving()) {
                    this.setAnimationState(IDLE);
                } else {
                    this.setAnimationState(WALK);
                }
            }
        }
    }

    public void spellCastParticles(){
        double d0 = MathHelper.rgbParticle(this.getNecromancerSpellType().particleSpeed)[0];
        double d1 = MathHelper.rgbParticle(this.getNecromancerSpellType().particleSpeed)[1];
        double d2 = MathHelper.rgbParticle(this.getNecromancerSpellType().particleSpeed)[2];
        for (int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
            this.level.addParticle(ModParticleTypes.CULT_SPELL.get(), this.getX(), this.getY(), this.getZ(), d0, d1, d2);
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity p_33317_, float p_33318_) {
        if (this.getNecroLevel() <= 0) {
            new SoulBoltSpell().SpellResult(this.level, this, ItemStack.EMPTY);
        } else {
            for (int i = -this.getNecroLevel(); i <= this.getNecroLevel(); i++) {
                Vec3 vector3d = this.getViewVector(1.0F);
                SoulBolt soulBolt = new SoulBolt(this, vector3d.x + (i / 10.0F), vector3d.y, vector3d.z + (i / 10.0F), this.level);
                soulBolt.setPos(this.getX() + vector3d.x / 2, this.getEyeY() - 0.2, this.getZ() + vector3d.z / 2);
                if (this.level.addFreshEntity(soulBolt)) {
                    SoundUtil.playSoulBolt(this);
                    this.swing(InteractionHand.MAIN_HAND);
                }
            }
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand p_230254_2_) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(p_230254_2_);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (!this.spawnUndeadIdle() && itemstack.isEmpty()){
                    if (this.idleSpellCool <= 0){
                        this.setUndeadIdle(true);
                    } else {
                        this.playSound(ModSounds.NECROMANCER_HURT.get());
                        this.level.broadcastEntityEvent(this, (byte) 9);
                    }
                    return InteractionResult.SUCCESS;
                } else if (item == Items.BONE && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(ModSounds.NECROMANCER_STEP.get(), 1.0F, 1.25F);
                    this.heal(2.0F);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.SKELETON_SERVANT.get()) && item == ModItems.OSSEOUS_FOCUS.get()){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.SKELETON_SERVANT.get());
                    this.playSound(ModSounds.NECROMANCER_LAUGH.get());
                    return InteractionResult.SUCCESS;
                } else if (/*this.getNecroLevel() > 0 && */!this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get()) && item == ModItems.SPOOKY_FOCUS.get()){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.WRAITH_SERVANT.get());
                    this.playSound(ModSounds.NECROMANCER_LAUGH.get());
                    return InteractionResult.SUCCESS;
                } else if (/*this.getNecroLevel() > 1 && */!this.getSummonList().contains(ModEntityType.VANGUARD_SERVANT.get()) && item == ModItems.VANGUARD_FOCUS.get()){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.VANGUARD_SERVANT.get());
                    this.playSound(ModSounds.NECROMANCER_LAUGH.get());
                    return InteractionResult.SUCCESS;
                } else if (item == ModItems.SOUL_JAR.get()){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (this.getNecroLevel() < 2) {
                        this.setNecroLevel(this.getNecroLevel() + 1);
                    }
                    this.heal(AttributesConfig.NecromancerHealth.get().floatValue());
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 1.0F, 0.5F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public Summoned getDefaultSummon(){
        return new ZombieServant(ModEntityType.ZOMBIE_SERVANT.get(), this.level);
    }

    public Summoned getSummon(){
        Summoned summoned = getDefaultSummon();
        if (this.getSummonList().contains(ModEntityType.ZOMBIE_SERVANT.get())) {
            if (this.level.random.nextBoolean()) {
                summoned = new ZombieServant(ModEntityType.ZOMBIE_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.SKELETON_SERVANT.get())) {
            if (this.level.random.nextBoolean()) {
                summoned = new SkeletonServant(ModEntityType.SKELETON_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get())) {
            if (this.level.random.nextFloat() <= 0.05F) {
                summoned = new WraithServant(ModEntityType.WRAITH_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.VANGUARD_SERVANT.get())){
            if (this.level.random.nextFloat() <= 0.15F) {
                summoned = new VanguardServant(ModEntityType.VANGUARD_SERVANT.get(), this.level);
            }
        }
        return summoned;
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
            AbstractNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
        }

        public void tick() {
            if (AbstractNecromancer.this.getTarget() != null) {
                AbstractNecromancer.this.getLookControl().setLookAt(AbstractNecromancer.this.getTarget(), (float) AbstractNecromancer.this.getMaxHeadYRot(), (float) AbstractNecromancer.this.getMaxHeadXRot());
            }
        }
    }

    public abstract class SummoningSpellGoal extends Goal {
        protected int spellTime;

        protected SummoningSpellGoal() {
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
            return this.spellTime > 0;
        }

        public void start() {
            this.spellTime = this.getCastingTime();
            AbstractNecromancer.this.setSpellCooldown(this.getCastingInterval());
            if (this.getSpellPrepareSound() != null) {
                AbstractNecromancer.this.playSound(this.getSpellPrepareSound(), 1.0F, 1.0F);
            }
            AbstractNecromancer.this.setAnimationState(SUMMON);
            AbstractNecromancer.this.setSpellCasting(true);
            AbstractNecromancer.this.setNecromancerSpellType(this.getNecromancerSpellType());
        }

        @Override
        public void stop() {
            AbstractNecromancer.this.setSpellCasting(false);
            AbstractNecromancer.this.setAnimationState(IDLE);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 10) {
                if (this.getCastSound() != null) {
                    AbstractNecromancer.this.playSound(this.getCastSound(), 1.0F, 1.0F);
                } else {
                    SoundUtil.playNecromancerSummon(AbstractNecromancer.this);
                }
                AbstractNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, AbstractNecromancer.this.getVoicePitch());
                this.castSpell();
                AbstractNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            }
        }

        protected abstract void castSpell();

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval(){
            return 100;
        };

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        @Nullable
        protected SoundEvent getCastSound(){
            return null;
        }

        protected abstract NecromancerSpellType getNecromancerSpellType();
    }

    public class SummonZombieSpell extends SummoningSpellGoal {

        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned && owned.getTrueOwner() instanceof AbstractNecromancer;
            int i = AbstractNecromancer.this.level.getEntitiesOfClass(LivingEntity.class, AbstractNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
            , predicate).size();
            return super.canUse() && i < 7;
        }

        protected void castSpell(){
            if (AbstractNecromancer.this.level instanceof ServerLevel serverLevel) {
                Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned && owned.getTrueOwner() instanceof AbstractNecromancer;
                int i = AbstractNecromancer.this.level.getEntitiesOfClass(LivingEntity.class, AbstractNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                        , predicate).size();
                if (i < 7) {
                    int j = 7 - i;
                    for (int i1 = 0; i1 < 1 + serverLevel.random.nextInt(j); ++i1) {
                        Summoned summonedentity = AbstractNecromancer.this.getSummon();
                        BlockPos blockPos = BlockFinder.SummonRadius(AbstractNecromancer.this.blockPosition(), summonedentity, serverLevel);
                        summonedentity.setTrueOwner(AbstractNecromancer.this);
                        summonedentity.moveTo(blockPos, AbstractNecromancer.this.getYRot(), AbstractNecromancer.this.getXRot());
                        if (MobsConfig.NecromancerSummonsLife.get()) {
                            summonedentity.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                        }
                        summonedentity.setPersistenceRequired();
                        summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(AbstractNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        summonedentity.setBaby(false);
                        this.populateDefaultEquipmentSlots(summonedentity, serverLevel.random);
                        if (serverLevel.addFreshEntity(summonedentity)) {
                            SoundUtil.playNecromancerSummon(summonedentity);
                        }
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
            return ModSounds.PREPARE_SUMMON.get();
        }

        @Override
        protected NecromancerSpellType getNecromancerSpellType() {
            return NecromancerSpellType.ZOMBIE;
        }
    }

    public class SummonUndeadGoal extends Goal{
        protected int spellTime;

        @Override
        public boolean canUse() {
            if (AbstractNecromancer.this.getTarget() != null && AbstractNecromancer.this.getTarget().isAlive()){
                return false;
            } else if (AbstractNecromancer.this.isShooting()){
                return false;
            } else if (AbstractNecromancer.this.isSpellCasting()) {
                return false;
            } else if (!AbstractNecromancer.this.spawnUndeadIdle()){
                return false;
            } else {
                return AbstractNecromancer.this.getSpellCooldown() <= 0 && AbstractNecromancer.this.idleSpellCool <= 0;
            }
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0 && AbstractNecromancer.this.hurtTime <= 0;
        }

        public void start() {
            this.spellTime = 29;
            AbstractNecromancer.this.setSpellCooldown(100);
            AbstractNecromancer.this.playSound(ModSounds.PREPARE_SUMMON.get(), 1.0F, 1.0F);
            AbstractNecromancer.this.setSpellCasting(true);
            AbstractNecromancer.this.setNecromancerSpellType(NecromancerSpellType.ZOMBIE);
            AbstractNecromancer.this.setAnimationState(SPELL_ANIM);
        }

        @Override
        public void stop() {
            super.stop();
            AbstractNecromancer.this.setSpellCasting(false);
            AbstractNecromancer.this.setAnimationState(IDLE);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 0) {
                AbstractNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, AbstractNecromancer.this.getVoicePitch());
                AbstractNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
                int i = 2 + AbstractNecromancer.this.level.random.nextInt(4);
                for (int i1 = 0; i1 < i; ++i1) {
                    if (AbstractNecromancer.this.level instanceof ServerLevel serverLevel) {
                        Summoned summonedentity = AbstractNecromancer.this.getSummon();
                        EntityType<?> entityType = summonedentity.getVariant(serverLevel, AbstractNecromancer.this.blockPosition());
                        if (entityType != null && entityType.create(serverLevel) instanceof Summoned summoned) {
                            summonedentity = summoned;
                        }
                        BlockPos blockPos = BlockFinder.SummonRadius(AbstractNecromancer.this.blockPosition(), summonedentity, serverLevel);
                        LivingEntity owner = AbstractNecromancer.this.getTrueOwner() != null ? AbstractNecromancer.this.getTrueOwner() : AbstractNecromancer.this;
                        summonedentity.setTrueOwner(owner);
                        summonedentity.moveTo(blockPos, AbstractNecromancer.this.getYRot(), AbstractNecromancer.this.getXRot());
                        if (MobsConfig.NecromancerSummonsLife.get()) {
                            summonedentity.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                        }
                        summonedentity.setPersistenceRequired();
                        summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(AbstractNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        this.populateDefaultEquipmentSlots(summonedentity, serverLevel.random);
                        if (serverLevel.addFreshEntity(summonedentity)){
                            SoundUtil.playNecromancerSummon(summonedentity);
                        }
                    }
                }
                AbstractNecromancer.this.setUndeadIdle(false);
                AbstractNecromancer.this.idleSpellCool = MathHelper.secondsToTicks(20);
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
        private final int attackInterval;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public NecromancerRangedGoal(AbstractNecromancer mob, double speed, int attackInterval, float attackRadius) {
            this.mob = mob;
            this.speedModifier = speed;
            this.attackInterval = attackInterval;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
            this.mob.setShooting(false);
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

                int speed = Mth.floor(Math.max(this.mob.getAttackSpeed(), 1.0F));
                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                int attackIntervalMin = this.attackInterval / speed;
                --this.attackTime;
                if (this.attackTime <= 5) {
                    this.mob.setShooting(true);
                    if (this.mob.getCurrentAnimation() != this.mob.getAnimationState(ATTACK)) {
                        this.mob.setAnimationState(ATTACK);
                    }
                }
                if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.mob.performRangedAttack(this.target, f1);
                    this.attackTime = attackIntervalMin;
                } else if (this.attackTime < 0) {
                    this.mob.setShooting(false);
                    this.attackTime = attackIntervalMin;
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 9){
            this.cantDo = 40;
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public enum NecromancerSpellType {
        NONE(0, 0xffffff),
        ZOMBIE(1, 0x16feff),
        CLOUD(2, 0xf2ffff);

        private final int id;
        private final int particleSpeed;

        NecromancerSpellType(int idIn, int color) {
            this.id = idIn;
            this.particleSpeed = color;
        }

        public static NecromancerSpellType getFromId(int idIn) {
            for(NecromancerSpellType necromancerSpellType : values()) {
                if (idIn == necromancerSpellType.id) {
                    return necromancerSpellType;
                }
            }

            return NONE;
        }
    }
}
