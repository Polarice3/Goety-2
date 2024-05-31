package com.Polarice3.Goety.common.entities.hostile.servants;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.neutral.BlazeServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class Inferno extends BlazeServant {

    public Inferno(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    public void attackGoal(){
        this.goalSelector.addGoal(4, new InfernoAttackGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.InfernoHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.InfernoArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.InfernoMeleeDamage.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.InfernoHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.InfernoArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.InfernoMeleeDamage.get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.INFERNO_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_32235_) {
        return ModSounds.INFERNO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.INFERNO_DEATH.get();
    }

    public boolean isOnFire() {
        return false;
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        if (this.isNatural()){
            return EntityType.BLAZE.getDefaultLootTable();
        } else {
            return super.getDefaultLootTable();
        }
    }

    protected void dropCustomDeathLoot(DamageSource p_33574_, int p_33575_, boolean p_33576_) {
        super.dropCustomDeathLoot(p_33574_, p_33575_, p_33576_);
        if (this.isNatural()) {
            if (this.level.getServer() != null) {
                LootTable loottable = this.level.getServer().getLootData().getLootTable(ModLootTables.INFERNO);
                LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) this.level)).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, p_33574_).withOptionalParameter(LootContextParams.KILLER_ENTITY, p_33574_.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, p_33574_.getDirectEntity());
                if (this.lastHurtByPlayerTime > 0 && this.lastHurtByPlayer != null) {
                    lootparams$builder = lootparams$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
                }

                LootParams lootparams = lootparams$builder.create(LootContextParamSets.ENTITY);
                loottable.getRandomItems(lootparams).forEach(this::spawnAtLocation);
            }
        }
    }

    @Override
    public void flyingTick() {
        LivingEntity livingentity = this.getTarget();
        if (livingentity != null && livingentity.getEyeY() + 1.0F > this.getEyeY() && this.canAttack(livingentity)) {
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, ((double)0.3F - vec3.y) * (double)0.3F, 0.0D));
            this.hasImpulse = true;
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.floatInferno();
    }

    @Override
    public void clientTick() {
        if (this.random.nextInt(24) == 0 && !this.isSilent()) {
            this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
        }

        for(int i = 0; i < 2; ++i) {
            this.level.addParticle(ModParticleTypes.BIG_FIRE.get(), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
        }
    }

    public boolean canStandOnFluid(FluidState p_204067_) {
        return p_204067_.is(FluidTags.LAVA);
    }

    private void floatInferno() {
        if (this.isInLava()) {
            CollisionContext collisioncontext = CollisionContext.of(this);
            if (collisioncontext.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level.getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setOnGround(true);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).add(0.0D, 0.05D, 0.0D));
            }
        }

    }

    public float getWalkTargetValue(@NotNull BlockPos p_33895_, LevelReader p_33896_) {
        if (p_33896_.getBlockState(p_33895_).getFluidState().is(FluidTags.LAVA)) {
            return 10.0F;
        } else {
            return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.isNatural()){
            this.setHostile(true);
        }
        if (this.isUpgraded()){
            AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
            AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (armor != null){
                armor.setBaseValue(AttributesConfig.InfernoArmor.get() * 2.0D);
            }
            if (speed != null){
                speed.setBaseValue(0.45D);
            }
        }
        return spawnGroupData;
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    protected boolean isAffectedByFluids() {
        return !this.isInWater();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FIRE)){
            this.heal(amount);
            return false;
        }
        return super.hurt(source, amount);
    }

    static class InfernoAttackGoal extends Goal {
        private final Inferno blaze;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public InfernoAttackGoal(Inferno p_32247_) {
            this.blaze = p_32247_;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.blaze.getTarget();
            return livingentity != null
                    && livingentity.isAlive()
                    && this.blaze.canAttack(livingentity);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
            this.blaze.setCharged(false);
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            --this.attackTime;
            LivingEntity livingentity = this.blaze.getTarget();
            if (livingentity != null) {
                boolean flag = this.blaze.getSensing().hasLineOfSight(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.blaze.distanceToSqr(livingentity);
                if (d0 < 4.0D) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0 || this.blaze.isCharged()) {
                        this.attackTime = 20;
                        this.blaze.doHurtTarget(livingentity);
                    }

                    this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    double d1 = livingentity.getX() - this.blaze.getX();
                    double d2 = livingentity.getY(0.5D) - this.blaze.getY(0.5D);
                    double d3 = livingentity.getZ() - this.blaze.getZ();
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 20;
                            this.blaze.setCharged(true);
                            if (!this.blaze.isSilent()) {
                                this.blaze.playSound(ModSounds.INFERNO_PRE_ATTACK.get(), 1.0F, 1.0F);
                            }
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            if (this.blaze.isUpgraded()){
                                this.attackTime = MathHelper.secondsToTicks(1);
                            } else {
                                this.attackTime = MathHelper.secondsToTicks(3);
                            }
                            this.attackStep = 0;
                            this.blaze.setCharged(false);
                        }

                        if (this.attackStep > 1) {
                            if (!this.blaze.isSilent()) {
                                this.blaze.playSound(ModSounds.HELL_BOLT_SHOOT.get(), 1.0F, 1.0F);
                            }

                            float damage = AttributesConfig.InfernoRangeDamage.get().floatValue() + this.blaze.getFireBallDamage();

                            for(int i = 0; i < 1; ++i) {
                                HellBolt hellBolt = new HellBolt(this.blaze, d1, d2, d3, this.blaze.level);
                                hellBolt.setPos(hellBolt.getX(), this.blaze.getY(0.5D) + 0.5D, hellBolt.getZ());
                                hellBolt.setDamage(damage);
                                this.blaze.level.addFreshEntity(hellBolt);
                            }
                        }
                    }
                    this.blaze.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.blaze.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), 1.0D);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}
