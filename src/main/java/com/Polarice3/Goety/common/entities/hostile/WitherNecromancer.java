package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.WitherSkeletonServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.projectiles.WitherBolt;
import com.Polarice3.Goety.common.entities.util.FirePillar;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class WitherNecromancer extends AbstractNecromancer implements Enemy {
    private final ModServerBossInfo bossInfo;

    public WitherNecromancer(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.PURPLE, false, false);
        this.setHostile(true);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
    }

    public void projectileGoal(int priority){
        this.goalSelector.addGoal(priority, new WitherNecromancerRangedGoal(this, 1.0D, 20, 12.0F));
    }

    public void summonSpells(int priority){
        this.goalSelector.addGoal(priority + 1, new SummonServantSpell());
        this.goalSelector.addGoal(priority, new SummonFirePillarsGoal());
        this.goalSelector.addGoal(priority, new SummonFireSurroundGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WitherNecromancerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.WitherNecromancerArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.WitherNecromancerFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WitherNecromancerDamage.get());
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WitherNecromancerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WitherNecromancerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.WitherNecromancerFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.NecromancerDamage.get());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setConfigurableAttributes();
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

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        float f1 = (float)this.getNecroLevel();
        float size = 1.0F + Math.max(f1 * 0.15F, 0);
        return 2.523F * size;
    }

    @Override
    public int xpReward() {
        return 40;
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

    protected SoundEvent getAmbientSound() {
        return ModSounds.WITHER_NECROMANCER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.WITHER_NECROMANCER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.WITHER_NECROMANCER_DEATH.get();
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
        WitherBolt witherBolt = new WitherBolt(this, vector3d.x, vector3d.y, vector3d.z, this.level);
        witherBolt.setOwner(this);
        witherBolt.setPos(this.getX() + vector3d.x / 2, this.getEyeY() - 0.2, this.getZ() + vector3d.z / 2);
        witherBolt.rotateToMatchMovement();
        if (this.level.addFreshEntity(witherBolt)) {
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

    public boolean canBeAffected(MobEffectInstance p_34192_) {
        return p_34192_.getEffect() != MobEffects.WITHER && super.canBeAffected(p_34192_);
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    public class SummonServantSpell extends SummoningSpellGoal {

        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned && owned.getTrueOwner() == WitherNecromancer.this;
            int i = WitherNecromancer.this.level.getEntitiesOfClass(LivingEntity.class, WitherNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                    , predicate).size();
            return super.canUse() && i < 6;
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
                for (int i1 = 0; i1 < 2; ++i1) {
                    Summoned summoned = new WitherSkeletonServant(ModEntityType.WITHER_SKELETON_SERVANT.get(), serverLevel);
                    BlockPos blockPos = BlockFinder.SummonRadius(WitherNecromancer.this.blockPosition(), summoned, serverLevel);
                    summoned.setTrueOwner(WitherNecromancer.this);
                    summoned.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summoned);
                    summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(WitherNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    summoned.setPersistenceRequired();
                    if (serverLevel.addFreshEntity(summoned)){
                        summoned.playSound(ModSounds.SUMMON_SPELL.get(), 1.0F, 0.75F);
                    }
                }
            }
        }

        protected int getCastingInterval(){
            return 200;
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

    public class SummonFirePillarsGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = WitherNecromancer.this.getTarget();
            if (WitherNecromancer.this.isSpellCasting()) {
                return false;
            } else {
                return target != null
                        && target.isAlive()
                        && WitherNecromancer.this.random.nextBoolean()
                        && WitherNecromancer.this.idleSpellCool <= 0;
            }
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0;
        }

        public void start() {
            this.spellTime = MathHelper.secondsToTicks(3);
            WitherNecromancer.this.setSpellCooldown(WitherNecromancer.this.getSpellCooldown() + 60);
            WitherNecromancer.this.playSound(ModSounds.RUMBLE.get(), 1.0F, 1.0F);
            WitherNecromancer.this.setSpellCasting(true);
            WitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.CLOUD);
            WitherNecromancer.this.setAnimationState(SPELL_ANIM);
            if (WitherNecromancer.this.level instanceof ServerLevel serverLevel) {
                int warmUp = 20;
                int duration = 180;
                Vec3 vector3d = WitherNecromancer.this.getViewVector(1.0F);
                float f = (float) Mth.atan2(vector3d.z - WitherNecromancer.this.getZ(), vector3d.x - WitherNecromancer.this.getX());
                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 1.0F;
                    FirePillar flames = new FirePillar(serverLevel, WitherNecromancer.this.getX() + (double) Mth.cos(f2), WitherNecromancer.this.getY(), WitherNecromancer.this.getZ() + (double) Mth.sin(f2));
                    flames.setOwner(WitherNecromancer.this);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    MobUtil.moveDownToGround(flames);
                    serverLevel.addFreshEntity(flames);
                }

                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 3.0F;
                    FirePillar flames = new FirePillar(serverLevel, WitherNecromancer.this.getX() + (double) Mth.cos(f2) * 3.0D, WitherNecromancer.this.getY(), WitherNecromancer.this.getZ() + (double) Mth.sin(f2) * 3.0D);
                    flames.setOwner(WitherNecromancer.this);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    MobUtil.moveDownToGround(flames);
                    serverLevel.addFreshEntity(flames);
                }

                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 6.0F;
                    FirePillar flames = new FirePillar(serverLevel, WitherNecromancer.this.getX() + (double) Mth.cos(f2) * 6.0D, WitherNecromancer.this.getY(), WitherNecromancer.this.getZ() + (double) Mth.sin(f2) * 6.0D);
                    flames.setOwner(WitherNecromancer.this);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    MobUtil.moveDownToGround(flames);
                    serverLevel.addFreshEntity(flames);
                }

                for (int k = 0; k < 16; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 9.0F;
                    FirePillar flames = new FirePillar(serverLevel, WitherNecromancer.this.getX() + (double) Mth.cos(f2) * 9.0F, WitherNecromancer.this.getY(), WitherNecromancer.this.getZ() + (double) Mth.sin(f2) * 9.0F);
                    flames.setOwner(WitherNecromancer.this);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    MobUtil.moveDownToGround(flames);
                    serverLevel.addFreshEntity(flames);
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            WitherNecromancer.this.setSpellCasting(false);
            WitherNecromancer.this.setAnimationState(IDLE);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 0) {
                WitherNecromancer.this.addEffect(new MobEffectInstance(GoetyEffects.TANGLED.get(), 180, 0, false, false));
                WitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
                WitherNecromancer.this.idleSpellCool = MathHelper.secondsToTicks(10);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public class SummonFireSurroundGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = WitherNecromancer.this.getTarget();
            if (WitherNecromancer.this.isSpellCasting()) {
                return false;
            } else {
                return target != null
                        && target.isAlive()
                        && WitherNecromancer.this.random.nextBoolean()
                        && WitherNecromancer.this.idleSpellCool <= 0;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity target = WitherNecromancer.this.getTarget();
            return this.spellTime > 0 && target != null && target.isAlive();
        }

        public void start() {
            this.spellTime = MathHelper.secondsToTicks(3);
            WitherNecromancer.this.setSpellCooldown(WitherNecromancer.this.getSpellCooldown() + 60);
            WitherNecromancer.this.playSound(ModSounds.RUMBLE.get(), 1.0F, 1.0F);
            WitherNecromancer.this.setSpellCasting(true);
            WitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.CLOUD);
            WitherNecromancer.this.setAnimationState(SPELL_ANIM);
            LivingEntity target = WitherNecromancer.this.getTarget();
            if (WitherNecromancer.this.level instanceof ServerLevel serverLevel
                    && target != null && target.isAlive()) {
                int warmUp = 20;
                int duration = 180;
                List<Vec3> vec3s = BlockFinder.buildOuterBlockCircle(target.position(), 6.0D);
                for (Vec3 vec3 : vec3s) {
                    FirePillar flames = new FirePillar(serverLevel, vec3.x, vec3.y, vec3.z);
                    flames.setOwner(WitherNecromancer.this);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    MobUtil.moveDownToGround(flames);
                    serverLevel.addFreshEntity(flames);
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            WitherNecromancer.this.setSpellCasting(false);
            WitherNecromancer.this.setAnimationState(IDLE);
            WitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            WitherNecromancer.this.idleSpellCool = MathHelper.secondsToTicks(10);
        }

        public void tick() {
            --this.spellTime;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public class WitherNecromancerRangedGoal extends NecromancerRangedGoal{

        public WitherNecromancerRangedGoal(AbstractNecromancer mob, double speed, int attackInterval, float attackRadius) {
            super(mob, speed, attackInterval, attackRadius);
        }

        public boolean canUse() {
            LivingEntity livingentity = WitherNecromancer.this.getTarget();
            if (livingentity != null){
                return super.canUse() && WitherNecromancer.this.hasLineOfSight(livingentity);
            }
            return false;
        }
    }
}
