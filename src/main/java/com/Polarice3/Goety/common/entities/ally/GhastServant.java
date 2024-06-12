package com.Polarice3.Goety.common.entities.ally;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.entities.projectiles.Lavaball;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class GhastServant extends Malghast implements IServant {

    public GhastServant(EntityType<? extends Malghast> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new MoveHelperController(this);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public void addFireballGoal(){
        this.goalSelector.addGoal(7, new FireballAttackGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GhastServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.GhastServantHealth.get());
    }

    protected float getStandingEyeHeight(Pose p_32741_, EntityDimensions p_32742_) {
        return 2.6F;
    }

    protected float getSoundVolume() {
        return 5.0F;
    }

    public float getVoicePitch() {
        return 1.0F;
    }

    public void setGhastSpawn(){
    }

    @Override
    public void lifeSpanDamage() {
        if (!this.level.isClientSide){
            for(int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), this.level);
            }
        }
        this.playSound(ModSounds.GHAST_DISAPPEAR.get(), this.getSoundVolume(), this.getVoicePitch());
        this.discard();
    }

    @Override
    public void push(Entity entity) {
        if (!this.level.isClientSide) {
            if (!MobUtil.areAllies(this, entity)) {
                super.push(entity);
            }
        }
    }

    protected void doPush(Entity entity) {
        if (!this.level.isClientSide) {
            if (!MobUtil.areAllies(this, entity)) {
                super.doPush(entity);
            }
        }
    }

    public boolean canCollideWith(Entity entity) {
        if (!MobUtil.areAllies(this, entity)){
            return super.canCollideWith(entity);
        } else {
            return false;
        }
    }

    @Override
    public boolean isWandering() {
        return true;
    }

    @Override
    public void setWandering(boolean wandering) {
    }

    @Override
    public boolean isStaying() {
        return false;
    }

    @Override
    public void setStaying(boolean staying) {
    }

    @Override
    public boolean canUpdateMove() {
        return false;
    }

    @Override
    public void updateMoveMode(Player player) {
    }

    @Override
    public void setCommandPos(BlockPos blockPos) {
    }

    @Override
    public void setCommandPos(BlockPos blockPos, boolean removeEntity) {
    }

    @Override
    public void setCommandPosEntity(LivingEntity living) {
    }

    @Override
    public void tryKill(Player player) {
        this.lifeSpanDamage();
    }

    static class MoveHelperController extends MoveControl {
        private final Malghast ghast;
        private int floatDuration;

        public MoveHelperController(Malghast p_i45838_1_) {
            super(p_i45838_1_);
            this.ghast = p_i45838_1_;
        }

        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
                    Vec3 vector3d = new Vec3(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(), this.wantedZ - this.ghast.getZ());
                    double d0 = vector3d.length();
                    vector3d = vector3d.normalize();
                    if (this.canReach(vector3d, Mth.ceil(d0))) {
                        this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(vector3d.scale(0.1D)));
                    } else {
                        this.operation = Operation.WAIT;
                    }
                }

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.ghast.getBoundingBox();

            for(int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.ghast.level.noCollision(this.ghast, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    static class FireballAttackGoal extends Goal {
        private final GhastServant ghast;
        public int chargeTime;

        public FireballAttackGoal(GhastServant p_i45837_1_) {
            this.ghast = p_i45837_1_;
        }

        public boolean canUse() {
            return this.ghast.getTarget() != null;
        }

        public void start() {
            this.chargeTime = 0;
        }

        public void stop() {
            this.ghast.setCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = this.ghast.getTarget();
            float d0 = 64.0F;
            if (livingentity != null && livingentity.distanceToSqr(this.ghast) < Mth.square(d0) && this.ghast.hasLineOfSight(livingentity)) {
                Level world = this.ghast.level;
                ++this.chargeTime;
                if (this.chargeTime == 10) {
                    if (!this.ghast.isSilent()) {
                        world.levelEvent((Player)null, 1015, this.ghast.blockPosition(), 0);
                    }
                }

                if (this.chargeTime == 20) {
                    Vec3 vec3 = this.ghast.getViewVector(1.0F);
                    double d2 = livingentity.getX() - (this.ghast.getX() + vec3.x() * 4.0D);
                    double d3 = livingentity.getBoundingBox().minY + livingentity.getBbHeight() / 2.0F - (0.5D + this.ghast.getY() + this.ghast.getBbHeight() / 2.0F);
                    double d4 = livingentity.getZ() - (this.ghast.getZ() + vec3.z() * 4.0D);
                    if (!this.ghast.isSilent()) {
                        world.levelEvent((Player)null, 1016, this.ghast.blockPosition(), 0);
                    }
                    Lavaball largefireball = new Lavaball(world, this.ghast, d2, d3, d4);
                    largefireball.setExplosionPower(this.ghast.getExplosionPower());
                    largefireball.setDamage(AttributesConfig.GhastServantDamage.get().floatValue() + this.ghast.getFireBallDamage());
                    largefireball.setPos(this.ghast.getX() + vec3.x() * 4.0D, this.ghast.getY() + this.ghast.getBbHeight() / 2.0F + 0.5D, this.ghast.getZ() + vec3.z() * 4.0D);
                    largefireball.setDangerous(ForgeEventFactory.getMobGriefingEvent(world, this.ghast));
                    world.addFreshEntity(largefireball);
                    this.chargeTime = -40;
                }
            } else if (this.chargeTime > 0) {
                --this.chargeTime;
            }

            this.ghast.setCharging(this.chargeTime > 10);
        }
    }
}
