package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.CreatureBowAttackGoal;
import com.Polarice3.Goety.common.entities.projectiles.NecroBolt;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Doppelganger extends Summoned implements RangedAttackMob {
    private static final EntityDataAccessor<Byte> DOPPELGANGER_FLAGS = SynchedEntityData.defineId(Doppelganger.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.defineId(Doppelganger.class, EntityDataSerializers.BYTE);
    public float oBob;
    public float bob;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    @Nullable
    private PlayerInfo playerInfo;

    public Doppelganger(EntityType<? extends Doppelganger> type, Level worldIn) {
        super(type, worldIn);
    }

    public void tick() {
        for (Mob mob: this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(16.0F))) {
            if (this.getTrueOwner() != null){
                if (mob.getTarget() == this.getTrueOwner()){
                    if (this.getTarget() != mob) {
                        this.setTarget(mob);
                    }
                    mob.setTarget(this);
                }
            } else {
                mob.setTarget(this);
            }
        }

        this.oBob = this.bob;
        float f = Math.min(0.1F, Mth.sqrt((float) getHorizontalDistanceSqr(this.getDeltaMovement())));
        this.bob += (f - this.bob) * 0.4F;

        this.moveCloak();

        if (this.getTrueOwner() != null){
            if (!this.isUndeadClone()){
                if (this.getTrueOwner().hurtTime == this.getTrueOwner().hurtDuration - 1){
                    this.die(DamageSource.STARVE);
                }
            }
            this.setCustomName(this.getTrueOwner().getDisplayName());
            if (this.getTrueOwner().getMaxHealth() != this.getMaxHealth()){
                AttributeInstance attributeInstance = this.getAttribute(Attributes.MAX_HEALTH);
                if (attributeInstance != null){
                    attributeInstance.setBaseValue(this.getTrueOwner().getMaxHealth());
                }
            }
        }

        if (this.isUndeadClone()){
            this.getNavigation().stop();
            if (this.getTarget() != null){
                this.getLookControl().setLookAt(this.getTarget());
            }
            if (this.getTrueOwner() == null || (this.getTrueOwner() != null && this.getTrueOwner().isDeadOrDying())){
                this.die(DamageSource.STARVE);
            }
        }

        if (this.hasShot()){
            if ((this.tickCount % 40 == 0 && this.random.nextFloat() <= 0.25F) || this.tickCount % 100 == 0){
                this.die(DamageSource.STARVE);
            }
        }
        if (LichdomHelper.isInLichMode(this.getTrueOwner())) {
            if (this.tickCount % 5 == 0) {
                if (this.level.isClientSide) {
                    this.level.addParticle(ModParticleTypes.LICH.get(), this.getRandomX(0.5D), this.getY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
                }
            }
        }
        super.tick();
    }

    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.getX() - this.xCloak;
        double d1 = this.getY() - this.yCloak;
        double d2 = this.getZ() - this.zCloak;
        double d3 = 10.0D;
        if (d0 > d3) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > d3) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > d3) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -d3) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -d3) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -d3) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25D;
        this.zCloak += d2 * 0.25D;
        this.yCloak += d1 * 0.25D;
    }

    public static double getHorizontalDistanceSqr(Vec3 pVector) {
        return pVector.x * pVector.x + pVector.z * pVector.z;
    }

    @Override
    public void lifeSpanDamage() {
        if (!this.level.isClientSide){
            for(int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), this.level);
            }
        }
        this.discard();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new NecroBoltGoal(this));
        this.goalSelector.addGoal(4, new CreatureBowAttackGoal<>(this, 1.0D, 20, 20.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 18.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MAX_HEALTH, 20.0D);
    }

    public boolean doHurtTarget(Entity entityIn) {
        return this.isUndeadClone();
    }

    protected SoundEvent getAmbientSound() {
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.GENERIC_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DOPPELGANGER_FLAGS, (byte)0);
        this.entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("UndeadClone")) {
            this.setUndeadClone(compound.getBoolean("UndeadClone"));
        }
        if (compound.contains("Shot")) {
            this.setShot(compound.getBoolean("Shot"));
        }
        if (compound.contains("TickCount")){
            this.tickCount = compound.getInt("TickCount");
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("UndeadClone", this.isUndeadClone());
        compound.putBoolean("Shot", this.hasShot());
        compound.putInt("TickCount", this.tickCount);
    }

    private boolean getDoppelgangerFlags(int mask) {
        int i = this.entityData.get(DOPPELGANGER_FLAGS);
        return (i & mask) != 0;
    }

    private void setDoppelgangerFlags(int mask, boolean value) {
        int i = this.entityData.get(DOPPELGANGER_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DOPPELGANGER_FLAGS, (byte)(i & 255));
    }

    public boolean isUndeadClone(){
        return this.getDoppelgangerFlags(1);
    }

    public void setUndeadClone(boolean undeadClone){
        this.setDoppelgangerFlags(1, undeadClone);
    }

    public boolean hasShot(){
        return this.getDoppelgangerFlags(2);
    }

    public void setShot(boolean undeadClone){
        this.setDoppelgangerFlags(2, undeadClone);
    }

    public boolean isCapeLoaded() {
        return this.getPlayerInfo() != null;
    }

    public boolean isModelPartShown(PlayerModelPart p_36171_) {
        return (this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & p_36171_.getMask()) == p_36171_.getMask();
    }

    public ResourceLocation getSkinTextureLocation() {
        PlayerInfo playerinfo = this.getPlayerInfo();
        return playerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUUID()) : playerinfo.getSkinLocation();
    }

    public String getModelName() {
        PlayerInfo playerinfo = this.getPlayerInfo();
        return playerinfo == null ? DefaultPlayerSkin.getSkinModelName(this.getUUID()) : playerinfo.getModelName();
    }

    @Nullable
    public ResourceLocation getCloakTextureLocation() {
        PlayerInfo playerinfo = this.getPlayerInfo();
        return playerinfo == null ? null : playerinfo.getCapeLocation();
    }

    @Nullable
    protected PlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            if (this.getOwnerId() != null) {
                this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getOwnerId());
            }
        }

        return this.playerInfo;
    }

    @Override
    public boolean isLeftHanded() {
        if (this.getTrueOwner() instanceof Player player){
            return player.getMainArm() == HumanoidArm.RIGHT;
        }
        return super.isLeftHanded();
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (this.isUndeadClone()){
            return source.isBypassInvul()
                    || (source.isBypassArmor()
                    && source.isBypassMagic());
        } else if (flag) {
            if (!this.level.isClientSide) {
                this.die(source);
            }
        }
        return flag;
    }

    public void die(DamageSource cause) {
        if (!this.level.isClientSide) {
            for (int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), this.level);
            }
        }
        this.playSound(SoundEvents.ILLUSIONER_MIRROR_MOVE, 1.0F, 1.0F);
        this.discard();
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(randomSource, difficulty);
        if (this.getTrueOwner() != null){
            for (EquipmentSlot equipmentSlotType: EquipmentSlot.values()){
                if (equipmentSlotType != EquipmentSlot.MAINHAND) {
                    this.setItemSlot(equipmentSlotType, this.getTrueOwner().getItemBySlot(equipmentSlotType).copy());
                    this.setDropChance(equipmentSlotType, 0.0F);
                }
            }
        }
        if (this.isUndeadClone()){
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.NAMELESS_STAFF.get()));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return potioneffectIn.getEffect() == MobEffects.GLOWING;
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.populateDefaultEquipmentSlots(pLevel.getRandom(), pDifficulty);
        this.populateDefaultEquipmentEnchantments(pLevel.getRandom(), pDifficulty);
        return pSpawnData;
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!this.isUndeadClone()) {
            ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
            AbstractArrow abstractarrowentity = this.getMobArrow(itemstack, distanceFactor);
            abstractarrowentity = ((BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333D) - abstractarrowentity.getY();
            double d2 = target.getZ() - this.getZ();
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
            this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level.addFreshEntity(abstractarrowentity);
        } else {
            double d3 = target.getX() - this.getX();
            double d4 = (target.getY() + 0.5F) - (this.getY() + 0.5F);
            double d5 = target.getZ() - this.getZ();
            NecroBolt soulBolt = new NecroBolt(this, d3, d4, d5, this.level);
            soulBolt.setPos(this.getX(), this.getEyeY() - 0.2F, this.getZ());
            soulBolt.setOwner(this);
            if (this.level.addFreshEntity(soulBolt)) {
                this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
                this.setShot(true);
            }
            this.swing(InteractionHand.MAIN_HAND);
        }
    }

    protected AbstractArrow getMobArrow(ItemStack arrowStack, float distanceFactor) {
        AbstractArrow abstractarrowentity = ProjectileUtil.getMobArrow(this, arrowStack, distanceFactor);
        if (!this.isUpgraded()) {
            abstractarrowentity.setBaseDamage(0.0F);
        } else {
            abstractarrowentity.setBaseDamage(0.5F);
        }
        return abstractarrowentity;
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
        return p_230280_1_ == Items.BOW;
    }

    public static class NecroBoltGoal extends Goal {
        private final Doppelganger rangedAttackMob;
        @Nullable
        private LivingEntity target;

        public NecroBoltGoal(Doppelganger p_25773_) {
            this.rangedAttackMob = p_25773_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.rangedAttackMob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return this.rangedAttackMob.isUndeadClone() && !this.rangedAttackMob.hasShot();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || this.target.isAlive() && !this.rangedAttackMob.getNavigation().isDone() && !this.rangedAttackMob.hasShot();
        }

        public void stop() {
            this.target = null;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null) {
                this.rangedAttackMob.getLookControl().setLookAt(this.target);
                if (this.rangedAttackMob.tickCount % 20 == 0) {
                    this.rangedAttackMob.performRangedAttack(this.target, 0);
                }
            }
        }
    }
}
