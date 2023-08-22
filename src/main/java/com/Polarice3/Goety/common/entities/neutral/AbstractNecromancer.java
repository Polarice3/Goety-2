package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.AttributesConfig;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ally.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.SkeletonServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.ZombieServant;
import com.Polarice3.Goety.common.entities.projectiles.SoulBolt;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public abstract class AbstractNecromancer extends AbstractSkeletonServant implements RangedAttackMob {
    private static final EntityDataAccessor<Byte> SPELL = SynchedEntityData.defineId(AbstractNecromancer.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(AbstractNecromancer.class, EntityDataSerializers.BYTE);
    protected int spellCooldown;
    private SpellType activeSpell = SpellType.NONE;

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
        this.goalSelector.addGoal(priority, new NecromancerAttackGoal(this));
    }

    public void summonSpells(int priority){
        this.goalSelector.addGoal(priority, new SummonZombieSpell());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.NecromancerHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    public AttributeSupplier.Builder getConfiguredAttributes(){
        return setCustomAttributes();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPELL, (byte)0);
        this.entityData.define(FLAGS, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.spellCooldown = compound.getInt("SpellCoolDown");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpellCoolDown", this.spellCooldown);
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

    public void tick() {
        super.tick();
        if (this.spellCooldown > 0) {
            --this.spellCooldown;
        }
        if (this.level.isClientSide && this.isSpellCasting() && this.isAlive()) {
            double d0 = MathHelper.rgbParticle(this.getSpellType().particleSpeed)[0];
            double d1 = MathHelper.rgbParticle(this.getSpellType().particleSpeed)[1];
            double d2 = MathHelper.rgbParticle(this.getSpellType().particleSpeed)[2];
            for (int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                this.level.addParticle(ModParticleTypes.CULT_SPELL.get(), this.getX(), this.getY(), this.getZ(), d0, d1, d2);
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
            if (this.spellTime == 0) {
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
            return 10;
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
                    summonedentity.setOwnerId(AbstractNecromancer.this.getUUID());
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

    public static class NecromancerAttackGoal extends RangedAttackGoal{
        public AbstractNecromancer necromancer;

        public NecromancerAttackGoal(AbstractNecromancer p_25768_) {
            super(p_25768_, 1.0D, 20, 10.0F);
            this.necromancer = p_25768_;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && necromancer.getTarget() != null;
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
