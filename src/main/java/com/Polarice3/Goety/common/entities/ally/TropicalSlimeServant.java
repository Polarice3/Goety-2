package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.client.render.TropicalSlimeTextures;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;

import java.util.EnumSet;

public class TropicalSlimeServant extends SlimeServant{
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(TropicalSlimeServant.class, EntityDataSerializers.INT);

    public TropicalSlimeServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new TropicalSlimeMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public ResourceLocation getResourceLocation() {
        return TropicalSlimeTextures.TEXTURES.getOrDefault(this.getAnimation(), TropicalSlimeTextures.TEXTURES.get(0));
    }

    public void slimeGoal(){
        this.goalSelector.addGoal(2, new SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new SlimeKeepOnJumpingGoal(this));
    }

    public void followGoal(){
        this.goalSelector.addGoal(8, new SlimeFollowGoal(this, 4.0F, 10.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE)
                .add(ForgeMod.SWIM_SPEED.get(), 0.1D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAnimation(pCompound.getInt("Animation"));
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Animation", this.getAnimation());
    }

    protected void dropCustomDeathLoot(DamageSource p_33574_, int p_33575_, boolean p_33576_) {
        super.dropCustomDeathLoot(p_33574_, p_33575_, p_33576_);
        if (this.level.getServer() != null) {
            if (this.shouldDropLoot()) {
                LootTable loottable = this.level.getServer().getLootData().getLootTable(ModLootTables.TROPICAL_SLIME);
                LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) this.level)).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, p_33574_).withOptionalParameter(LootContextParams.KILLER_ENTITY, p_33574_.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, p_33574_.getDirectEntity());
                if (this.lastHurtByPlayerTime > 0 && this.lastHurtByPlayer != null) {
                    lootparams$builder = lootparams$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
                }

                LootParams lootparams = lootparams$builder.create(LootContextParamSets.ENTITY);
                loottable.getRandomItems(lootparams, this.getLootTableSeed(), this::spawnAtLocation);
            }
        }
    }

    protected float getSoundPitch() {
        return this.getVoicePitch();
    }

    protected SoundEvent getHurtSound(DamageSource p_33631_) {
        if (this.isTiny()) {
            return ModSounds.TROPICAL_SLIME_SMALL_HURT.get();
        } else if (this.isMedium()) {
            return ModSounds.TROPICAL_SLIME_MEDIUM_HURT.get();
        } else if (this.isLarge()) {
            return ModSounds.TROPICAL_SLIME_LARGE_HURT.get();
        }
        return ModSounds.TROPICAL_SLIME_SMALL_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        if (this.isTiny()) {
            return ModSounds.TROPICAL_SLIME_SMALL_DEATH.get();
        } else if (this.isMedium()) {
            return ModSounds.TROPICAL_SLIME_MEDIUM_DEATH.get();
        } else if (this.isLarge()) {
            return ModSounds.TROPICAL_SLIME_LARGE_DEATH.get();
        }
        return ModSounds.TROPICAL_SLIME_SMALL_DEATH.get();
    }

    protected SoundEvent getSquishSound() {
        if (this.isTiny()) {
            return ModSounds.TROPICAL_SLIME_SMALL_SQUISH.get();
        } else if (this.isMedium()) {
            return ModSounds.TROPICAL_SLIME_MEDIUM_SQUISH.get();
        } else if (this.isLarge()) {
            return ModSounds.TROPICAL_SLIME_LARGE_SQUISH.get();
        }
        return ModSounds.TROPICAL_SLIME_SMALL_SQUISH.get();
    }

    protected SoundEvent getJumpSound() {
        if (this.isTiny()) {
            return ModSounds.TROPICAL_SLIME_SMALL_JUMP.get();
        } else if (this.isMedium()) {
            return ModSounds.TROPICAL_SLIME_MEDIUM_JUMP.get();
        } else if (this.isLarge()) {
            return ModSounds.TROPICAL_SLIME_LARGE_JUMP.get();
        }
        return ModSounds.TROPICAL_SLIME_SMALL_JUMP.get();
    }

    protected SoundEvent getAttackSound() {
        if (this.isTiny()) {
            return ModSounds.TROPICAL_SLIME_SMALL_ATTACK.get();
        } else if (this.isMedium()) {
            return ModSounds.TROPICAL_SLIME_MEDIUM_ATTACK.get();
        } else if (this.isLarge()) {
            return ModSounds.TROPICAL_SLIME_LARGE_ATTACK.get();
        }
        return ModSounds.TROPICAL_SLIME_SMALL_ATTACK.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            if (this.getAnimation() < TropicalSlimeTextures.TEXTURES.size()){
                this.setAnimation(this.getAnimation() + 1);
            } else {
                this.setAnimation(0);
            }
        }
    }

    @Override
    public void commandMode() {
        if (this.isCommanded()) {
            if (this.isInWater()) {
                this.lookAt(EntityAnchorArgument.Anchor.EYES, this.getCommandPos().getCenter());
                if (this.getMoveControl() instanceof TropicalSlimeMoveControl control) {
                    control.setDirection(this.getYRot(), true);
                    control.setWantedMovement(0.5D);
                }
            }
        }
        super.commandMode();
    }

    @Override
    public double getCommandSpeed() {
        if (this.isInWater()){
            return 0.5D;
        }
        return 1.0D;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean canDrownInFluidType(FluidType type){
        return false;
    }

    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    public boolean checkSpawnObstruction(LevelReader p_32829_) {
        return p_32829_.isUnobstructed(this);
    }

    @Override
    public void die(DamageSource pCause) {
        float size = 1.5F * Math.max(1, this.getSize());
        for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(size))){
            int air = Math.min(livingEntity.getAirSupply() + (3 * Math.max(1, this.getSize())), livingEntity.getMaxAirSupply());
            livingEntity.setAirSupply(air);
        }
        super.die(pCause);
    }

    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.SPLASH;
    }

    public void travel(Vec3 p_32394_) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), p_32394_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(p_32394_);
        }

    }

    //Movements based on @baguchi's codes: https://github.com/baguchi/EarthMobs/blob/1.20.x/src/main/java/baguchan/earthmobsmod/entity/TropicalSlime.java
    static class TropicalSlimeMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final TropicalSlimeServant slime;
        private boolean isAggressive;

        public TropicalSlimeMoveControl(TropicalSlimeServant p_33668_) {
            super(p_33668_);
            this.slime = p_33668_;
            this.yRot = 180.0F * p_33668_.getYRot() / (float) Math.PI;
        }

        public void setDirection(float p_33673_, boolean p_33674_) {
            this.yRot = p_33673_;
            this.isAggressive = p_33674_;
        }

        public void setWantedY(double y) {
            this.wantedY = y;
        }

        public void setWantedMovement(double p_33671_) {
            this.speedModifier = p_33671_;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().subtract(0.0D, 0.008D, 0.0D));
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setSpeed(0.0F);
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.isInWater() && !this.mob.onGround()) {

                    float f1 = (float) (this.speedModifier * this.mob.getAttributeValue(ForgeMod.SWIM_SPEED.get()));

                    double d1 = this.wantedY - this.mob.getY();
                    boolean flag = d1 < 0.0D && this.mob.getTarget() != null;

                    float f2 = Mth.lerp(0.125F, this.mob.getSpeed(), f1);
                    this.mob.setSpeed(f2);
                    if (flag) {
                        if (Math.abs(d1) > (double) 1.0E-5F) {
                            this.mob.setYya(f2 * 3F);
                        }
                    }
                } else if (this.mob.onGround()) {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));

                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpControl().jump();
                        if (this.slime.doPlayJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.mob.setZza(0.0F);
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }
        }
    }

    static class SlimeFollowGoal extends Goal {
        private final TropicalSlimeServant slime;
        private LivingEntity owner;
        private final float stopDistance;
        private final float startDistance;

        public SlimeFollowGoal(TropicalSlimeServant p_33648_, float startDistance, float stopDistance) {
            this.slime = p_33648_;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.slime.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (!(this.slime.getMoveControl() instanceof TropicalSlimeMoveControl)){
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.slime.distanceToSqr(livingentity) < (double)(Mth.square(this.startDistance))) {
                return false;
            } else if (!this.slime.isFollowing() || this.slime.isCommanded()) {
                return false;
            } else if (this.slime.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public void start() {
            super.start();
        }

        public boolean canContinueToUse() {
            if (this.slime.getNavigation().isDone()) {
                return false;
            } else if (this.slime.getTarget() != null){
                return false;
            } else {
                return !(this.slime.distanceToSqr(this.owner) <= (double)(Mth.square(this.stopDistance)));
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof TropicalSlimeMoveControl control) {
                if (this.owner != null) {
                    this.slime.lookAt(this.owner, 10.0F, 10.0F);
                    control.setWantedY(this.owner.getY());
                }
                control.setDirection(this.slime.getYRot(), true);
                control.setWantedMovement(1.0D);
            }
        }
    }

    static class SlimeKeepOnJumpingGoal extends Goal {
        private final TropicalSlimeServant slime;

        public SlimeKeepOnJumpingGoal(TropicalSlimeServant p_33660_) {
            this.slime = p_33660_;
        }

        public boolean canUse() {
            return !this.slime.isPassenger()
                    && !this.slime.isStaying()
                    && (this.slime.getTrueOwner() == null
                    || this.slime.isWandering() || this.slime.getTarget() != null);
        }

        public void tick() {
            if (this.slime.getMoveControl() instanceof TropicalSlimeMoveControl control) {
                control.setWantedMovement(1.0D);
            }
        }
    }

    static class SlimeRandomDirectionGoal extends Goal {
        private final TropicalSlimeServant slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(TropicalSlimeServant p_33679_) {
            this.slime = p_33679_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return this.slime.getTarget() == null
                    && (this.slime.getTrueOwner() == null || this.slime.isWandering())
                    && (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION))
                    && this.slime.getMoveControl() instanceof TropicalSlimeMoveControl;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = (float) this.slime.getRandom().nextInt(360);
            }

            if (this.slime.getMoveControl() instanceof TropicalSlimeMoveControl control) {
                control.setDirection(this.chosenDegrees, false);
            }
        }
    }

    static class SlimeAttackGoal extends Goal {
        private final TropicalSlimeServant slime;
        private int growTiredTimer;

        public SlimeAttackGoal(TropicalSlimeServant p_33648_) {
            this.slime = p_33648_;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return this.slime.canAttack(livingentity) && this.slime.getMoveControl() instanceof TropicalSlimeMoveControl;
            }
        }

        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!this.slime.canAttack(livingentity)) {
                return false;
            } else {
                return --this.growTiredTimer > 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.slime.getMoveControl() instanceof TropicalSlimeMoveControl control) {
                LivingEntity livingentity = this.slime.getTarget();
                if (livingentity != null) {
                    this.slime.lookAt(livingentity, 10.0F, 10.0F);
                    control.setWantedY(livingentity.getY());
                }
                control.setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
            }
        }
    }
}
