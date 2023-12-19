package com.Polarice3.Goety.common.entities.hostile.cultists;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class Cultist extends Raider implements ICustomAttributes {

    protected Cultist(EntityType<? extends Cultist> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.getNavigation().setCanFloat(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this).setAlertOthers()));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, Cultist.class)).setAlertOthers());
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, Witch.class)).setAlertOthers());
    }

    public void setConfigurableAttributes(){
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setConfigurableAttributes();
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
            if (this.getLastHurtByMob() != null && !(this.getLastHurtByMob() instanceof Raider) && !mob.isAlliedTo(this.getLastHurtByMob()) && !this.isAlliedTo(this.getLastHurtByMob())) {
                if (mob instanceof Witch) {
                    mob.setTarget(this.getLastHurtByMob());
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
        DYING,
        NEUTRAL;
    }
}
