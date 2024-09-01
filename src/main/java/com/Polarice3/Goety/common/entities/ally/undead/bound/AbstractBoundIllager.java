package com.Polarice3.Goety.common.entities.ally.undead.bound;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public abstract class AbstractBoundIllager extends Summoned {
    private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(AbstractBoundIllager.class, EntityDataSerializers.BYTE);
    protected int spellCastingTickCount;
    private BoundSpell currentSpell = BoundSpell.NONE;

    public AbstractBoundIllager(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setNoGravity(true);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag p_33732_) {
        super.readAdditionalSaveData(p_33732_);
        this.spellCastingTickCount = p_33732_.getInt("SpellTicks");
    }

    public void addAdditionalSaveData(CompoundTag p_33734_) {
        super.addAdditionalSaveData(p_33734_);
        p_33734_.putInt("SpellTicks", this.spellCastingTickCount);
    }

    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel) {
            public boolean isStableDestination(BlockPos blockPos) {
                return !this.level.getBlockState(blockPos.below()).isAir();
            }

            public void tick() {
                super.tick();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.9F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    public BoundArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return BoundArmPose.SPELLCASTING;
        } else {
            return BoundArmPose.CROSSED;
        }
    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource damageSource) {
        return false;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean isNoGravity() {
        return true;
    }

    protected SoundEvent getStepSound() {
        return ModSounds.HAUNT_FLY.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected float nextStep() {
        return this.moveDist + 2.0F;
    }

    public boolean isCastingSpell() {
        if (this.level.isClientSide) {
            return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
        } else {
            return this.spellCastingTickCount > 0;
        }
    }

    public void setIsCastingSpell(BoundSpell p_33728_) {
        this.currentSpell = p_33728_;
        this.entityData.set(DATA_SPELL_CASTING_ID, (byte)p_33728_.id);
    }

    protected BoundSpell getCurrentSpell() {
        return !this.level.isClientSide ? this.currentSpell : BoundSpell.byId(this.entityData.get(DATA_SPELL_CASTING_ID));
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.spellCastingTickCount > 0) {
            --this.spellCastingTickCount;
        }

    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide && this.isCastingSpell() && this.getCurrentSpell() != BoundSpell.CLOUDLESS) {
            BoundSpell spellcasterillager$illagerspell = this.getCurrentSpell();
            double d0 = spellcasterillager$illagerspell.spellColor[0];
            double d1 = spellcasterillager$illagerspell.spellColor[1];
            double d2 = spellcasterillager$illagerspell.spellColor[2];
            float f = this.yBodyRot * ((float)Math.PI / 180F) + Mth.cos((float)this.tickCount * 0.6662F) * 0.25F;
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double)f2 * 0.6D, d0, d1, d2);
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() - (double)f2 * 0.6D, d0, d1, d2);
        }

    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    protected abstract SoundEvent getCastingSoundEvent();

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level.isClientSide){
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (itemstack.is(ModItems.ECTOPLASM.get()) && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.SOUL_ESCAPE, 1.0F, 1.0F);
                    this.heal(2.0F);
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    pPlayer.swing(pHand);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    protected static enum BoundSpell {
        NONE(0, 0.0D, 0.0D, 0.0D),
        SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
        FANGS(2, 0.4D, 0.3D, 0.35D),
        WOLOLO(3, 0.7D, 0.5D, 0.2D),
        DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
        BLINDNESS(5, 0.1D, 0.1D, 0.2D),
        CLOUDLESS(6, 0.0D, 0.0D, 0.0D);

        final int id;
        final double[] spellColor;

        private BoundSpell(int p_33754_, double p_33755_, double p_33756_, double p_33757_) {
            this.id = p_33754_;
            this.spellColor = new double[]{p_33755_, p_33756_, p_33757_};
        }

        public static BoundSpell byId(int p_33759_) {
            for(BoundSpell spellcasterillager$illagerspell : values()) {
                if (p_33759_ == spellcasterillager$illagerspell.id) {
                    return spellcasterillager$illagerspell;
                }
            }

            return NONE;
        }
    }

    protected class BoundCastingSpellGoal extends Goal {
        public BoundCastingSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return AbstractBoundIllager.this.getSpellCastingTime() > 0;
        }

        public void start() {
            super.start();
            AbstractBoundIllager.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            AbstractBoundIllager.this.setIsCastingSpell(BoundSpell.NONE);
        }

        public void tick() {
            if (AbstractBoundIllager.this.getTarget() != null) {
                AbstractBoundIllager.this.getLookControl().setLookAt(AbstractBoundIllager.this.getTarget(), (float)AbstractBoundIllager.this.getMaxHeadYRot(), (float)AbstractBoundIllager.this.getMaxHeadXRot());
            }

        }
    }

    protected abstract class BoundUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        public boolean canUse() {
            LivingEntity livingentity = AbstractBoundIllager.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                if (AbstractBoundIllager.this.isCastingSpell()) {
                    return false;
                } else {
                    return AbstractBoundIllager.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = AbstractBoundIllager.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            AbstractBoundIllager.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = AbstractBoundIllager.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                AbstractBoundIllager.this.playSound(soundevent, 1.0F, 1.0F);
            }

            AbstractBoundIllager.this.setIsCastingSpell(this.getSpell());
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                if (AbstractBoundIllager.this.getCastingSoundEvent() != null) {
                    AbstractBoundIllager.this.playSound(AbstractBoundIllager.this.getCastingSoundEvent(), 1.0F, 1.0F);
                }
            }

        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract BoundSpell getSpell();
    }

    public static enum BoundArmPose {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        CELEBRATING,
        NEUTRAL;
    }
}
