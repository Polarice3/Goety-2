package com.Polarice3.Goety.common.entities.hostile.servants;

import com.Polarice3.Goety.client.particles.PortalShockwaveParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ObsidianMonolith extends AbstractMonolith {

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
            serverLevel.sendParticles(new PortalShockwaveParticleOption(0), this.getX(), this.getY(), this.getZ(), 0, 0, 0, 0, 0);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), new BlockPos(this.position()), this.getState(), serverLevel);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), new BlockPos(this.position()).above(), this.getState(), serverLevel);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), new BlockPos(this.position()).above().above(), Blocks.CRYING_OBSIDIAN.defaultBlockState(), serverLevel);
            for (Owned owned : this.level.getEntitiesOfClass(Owned.class, this.getBoundingBox().inflate(16))){
                if (owned.getTrueOwner() == this.getTrueOwner()){
                    if (!(owned instanceof ObsidianMonolith)) {
                        if (owned.hurt(DamageSource.MAGIC, this.level.random.nextInt(10) + 5.0F)){
                            owned.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 16000, 4));
                            this.launch(owned, this);
                        }
                    }
                }
            }
            if (this.getTrueOwner() instanceof Apostle apostle && apostle.isAlive()){
                apostle.setMonolithCoolDown(apostle.getMonolithCoolDown() + MathHelper.secondsToTicks(45));
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
            if (!this.isActivate()){
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 0.5F);
                this.setActivate(true);
            }
            if (this.getTrueOwner() instanceof Apostle apostle) {
                if (apostle.isDeadOrDying()){
                    this.silentDie(DamageSource.STARVE);
                }
                if (this.distanceTo(apostle) > 32){
                    this.teleportTowards(apostle);
                }
                int i = this.level.getEntitiesOfClass(Owned.class, this.getBoundingBox().inflate(64.0D), apostle.ZOMBIE_MINIONS).size();
                Integer[] difficulty = this.difficultyIntegerMap().get(this.level.getDifficulty());
                int j = this.getCrackiness() == Crackiness.NONE ? difficulty[0] : this.getCrackiness() == Crackiness.LOW ? difficulty[1] : this.getCrackiness() == Crackiness.MEDIUM ? difficulty[2] : 1;
                if (this.tickCount % 100 == 0 && i < j && this.level.random.nextFloat() <= 0.25F && !apostle.isSettingUpSecond()) {
                    if (!this.level.isClientSide) {
                        ServerLevel ServerLevel = (ServerLevel) this.level;
                        RandomSource r = this.level.random;
                        int numbers = apostle.isSecondPhase() ? 4 : 2;
                        if (this.level.dimension() != Level.NETHER) {
                            for (int p = 0; p < r.nextInt(numbers) + 1; ++p) {
                                int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                                int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                                BlockPos.MutableBlockPos blockpos$mutable = this.blockPosition().mutable().move(k, 0, l);
                                blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                                blockpos$mutable.setY((int) BlockFinder.moveDownToGround(this));
                                blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                                Owned summonedentity;
                                if (!apostle.isSecondPhase()) {
                                    summonedentity = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), this.level);
                                } else {
                                    summonedentity = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), this.level);
                                }
                                summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                                summonedentity.setTrueOwner(apostle);
                                summonedentity.setLimitedLife(60 * (90 + this.level.random.nextInt(180)));
                                summonedentity.finalizeSpawn(ServerLevel, this.level.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                                summonedentity.setTarget(apostle.getTarget());
                                SummonCircle summonCircle = new SummonCircle(this.level, blockpos$mutable, summonedentity, false, true, apostle);
                                this.level.addFreshEntity(summonCircle);
                            }
                        } else {
                            for (ZombifiedPiglin zombifiedPiglin : this.level.getEntitiesOfClass(ZombifiedPiglin.class, this.getBoundingBox().inflate(16))) {
                                if (zombifiedPiglin.getTarget() != apostle.getTarget()) {
                                    zombifiedPiglin.setTarget(apostle.getTarget());
                                }
                            }
                            for (int p = 0; p < r.nextInt(numbers) + 1; ++p) {
                                int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                                int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                                BlockPos.MutableBlockPos blockpos$mutable = this.blockPosition().mutable().move(k, 0, l);
                                blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                                blockpos$mutable.setY((int) BlockFinder.moveDownToGround(this));
                                blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                                ZPiglinServant summonedentity = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), this.level);
                                summonedentity.moveTo(blockpos$mutable, 0.0F, 0.0F);
                                summonedentity.setTrueOwner(apostle);
                                summonedentity.setLimitedLife(60 * (90 + this.level.random.nextInt(180)));
                                summonedentity.finalizeSpawn(ServerLevel, this.level.getCurrentDifficultyAt(blockpos$mutable), MobSpawnType.MOB_SUMMONED, null, null);
                                summonedentity.setTarget(apostle.getTarget());
                                SummonCircle summonCircle = new SummonCircle(this.level, blockpos$mutable, summonedentity, false, true, apostle);
                                this.level.addFreshEntity(summonCircle);
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

    private void teleportTowards(Entity entity) {
        if (!this.level.isClientSide() && this.isAlive()) {
            for(int i = 0; i < 128; ++i) {
                Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
                vector3d = vector3d.normalize();
                double d0 = 16.0D;
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
                double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vector3d.y * d0;
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;
                if (this.randomTeleport(d1, d2, d3, false)) {
                    this.teleportHits();
                    break;
                }
            }
        }
    }

    public void teleportHits(){
        this.level.broadcastEntityEvent(this, (byte) 5);
        if (!this.isSilent()) {
            this.level.playSound((Player) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 5){
            int i = 128;

            for(int j = 0; j < i; ++j) {
                double d0 = (double)j / (i - 1);
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d1 = Mth.lerp(d0, this.xo, this.getX()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.yo, this.getY()) + this.random.nextDouble() * (double)this.getBbHeight();
                double d3 = Mth.lerp(d0, this.zo, this.getZ()) + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth() * 2.0D;
                this.level.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
            }
        } else {
            super.handleEntityEvent(pId);
        }
    }
}
