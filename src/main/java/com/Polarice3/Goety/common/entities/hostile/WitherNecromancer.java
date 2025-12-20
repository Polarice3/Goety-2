package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.neutral.AbstractWitherNecromancer;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class WitherNecromancer extends AbstractWitherNecromancer implements Enemy {
    private final ModServerBossInfo bossInfo;

    public WitherNecromancer(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.PURPLE, false, false);
        this.setHostile(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (MainConfig.SpecialBossBar.get()) {
            this.bossInfo.addPlayer(pPlayer);
        }
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        if (!this.level.isClientSide){
            if (this.level instanceof ServerLevel serverLevel){
                for (int k = 0; k < 64; ++k) {
                    float f2 = random.nextFloat() * 4.0F;
                    float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                    double d1 = Mth.cos(f1) * f2;
                    double d2 = 0.01D + random.nextDouble() * 0.5D;
                    double d3 = Mth.sin(f1) * f2;
                    serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                    serverLevel.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                }
            }
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                ModLootTables.createLootChest(this,
                        ModBlocks.LOFTY_CHEST.get().defaultBlockState(),
                        this.blockPosition(),
                        pCause);
            }
        }
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() != null){
            if (pSource.getEntity() instanceof LivingEntity livingEntity){
                if (!(livingEntity instanceof WitherSkeleton) && !livingEntity.isAlliedTo(this)){
                    for (WitherSkeleton witherSkeleton : this.level.getEntitiesOfClass(WitherSkeleton.class, this.getBoundingBox().inflate(10))){
                        if (witherSkeleton.getTarget() != livingEntity) {
                            if (witherSkeleton.canAttack(livingEntity)) {
                                witherSkeleton.setTarget(livingEntity);
                            }
                        }
                    }
                }
            }
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        } else if (pEntity instanceof WitherSkeleton || pEntity instanceof Blaze) {
            return this.getTeam() == null && pEntity.getTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }
}
