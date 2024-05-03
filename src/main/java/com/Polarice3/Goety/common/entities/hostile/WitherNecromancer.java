package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.neutral.Volcano;
import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class WitherNecromancer extends AbstractNecromancer implements Enemy {
    private final ModServerBossInfo bossInfo;

    public WitherNecromancer(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.bossInfo = new ModServerBossInfo(this.getUUID(), this, BossEvent.BossBarColor.PURPLE, false, false);
        this.setHostile(true);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        if (this.level.isClientSide){
            Goety.PROXY.addBoss(this);
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
    }

    public void summonSpells(int priority){
        this.goalSelector.addGoal(priority, new SummonServantSpell());
        this.goalSelector.addGoal(priority + 1, new SummonVolcanosGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 160.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.NecromancerDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), 160.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.NecromancerDamage.get());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setConfigurableAttributes();
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        this.bossInfo.setId(this.getUUID());
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

    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.WITHER_SKELETON_STEP;
    }

    @Override
    public void spellCastParticles() {
        for (int i = 0; i < this.level.random.nextInt(35) + 10; ++i) {
            this.level.addParticle(ModParticleTypes.TOTEM_EFFECT.get(), this.getX(), this.getY(), this.getZ(), 0.45, 0.45, 0.45);
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity p_33317_, float p_33318_) {
        Vec3 vector3d = this.getViewVector(1.0F);
        HellBolt hellBolt = new HellBolt(this, vector3d.x, vector3d.y, vector3d.z, this.level);
        hellBolt.setPos(this.getX() + vector3d.x / 2, this.getEyeY() - 0.2, this.getZ() + vector3d.z / 2);
        hellBolt.rotateToMatchMovement();
        if (this.level.addFreshEntity(hellBolt)) {
            this.playSound(SoundEvents.WITHER_SHOOT, 0.5F, 0.25F);
            this.playSound(ModSounds.HELL_BOLT_SHOOT.get());
            this.swing(InteractionHand.MAIN_HAND);
        }
    }

    public boolean doHurtTarget(Entity p_34169_) {
        if (!super.doHurtTarget(p_34169_)) {
            return false;
        } else {
            if (p_34169_ instanceof LivingEntity) {
                ((LivingEntity)p_34169_).addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
            }

            return true;
        }
    }

    public boolean canBeAffected(MobEffectInstance p_34192_) {
        return p_34192_.getEffect() != MobEffects.WITHER && super.canBeAffected(p_34192_);
    }

    @Override
    public void remove(RemovalReason p_146834_) {
        if (this.level.isClientSide) {
            Goety.PROXY.removeBoss(this);
        }
        super.remove(p_146834_);
    }

    public class SummonServantSpell extends SummoningSpellGoal {

        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned && owned.getTrueOwner() == WitherNecromancer.this;
            int i = WitherNecromancer.this.level.getEntitiesOfClass(LivingEntity.class, WitherNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                    , predicate).size();
            return super.canUse() && i < 2;
        }

        @Override
        public void tick() {
            --this.spellTime;
            if (this.spellTime == 10) {
                if (this.getCastSound() != null) {
                    WitherNecromancer.this.playSound(this.getCastSound(), 1.0F, 1.0F);
                }
                WitherNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, 0.05F);
                this.castSpell();
                WitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            }
        }

        protected void castSpell(){
            if (WitherNecromancer.this.level instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 1 + serverLevel.random.nextInt(3); ++i1) {
                    Summoned summoned = new SkeletonServant(ModEntityType.SKELETON_SERVANT.get(), serverLevel);
                    BlockPos blockPos = BlockFinder.SummonRadius(WitherNecromancer.this, serverLevel);
                    summoned.setTrueOwner(WitherNecromancer.this);
                    summoned.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summoned);
                    summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(WitherNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    summoned.setPersistenceRequired();
                    if (serverLevel.addFreshEntity(summoned)){
                        summoned.playSound(ModSounds.SUMMON_SPELL.get(), 1.0F, 1.0F);
                    }
                }
            }
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.PREPARE_SUMMON.get();
        }

        @Override
        protected NecromancerSpellType getNecromancerSpellType() {
            return NecromancerSpellType.ZOMBIE;
        }
    }

    public class SummonVolcanosGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = WitherNecromancer.this.getTarget();
            if (WitherNecromancer.this.isShooting()){
                return false;
            } else if (WitherNecromancer.this.isSpellCasting()) {
                return false;
            } else {
                return target != null && target.isAlive() && WitherNecromancer.this.idleSpellCool <= 0;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity target = WitherNecromancer.this.getTarget();
            return this.spellTime > 0 && target != null && target.isAlive();
        }

        public void start() {
            this.spellTime = 29;
            WitherNecromancer.this.setSpellCooldown(100);
            WitherNecromancer.this.playSound(ModSounds.PREPARE_SUMMON.get(), 1.0F, 1.0F);
            WitherNecromancer.this.setSpellCasting(true);
            WitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.CLOUD);
            WitherNecromancer.this.level.broadcastEntityEvent(WitherNecromancer.this, (byte) 6);
            if (WitherNecromancer.this.level instanceof ServerLevel serverLevel) {
                LivingEntity target = WitherNecromancer.this.getTarget();
                if (target != null) {
                    float f = (float) Mth.atan2(target.getZ() - WitherNecromancer.this.getZ(), target.getX() - WitherNecromancer.this.getX());
                    for (int k = 0; k < 4; ++k) {
                        float f2 = f + (float) k * (float) Math.PI * 0.25F + 3.0F;
                        Volcano volcano = new Volcano(ModEntityType.VOLCANO.get(), serverLevel);
                        volcano.setTrueOwner(WitherNecromancer.this);
                        volcano.setPos(WitherNecromancer.this.getX() + (double) Mth.cos(f2) * 3.0D, WitherNecromancer.this.getY() + 4.0F, WitherNecromancer.this.getZ() + (double) Mth.sin(f2) * 3.0D);
                        volcano.setExplosionPower(1.5F);
                        volcano.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(WitherNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        serverLevel.addFreshEntity(volcano);
                    }

                    for (int k = 0; k < 8; ++k) {
                        float f2 = f + (float) k * (float) Math.PI * 0.25F + 6.0F;
                        Volcano volcano = new Volcano(ModEntityType.VOLCANO.get(), serverLevel);
                        volcano.setTrueOwner(WitherNecromancer.this);
                        volcano.setPos(WitherNecromancer.this.getX() + (double) Mth.cos(f2) * 6.0D, WitherNecromancer.this.getY() + 4.0F, WitherNecromancer.this.getZ() + (double) Mth.sin(f2) * 6.0D);
                        volcano.setExplosionPower(1.5F);
                        volcano.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(WitherNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        serverLevel.addFreshEntity(volcano);
                    }
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            WitherNecromancer.this.setSpellCasting(false);
            WitherNecromancer.this.level.broadcastEntityEvent(WitherNecromancer.this, (byte) 7);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 0) {
                WitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
                WitherNecromancer.this.idleSpellCool = MathHelper.secondsToTicks(10);
            }
        }
    }
}
