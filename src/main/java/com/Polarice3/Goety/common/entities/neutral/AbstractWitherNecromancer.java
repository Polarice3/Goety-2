package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.ReaperServant;
import com.Polarice3.Goety.common.entities.ally.undead.WraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.WitherSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.common.entities.projectiles.WitherBolt;
import com.Polarice3.Goety.common.entities.util.FirePillar;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.revive.SoulJar;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class AbstractWitherNecromancer extends AbstractNecromancer {

    public AbstractWitherNecromancer(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
    }

    public void projectileGoal(int priority){
        this.goalSelector.addGoal(priority, new WitherNecromancerRangedGoal(this, 1.0D, 20, 12.0F));
    }

    public void avoidGoal(int priority){
    }

    public void summonSpells(int priority){
        this.goalSelector.addGoal(priority + 1, new SummonServantSpell());
        this.goalSelector.addGoal(priority + 2, new WitherSummonUndeadGoal());
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
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.WitherNecromancerDamage.get());
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof AbstractWitherNecromancer;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return SpellConfig.WitherNecromancerLimit.get();
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
        witherBolt.setExtraDamage(this.getNecroLevel());
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

    public boolean canBeAffected(MobEffectInstance p_34192_) {
        return p_34192_.getEffect() != MobEffects.WITHER && super.canBeAffected(p_34192_);
    }

    @Override
    public void playLaughSound() {
        this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 1.0F, 0.05F);
    }

    public Summoned getDefaultSummon(){
        return new WitherSkeletonServant(ModEntityType.WITHER_SKELETON_SERVANT.get(), this.level);
    }

    public Summoned getSummon(){
        Summoned summoned = getDefaultSummon();
        if (this.getSummonList().stream().anyMatch(entityType -> entityType.is(ModTags.EntityTypes.ZOMBIE_SERVANTS))) {
            if (this.level.random.nextBoolean()) {
                summoned = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().stream().anyMatch(entityType -> entityType.is(ModTags.EntityTypes.SKELETON_SERVANTS))) {
            if (this.level.random.nextBoolean()) {
                summoned = new WitherSkeletonServant(ModEntityType.WITHER_SKELETON_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get())) {
            if (this.level.random.nextFloat() <= 0.05F) {
                summoned = new WraithServant(ModEntityType.WRAITH_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.REAPER_SERVANT.get())) {
            if (this.level.random.nextFloat() <= 0.05F) {
                summoned = new ReaperServant(ModEntityType.REAPER_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.VANGUARD_SERVANT.get())){
            if (this.level.random.nextFloat() <= 0.15F) {
                summoned = new VanguardServant(ModEntityType.VANGUARD_SERVANT.get(), this.level);
            }
        }
        if (this.getSummonList().contains(ModEntityType.BLACKGUARD_SERVANT.get())) {
            if (this.level.random.nextFloat() <= 0.05F) {
                summoned = new BlackguardServant(ModEntityType.BLACKGUARD_SERVANT.get(), this.level);
            }
        }
        return summoned;
    }

    public void setNecroLevel(int shot){
        int i = Mth.clamp(shot, 0, 2);
        this.entityData.set(LEVEL, i);
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance != null){
            attributeInstance.setBaseValue(AttributesConfig.WitherNecromancerHealth.get() * Math.max(i * 1.25F, 1));
        }
        this.reapplyPosition();
        this.refreshDimensions();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level.isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (SoulJar.isWither(itemstack)){
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (this.getNecroLevel() < 2) {
                        this.setNecroLevel(this.getNecroLevel() + 1);
                    }
                    this.heal(AttributesConfig.WitherNecromancerHealth.get().floatValue());
                    if (this.level instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!itemstack.is(ModItems.SOUL_JAR.get())) {
                    return super.mobInteract(pPlayer, pHand);
                }
            }
        }
        return InteractionResult.PASS;
    }

    public class SummonServantSpell extends SummoningSpellGoal {

        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned && owned.getTrueOwner() == AbstractWitherNecromancer.this;
            int i = AbstractWitherNecromancer.this.level.getEntitiesOfClass(LivingEntity.class, AbstractWitherNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                    , predicate).size();
            return super.canUse() && i < 6;
        }

        @Override
        public void tick() {
            --this.spellTime;
            if (this.spellTime == 10) {
                if (this.getCastSound() != null) {
                    AbstractWitherNecromancer.this.playSound(this.getCastSound(), 1.0F, 1.0F);
                }
                AbstractWitherNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, 0.05F);
                this.castSpell();
                AbstractWitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            }
        }

        protected void castSpell(){
            if (AbstractWitherNecromancer.this.level instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 2; ++i1) {
                    Summoned summoned = AbstractWitherNecromancer.this.getSummon();
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractWitherNecromancer.this.blockPosition(), summoned, serverLevel);
                    summoned.setTrueOwner(AbstractWitherNecromancer.this);
                    summoned.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summoned);
                    if (!AbstractWitherNecromancer.this.getType().is(ModTags.EntityTypes.MINI_BOSSES)) {
                        if (MobsConfig.NecromancerSummonsLife.get()) {
                            summoned.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                        }
                    }
                    summoned.setPersistenceRequired();
                    summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(AbstractWitherNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    if (serverLevel.addFreshEntity(summoned)){
                        SoundUtil.playNecromancerSummon(summoned);
                        ServerParticleUtil.summonUndeadParticles(serverLevel, summoned, new ColorUtil(0xffa300), 0xffa300, 0xffff6e);
                    }
                }
            }
        }

        protected int getCastingInterval(){
            return 200;
        }

        @Override
        protected NecromancerSpellType getNecromancerSpellType() {
            return NecromancerSpellType.ZOMBIE;
        }
    }

    public class WitherSummonUndeadGoal extends SummonUndeadGoal {
        @Override
        public void playLaughSound() {
            AbstractWitherNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, 0.05F);
        }

        public void summonUndeadParticles(ServerLevel serverLevel, Entity entity) {
            ServerParticleUtil.summonUndeadParticles(serverLevel, entity, new ColorUtil(0xffa300), 0xffa300, 0xffff6e);
        }
    }

    public class SummonFirePillarsGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractWitherNecromancer.this.getTarget();
            if (AbstractWitherNecromancer.this.isSpellCasting()) {
                return false;
            } else {
                return target != null
                        && target.isAlive()
                        && AbstractWitherNecromancer.this.random.nextBoolean()
                        && AbstractWitherNecromancer.this.idleSpellCool <= 0;
            }
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0;
        }

        public void start() {
            this.spellTime = MathHelper.secondsToTicks(3);
            AbstractWitherNecromancer.this.setSpellCooldown(AbstractWitherNecromancer.this.getSpellCooldown() + 60);
            AbstractWitherNecromancer.this.playSound(ModSounds.RUMBLE.get(), 1.0F, 1.0F);
            AbstractWitherNecromancer.this.setSpellCasting(true);
            AbstractWitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.CLOUD);
            AbstractWitherNecromancer.this.setAnimationState(SPELL_ANIM);
            if (AbstractWitherNecromancer.this.level instanceof ServerLevel serverLevel) {
                int warmUp = 20;
                int duration = 180;
                Vec3 vector3d = AbstractWitherNecromancer.this.getViewVector(1.0F);
                float f = (float) Mth.atan2(vector3d.z - AbstractWitherNecromancer.this.getZ(), vector3d.x - AbstractWitherNecromancer.this.getX());
                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 1.0F;
                    FirePillar flames = new FirePillar(serverLevel, AbstractWitherNecromancer.this.getX() + (double) Mth.cos(f2), AbstractWitherNecromancer.this.getY(), AbstractWitherNecromancer.this.getZ() + (double) Mth.sin(f2));
                    flames.setOwner(AbstractWitherNecromancer.this);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    MobUtil.moveDownToGround(flames);
                    serverLevel.addFreshEntity(flames);
                }

                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 3.0F;
                    FirePillar flames = new FirePillar(serverLevel, AbstractWitherNecromancer.this.getX() + (double) Mth.cos(f2) * 3.0D, AbstractWitherNecromancer.this.getY(), AbstractWitherNecromancer.this.getZ() + (double) Mth.sin(f2) * 3.0D);
                    flames.setOwner(AbstractWitherNecromancer.this);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    MobUtil.moveDownToGround(flames);
                    serverLevel.addFreshEntity(flames);
                }

                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 6.0F;
                    FirePillar flames = new FirePillar(serverLevel, AbstractWitherNecromancer.this.getX() + (double) Mth.cos(f2) * 6.0D, AbstractWitherNecromancer.this.getY(), AbstractWitherNecromancer.this.getZ() + (double) Mth.sin(f2) * 6.0D);
                    flames.setOwner(AbstractWitherNecromancer.this);
                    flames.setDuration(duration);
                    flames.setWarmUp(warmUp);
                    MobUtil.moveDownToGround(flames);
                    serverLevel.addFreshEntity(flames);
                }

                for (int k = 0; k < 16; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 0.25F + 9.0F;
                    FirePillar flames = new FirePillar(serverLevel, AbstractWitherNecromancer.this.getX() + (double) Mth.cos(f2) * 9.0F, AbstractWitherNecromancer.this.getY(), AbstractWitherNecromancer.this.getZ() + (double) Mth.sin(f2) * 9.0F);
                    flames.setOwner(AbstractWitherNecromancer.this);
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
            AbstractWitherNecromancer.this.setSpellCasting(false);
            AbstractWitherNecromancer.this.setAnimationState(IDLE);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 0) {
                AbstractWitherNecromancer.this.addEffect(new MobEffectInstance(GoetyEffects.TANGLED.get(), 180, 0, false, false));
                AbstractWitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
                AbstractWitherNecromancer.this.idleSpellCool = MathHelper.secondsToTicks(10);
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
            LivingEntity target = AbstractWitherNecromancer.this.getTarget();
            if (AbstractWitherNecromancer.this.isSpellCasting()) {
                return false;
            } else {
                return target != null
                        && target.isAlive()
                        && AbstractWitherNecromancer.this.random.nextBoolean()
                        && AbstractWitherNecromancer.this.idleSpellCool <= 0;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity target = AbstractWitherNecromancer.this.getTarget();
            return this.spellTime > 0 && target != null && target.isAlive();
        }

        public void start() {
            this.spellTime = MathHelper.secondsToTicks(3);
            AbstractWitherNecromancer.this.setSpellCooldown(AbstractWitherNecromancer.this.getSpellCooldown() + 60);
            AbstractWitherNecromancer.this.playSound(ModSounds.RUMBLE.get(), 1.0F, 1.0F);
            AbstractWitherNecromancer.this.setSpellCasting(true);
            AbstractWitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.CLOUD);
            AbstractWitherNecromancer.this.setAnimationState(SPELL_ANIM);
            LivingEntity target = AbstractWitherNecromancer.this.getTarget();
            if (AbstractWitherNecromancer.this.level instanceof ServerLevel serverLevel
                    && target != null && target.isAlive()) {
                int warmUp = 20;
                int duration = 180;
                List<Vec3> vec3s = BlockFinder.buildOuterBlockCircle(target.position(), 6.0D);
                for (Vec3 vec3 : vec3s) {
                    FirePillar flames = new FirePillar(serverLevel, vec3.x, vec3.y, vec3.z);
                    flames.setOwner(AbstractWitherNecromancer.this);
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
            AbstractWitherNecromancer.this.setSpellCasting(false);
            AbstractWitherNecromancer.this.setAnimationState(IDLE);
            AbstractWitherNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            AbstractWitherNecromancer.this.idleSpellCool = MathHelper.secondsToTicks(10);
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
            LivingEntity livingentity = AbstractWitherNecromancer.this.getTarget();
            if (livingentity != null){
                return super.canUse() && AbstractWitherNecromancer.this.hasLineOfSight(livingentity);
            }
            return false;
        }
    }
}
