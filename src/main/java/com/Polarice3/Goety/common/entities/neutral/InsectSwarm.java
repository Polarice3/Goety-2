package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class InsectSwarm extends Owned{

    public InsectSwarm(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new FlyingMoveControl(this, 15, true);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public InsectSwarm(Level worldIn, LivingEntity owner, Vec3 pos){
        this(ModEntityType.INSECT_SWARM.get(), worldIn);
        this.setTrueOwner(owner);
        this.setPos(pos);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FLYING_SPEED, 0.2D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new FlyingPathNavigation(this, pLevel);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 0.25F;
    }

    @Override
    public void mobSense() {
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.tickCount % 2 == 0) {
                if (this.level instanceof ServerLevel serverLevel) {
                    ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ModParticleTypes.FLY.get(), this);
                }
            }
            if (this.getTarget() != null) {
                this.navigation.moveTo(this.getTarget(), 7.0D);
            } else {
                Vec3 vec3 = LandRandomPos.getPos(this, 10, 7);
                if (vec3 == null){
                    vec3 = DefaultRandomPos.getPos(this, 10, 7);
                }
                if (vec3 != null){
                    this.navigation.moveTo(vec3.x, vec3.y, vec3.z, 1.0D);
                }
            }
            if (this.level.collidesWithSuffocatingBlock(this, this.getBoundingBox().move(0.0D, -1.0D, 0.0D))) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.02D, 0.0D));
            } else {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
            }
            if (!this.moveControl.hasWanted()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.95F, 1.0F, 0.95F));
            }
            float inflate = 2.0F - this.getBbWidth() * 0.5F;
            for (LivingEntity living : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(inflate))) {
                if (living != this && !MobUtil.areAllies(this, living) && SummonTargetGoal.predicate(this).test(living)) {
                    if (living.hurt(ModDamageSource.swarm(this, this.getTrueOwner()), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                        this.playSound(ModSounds.INSECT_SWARM_BITE.get(), 1.0F, this.getVoicePitch());
                        living.invulnerableTime = 15;
                    }
                }
            }
        }
    }

    @Override
    public void lifeSpanDamage() {
        this.discard();
    }
}
