package com.Polarice3.Goety.common.entities.hostile.cultists;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ai.WitchBarterGoal;
import com.Polarice3.Goety.common.entities.ally.MagmaCubeServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.hostile.servants.ObsidianMonolith;
import com.Polarice3.Goety.common.entities.neutral.BlazeServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.projectiles.HellChant;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class Heretic extends Cultist {
    private static final EntityDataAccessor<Boolean> CHANTING = SynchedEntityData.defineId(Heretic.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CASTING = SynchedEntityData.defineId(Heretic.class, EntityDataSerializers.BOOLEAN);
    private final DynamicGameEventListener<GameEventListener> gameEventListener;
    private ObsidianMonolith monolith;
    public List<Vec3> revivePos = new ArrayList<>();
    public int chantCoolDown;
    public int chantTimes;
    public int castCoolDown;
    public float castAmount = 0.125F;
    public float castOld;
    public float cast;

    public Heretic(EntityType<? extends Cultist> type, Level worldIn) {
        super(type, worldIn);
        this.gameEventListener = new DynamicGameEventListener<>(new GameEventListener() {
            @Override
            public boolean handleEventsImmediately() {
                return true;
            }

            public PositionSource getListenerSource() {
                return new BlockPositionSource(Heretic.this.blockPosition());
            }

            public int getListenerRadius() {
                return 32;
            }

            @Override
            public boolean handleGameEvent(ServerLevel p_223757_, GameEvent.Message p_223758_) {
                if (!Heretic.this.isRemoved()) {
                    if (p_223758_.gameEvent() == GameEvent.ENTITY_DIE) {
                        Entity sourceEntity = p_223758_.context().sourceEntity();
                        if (sourceEntity instanceof Mob mob && !(mob instanceof IOwned) && !(mob instanceof Heretic)) {
                            Heretic.this.getRevivePos().add(mob.position());
                            return true;
                        }
                    }

                }
                return false;
            }
        });
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PrayToMonolithGoal(this));
        this.goalSelector.addGoal(1, new WitchBarterGoal(this));
        this.goalSelector.addGoal(2, new ChantAtTargetGoal(this));
        this.goalSelector.addGoal(3, new CastingGoal(this));
        this.goalSelector.addGoal(4, AvoidTargetGoal.newGoal(this, 4.0F, 1.0F, 1.0F));
        this.targetSelector.addGoal(3, new NearestAttackableWitchTargetGoal<>(this, Player.class, 10, true, false, null));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(CHANTING, false);
        this.getEntityData().define(CASTING, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ChantCoolDown", this.getChantCoolDown());
        pCompound.putInt("ChantTimes", this.getChantTimes());
        pCompound.putInt("CastCoolDown", this.getCastCoolDown());
        if (!this.getRevivePos().isEmpty()) {
            ListTag listTag = new ListTag();
            for (Vec3 vec3 : this.getRevivePos()) {
                CompoundTag tag = new CompoundTag();
                tag.putDouble("PosX", vec3.x);
                tag.putDouble("PosY", vec3.y);
                tag.putDouble("PosZ", vec3.z);
                listTag.add(tag);
            }
            pCompound.put("RevivePos", listTag);
        }
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ChantCoolDown")) {
            this.setChantCoolDown(pCompound.getInt("ChantCoolDown"));
        }
        if (pCompound.contains("ChantTimes")) {
            this.setChantTimes(pCompound.getInt("ChantTimes"));
        }
        if (pCompound.contains("CastCoolDown")) {
            this.setCastCoolDown(pCompound.getInt("CastCoolDown"));
        }
        if (pCompound.contains("RevivePos")) {
            ListTag listTag = pCompound.getList("RevivePos", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag tag = listTag.getCompound(i);
                Vec3 vec3 = new Vec3(tag.getDouble("PosX"), tag.getDouble("PosY"), tag.getDouble("PosZ"));
                this.getRevivePos().add(vec3);
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.HERETIC_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_34154_) {
        return ModSounds.HERETIC_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.HERETIC_DEATH.get();
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.HERETIC_CELEBRATE.get();
    }

    public void setChanting(boolean chanting) {
        this.getEntityData().set(CHANTING, chanting);
    }

    public boolean isChanting() {
        return this.getEntityData().get(CHANTING);
    }

    public int getChantCoolDown() {
        return this.chantCoolDown;
    }

    public void setChantCoolDown(int chantCoolDown) {
        this.chantCoolDown = chantCoolDown;
    }

    public int getChantTimes() {
        return this.chantTimes;
    }

    public void setChantTimes(int chantTimes) {
        this.chantTimes = chantTimes;
    }

    public void setCasting(boolean casting) {
        this.getEntityData().set(CASTING, casting);
    }

    public boolean isCasting() {
        return this.getEntityData().get(CASTING);
    }

    public int getCastCoolDown() {
        return this.castCoolDown;
    }

    public void setCastCoolDown(int castCoolDown) {
        this.castCoolDown = castCoolDown;
    }

    public List<Vec3> getRevivePos() {
        return this.revivePos;
    }

    public void setMonolith(@Nullable ObsidianMonolith monolith) {
        this.monolith = monolith;
    }

    @Nullable
    public ObsidianMonolith getMonolith() {
        return this.monolith;
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        if (entityIn instanceof ObsidianMonolith) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else if (entityIn instanceof ZombifiedPiglin){
            return this.getTeam() == null && entityIn.getTeam() == null;
        }
        return super.isAlliedTo(entityIn);
    }

    @Override
    public void push(Entity p_21294_) {
        if (!this.level.isClientSide) {
            if (!this.isCasting() && this.tickCount >= 20) {
                super.push(p_21294_);
            }
        }
    }

    protected void doPush(Entity p_20971_) {
        if (!this.level.isClientSide) {
            if (!this.isCasting() && this.tickCount >= 20) {
                super.doPush(p_20971_);
            }
        }
    }

    public boolean canCollideWith(Entity p_20303_) {
        if (!this.isCasting() && this.tickCount >= 20){
            return super.canCollideWith(p_20303_);
        } else {
            return false;
        }
    }

    @Override
    public boolean isBarterable() {
        return !this.isCasting();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide){
            if (this.chantCoolDown > 0){
                --this.chantCoolDown;
            }
            if (this.castCoolDown > 0){
                --this.castCoolDown;
            }
            if (this.getMonolith() == null){
                if (this.getLeader() instanceof ObsidianMonolith obsidianMonolith){
                    this.setMonolith(obsidianMonolith);
                } else {
                    for (ObsidianMonolith monolith1 : this.level.getEntitiesOfClass(ObsidianMonolith.class, this.getBoundingBox().inflate(64.0D, 8.0D, 64.0D))){
                        if (monolith1.getTrueOwner() == null || monolith1.getTrueOwner() instanceof Cultist){
                            this.setMonolith(monolith1);
                        }
                    }
                }
            } else if (this.getMonolith() != null && this.getLeader() != this.getMonolith()){
                this.setLeader(this.getMonolith());
            }
        } else {
            this.castOld = this.cast;
            if (this.isCasting()){
                this.casting();
            } else {
                this.cast = Mth.clamp(this.cast - 0.1F, 0.0F, 1.0F);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.random.nextFloat() < 7.5E-4F) {
            this.level.broadcastEntityEvent(this, (byte)15);
        }
    }

    public void casting() {
        this.cast = Mth.clamp(this.cast + this.castAmount, 0.0F, 1.0F);
        if (this.cast == 0 || this.cast == 1) {
            this.castAmount *= -1;
        }
    }

    public float getCast(float p_268054_) {
        return Mth.lerp(p_268054_, this.castOld, this.cast);
    }

    protected float getDamageAfterMagicAbsorb(DamageSource damageSource, float damage) {
        damage = super.getDamageAfterMagicAbsorb(damageSource, damage);
        if (damageSource.getEntity() == this) {
            damage = 0.0F;
        }

        if (damageSource.isMagic() || damageSource.isFire()) {
            damage *= 0.15F;
        }

        return damage;
    }

    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> p_218348_) {
        Level level = this.level;
        if (level instanceof ServerLevel serverlevel) {
            p_218348_.accept(this.gameEventListener, serverlevel);
        }

    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4){
            this.setChanting(true);
        } else if (pId == 5){
            this.setChanting(false);
        } else if (pId == 6){
            this.setCasting(true);
        } else if (pId == 7){
            this.setCasting(false);
        } else if (pId == 15) {
            for(int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
                this.level.addParticle(ModParticleTypes.RISING_ENCHANT.get(), this.getX() + this.random.nextGaussian() * (double)0.13F, this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * (double)0.13F, this.getZ() + this.random.nextGaussian() * (double)0.13F, colorUtil.red(), colorUtil.green(), colorUtil.blue());
            }
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public static class ChantAtTargetGoal extends Goal {
        public Heretic heretic;
        public int chantTime;

        public ChantAtTargetGoal(Heretic heretic){
            this.heretic = heretic;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.heretic.getChantCoolDown() > 0){
                return false;
            }
            LivingEntity target = this.heretic.getTarget();
            return target != null && target.isAlive() && this.heretic.distanceTo(target) <= this.heretic.getAttributeValue(Attributes.FOLLOW_RANGE) && this.heretic.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.chantTime < 60;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            super.start();
            this.chantTime = 0;
            this.heretic.getNavigation().stop();
            this.heretic.setChanting(true);
            this.heretic.level.broadcastEntityEvent(this.heretic, (byte) 4);
            this.heretic.setChantTimes(0);
            this.heretic.playSound(ModSounds.HERETIC_CHANT.get(), 2.0F, 0.5F);
        }

        @Override
        public void stop() {
            super.stop();
            this.heretic.setChanting(false);
            this.heretic.level.broadcastEntityEvent(this.heretic, (byte) 5);
            this.heretic.setChantCoolDown(100);
            this.heretic.setChantTimes(0);
        }

        @Override
        public void tick() {
            ++this.chantTime;
            this.heretic.getNavigation().stop();
            this.heretic.getMoveControl().strafe(0.0F, 0.0F);
            if (this.heretic.getTarget() != null) {
                MobUtil.instaLook(this.heretic, this.heretic.getTarget());
            }
            if (this.chantTime % 10 == 0) {
                HellChant hellChant = ModEntityType.HELL_CHANT.get().create(this.heretic.level);
                if (hellChant != null) {
                    hellChant.setExtraDamage(3.0F);
                    if (this.heretic.level.getDifficulty() == Difficulty.HARD){
                        hellChant.setBurning(1);
                    }
                    hellChant.chant(this.heretic);
                    this.heretic.level.addFreshEntity(hellChant);
                }
            }
            super.tick();
        }
    }

    public static class CastingGoal extends Goal {
        public Heretic heretic;
        public int movingTime;
        public int castingTime;
        public Vec3 targetPos = null;
        public static int TOTAL_CAST_TIME = 30;

        public CastingGoal(Heretic heretic){
            this.heretic = heretic;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.heretic.getTarget();
            return target != null && target.isAlive() && !this.heretic.getRevivePos().isEmpty() && this.heretic.getCastCoolDown() <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.movingTime <= 100 && this.castingTime <= TOTAL_CAST_TIME;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            super.start();
            this.movingTime = 0;
            this.castingTime = 0;
            this.findTargetPos();
        }

        @Override
        public void stop() {
            super.stop();
            this.heretic.setCasting(false);
            this.heretic.level.broadcastEntityEvent(this.heretic, (byte) 7);
        }

        public void findTargetPos(){
            double hitDist = 0.0D;
            if (!this.heretic.getRevivePos().isEmpty()) {
                for (Vec3 vec31 : this.heretic.getRevivePos()) {
                    if (this.heretic.getNavigation().isStableDestination(new BlockPos(vec31))) {
                        Optional<Vec3> interceptPos = this.heretic.getBoundingBox().clip(vec31, vec31.add(1.0D, 1.0D, 1.0D));
                        if (this.heretic.getBoundingBox().inflate(this.heretic.getAttributeValue(Attributes.FOLLOW_RANGE)).contains(vec31)) {
                            if (0.0D <= hitDist) {
                                this.targetPos = vec31;
                                hitDist = 0.0D;
                            }
                        } else if (interceptPos.isPresent()) {
                            double possibleDist = vec31.distanceTo(interceptPos.get());

                            if (possibleDist < hitDist || hitDist == 0.0D) {
                                this.targetPos = vec31;
                                hitDist = possibleDist;
                            }
                        }
                    }
                }
            } else {
                this.targetPos = null;
            }
        }

        @Override
        public void tick() {
            if (this.targetPos != null) {
                if (this.heretic.level instanceof ServerLevel serverLevel) {
                    ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
                    ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.RISING_ENCHANT.get(), this.targetPos.x, this.targetPos.y, this.targetPos.z, colorUtil.red, colorUtil.green, colorUtil.blue, 1.0F);
                }
                if (this.heretic.distanceToSqr(this.targetPos) > Mth.square(4.0D)){
                    ++this.movingTime;
                    this.heretic.getNavigation().moveTo(this.targetPos.x, this.targetPos.y, this.targetPos.z, 1.0F);
                } else {
                    MobUtil.instaLook(this.heretic, this.targetPos);
                    ++this.castingTime;
                    if (!this.heretic.isCasting()){
                        this.heretic.setCasting(true);
                        this.heretic.level.broadcastEntityEvent(this.heretic, (byte) 6);
                    }
                    if (this.castingTime == TOTAL_CAST_TIME){
                        Summoned summon = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), this.heretic.level);
                        if (this.heretic.random.nextFloat() <= 0.25F){
                            summon = new MagmaCubeServant(ModEntityType.MAGMA_CUBE_SERVANT.get(), this.heretic.level);
                        } else if (this.heretic.random.nextFloat() <= 0.05F){
                            summon = new BlazeServant(ModEntityType.BLAZE_SERVANT.get(), this.heretic.level);
                        }
                        summon.moveTo(this.targetPos);
                        summon.setTrueOwner(this.heretic);
                        if (this.heretic.level instanceof ServerLevel serverLevel) {
                            summon.finalizeSpawn(serverLevel, this.heretic.level.getCurrentDifficultyAt(new BlockPos(this.targetPos)), MobSpawnType.MOB_SUMMONED, null, null);
                            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.FLAME, summon);
                        }
                        if (summon instanceof MagmaCubeServant cube){
                            cube.setSize(2, true);
                        }
                        if (this.heretic.getTarget() != null) {
                            summon.setTarget(this.heretic.getTarget());
                        }
                        summon.setLimitedLife(MathHelper.minecraftDayToTicks(5));
                        if (this.heretic.level.addFreshEntity(summon)){
                            summon.playSound(ModSounds.SUMMON_SPELL_FIERY.get(), 1.0F, 1.0F);
                            this.heretic.getRevivePos().remove(this.targetPos);
                            this.heretic.setCastCoolDown(100);
                        }
                    }
                }
            } else {
                this.findTargetPos();
            }
            super.tick();
        }
    }

    public static class PrayToMonolithGoal extends Goal{
        public Heretic heretic;

        public PrayToMonolithGoal(Heretic heretic){
            this.heretic = heretic;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.heretic.getMonolith() != null && this.heretic.getMonolith().isAlive() && this.heretic.getHealth() == this.heretic.getMaxHealth();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            super.stop();
            if (this.heretic.isCasting()){
                this.heretic.setCasting(false);
            }
        }

        @Override
        public void tick() {
            if (this.heretic.getMonolith() != null) {
                if (this.heretic.distanceTo(this.heretic.getMonolith()) <= 8.0D && this.heretic.hasLineOfSight(this.heretic.getMonolith())) {
                    this.heretic.setTarget(null);
                    if (!this.heretic.isCasting()){
                        this.heretic.setCasting(true);
                    }
                    MobUtil.instaLook(this.heretic, this.heretic.getMonolith());
                    this.drawParticleBeam(this.heretic, this.heretic.getMonolith());
                    if (this.heretic.tickCount % 20 == 0){
                        this.heretic.getMonolith().heal(1.0F);
                    }
                    this.heretic.getMonolith().empowered = 10;
                    this.heretic.getNavigation().stop();
                    this.heretic.noActionTime = 0;
                } else {
                    if (this.heretic.isCasting()){
                        this.heretic.setCasting(false);
                    }
                    Vec3 vec3 = this.heretic.getMonolith().position();
                    this.heretic.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0D);
                }
            }
        }

        private void drawParticleBeam(LivingEntity pSource, LivingEntity pTarget) {
            double d0 = pTarget.getX() - pSource.getX();
            double d1 = (pTarget.getY() + (double) pTarget.getBbHeight() * 0.5F) - (pSource.getY() + (double) pSource.getBbHeight() * 0.5D);
            double d2 = pTarget.getZ() - pSource.getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d0 = d0 / d3;
            d1 = d1 / d3;
            d2 = d2 / d3;
            double d4 = pSource.level.random.nextDouble();
            if (!pSource.level.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) pSource.level;
                while (d4 < d3) {
                    d4 += 0.5D;
                    serverWorld.sendParticles(ModParticleTypes.CHANT.get(), pSource.getX() + d0 * d4, pSource.getY() + d1 * d4 + (double) pSource.getEyeHeight(), pSource.getZ() + d2 * d4, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}
