package com.Polarice3.Goety.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.IntFunction;

public abstract class SpellcasterIllagerServant extends AbstractIllagerServant{
    private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllagerServant.class, EntityDataSerializers.BYTE);
    protected int spellCastingTickCount;
    private IllagerServantSpell currentSpell = IllagerServantSpell.NONE;

    public SpellcasterIllagerServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.spellCastingTickCount = compound.getInt("SpellTicks");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpellTicks", this.spellCastingTickCount);
    }

    public IllagerServantArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return IllagerServantArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating() ? IllagerServantArmPose.CELEBRATING : IllagerServantArmPose.CROSSED;
        }
    }

    public boolean isCastingSpell() {
        if (this.level.isClientSide) {
            return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
        } else {
            return this.spellCastingTickCount > 0;
        }
    }

    public void setIsCastingSpell(IllagerServantSpell p_33728_) {
        this.currentSpell = p_33728_;
        this.entityData.set(DATA_SPELL_CASTING_ID, (byte)p_33728_.id);
    }

    protected IllagerServantSpell getCurrentSpell() {
        return !this.level.isClientSide ? this.currentSpell : IllagerServantSpell.byId(this.entityData.get(DATA_SPELL_CASTING_ID));
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.spellCastingTickCount > 0) {
            --this.spellCastingTickCount;
        }
    }

    public void tick() {
        super.tick();
        this.spellParticles();
    }

    public void spellParticles(){
        if (this.level.isClientSide && this.isCastingSpell()) {
            IllagerServantSpell spell = this.getCurrentSpell();
            if (spell != IllagerServantSpell.NONE) {
                double d0 = spell.spellColor[0];
                double d1 = spell.spellColor[1];
                double d2 = spell.spellColor[2];
                float f = this.yBodyRot * ((float) Math.PI / 180F) + Mth.cos((float) this.tickCount * 0.6662F) * 0.25F;
                float f1 = Mth.cos(f);
                float f2 = Mth.sin(f);
                this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double) f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double) f2 * 0.6D, d0, d1, d2);
                this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double) f1 * 0.6D, this.getY() + 1.8D, this.getZ() - (double) f2 * 0.6D, d0, d1, d2);
            }
        }
    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    protected abstract SoundEvent getCastingSoundEvent();

    public enum IllagerServantSpell {
        NONE(0, 0.0D, 0.0D, 0.0D),
        SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
        FANGS(2, 0.4D, 0.3D, 0.35D),
        WOLOLO(3, 0.7D, 0.5D, 0.2D),
        DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
        BLINDNESS(5, 0.1D, 0.1D, 0.2D),
        RAVAGING(6, 96.0D / 255.0D, 95.0D / 255.0D, 90.0D / 255.0D);

        private static final IntFunction<IllagerServantSpell> BY_ID = ByIdMap.continuous((p_263091_) -> {
            return p_263091_.id;
        }, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        final int id;
        final double[] spellColor;

        IllagerServantSpell(int p_33754_, double p_33755_, double p_33756_, double p_33757_) {
            this.id = p_33754_;
            this.spellColor = new double[]{p_33755_, p_33756_, p_33757_};
        }

        public static IllagerServantSpell byId(int p_33759_) {
            return BY_ID.apply(p_33759_);
        }
    }

    protected class SpellcasterCastingSpellGoal extends Goal {
        public SpellcasterCastingSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return SpellcasterIllagerServant.this.getSpellCastingTime() > 0;
        }

        public void start() {
            super.start();
            SpellcasterIllagerServant.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            SpellcasterIllagerServant.this.setIsCastingSpell(IllagerServantSpell.NONE);
        }

        public void tick() {
            if (SpellcasterIllagerServant.this.getTarget() != null) {
                SpellcasterIllagerServant.this.getLookControl().setLookAt(SpellcasterIllagerServant.this.getTarget(), (float)SpellcasterIllagerServant.this.getMaxHeadYRot(), (float)SpellcasterIllagerServant.this.getMaxHeadXRot());
            }

        }
    }

    protected abstract class SpellcasterUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        public boolean canUse() {
            LivingEntity livingentity = SpellcasterIllagerServant.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                if (SpellcasterIllagerServant.this.isCastingSpell()) {
                    return false;
                } else {
                    return SpellcasterIllagerServant.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = SpellcasterIllagerServant.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            SpellcasterIllagerServant.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = SpellcasterIllagerServant.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                SpellcasterIllagerServant.this.playSound(soundevent, 1.0F, 1.0F);
            }

            SpellcasterIllagerServant.this.setIsCastingSpell(this.getSpell());
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                SpellcasterIllagerServant.this.playSound(SpellcasterIllagerServant.this.getCastingSoundEvent(), 1.0F, 1.0F);
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

        protected abstract IllagerServantSpell getSpell();
    }
}
