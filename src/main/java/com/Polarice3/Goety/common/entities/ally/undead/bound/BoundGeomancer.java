package com.Polarice3.Goety.common.entities.ally.undead.bound;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class BoundGeomancer extends AbstractBoundIllager{
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(BoundGeomancer.class, EntityDataSerializers.INT);
    public int spellCool;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public BoundGeomancer(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new AvoidTargetGoal<>(this, LivingEntity.class, 8.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(4, new GeomancyGoal(this));
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GeomancerServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.GeomancerServantArmor.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.GeomancerServantHealth.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.GeomancerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.GeomancerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.GeomancerServantFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpellCool", this.spellCool);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SpellCool")) {
            this.spellCool = compound.getInt("SpellCool");
        }
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
        } else if (Objects.equals(animation, "attack")){
            return 2;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations(){
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.attackAnimationState);
        return list;
    }

    public void stopMostAnimation(AnimationState exception){
        for (AnimationState state : this.getAllAnimations()){
            if (state != exception){
                state.stop();
            }
        }
    }

    public int getCurrentAnimation(){
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level.isClientSide){
                switch (this.entityData.get(ANIM_STATE)){
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                }
            }
        }
    }

    public boolean isAttacking(){
        return this.getCurrentAnimation() == this.getAnimationState("attack");
    }

    @Override
    public int xpReward() {
        return 10;
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.GEOMANCER_AMBIENT.get();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.GEOMANCER_ATTACK.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.GEOMANCER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_32654_) {
        return ModSounds.GEOMANCER_HURT.get();
    }

    @Override
    public float getVoicePitch() {
        return 0.45F;
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            this.idleAnimationState.animateWhen(this.getCurrentAnimation() != this.getAnimationState("attack") && !this.walkAnimation.isMoving(), this.tickCount);
            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getY() + 0.5D, this.getRandomZ(0.5D), (0.5D - this.random.nextDouble()) * 0.15D, 0.01F, (0.5D - this.random.nextDouble()) * 0.15D);
            }
        }
        super.tick();
        if (!this.level.isClientSide) {
            if (this.spellCool > 0) {
                --this.spellCool;
            }
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    public static class GeomancyGoal extends Goal {
        public BoundGeomancer geomancer;
        public int spellTick;

        public GeomancyGoal(BoundGeomancer geomancer){
            this.geomancer = geomancer;
            this.setFlags(EnumSet.of(Flag.TARGET, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.geomancer.getTarget() != null){
                if (this.geomancer.distanceTo(this.geomancer.getTarget()) > this.geomancer.getAttributeValue(Attributes.FOLLOW_RANGE)) {
                    this.geomancer.getNavigation().moveTo(this.geomancer.getTarget(), 1.0F);
                    return false;
                }
                return this.geomancer.spellCool <= 0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.geomancer.getTarget() != null && this.spellTick < 39;
        }

        public void start() {
            super.start();
            this.spellTick = 0;
            this.geomancer.playSound(ModSounds.GEOMANCER_PRE_ATTACK.get(), 0.55F, 0.8F);
            this.geomancer.getNavigation().stop();
            this.geomancer.setAnimationState("attack");
        }

        public void stop() {
            super.stop();
            this.spellTick = 0;
            this.geomancer.setAnimationState("idle");
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = this.geomancer.getTarget();
            if (target != null) {
                MobUtil.instaLook(this.geomancer, target);
                this.geomancer.getNavigation().stop();
                ++this.spellTick;
                if (this.spellTick == 13){
                    this.geomancer.playSound(ModSounds.GEOMANCER_ATTACK.get(), 0.55F, 0.8F);
                    int trueCooldown = MathHelper.secondsToTicks(3);
                    float chance = 0.25F;
                    if (this.geomancer.random.nextBoolean()){
                        if (this.geomancer.random.nextFloat() <= chance){
                            WandUtil.summonQuadOffensiveTrap(this.geomancer, target, ModEntityType.TOTEMIC_BOMB.get(), 0);
                            trueCooldown += MathHelper.secondsToTicks(3);
                        } else {
                            int xShift = this.geomancer.getRandom().nextInt(-1, 1);
                            int zShift = this.geomancer.getRandom().nextInt(-1, 1);
                            WandUtil.summonMonolith(this.geomancer, target, ModEntityType.TOTEMIC_BOMB.get(), xShift, zShift, 0);
                            trueCooldown += MathHelper.secondsToTicks(2);
                        }
                    } else {
                        int random = this.geomancer.random.nextInt(3);
                        if (random == 0) {
                            int[] rowToRemove = Util.getRandom(WandUtil.CONFIG_1_ROWS, this.geomancer.getRandom());
                            Direction direction = Direction.fromYRot(target.getYHeadRot());
                            switch (direction){
                                case NORTH -> rowToRemove = WandUtil.CONFIG_1_NORTH_ROW;
                                case SOUTH -> rowToRemove = WandUtil.CONFIG_1_SOUTH_ROW;
                                case WEST -> rowToRemove = WandUtil.CONFIG_1_WEST_ROW;
                                case EAST -> rowToRemove = WandUtil.CONFIG_1_EAST_ROW;
                            }
                            WandUtil.summonSquareTrap(this.geomancer, target, ModEntityType.TOTEMIC_WALL.get(), rowToRemove, 0);
                        } else if (random == 1){
                            WandUtil.summonWallTrap(this.geomancer, target, ModEntityType.TOTEMIC_WALL.get(), 0);
                        } else {
                            WandUtil.summonRandomPillarsTrap(this.geomancer, target, ModEntityType.TOTEMIC_WALL.get(), 0);
                        }
                    }
                    this.geomancer.spellCool = trueCooldown;
                }
            }
        }
    }
}
