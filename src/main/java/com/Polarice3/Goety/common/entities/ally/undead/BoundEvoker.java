package com.Polarice3.Goety.common.entities.ally.undead;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ally.AllyVex;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class BoundEvoker extends AbstractBoundIllager{
    @Nullable
    private Sheep wololoTarget;

    public BoundEvoker(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new EvokerCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidTargetGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new EvokerSummonSpellGoal());
        this.goalSelector.addGoal(5, new EvokerAttackSpellGoal());
        this.goalSelector.addGoal(6, new EvokerWololoSpellGoal());
        this.goalSelector.addGoal(8, new WanderGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoundEvokerHealth.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BoundEvokerHealth.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void readAdditionalSaveData(CompoundTag p_32642_) {
        super.readAdditionalSaveData(p_32642_);
    }

    public void addAdditionalSaveData(CompoundTag p_32646_) {
        super.addAdditionalSaveData(p_32646_);
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource p_32654_) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    public float getVoicePitch() {
        return 0.45F;
    }

    void setWololoTarget(@Nullable Sheep p_32635_) {
        this.wololoTarget = p_32635_;
    }

    @Override
    public int xpReward() {
        return 10;
    }

    @Nullable
    Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide){
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getY() + 0.5D, this.getRandomZ(0.5D), (0.5D - this.random.nextDouble()) * 0.15D, 0.01F, (0.5D - this.random.nextDouble()) * 0.15D);
            }
        }
    }

    class EvokerAttackSpellGoal extends BoundUseSpellGoal {
        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = BoundEvoker.this.getTarget();
            double d0 = Math.min(livingentity.getY(), BoundEvoker.this.getY());
            double d1 = Math.max(livingentity.getY(), BoundEvoker.this.getY()) + 1.0D;
            float f = (float) Mth.atan2(livingentity.getZ() - BoundEvoker.this.getZ(), livingentity.getX() - BoundEvoker.this.getX());
            if (BoundEvoker.this.distanceToSqr(livingentity) < 9.0D) {
                for(int i = 0; i < 5; ++i) {
                    float f1 = f + (float)i * (float)Math.PI * 0.4F;
                    this.createSpellEntity(BoundEvoker.this.getX() + (double)Mth.cos(f1) * 1.5D, BoundEvoker.this.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0);
                }

                for(int k = 0; k < 8; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(BoundEvoker.this.getX() + (double)Mth.cos(f2) * 2.5D, BoundEvoker.this.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
                }
            } else {
                for(int l = 0; l < 16; ++l) {
                    double d2 = 1.25D * (double)(l + 1);
                    int j = 1 * l;
                    this.createSpellEntity(BoundEvoker.this.getX() + (double)Mth.cos(f) * d2, BoundEvoker.this.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, j);
                }
            }

        }

        private void createSpellEntity(double p_32673_, double p_32674_, double p_32675_, double p_32676_, float p_32677_, int p_32678_) {
            BlockPos blockpos = BlockPos.containing(p_32673_, p_32676_, p_32674_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = BoundEvoker.this.level.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(BoundEvoker.this.level, blockpos1, Direction.UP)) {
                    if (!BoundEvoker.this.level.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = BoundEvoker.this.level.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(BoundEvoker.this.level, blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while(blockpos.getY() >= Mth.floor(p_32675_) - 1);

            if (flag) {
                BoundEvoker.this.level.addFreshEntity(new Fangs(BoundEvoker.this.level, p_32673_, (double)blockpos.getY() + d0, p_32674_, p_32677_, p_32678_, BoundEvoker.this));
            }

        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        protected BoundSpell getSpell() {
            return BoundSpell.FANGS;
        }
    }

    class EvokerCastingSpellGoal extends BoundCastingSpellGoal {
        public void tick() {
            if (BoundEvoker.this.getTarget() != null) {
                BoundEvoker.this.getLookControl().setLookAt(BoundEvoker.this.getTarget(), (float)BoundEvoker.this.getMaxHeadYRot(), (float)BoundEvoker.this.getMaxHeadXRot());
            } else if (BoundEvoker.this.getWololoTarget() != null) {
                BoundEvoker.this.getLookControl().setLookAt(BoundEvoker.this.getWololoTarget(), (float)BoundEvoker.this.getMaxHeadYRot(), (float)BoundEvoker.this.getMaxHeadXRot());
            }

        }
    }

    class EvokerSummonSpellGoal extends BoundUseSpellGoal {
        private final TargetingConditions vexCountTargeting = TargetingConditions.forNonCombat().range(16.0D).ignoreLineOfSight().ignoreInvisibilityTesting();

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = BoundEvoker.this.level.getNearbyEntities(AllyVex.class, this.vexCountTargeting, BoundEvoker.this, BoundEvoker.this.getBoundingBox().inflate(16.0D)).size();
                return BoundEvoker.this.random.nextInt(8) + 1 > i;
            }
        }

        protected int getCastingTime() {
            return 100;
        }

        protected int getCastingInterval() {
            return 340;
        }

        protected void performSpellCasting() {
            ServerLevel serverlevel = (ServerLevel)BoundEvoker.this.level;

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos = BoundEvoker.this.blockPosition().offset(-2 + BoundEvoker.this.random.nextInt(5), 1, -2 + BoundEvoker.this.random.nextInt(5));
                AllyVex vex = ModEntityType.ALLY_VEX.get().create(BoundEvoker.this.level);
                vex.moveTo(blockpos, 0.0F, 0.0F);
                vex.finalizeSpawn(serverlevel, BoundEvoker.this.level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                vex.setTrueOwner(BoundEvoker.this);
                vex.setBoundOrigin(blockpos);
                vex.setLimitedLife(20 * (30 + BoundEvoker.this.random.nextInt(90)));
                serverlevel.addFreshEntityWithPassengers(vex);
            }

        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        protected BoundSpell getSpell() {
            return BoundSpell.SUMMON_VEX;
        }
    }

    public class EvokerWololoSpellGoal extends BoundUseSpellGoal {
        private final TargetingConditions wololoTargeting = TargetingConditions.forNonCombat().range(16.0D).selector((p_32710_) -> {
            return ((Sheep)p_32710_).getColor() == DyeColor.RED;
        });

        public boolean canUse() {
            if (BoundEvoker.this.getTarget() != null) {
                return false;
            } else if (BoundEvoker.this.isCastingSpell()) {
                return false;
            } else if (BoundEvoker.this.tickCount < this.nextAttackTickCount) {
                return false;
            } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(BoundEvoker.this.level, BoundEvoker.this)) {
                return false;
            } else {
                List<Sheep> list = BoundEvoker.this.level.getNearbyEntities(Sheep.class, this.wololoTargeting, BoundEvoker.this, BoundEvoker.this.getBoundingBox().inflate(16.0D, 4.0D, 16.0D));
                if (list.isEmpty()) {
                    return false;
                } else {
                    BoundEvoker.this.setWololoTarget(list.get(BoundEvoker.this.random.nextInt(list.size())));
                    return true;
                }
            }
        }

        public boolean canContinueToUse() {
            return BoundEvoker.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
        }

        public void stop() {
            super.stop();
            BoundEvoker.this.setWololoTarget((Sheep)null);
        }

        protected void performSpellCasting() {
            Sheep sheep = BoundEvoker.this.getWololoTarget();
            if (sheep != null && sheep.isAlive()) {
                sheep.setColor(DyeColor.BLUE);
            }

        }

        protected int getCastWarmupTime() {
            return 40;
        }

        protected int getCastingTime() {
            return 60;
        }

        protected int getCastingInterval() {
            return 140;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        protected BoundSpell getSpell() {
            return BoundSpell.WOLOLO;
        }
    }
}
