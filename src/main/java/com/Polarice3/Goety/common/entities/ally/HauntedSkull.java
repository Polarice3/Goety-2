package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.client.render.HauntedSkullTextures;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Minion;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class HauntedSkull extends Minion {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(HauntedSkull.class, EntityDataSerializers.INT);
    private int burning = 0;
    private float explosionPower = 1.0F;
    public boolean upgraded;

    public HauntedSkull(EntityType<? extends Minion> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new ChargeGoal());
        this.goalSelector.addGoal(8, new RandomMoveGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new SummonTargetGoal<>(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
    }

    public ResourceLocation getResourceLocation() {
        return HauntedSkullTextures.TEXTURES.getOrDefault(this.getAnimation(), HauntedSkullTextures.TEXTURES.get(0));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0F)
                .add(Attributes.ATTACK_DAMAGE, 5.0F);
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (this.level.isClientSide) {
            Vec3 vector3d = this.getDeltaMovement();
            double d0 = this.getX() + vector3d.x;
            double d1 = this.getY() + vector3d.y;
            double d2 = this.getZ() + vector3d.z;
            ParticleOptions particleData = ParticleTypes.SMOKE;
            if (this.isCharging()){
                particleData = ParticleTypes.SOUL_FIRE_FLAME;
                if (this.getAnimation() < 16){
                    this.setAnimation(this.getAnimation() + 1);
                } else {
                    this.setAnimation(9);
                }
            } else {
                if (this.getAnimation() < 8){
                    this.setAnimation(this.getAnimation() + 1);
                } else {
                    this.setAnimation(1);
                }
            }
            for(int j = 0; j < 2; ++j) {
                this.level.addParticle(particleData, d0 + this.random.nextGaussian() * (double)0.3F, d1 + this.random.nextGaussian() * (double)0.3F, d2 + this.random.nextGaussian() * (double)0.3F, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void lifeSpanDamage(){
        this.limitedLifeTicks = 20;
        this.explode();
    }

    public void move(MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
    }

    public void readAdditionalSaveData(CompoundTag p_34008_) {
        super.readAdditionalSaveData(p_34008_);
        this.setAnimation(p_34008_.getInt("Animation"));
        this.setBurning(p_34008_.getInt("Burning"));
        this.setExplosionPower(p_34008_.getFloat("ExplosionPower"));
        this.setUpgraded(p_34008_.getBoolean("Upgraded"));
        if (p_34008_.contains("Burning")){
            this.burning = p_34008_.getInt("Burning");
        }
    }

    public void addAdditionalSaveData(CompoundTag p_34015_) {
        super.addAdditionalSaveData(p_34015_);
        p_34015_.putInt("Animation", this.getAnimation());
        p_34015_.putInt("Burning", this.getBurning());
        p_34015_.putFloat("ExplosionPower", this.getExplosionPower());
        p_34015_.putBoolean("Upgraded", this.isUpgraded());
    }

    public float getExplosionPower() {
        return this.explosionPower;
    }

    public void setExplosionPower(float explosionPower) {
        this.explosionPower = explosionPower;
    }

    public int getBurning() {
        return this.burning;
    }

    public void setBurning(int burning) {
        this.burning = burning;
    }

    public void explode(){
        boolean loot = false;
        if (this.getTrueOwner() instanceof Player player){
            if (CuriosFinder.findRing(player).getItem() == ModItems.RING_OF_WANT.get()){
                if (CuriosFinder.findRing(player).isEnchanted()){
                    float wanting = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WANTING.get(), CuriosFinder.findRing(player));
                    if (wanting > 0){
                        loot = true;
                    }
                }
            }
        }
        LootingExplosion.Mode lootMode = loot ? LootingExplosion.Mode.LOOT : LootingExplosion.Mode.REGULAR;
        ExplosionUtil.lootExplode(this.level, this, this.getX(), this.getY(), this.getZ(), this.explosionPower, false, Explosion.BlockInteraction.NONE, lootMode);
        this.discard();
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.FIRE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SKELETON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34002_, DifficultyInstance p_34003_, MobSpawnType p_34004_, @Nullable SpawnGroupData p_34005_, @Nullable CompoundTag p_34006_) {
        RandomSource randomsource = p_34002_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_34003_);
        this.populateDefaultEquipmentEnchantments(randomsource, p_34003_);
        if (this.getTrueOwner() == null){
            this.setBoundOrigin(this.blockPosition());
        }
        return super.finalizeSpawn(p_34002_, p_34003_, p_34004_, p_34005_, p_34006_);
    }

    public boolean isUpgraded() {
        return this.upgraded;
    }

    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
    }

    public void playChargeCry(){
        this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 1.0F);
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public class RandomMoveGoal extends Goal {
        public RandomMoveGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !HauntedSkull.this.getMoveControl().hasWanted() && HauntedSkull.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = HauntedSkull.this.blockPosition();
            if (HauntedSkull.this.getTrueOwner() != null){
                blockpos = new BlockPos(HauntedSkull.this.getTrueOwner().getEyePosition());
            } else if (HauntedSkull.this.getTarget() != null){
                blockpos = new BlockPos(HauntedSkull.this.getTarget().getEyePosition());
            } else if (HauntedSkull.this.getBoundOrigin() != null){
                blockpos = HauntedSkull.this.getBoundOrigin();
            }

            if (blockpos != null) {
                if (HauntedSkull.this.getTrueOwner() != null && HauntedSkull.this.distanceTo(HauntedSkull.this.getTrueOwner()) > 8.0D){
                    blockpos = new BlockPos(HauntedSkull.this.getTrueOwner().getEyePosition());
                    HauntedSkull.this.moveControl.setWantedPosition((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D, 1.0D);
                } else {
                    for (int i = 0; i < 3; ++i) {
                        BlockPos blockpos1 = blockpos.offset(HauntedSkull.this.random.nextInt(15) - 7, HauntedSkull.this.random.nextInt(4) - 2, HauntedSkull.this.random.nextInt(15) - 7);
                        if (BlockFinder.isEmptyBox(HauntedSkull.this.level, blockpos1)) {
                            HauntedSkull.this.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                            if (HauntedSkull.this.getTarget() == null) {
                                HauntedSkull.this.getLookControl().setLookAt((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                            }
                            break;
                        }
                    }
                }
            }

        }
    }

    public class ChargeGoal extends Goal {
        public ChargeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            LivingEntity livingentity = HauntedSkull.this.getTarget();
            return livingentity != null && livingentity.isAlive() && !HauntedSkull.this.isInWall() && !HauntedSkull.this.getMoveControl().hasWanted();
        }

        public boolean canContinueToUse() {
            return HauntedSkull.this.getMoveControl().hasWanted() && HauntedSkull.this.isCharging() && HauntedSkull.this.getTarget() != null && HauntedSkull.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = HauntedSkull.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                HauntedSkull.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 2.0D);
            }

            HauntedSkull.this.setIsCharging(true);
            HauntedSkull.this.playChargeCry();
        }

        public void stop() {
            HauntedSkull.this.setIsCharging(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = HauntedSkull.this.getTarget();
            if (livingentity != null) {
                if (HauntedSkull.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    dealDamage(livingentity);
                } else {
                    double d0 = HauntedSkull.this.distanceToSqr(livingentity);
                    if (d0 < 9.0D) {
                        Vec3 vec3 = livingentity.getEyePosition();
                        HauntedSkull.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 2.0D);
                    }
                }
            }
        }

        public void dealDamage(LivingEntity livingEntity){
            boolean flag;
            if (HauntedSkull.this.getTrueOwner() != null) {
                flag = livingEntity.hurt(DamageSource.indirectMagic(HauntedSkull.this, HauntedSkull.this.getTrueOwner()), (float) HauntedSkull.this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            } else {
                flag = livingEntity.hurt(DamageSource.MAGIC, (float) HauntedSkull.this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
            if (flag) {
                if (HauntedSkull.this.isUpgraded()) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ModMathHelper.ticksToSeconds(5), 1));
                }
                if (HauntedSkull.this.getBurning() > 0) {
                    livingEntity.setSecondsOnFire(HauntedSkull.this.getBurning() * 8);
                }
                HauntedSkull.this.explode();
            }
        }
    }

}
