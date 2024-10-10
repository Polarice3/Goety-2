package com.Polarice3.Goety.common.entities.hostile.servants;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.PortalShockwaveParticleOption;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.cultists.Heretic;
import com.Polarice3.Goety.common.entities.hostile.cultists.Maverick;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.util.SummonCircle;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObsidianMonolith extends AbstractMonolith implements Enemy {
    protected static final EntityDataAccessor<Float> DATA_PROGRESS = SynchedEntityData.defineId(ObsidianMonolith.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Integer> DATA_LEVEL = SynchedEntityData.defineId(ObsidianMonolith.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_LEVELING = SynchedEntityData.defineId(ObsidianMonolith.class, EntityDataSerializers.INT);
    public int empowered;
    public int shieldTime;
    public int destroyBlocksTick;
    public boolean shouldSpawnHeretics;
    private final NetherSpreaderUtil netherSpreaderUtil = NetherSpreaderUtil.createLevelSpreader();

    public ObsidianMonolith(EntityType<? extends AbstractMonolith> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ObsidianMonolithHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.ARMOR, AttributesConfig.ObsidianMonolithArmor.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public void setConfigurableAttributes(){
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ObsidianMonolithHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ObsidianMonolithArmor.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PROGRESS, 0.0F);
        this.entityData.define(DATA_LEVEL, 0);
        this.entityData.define(DATA_LEVELING, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_31485_) {
        super.addAdditionalSaveData(p_31485_);
        p_31485_.putBoolean("ShouldSpawnHeretics", this.shouldSpawnHeretics);
        p_31485_.putFloat("SpreaderProgress", this.getSpreaderProgress());
        p_31485_.putInt("SpreaderLevel", this.getSpreaderLevel());
        p_31485_.putInt("SpreaderLeveling", this.getSpreaderLeveling());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_31474_) {
        super.readAdditionalSaveData(p_31474_);
        if (p_31474_.contains("ShouldSpawnHeretics")) {
            this.shouldSpawnHeretics = p_31474_.getBoolean("ShouldSpawnHeretics");
        }
        if (p_31474_.contains("SpreaderProgress")) {
            this.setSpreaderProgress(p_31474_.getFloat("SpreaderProgress"));
        }
        if (p_31474_.contains("SpreaderLevel")) {
            this.setSpreaderLevel(p_31474_.getInt("SpreaderLevel"));
        }
        if (p_31474_.contains("SpreaderLeveling")) {
            this.setSpreaderLeveling(p_31474_.getInt("SpreaderLeveling"));
        }
    }

    public void setSpreaderProgress(float spreaderProgress) {
        this.entityData.set(DATA_PROGRESS, spreaderProgress);
    }

    public float getSpreaderProgress() {
        return this.entityData.get(DATA_PROGRESS);
    }

    public void setSpreaderLevel(int spreaderLevel) {
        this.entityData.set(DATA_LEVEL, spreaderLevel);
    }

    public int getSpreaderLevel() {
        return this.entityData.get(DATA_LEVEL);
    }

    public void setSpreaderLeveling(int spreaderLevel) {
        this.entityData.set(DATA_LEVELING, spreaderLevel);
    }

    public int getSpreaderLeveling() {
        return this.entityData.get(DATA_LEVELING);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason != MobSpawnType.STRUCTURE) {
            if (this.canSpawn(pLevel.getLevel())) {
                if (pReason != MobSpawnType.MOB_SUMMONED && pReason != MobSpawnType.SPAWN_EGG){
                    this.shouldSpawnHeretics = this.getTrueOwner() == null;
                }
                this.playSound(ModSounds.RUMBLE.get(), 10.0F, 1.0F);
                this.playSound(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 10.0F, 0.25F);
            }
        } else {
            this.shouldSpawnHeretics = false;
        }
        return pSpawnData;
    }

    public static boolean checkOMSpawnRules(EntityType<? extends Owned> p_217058_, LevelAccessor p_217059_, MobSpawnType p_217060_, BlockPos p_217061_, RandomSource p_217062_) {
        List<ObsidianMonolith> monoliths = p_217059_.getEntitiesOfClass(ObsidianMonolith.class, new AABB(p_217061_).inflate(32.0D, 16.0D, 32.0D));
        if (!monoliths.isEmpty()){
            return false;
        }
        if (p_217059_ instanceof ServerLevelAccessor serverLevelAccessor) {
            return checkHostileSpawnRules(p_217058_, serverLevelAccessor, p_217060_, p_217061_, p_217062_);
        } else {
            return false;
        }
    }

    public BlockState getState(){
        return Blocks.OBSIDIAN.defaultBlockState();
    }

    @Override
    public boolean attackable() {
        return super.attackable() && this.empowered <= 0;
    }

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired() || this.empowered > 0;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        int particles = 5;
        int efficiency = 0;
        boolean damage = false;
        if (!this.level.isClientSide && !this.isEmerging()) {
            if (pSource.is(DamageTypeTags.IS_EXPLOSION) || pSource.is(DamageTypeTags.IS_FIRE)){
                return false;
            }
            if (this.empowered > 0 && !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)){
                this.level.broadcastEntityEvent(this, (byte) 6);
                this.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0F, 0.5F);
                if (pSource.is(DamageTypes.IN_WALL)){
                    if (this.destroyBlocksTick <= 0) {
                        this.destroyBlocksTick = 20;
                    }
                }
                return false;
            }
            if (pSource.is(DamageTypes.IN_WALL) || pSource.is(DamageTypes.OUTSIDE_BORDER)){
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
            ServerParticleUtil.blockBreakParticles(this.getParticles(), BlockPos.containing(this.position()), this.getState(), serverLevel);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), BlockPos.containing(this.position()).above(), this.getState(), serverLevel);
            ServerParticleUtil.blockBreakParticles(this.getParticles(), BlockPos.containing(this.position()).above().above(), Blocks.CRYING_OBSIDIAN.defaultBlockState(), serverLevel);
            new SpellExplosion(this.level, null, this.damageSources().magic(), this.blockPosition(), 16, 5.0F){
                @Override
                public void explodeHurt(Entity target, DamageSource damageSource, double x, double y, double z, double seen, float actualDamage) {
                    if (target instanceof IOwned owned && target instanceof LivingEntity living){
                        if (owned.getTrueOwner() == ObsidianMonolith.this.getTrueOwner() || owned.getTrueOwner() == ObsidianMonolith.this){
                            if (!(owned instanceof ObsidianMonolith)) {
                                if (living.hurt(living.damageSources().magic(), ObsidianMonolith.this.level.random.nextInt(10) + 5.0F)){
                                    ObsidianMonolith.this.launch(living, ObsidianMonolith.this);
                                }
                            }
                        }
                    }
                    if (target instanceof ZombifiedPiglin piglin){
                        if (piglin.isAlive()){
                            piglin.spawnAnim();
                            piglin.discard();
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
        this.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), 5.0F, 0.5F);
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
        return level.noCollision(this.getInitialBB()) && level.getEntityCollisions(this, this.getInitialBB()).isEmpty() && !level.containsAnyLiquid(this.getInitialBB());
    }

    public boolean isCurrentlyGlowing() {
        return (!this.level.isClientSide() && !this.isEmerging() && this.getTrueOwner() != null) || super.isCurrentlyGlowing();
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 6){
            this.shieldTime = 10;
        } else {
            super.handleEntityEvent(pId);
        }
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
                if (this.shieldTime > 0){
                    --this.shieldTime;
                }
            }
            if (!this.isActivate()){
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 0.5F);
                if (this.shouldSpawnHeretics){
                    this.spawnHeretics();
                }
                this.setActivate(true);
            } else {
                if (this.empowered > 0){
                    if (this.tickCount % MathHelper.secondsToTicks(20) == 0){
                        this.playSound(ModSounds.SCARY_RECITE.get(), 1.0F, this.getVoicePitch());
                    }
                }
                if (this.tickCount % 50 == 0) {
                    if (this.level instanceof ServerLevel serverLevel){
                        ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_RED);
                        serverLevel.sendParticles(new ShockwaveParticleOption(colorUtil.red, colorUtil.green, colorUtil.blue, 5, 0, true), this.getX(), this.getEyeY(), this.getZ(), 0, 0, 0, 0, 0);
                    }
                    for (Cultist cultist : this.level.getEntitiesOfClass(Cultist.class, this.getBoundingBox().inflate(16.0D))){
                        if (!(cultist instanceof Apostle)){
                            if (cultist.getHealth() < cultist.getMaxHealth()) {
                                cultist.heal(1.0F);
                            }
                        }
                    }
                }
            }

            int time = 160;
            switch (this.level.getDifficulty()){
                case NORMAL -> time = 80;
                case HARD -> time = 40;
            }

            if (this.getTrueOwner() instanceof Apostle apostle) {
                this.setGlowingTag(true);
                if (apostle.isDeadOrDying()){
                    this.silentDie(this.damageSources().starve());
                }
                if (!this.hasLineOfSight(apostle)){
                    this.teleportTowards(apostle);
                }
                apostle.obsidianInvul = 10;
                if (MobsConfig.ApocalypseMode.get()){
                    this.spreadNether();
                } else {
                    if (!this.netherSpreaderUtil.getCursors().isEmpty()) {
                        this.netherSpreaderUtil.clear();
                    }
                }
                if (apostle.getTarget() != null){
                    int i = this.level.getEntitiesOfClass(Owned.class, this.getBoundingBox().inflate(16.0D), apostle.ZOMBIE_MINIONS).size();
                    Integer[] difficulty = this.difficultyIntegerMap().get(this.level.getDifficulty());
                    int j = this.getCrackiness() == Crackiness.NONE ? difficulty[0] : this.getCrackiness() == Crackiness.LOW ? difficulty[1] : this.getCrackiness() == Crackiness.MEDIUM ? difficulty[2] : 1;
                    if (this.tickCount % time == 0 && i < j && this.level.random.nextFloat() <= 0.25F && !apostle.isSettingUpSecond()) {
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
            } else if (this.getTrueOwner() == null){
                if (!this.level.isClientSide) {
                    int heretics = this.level.getEntitiesOfClass(Heretic.class, this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D), LivingEntity::isAlive).size();
                    ServerLevel serverLevel = (ServerLevel) this.level;
                    RandomSource random1 = this.level.random;
                    int spawnChance = 256;
                    if (this.empowered > 0) {
                        --this.empowered;
                        this.spreadNether();
                        int i = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(24.0D, 16.0D, 24.0D), livingEntity -> livingEntity.isAlive() && livingEntity instanceof Maverick).size();
                        if (this.tickCount % time == 0) {
                            if (random1.nextFloat() <= 0.25F && i < 8){
                                Maverick maverick = new Maverick(ModEntityType.MAVERICK.get(), this.level);
                                int i1 = this.blockPosition().getX() + (Mth.randomBetweenInclusive(random1, 4, 12) * Mth.randomBetweenInclusive(random1, -1, 1));
                                int j1 = this.blockPosition().getY() + (Mth.randomBetweenInclusive(random1, 0, 3) * Mth.randomBetweenInclusive(random1, -1, 1));
                                int k1 = this.blockPosition().getZ() + (Mth.randomBetweenInclusive(random1, 4, 12) * Mth.randomBetweenInclusive(random1, -1, 1));
                                BlockPos blockPos = BlockFinder.SummonPosition(maverick, new BlockPos(i1, j1, k1));
                                maverick.setPos(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D);
                                if (this.level.noCollision(maverick.getBoundingBox()) && this.level.getEntityCollisions(maverick, maverick.getBoundingBox()).isEmpty() && !this.level.containsAnyLiquid(maverick.getBoundingBox())) {
                                    maverick.finalizeSpawn(serverLevel, this.level.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                                    maverick.setLeader(this);
                                    if (this.getTarget() != null) {
                                        maverick.setTarget(this.getTarget());
                                    }
                                    maverick.spawnAnim();
                                    this.level.addFreshEntity(maverick);
                                }
                            }
                        }
                        spawnChance = 64;
                    } else {
                        this.netherSpreaderUtil.clear();
                    }
                    if (serverLevel.random.nextInt(spawnChance) == 0) {
                        int j = serverLevel.getNearbyEntities(Mob.class, TargetingConditions.DEFAULT, this, this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D)).size();
                        if (j < 16){
                            WeightedRandomList<MobSpawnSettings.SpawnerData> spawners = MobUtil.mobsAt(serverLevel, serverLevel.structureManager(), serverLevel.getChunkSource().getGenerator(), MobCategory.MONSTER, this.blockPosition(), serverLevel.getBiome(this.blockPosition()));
                            if (!spawners.isEmpty()) {
                                MobSpawnSettings.SpawnerData spawner = spawners.getRandom(serverLevel.random).orElse(null);
                                if (spawner != null) {
                                    Entity entity = spawner.type.create(serverLevel);
                                    BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), entity, serverLevel, 24);
                                    if (SpawnPlacements.checkSpawnRules(spawner.type, serverLevel, MobSpawnType.SPAWNER, blockPos, serverLevel.random)) {
                                        if (entity instanceof Mob mob) {
                                            if (!(entity instanceof Ghast) && !(entity instanceof AbstractPiglin) && !(entity instanceof Hoglin)) {
                                                mob.setPos(blockPos.getX() + 0.5F, blockPos.getY(), blockPos.getZ() + 0.5F);
                                                ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(blockPos), MobSpawnType.SPAWNER, null, null);
                                                if (serverLevel.addFreshEntity(mob)) {
                                                    ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.FLAME, mob);
                                                }
                                            }
                                        }
                                    } else {
                                        if (entity != null) {
                                            entity.discard();
                                        }
                                    }
                                }
                            }
                        }
                        float f1 = this.getCrackiness() == Crackiness.NONE ? 0.125F : this.getCrackiness() == Crackiness.LOW ? 0.2F : this.getCrackiness() == Crackiness.MEDIUM ? 0.25F : 0.3F;
                        if (heretics <= 2 && random1.nextFloat() <= f1){
                            Heretic heretic = new Heretic(ModEntityType.HERETIC.get(), this.level);
                            int i1 = this.blockPosition().getX() + (Mth.randomBetweenInclusive(random1, 4, 12) * Mth.randomBetweenInclusive(random1, -1, 1));
                            int j1 = this.blockPosition().getY() + (Mth.randomBetweenInclusive(random1, 0, 3) * Mth.randomBetweenInclusive(random1, -1, 1));
                            int k1 = this.blockPosition().getZ() + (Mth.randomBetweenInclusive(random1, 4, 12) * Mth.randomBetweenInclusive(random1, -1, 1));
                            BlockPos blockPos = BlockFinder.SummonPosition(heretic, new BlockPos(i1, j1, k1));
                            heretic.setPos(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D);
                            if (this.level.noCollision(heretic.getBoundingBox()) && this.level.getEntityCollisions(heretic, heretic.getBoundingBox()).isEmpty() && !this.level.containsAnyLiquid(heretic.getBoundingBox())) {
                                heretic.finalizeSpawn(serverLevel, this.level.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                                heretic.setPersistenceRequired();
                                heretic.setLeader(this);
                                heretic.setMonolith(this);
                                heretic.spawnAnim();
                                this.level.addFreshEntity(heretic);
                            }
                        }
                    }
                    if (this.destroyBlocksTick > 0) {
                        --this.destroyBlocksTick;
                        if (this.destroyBlocksTick == 0 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
                            int x = Mth.floor(this.getX());
                            int y = Mth.floor(this.getY());
                            int z = Mth.floor(this.getZ());
                            boolean flag = false;

                            for(int x1 = -3; x1 <= 3; ++x1) {
                                for(int y1 = 0; y1 <= 4; ++y1) {
                                    for(int z1 = -3; z1 <= 3; ++z1) {
                                        int l2 = x + x1;
                                        int l = y + y1;
                                        int i1 = z + z1;
                                        BlockPos blockpos = new BlockPos(l2, l, i1);
                                        BlockState blockstate = this.level.getBlockState(blockpos);
                                        if (!blockstate.is(BlockTags.WITHER_IMMUNE) && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
                                            flag = this.level.destroyBlock(blockpos, true, this) || flag;
                                        }
                                    }
                                }
                            }

                            if (flag) {
                                this.level.levelEvent(null, 1022, this.blockPosition(), 0);
                            }
                        }
                    }
                }
            }
        }
    }

    public void spreadNether(){
        if (this.level instanceof ServerLevel serverLevel) {
            int activeHeretics = this.level.getEntitiesOfClass(Heretic.class, this.getBoundingBox().inflate(10.0D), living -> living.isAlive() && living.isCasting() && living.getMonolith() == this).size();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && MobsConfig.ObsidianMonolithSpread.get() && this.level.dimension() != Level.NETHER) {
                this.netherSpreaderUtil.updateCursors(this.level, this.blockPosition().below(), this.random, true);

                if (this.getSpreaderLeveling() >= 100) {
                    this.setSpreaderLeveling(0);
                    if (this.getSpreaderLevel() < 9) {
                        this.setSpreaderLevel(this.getSpreaderLevel() + 1);
                    }
                }

                if (this.getSpreaderProgress() >= 1.0F) {
                    this.setSpreaderProgress(0.0F);
                    this.setSpreaderLeveling(this.getSpreaderLeveling() + 1);
                    this.netherSpreaderUtil.clear();
                    for (int i = 0; i < 5; i++) {
                        int range = this.getSpreaderLevel();
                        BlockPos blockPos = this.blockPosition().below();
                        if (range > 0) {
                            int x = serverLevel.getRandom().nextInt(-range, range);
                            int y = serverLevel.getRandom().nextInt(-range, range / 2);
                            int z = serverLevel.getRandom().nextInt(-range, range);
                            blockPos = this.blockPosition().below().offset(x, y, z);
                        }
                        this.netherSpreaderUtil.addCursors(blockPos, 10);
                    }
                } else {
                    this.setSpreaderProgress(this.getSpreaderProgress() + (0.005F * (activeHeretics + 1)));
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

    private void spawnHeretics() {
        for(int i = 0; i < 4; ++i) {
            int x = 2;
            int z = 2;
            switch (i) {
                case 0 -> {
                    x *= -1;
                    z *= -1;
                }
                case 1 -> x *= -1;
                case 2 -> z *= -1;
            }
            Heretic heretic = new Heretic(ModEntityType.HERETIC.get(), this.level);
            BlockPos blockPos = BlockPos.containing(this.getX() + x, this.getY() - 1, this.getZ() + z);
            if (!this.level.getBlockState(blockPos).getCollisionShape(this.level, blockPos).isEmpty()) {
                heretic.setPos(blockPos.getX() + 0.5D, this.getY(), blockPos.getZ() + 0.5D);
                if (this.level.noCollision(heretic.getBoundingBox()) && this.level.getEntityCollisions(heretic, heretic.getBoundingBox()).isEmpty() && !this.level.containsAnyLiquid(heretic.getBoundingBox())) {
                    if (this.level instanceof ServerLevel serverLevel) {
                        heretic.finalizeSpawn(serverLevel, this.level.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                    }
                    MobUtil.instaLook(heretic, this);
                    heretic.setPersistenceRequired();
                    heretic.spawnAnim();
                    heretic.setLeader(this);
                    if (this.level.addFreshEntity(heretic)) {
                        heretic.setMonolith(this);
                    }
                }
            }
        }
    }
}
