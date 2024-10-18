package com.Polarice3.Goety.api.entities.ally;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface IServant extends IOwned {
    boolean isWandering();

    void setWandering(boolean wandering);

    boolean isStaying();

    void setStaying(boolean staying);

    default boolean isPatrolling(){
        return this.getBoundPos() != null;
    }

    default BlockPos getBoundPos(){
        return null;
    }

    default void setBoundPos(BlockPos blockPos){
    }

    default boolean isFollowing(){
        return !this.isWandering() && !this.isStaying() && !this.isPatrolling();
    }

    boolean canUpdateMove();

    void updateMoveMode(Player player);

    boolean isCommanded();

    void setCommandPos(BlockPos blockPos);

    void setCommandPos(BlockPos blockPos, boolean removeEntity);

    void setCommandPosEntity(LivingEntity living);

    void tryKill(Player player);

    default void healServant(LivingEntity livingEntity){
        if (this.getTrueOwner() != null){
            boolean crown = false;
            if (livingEntity.getMobType() == ModMobType.FROST){
                crown = CuriosFinder.hasFrostCrown(this.getTrueOwner());
            }
            if (livingEntity.getMobType() == ModMobType.NATURAL){
                crown = CuriosFinder.hasWildCrown(this.getTrueOwner());
            }
            if (livingEntity.getMobType() == ModMobType.NETHER){
                crown = CuriosFinder.hasNetherCrown(this.getTrueOwner());
            }
            if (livingEntity.getMobType() == MobType.UNDEAD){
                crown = CuriosFinder.hasUndeadCrown(this.getTrueOwner());
            }
            if (!crown){
                if (this.getLifespan() > 0){
                    this.setHasLifespan(true);
                }
            } else {
                this.setHasLifespan(false);
            }
            if (!livingEntity.level.isClientSide) {
                if (!livingEntity.isOnFire() && !livingEntity.isDeadOrDying() && (!this.hasLifespan() || this.getLifespan() > 20)) {
                    if (livingEntity.getHealth() < livingEntity.getMaxHealth()){
                        if (this.getTrueOwner() instanceof Player owner) {
                            boolean curio = false;
                            int soulCost = 0;
                            int healRate = 0;
                            float healAmount = 0;
                            if (livingEntity.getMobType() == MobType.UNDEAD && MobsConfig.UndeadMinionHeal.get()){
                                curio = CuriosFinder.hasUndeadCape(owner);
                                soulCost = MobsConfig.UndeadMinionHealCost.get();
                                healRate = MobsConfig.UndeadMinionHealTime.get();
                                healAmount = MobsConfig.UndeadMinionHealAmount.get().floatValue();
                            }
                            if (livingEntity.getMobType() == ModMobType.NATURAL && MobsConfig.NaturalMinionHeal.get()){
                                curio = CuriosFinder.hasWildRobe(owner);
                                soulCost = MobsConfig.NaturalMinionHealCost.get();
                                healRate = MobsConfig.NaturalMinionHealTime.get();
                                healAmount = MobsConfig.NaturalMinionHealAmount.get().floatValue();
                            }
                            if (livingEntity.getMobType() == ModMobType.FROST && MobsConfig.FrostMinionHeal.get()){
                                curio = CuriosFinder.hasFrostRobes(owner);
                                soulCost = MobsConfig.FrostMinionHealCost.get();
                                healRate = MobsConfig.FrostMinionHealTime.get();
                                healAmount = MobsConfig.FrostMinionHealAmount.get().floatValue();
                            }
                            if (livingEntity.getMobType() == ModMobType.NETHER && MobsConfig.NetherMinionHeal.get()){
                                curio = CuriosFinder.hasNetherRobe(owner);
                                soulCost = MobsConfig.NetherMinionHealCost.get();
                                healRate = MobsConfig.NetherMinionHealTime.get();
                                healAmount = MobsConfig.NetherMinionHealAmount.get().floatValue();
                            }
                            if (curio) {
                                if (SEHelper.getSoulsAmount(owner, soulCost)) {
                                    if (livingEntity.tickCount % (MathHelper.secondsToTicks(healRate) + 1) == 0) {
                                        livingEntity.heal(healAmount);
                                        Vec3 vector3d = livingEntity.getDeltaMovement();
                                        if (livingEntity.level instanceof ServerLevel serverWorld) {
                                            SEHelper.decreaseSouls(owner, soulCost);
                                            serverWorld.sendParticles(ParticleTypes.SCULK_SOUL, livingEntity.getRandomX(0.5D), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5D), 0, vector3d.x * -0.2D, 0.1D, vector3d.z * -0.2D, 0.5F);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
