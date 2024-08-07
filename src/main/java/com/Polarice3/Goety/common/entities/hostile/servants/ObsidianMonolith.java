package com.Polarice3.Goety.common.entities.hostile.servants;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.PortalShockwaveParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.util.SummonCircle;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ObsidianMonolith extends AbstractMonolith implements Enemy {

    public ObsidianMonolith(EntityType<? extends AbstractMonolith> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.playSound(ModSounds.RUMBLE.get(), 10.0F, 1.0F);
        this.playSound(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 10.0F, 0.25F);
        return pSpawnData;
    }

    public BlockState getState(){
        return Blocks.OBSIDIAN.defaultBlockState();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        int particles = 5;
        int efficiency = 0;
        boolean damage = false;
        if (!this.level.isClientSide && !this.isEmerging()) {
            if (pSource.isExplosion() || pSource.isFire()){
                return false;
            }
            if (pSource == DamageSource.IN_WALL){
                if (this.getTrueOwner() instanceof Apostle apostle) {
                    this.teleportTowards(apostle);
                }
            }
            if (ModDamageSource.physicalAttacks(pSource)) {
                if (pSource.getDirectEntity() instanceof LivingEntity living) {
                    if (living.getMainHandItem().isCorrectToolForDrops(this.getState())){
                        damage = true;
                        efficiency += EnchantmentHelper.getBlockEfficiency(living);
                    }
                }
            }
            if (damage){
                pAmount *= 2.0F + (efficiency / 2.0F);
                particles = 20;
            }
            if (this.level instanceof ServerLevel serverLevel){
                for(int i = 0; i < particles; ++i) {
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, this.getParticles(), this);
                }
            }
        }
        return super.hurt(pSource, pAmount);
    }

    public void silentDie(DamageSource cause){
        super.die(cause);
        if (this.level instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(new PortalShockwaveParticleOption(), this.getX(), this.getY(), this.getZ(), 0, 0, 0, 0, 0);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), new BlockPos(this.position()), this.getState(), serverLevel);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), new BlockPos(this.position()).above(), this.getState(), serverLevel);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), new BlockPos(this.position()).above().above(), Blocks.CRYING_OBSIDIAN.defaultBlockState(), serverLevel);
            new SpellExplosion(this.level, null, DamageSource.MAGIC, this.blockPosition(), 16, 5.0F){
                @Override
                public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                    if (target instanceof IOwned owned && target instanceof LivingEntity living){
                        if (owned.getTrueOwner() == ObsidianMonolith.this.getTrueOwner() || owned.getTrueOwner() == ObsidianMonolith.this){
                            if (!(owned instanceof ObsidianMonolith)) {
                                if (living.hurt(DamageSource.MAGIC, ObsidianMonolith.this.level.random.nextInt(10) + 5.0F)){
                                    ObsidianMonolith.this.launch(living, ObsidianMonolith.this);
                                }
                            }
                        }
                    }
                }
            };
            if (cause.getEntity() instanceof Mob mob && mob.getTarget() == this && this.getTrueOwner() != null){
                mob.setTarget(this.getTrueOwner());
            }
            if (this.getTrueOwner() instanceof Apostle apostle && apostle.isAlive()){
                apostle.setMonolithCoolDown(MathHelper.minutesToTicks(1));
            }
        }
        this.remove(RemovalReason.KILLED);
    }

    public void die(DamageSource cause) {
        this.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE, 5.0F, 0.5F);
        this.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 5.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        this.silentDie(cause);
    }

    protected void dropAllDeathLoot(DamageSource p_21192_) {
        if (ModDamageSource.physicalAttacks(p_21192_)) {
            if (p_21192_.getDirectEntity() instanceof LivingEntity living) {
                if (living.getMainHandItem().isCorrectToolForDrops(this.getState())){
                    super.dropAllDeathLoot(p_21192_);
                }
            }
        }
    }

    private void launch(Entity p_213688_1_, LivingEntity livingEntity) {
        double d0 = p_213688_1_.getX() - livingEntity.getX();
        double d1 = p_213688_1_.getZ() - livingEntity.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        MobUtil.push(p_213688_1_, d0 / d2 * 6.0D, 0.4D, d1 / d2 * 6.0D);
    }

    public int getAmbientSoundInterval() {
        return 100;
    }

    protected SoundEvent getAmbientSound() {
        if (!this.isEmerging()) {
            return SoundEvents.RESPAWN_ANCHOR_AMBIENT;
        } else {
            return null;
        }
    }

    protected SoundEvent getHurtSound(DamageSource p_34154_) {
        if (ModDamageSource.physicalAttacks(p_34154_)) {
            if (p_34154_.getDirectEntity() instanceof LivingEntity living) {
                if (living.getMainHandItem().isCorrectToolForDrops(this.getState())){
                    return SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;
                }
            }
        }
        return SoundEvents.NETHERITE_BLOCK_BREAK;
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    public boolean canSpawn(Level level){
        return true;
    }

    public boolean isCurrentlyGlowing() {
        return (!this.level.isClientSide() && !this.isEmerging()) || super.isCurrentlyGlowing();
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isEmerging()){
            if (this.level.isClientSide) {
                for(int i = 0; i < 2; ++i) {
                    this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
                }
                if (this.getCrackiness() != Crackiness.NONE) {
                    if (this.level.random.nextInt(5) == 0) {
                        int j = this.getCrackiness() == Crackiness.LOW ? 1 : this.getCrackiness() == Crackiness.MEDIUM ? 3 : 5;
                        for(int i = 0; i < j; ++i) {
                            this.level.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
                        }
                    }
                }
            }
            this.setGlowingTag(true);
            if (!this.isActivate()){
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 0.5F);
                this.setActivate(true);
            }
            if (this.getTrueOwner() instanceof Apostle apostle) {
                if (apostle.isDeadOrDying()){
                    this.silentDie(DamageSource.STARVE);
                }
                if (!this.hasLineOfSight(apostle)){
                    this.teleportTowards(apostle);
                }
                apostle.obsidianInvul = 10;
                if (apostle.getTarget() != null){
                    if (apostle.getTarget().distanceTo(this) <= 4.0D){
                        int i = this.level.getEntitiesOfClass(Owned.class, this.getBoundingBox().inflate(16.0D), apostle.ZOMBIE_MINIONS).size();
                        Integer[] difficulty = this.difficultyIntegerMap().get(this.level.getDifficulty());
                        int j = this.getCrackiness() == Crackiness.NONE ? difficulty[0] : this.getCrackiness() == Crackiness.LOW ? difficulty[1] : this.getCrackiness() == Crackiness.MEDIUM ? difficulty[2] : 1;
                        if (this.tickCount % 20 == 0 && i < j && this.level.random.nextFloat() <= 0.25F && !apostle.isSettingUpSecond()) {
                            if (!this.level.isClientSide) {
                                ServerLevel ServerLevel = (ServerLevel) this.level;
                                RandomSource r = this.level.random;
                                int numbers = apostle.isSecondPhase() ? 4 : 2;
                                for (ZombifiedPiglin zombifiedPiglin : this.level.getEntitiesOfClass(ZombifiedPiglin.class, this.getBoundingBox().inflate(16))) {
                                    if (zombifiedPiglin.getTarget() != apostle.getTarget()) {
                                        zombifiedPiglin.setTarget(apostle.getTarget());
                                    }
                                }
                                for (int p = 0; p < r.nextInt(numbers) + 1; ++p) {
                                    Owned summon;
                                    if (this.level.random.nextFloat() > 0.25F || !apostle.isSecondPhase()) {
                                        summon = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), this.level);
                                    } else {
                                        summon = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), this.level);
                                    }
                                    BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), summon, this.level);
                                    summon.moveTo(blockPos, 0.0F, 0.0F);
                                    summon.setTrueOwner(apostle);
                                    summon.setLimitedLife(MobUtil.getSummonLifespan(this.level));
                                    summon.finalizeSpawn(ServerLevel, this.level.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                                    summon.setTarget(apostle.getTarget());
                                    SummonCircle summonCircle = new SummonCircle(this.level, blockPos, summon, false, true, apostle);
                                    this.level.addFreshEntity(summonCircle);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Map<Difficulty, Integer[]> difficultyIntegerMap(){
        Map<Difficulty, Integer[]> difficultyIntegerMap = new HashMap<>();
        difficultyIntegerMap.put(Difficulty.PEACEFUL, new Integer[]{0, 0, 0});
        difficultyIntegerMap.put(Difficulty.EASY, new Integer[]{6, 4, 2});
        difficultyIntegerMap.put(Difficulty.NORMAL, new Integer[]{8, 6, 4});
        difficultyIntegerMap.put(Difficulty.HARD, new Integer[]{12, 10, 8});
        return difficultyIntegerMap;
    }

    public boolean shouldRenderAtSqrDistance(double p_31046_) {
        return super.shouldRenderAtSqrDistance(p_31046_) || this.getTrueOwner() != null;
    }
}
