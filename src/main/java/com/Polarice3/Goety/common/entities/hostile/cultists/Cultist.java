package com.Polarice3.Goety.common.entities.hostile.cultists;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ai.StrollAroundLeaderGoal;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Cultist extends Raider implements ICustomAttributes {
    protected static final EntityDataAccessor<Optional<UUID>> LEADER_UUID = SynchedEntityData.defineId(Cultist.class, EntityDataSerializers.OPTIONAL_UUID);

    protected Cultist(EntityType<? extends Cultist> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.getNavigation().setCanFloat(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.extraGoal();
        this.goalSelector.addGoal(8, new StrollAroundLeaderGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, Cultist.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, Witch.class)).setAlertOthers());
    }

    public void extraGoal(){
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    public void setConfigurableAttributes(){
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEADER_UUID, Optional.empty());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("LeaderUUID")){
            this.setLeaderID(compound.getUUID("LeaderUUID"));
        }
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getLeaderID() != null) {
            compound.putUUID("LeaderUUID", this.getLeaderID());
        }
    }

    public UUID getLeaderID(){
        return this.entityData.get(LEADER_UUID).orElse(null);
    }

    public void setLeaderID(UUID uuid){
        this.entityData.set(LEADER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    public Entity getLeader(){
        UUID uuid = this.getLeaderID();
        return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
    }

    public void setLeader(@Nullable Entity entity){
        if (entity != null) {
            this.setLeaderID(entity.getUUID());
        }
    }

    public boolean isBarterable(){
        return true;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        this.alertWitches();
        return super.hurt(pSource, pAmount);
    }

    protected void alertWitches(){
        double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB axisalignedbb = AABB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0D, d0);
        List<Mob> list = this.level.getEntitiesOfClass(Mob.class, axisalignedbb);

        for (Mob mob : list){
            if (this.getLastHurtByMob() != null && !(this.getLastHurtByMob() instanceof Raider) && !MobUtil.areAllies(this.getLastHurtByMob(), this)) {
                if (mob instanceof Witch && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.getLastHurtByMob())) {
                    mob.setTarget(this.getLastHurtByMob());
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.getLeader() != null) {
                if (!this.getLeader().isAlive()) {
                    this.setLeader(null);
                } else {
                    Vec3 vec3 = this.getLeader().position();
                    if (this.getTarget() != null) {
                        if (this.getTarget().distanceToSqr(vec3) > Mth.square(32)) {
                            this.setTarget(null);
                            this.getNavigation().moveTo(this.getLeader(), 1.0F);
                        }
                    }
                    if (this.getLeader() instanceof LivingEntity livingEntity) {
                        if (livingEntity.getLastHurtByMob() != null && !MobUtil.areAllies(livingEntity.getLastHurtByMob(), this)) {
                            if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity.getLastHurtByMob())) {
                                this.setTarget(livingEntity.getLastHurtByMob());
                            }
                        }
                    }
                }
            }
        }
    }

    public CultistArmPose getArmPose() {
        return CultistArmPose.CROSSED;
    }

    @Override
    public void applyRaidBuffs(int wave, boolean p_213660_2_) {
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 1.62F;
    }

    public boolean canBeLeader() {
        return false;
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (super.isAlliedTo(entityIn)) {
            return true;
        } else if (entityIn instanceof Witch) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else if (entityIn instanceof Cultist) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else if (entityIn instanceof AbstractPiglin){
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else {
            return entityIn instanceof IOwned && ((IOwned) entityIn).getTrueOwner() instanceof Cultist;
        }
    }

    public static enum CultistArmPose {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        PRAYING,
        SPELL_AND_WEAPON,
        BOW_AND_ARROW,
        TORCH_AND_WEAPON,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        BOMB_AND_WEAPON,
        THROW_SPEAR,
        ITEM,
        DYING,
        NEUTRAL;
    }
}
