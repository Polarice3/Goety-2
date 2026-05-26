package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.entities.ai.FloatAroundGoal;
import com.Polarice3.Goety.common.entities.neutral.SummonedFlying;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.spells.storm.ShockingSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SpriteMob extends SummonedFlying {
    public int attackTime;

    public SpriteMob(EntityType<? extends SummonedFlying> type, Level worldIn) {
        super(type, worldIn);
        this.attackTime = 0;
        this.moveControl = new MoveHelperController(this);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new FloatAroundGoal<>(this, 4.0F, 2, 1.0D));
        this.goalSelector.addGoal(7, new LookAroundGoal(this));
        this.goalSelector.addGoal(7, new ShockAttackGoal(this));
    }

    public void followGoal(){
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SpriteHealth.get())
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SpriteDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SpriteHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SpriteDamage.get());
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setSpriteSpawn();
        return pSpawnData;
    }

    public void setSpriteSpawn(){
        if (this.getTrueOwner() == null) {
            this.setBoundPos(this.blockPosition());
            this.setWandering(false);
            this.setStaying(false);
        }
    }

    public void tick() {
        super.tick();
        if (this.level instanceof ServerLevel serverLevel) {
            if (this.tickCount % 5 == 0 || this.attackTime > 0) {
                serverLevel.sendParticles(ModParticleTypes.SPELL_ELECTRIC.get(), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (this.isStaying()) {
                this.getMoveControl().strafe(0.0F, 0.0F);
            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return SoundEvents.ALLAY_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        this.playSound(SoundEvents.GLASS_BREAK, this.getSoundVolume(), this.getVoicePitch());
        if (this.level instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(0xfef597);
            serverLevel.sendParticles(new SphereExplodeParticleOption(colorUtil, 1.0F, 1), this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
            for (int i = 0; i < 16; ++i) {
                Vec3 vec3 = this.position();
                int random1 = this.getRandom().nextIntBetweenInclusive(-2, 2);
                int random2 = this.getRandom().nextIntBetweenInclusive(-2, 2);
                int random3 = this.getRandom().nextIntBetweenInclusive(-2, 2);
                Vec3 vec31 = vec3.add(this.getRandom().nextDouble() * random1, this.getRandom().nextDouble() * random2, this.getRandom().nextDouble() * random3);
                ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, colorUtil, 8));
            }
        }
        this.discard();
    }

    protected MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    protected void checkFallDamage(double p_27419_, boolean p_27420_, BlockState p_27421_, BlockPos p_27422_) {
    }

    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    static class MoveHelperController extends MoveControl {
        private final SpriteMob spriteMob;
        private int floatDuration;

        public MoveHelperController(SpriteMob spriteMob) {
            super(spriteMob);
            this.spriteMob = spriteMob;
        }

        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.spriteMob.getRandom().nextInt(5) + 2;
                    Vec3 vec3 = new Vec3(this.wantedX - this.spriteMob.getX(), this.wantedY - this.spriteMob.getY(), this.wantedZ - this.spriteMob.getZ());
                    double d0 = vec3.length();
                    vec3 = vec3.normalize();
                    if (this.canReach(vec3, Mth.ceil(d0))) {
                        this.spriteMob.setDeltaMovement(this.spriteMob.getDeltaMovement().add(vec3.scale(0.1D)));
                    } else {
                        this.operation = Operation.WAIT;
                    }
                }

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB aabb = this.spriteMob.getBoundingBox();

            for(int i = 1; i < p_220673_2_; ++i) {
                aabb = aabb.move(p_220673_1_);
                if (!this.spriteMob.level.noCollision(this.spriteMob, aabb)) {
                    return false;
                }
            }

            return true;
        }
    }

    static class ShockAttackGoal extends Goal {
        private final SpriteMob spriteMob;

        public ShockAttackGoal(SpriteMob p_i45837_1_) {
            this.spriteMob = p_i45837_1_;
        }

        public boolean canUse() {
            return this.spriteMob.getTarget() != null;
        }

        public void start() {
            this.spriteMob.attackTime = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.spriteMob.getTarget();
            double attackRange = this.spriteMob.getAttributeValue(Attributes.FOLLOW_RANGE);
            if (livingentity != null && livingentity.distanceTo(this.spriteMob) < attackRange && this.spriteMob.hasLineOfSight(livingentity)) {
                ++this.spriteMob.attackTime;
                if (this.spriteMob.attackTime == 20) {
                    Spell spell = new ShockingSpell();
                    boolean flag;
                    int potency = 0;
                    Vec3 vec31 = livingentity.getDeltaMovement();
                    if (Math.abs(vec31.x) > 0.1F || Math.abs(vec31.y) > 0.1F || Math.abs(vec31.z) > 0.1F) {
                        flag = this.spriteMob.getRandom().nextFloat() < 0.4F;
                        potency += 1;
                    } else {
                        flag = this.spriteMob.getRandom().nextFloat() < 0.66F;
                    }
                    if (flag) {
                        SoundEvent soundEvent = spell.CastingSound(this.spriteMob);
                        if (soundEvent != null) {
                            this.spriteMob.playSound(soundEvent, spell.castingVolume(), spell.castingPitch());
                        }
                        spell.mobSpellResult(this.spriteMob, this.spriteMob.isUpgraded() ? ModItems.STORM_STAFF.get().getDefaultInstance() : ItemStack.EMPTY, WandUtil.getStats(this.spriteMob, spell).setRange((int) attackRange).increasePotency(potency));
                    }
                    this.spriteMob.attackTime = -20 + this.spriteMob.getRandom().nextInt(20);
                }
            } else {
                if (this.spriteMob.attackTime > 0) {
                    --this.spriteMob.attackTime;
                }
            }
        }
    }

    static class LookAroundGoal extends Goal {
        private final SpriteMob spriteMob;

        public LookAroundGoal(SpriteMob p_i45839_1_) {
            this.spriteMob = p_i45839_1_;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity toLookAt = null;
            if (this.spriteMob.getTarget() != null) {
                toLookAt = this.spriteMob.getTarget();
            }
            if (toLookAt == null) {
                if (this.spriteMob.getRandom().nextFloat() < 0.02F) {
                    toLookAt = this.spriteMob.level.getNearestEntity(this.spriteMob.level.getEntitiesOfClass(LivingEntity.class, this.spriteMob.getBoundingBox().inflate(8.0F, 3.0D, 8.0F), (p_148124_) -> true), TargetingConditions.forNonCombat().range(8.0F), this.spriteMob, this.spriteMob.getX(), this.spriteMob.getEyeY(), this.spriteMob.getZ());
                }
            }
            if (toLookAt == null) {
                Vec3 vector3d = this.spriteMob.getDeltaMovement();
                this.spriteMob.setYRot(-((float)Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI));
            } else {
                double d1 = toLookAt.getX() - this.spriteMob.getX();
                double d2 = toLookAt.getZ() - this.spriteMob.getZ();
                this.spriteMob.getLookControl().setLookAt(toLookAt, 10.0F, this.spriteMob.getMaxHeadXRot());
                this.spriteMob.setYRot(-((float)Mth.atan2(d1, d2)) * (180F / (float)Math.PI));
            }
            this.spriteMob.yBodyRot = this.spriteMob.getYRot();

        }
    }

}
