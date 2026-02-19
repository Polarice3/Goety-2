package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.api.blocks.entities.IOwnedBlock;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServantUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public interface IOwned {

    UUID SPEED_MODIFIER_UUID = UUID.fromString("9c47949c-b896-4802-8e8a-f08c50791a8a");

    AttributeModifier SPEED_MODIFIER = new AttributeModifier(SPEED_MODIFIER_UUID, "Staying speed penalty", -1.0D, AttributeModifier.Operation.ADDITION);

    LivingEntity getTrueOwner();

    UUID getOwnerId();

    void setOwnerId(@Nullable UUID p_184754_1_);

    default void setOwnerClientId(int id) {
    }

    default void setTrueOwner(@Nullable LivingEntity livingEntity){
        if (livingEntity != null) {
            this.setOwnerId(livingEntity.getUUID());
            this.setOwnerClientId(livingEntity.getId());
        }
    }

    default void copyTrueOwner(IOwned owned){
        if (owned.getOwnerId() != null) {
            this.setOwnerId(owned.getOwnerId());
            this.setOwnerClientId(owned.getOwnerClientId());
        }
    }

    default void copyTrueOwner(IOwnedBlock owned){
        if (owned.getOwnerUUID() != null) {
            this.setOwnerId(owned.getOwnerUUID());
            this.setOwnerClientId(owned.getOwnerId());
        }
    }

    default void removeTrueOwner() {
        this.setOwnerId(null);
        this.setOwnerClientId(-1);
    }

    void setHostile(boolean hostile);

    boolean isHostile();

    @Nullable
    default EntityType<?> getVariant(@Nullable Player player, Level level, BlockPos blockPos){
        return this.getVariant(level, blockPos);
    }

    @Nullable
    default EntityType<?> getVariant(Level level, BlockPos blockPos){
        return null;
    }

    default LivingEntity getMasterOwner(){
        if (this.getTrueOwner() instanceof IOwned owned){
            return owned.getTrueOwner();
        } else {
            return this.getTrueOwner();
        }
    }

    default int getOwnerClientId(){
        return -1;
    }

    default void convertNewEquipment(Entity entity) {
    }

    default void setLimitedLife(int limitedLifeTicksIn) {
        this.setHasLifespan(true);
        this.setLifespan(limitedLifeTicksIn);
    }

    default void setHasLifespan(boolean lifespan){
    }

    default boolean hasLifespan(){
        return false;
    }

    default void setLifespan(int lifespan){
    }

    default int getLifespan(){
        return 0;
    }

    default boolean isLimitedLife(){
        return this.getLifespan() > 0;
    }

    default void setNatural(boolean natural){
    }

    default boolean isNatural(){
        return false;
    }

    default boolean isChargingCrossbow() {
        return false;
    }

    default void setFamiliar(){
    }

    default boolean isFamiliar(){
        return false;
    }

    default boolean canBeFamiliar(){
        return false;
    }

    default void onCeaseFire(ServerPlayer player){

    }

    default void onStopAttack() {
        if (this instanceof Entity entity) {
            if (entity.level instanceof ServerLevel serverLevel) {
                serverLevel.broadcastEntityEvent(entity, (byte) 20);
            }
        }
    }

    default void checkHostility() {
        if (this instanceof Entity entity) {
            if (!entity.level.isClientSide) {
                if (this.getTrueOwner() instanceof Enemy) {
                    this.setHostile(true);
                }
                if (this.getTrueOwner() instanceof IOwned owned) {
                    if (owned.isHostile()) {
                        this.setHostile(true);
                    }
                }
                if (this instanceof Enemy) {
                    this.setHostile(true);
                }
            }
        }
    }

    default void ownedTick(){
        if (this instanceof Mob mob) {
            if (!mob.level.isClientSide) {
                if (!mob.hasEffect(GoetyEffects.WILD_RAGE.get())) {
                    if (mob.getTarget() instanceof IOwned ownedEntity) {
                        if (this.getTrueOwner() != null && (ownedEntity.getTrueOwner() == this.getTrueOwner())) {
                            mob.setTarget(null);
                            if (mob.getLastHurtByMob() == ownedEntity) {
                                mob.setLastHurtByMob(null);
                            }
                        }
                        if (ownedEntity.getTrueOwner() == this) {
                            mob.setTarget(null);
                            if (mob.getLastHurtByMob() == ownedEntity) {
                                mob.setLastHurtByMob(null);
                            }
                        }
                        if (MobUtil.ownerStack(this, ownedEntity)) {
                            mob.setTarget(null);
                            if (mob.getLastHurtByMob() == ownedEntity) {
                                mob.setLastHurtByMob(null);
                            }
                        }
                    }
                    if (this.getTrueOwner() != null) {
                        if (mob.getLastHurtByMob() == this.getTrueOwner()) {
                            mob.setLastHurtByMob(null);
                        }
                    }
                }
                if (this.getTrueOwner() != null) {
                    this.ownerCheck();
                    if (this.getTrueOwner() instanceof Mob mobOwner) {
                        if (mobOwner.getTarget() != null && mob.getTarget() == null) {
                            mob.setTarget(mobOwner.getTarget());
                        }
                        if (mobOwner instanceof Apostle apostle) {
                            if (mob.distanceTo(apostle) > 32) {
                                this.teleportTowards(apostle);
                            }
                        }
                        if (mobOwner.getType().is(Tags.EntityTypes.BOSSES) || mobOwner.getType().is(ModTags.EntityTypes.SUMMON_KILL)) {
                            if (mobOwner.isRemoved() || mobOwner.isDeadOrDying()) {
                                mob.kill();
                            }
                        }
                    }
                    if (this.getTrueOwner() instanceof IOwned owned) {
                        try {
                            if (owned.getHasSummonCheck() <= 0) {
                                owned.setHasSummonCheck(2);
                            }
                        } catch (NullPointerException ignored) {
                        }
                        if (this.getTrueOwner().isRemoved() || this.getTrueOwner().isDeadOrDying() || !this.getTrueOwner().isAlive()) {
                            if (owned.getTrueOwner() != null) {
                                this.setTrueOwner(owned.getTrueOwner());
                            } else if (!this.isHostile() && !this.isNatural() && !(owned instanceof Enemy) && !owned.isHostile()) {
                                mob.kill();
                            }
                        }
                    }
                    for (Mob target : mob.level.getEntitiesOfClass(Mob.class, mob.getBoundingBox().inflate(mob.getAttributeValue(Attributes.FOLLOW_RANGE)), mob1 -> mob1.isAlive() && mob != mob1)) {
                        boolean flag = false;
                        if (MainConfig.GoodwillServantGuard.get()) {
                            if (this.isAllyWith(target)) {
                                LivingEntity target2 = null;
                                if (target.getLastHurtByMob() != null && mob.getLastHurtByMob() == null) {
                                    target2 = target.getLastHurtByMob();
                                } else if (target.getLastHurtMob() != null && mob.getLastHurtMob() == null) {
                                    target2 = target.getLastHurtMob();
                                }
                                if (target2 != null) {
                                    if (!MobUtil.areAllies(mob, target2) && mob.canAttack(target2, TargetingConditions.DEFAULT)) {
                                        mob.setTarget(target2);
                                        mob.setLastHurtByMob(target2);
                                        mob.setLastHurtMob(target2);
                                    }
                                }
                            } else if (target.getTarget() != null) {
                                if (this.isAllyWith(target.getTarget())) {
                                    flag = true;
                                }
                            }
                        }
                        if (target instanceof IOwned owned) {
                            if (this.getTrueOwner() != owned.getTrueOwner()
                                    && target.getTarget() == this.getTrueOwner()) {
                                flag = true;
                            }
                        }
                        if (flag) {
                            if (mob.canAttack(target, TargetingConditions.DEFAULT) && !MobUtil.areAllies(mob, target) && !MobUtil.areAllies(this.getTrueOwner(), target)) {
                                mob.setTarget(target);
                            }
                        }
                    }
                }
                if (mob.getTarget() != null) {
                    if (this.isAllyWith(mob.getTarget()) || mob.getTarget().isRemoved() || mob.getTarget().isDeadOrDying()) {
                        mob.setTarget(null);
                    }
                }
                this.mobSense();
                if (this.hasLifespan()) {
                    this.setLifespan(this.getLifespan() - 1);
                    if (this.getLifespan() <= 1) {
                        this.lifeSpanDamage();
                    }
                }
                if (this.getHasSummonCheck() > 0) {
                    this.setHasSummonCheck(this.getHasSummonCheck() - 1);
                }
                if (this.getRevivingTime() > 0) {
                    this.reviveTick();
                    this.setRevivingTime(this.getRevivingTime() - 1);
                }
            }
        }
    }

    default void ownerCheck(){
        if (this instanceof Mob mob){
            if (!mob.level.isClientSide) {
                if (this.getTrueOwner() != null) {
                    if (this.getTrueOwner().tickCount < 20) {
                        Entity entity = mob.level.getEntity(this.getOwnerClientId());
                        if (entity instanceof LivingEntity livingEntity) {
                            if (livingEntity != this.getTrueOwner()) {
                                this.setOwnerClientId(this.getTrueOwner().getId());
                            }
                        } else {
                            this.setOwnerClientId(this.getTrueOwner().getId());
                        }
                    }
                }
            }
        }
    }

    default void mobSense(){
        if (this instanceof Mob owned) {
            if (MobsConfig.MobSense.get()) {
                if (owned.isAlive()) {
                    if (owned.getTarget() != null) {
                        if (owned.getTarget() instanceof Mob mob && !(mob instanceof Guardian)) {
                            if (owned.getTarget() instanceof Animal animal) {
                                animal.setLastHurtByMob(owned);
                            } else if (mob.getTarget() == null) {
                                LivingEntity target = owned;
                                if (mob.getType().is(ModTags.EntityTypes.IGNORE_SERVANTS)) {
                                    if (this.getTrueOwner() != null) {
                                        if (mob.canAttack(this.getTrueOwner()) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(mob)) {
                                            target = this.getTrueOwner();
                                        }
                                    }
                                }
                                mob.setTarget(target);
                            }
                            if (!mob.getBrain().isActive(Activity.FIGHT) && !(mob instanceof Warden)) {
                                LivingEntity target = owned;
                                if (mob.getType().is(ModTags.EntityTypes.IGNORE_SERVANTS)) {
                                    if (this.getTrueOwner() != null) {
                                        if (mob.canAttack(this.getTrueOwner()) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(mob)) {
                                            target = this.getTrueOwner();
                                        }
                                    }
                                }
                                mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), 600L);
                                mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, target, 600L);
                            }
                        }
                    }
                }
            }
        }
    }

    default void lifeSpanDamage(){
        if (this instanceof LivingEntity owned) {
            this.setLifespan(20);
            owned.hurt(owned.damageSources().starve(), 1.0F);
        }
    }

    default void teleportTowards(Entity entity) {
        this.teleportTowards(entity, 16.0D);
    }

    default void teleportTowards(Entity entity, double range) {
        if (this instanceof LivingEntity owned) {
            if (!owned.level.isClientSide() && owned.isAlive()) {
                for (int i = 0; i < 128; ++i) {
                    Vec3 vector3d = new Vec3(owned.getX() - entity.getX(), owned.getY(0.5D) - entity.getEyeY(), owned.getZ() - entity.getZ());
                    vector3d = vector3d.normalize();
                    double d1 = owned.getX() + (owned.getRandom().nextDouble() - 0.5D) * (range / 2.0D) - vector3d.x * range;
                    double d2 = owned.getY() + (owned.getRandom().nextInt(Mth.floor(range)) - (range / 2.0D)) - vector3d.y * range;
                    double d3 = owned.getZ() + (owned.getRandom().nextDouble() - 0.5D) * (range / 2.0D) - vector3d.z * range;
                    net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(owned, d1, d2, d3);
                    if (event.isCanceled()) {
                        break;
                    }
                    if (this.ownedTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ())) {
                        this.teleportHits();
                        break;
                    }
                }
            }
        }
    }

    default boolean ownedTeleport(Vec3 vec3) {
        return this.ownedTeleport(vec3.x, vec3.y, vec3.z);
    }

    default boolean ownedTeleport(double x, double y, double z) {
        if (this instanceof LivingEntity owned) {
            return owned.randomTeleport(x, y, z, false);
        }
        return false;
    }

    default void teleportHits(){
        if (this instanceof LivingEntity owned) {
            owned.level.broadcastEntityEvent(owned, (byte) 46);
            if (!owned.isSilent()) {
                owned.level.playSound((Player) null, owned.xo, owned.yo, owned.zo, SoundEvents.ENDERMAN_TELEPORT, owned.getSoundSource(), 1.0F, 1.0F);
                owned.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
    }

    default boolean preventsSleep(Player p_33036_) {
        return this.isHostile();
    }

    default Predicate<Entity> summonPredicate(){
        if (this instanceof Mob mob){
            return entity -> mob.getClass().isAssignableFrom(entity.getClass());
        }
        return entity -> entity instanceof IOwned;
    }

    default int getSummonLimit(LivingEntity owner){
        return 64;
    }

    default void uncreditedKill(LivingEntity target){
    }

    default boolean canRevive(DamageSource damageSource) {
        return false;
    }

    default int getRevivingTime() {
        return 0;
    }

    default void setRevivingTime(int tick) {

    }

    default boolean isReviving() {
        return this.getRevivingTime() > 0;
    }

    default void startRevival() {
        this.setRevivingTime(20);
        this.pacifySurroundingMobs(64.0D);
        Entity entity = ServantUtil.teleportToRevive(this);
        if (entity != null) {
            if (entity instanceof LivingEntity living) {
                living.setHealth(1.0F);
                living.removeAllEffects();
            }
            entity.clearFire();
            if (entity instanceof IOwned owned) {
                owned.setRevivingTime(20);
                owned.reviveOwned();
            }
            entity.invulnerableTime = 100;
        }
    }

    default void reviveOwned() {
    }

    default void reviveTick() {
    }

    @Nullable
    default BlockPos getRevivePos(){
        return null;
    }

    @Nullable
    default Vec3 vec3RevivePos(){
        if (this.getRevivePos() != null) {
            return Vec3.atBottomCenterOf(this.getRevivePos());
        }
        return null;
    }

    default void setRevivePos(BlockPos blockPos){
        if (this instanceof Entity entity) {
            this.setReviveDim(entity.level.dimension());
        }
    }

    default ResourceKey<Level> getReviveLevel() {
        ResourceLocation resourcelocation = new ResourceLocation(this.getReviveDim());
        return ResourceKey.create(Registries.DIMENSION, resourcelocation);
    }

    default String getReviveDim(){
        return Level.OVERWORLD.location().toString();
    }

    default void setReviveDim(ResourceKey<Level> resourceKey) {
        this.setReviveDim(resourceKey.location().toString());
    }

    default void setReviveDim(String string) {

    }

    default void pacifySurroundingMobs(double range) {
        if (this instanceof LivingEntity owned) {
            for (Mob mob : owned.level.getEntitiesOfClass(Mob.class, owned.getBoundingBox().inflate(range), mob -> !MobUtil.areAllies(mob, owned))) {
                mob.setTarget(null);
                mob.setLastHurtByMob(null);
                Brain<?> brain = mob.getBrain();
                if (brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
                    Optional<?> memory = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
                    if (memory.isPresent() && memory.get() == owned) {
                        brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
                    }
                }
                if (brain.hasMemoryValue(MemoryModuleType.ANGRY_AT)) {
                    Optional<?> memory = brain.getMemory(MemoryModuleType.ANGRY_AT);
                    if (memory.isPresent() && memory.get() == owned.getUUID()) {
                        brain.eraseMemory(MemoryModuleType.ANGRY_AT);
                    }
                }
                if (brain.hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY)) {
                    Optional<?> memory = brain.getMemory(MemoryModuleType.HURT_BY_ENTITY);
                    if (memory.isPresent() && memory.get() == owned) {
                        brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
                    }
                }
            }
        }
    }

    default boolean hasSummons() {
        return this.getHasSummonCheck() > 0;
    }

    default int getHasSummonCheck() {
        return 0;
    }

    default void setHasSummonCheck(int count) {
    }

    default boolean isGrudgedTowardsType(EntityType<?> entityType) {
        if (this.getTrueOwner() instanceof Player player) {
            return SEHelper.getGrudgeEntityTypes(player).contains(entityType);
        } else if (this.getOwnerId() != null && this instanceof Entity entity && entity.level instanceof ServerLevel serverLevel) {
            return SEHelper.isSavedGrudgeType(serverLevel, this.getOwnerId(), entityType);
        }
        return false;
    }

    default boolean isGrudgedTowards(LivingEntity target) {
        if (this.getTrueOwner() instanceof Player player) {
            return SEHelper.isGrudged(player, target);
        } else if (this.getOwnerId() != null && target.level instanceof ServerLevel serverLevel) {
            return SEHelper.isSavedGrudge(serverLevel, this.getOwnerId(), target);
        }
        return false;
    }

    default boolean isAllyWithType(EntityType<?> entityType) {
        if (this.getTrueOwner() instanceof Player player) {
            return SEHelper.getAllyEntityTypes(player).contains(entityType);
        } else if (this.getOwnerId() != null && this instanceof Entity entity && entity.level instanceof ServerLevel serverLevel) {
            return SEHelper.isSavedAllyType(serverLevel, this.getOwnerId(), entityType);
        }
        return false;
    }

    default boolean isAllyWith(LivingEntity target) {
        if (this.getTrueOwner() instanceof Player player) {
            return SEHelper.isAlly(player, target);
        } else if (this.getOwnerId() != null && target.level instanceof ServerLevel serverLevel) {
            return SEHelper.isSavedAlly(serverLevel, this.getOwnerId(), target);
        }
        return false;
    }

    default void readOwnedData(CompoundTag compound){
        if (compound.hasUUID("Owner")) {
            this.setOwnerId(compound.getUUID("Owner"));
        }

        if (compound.contains("OwnerClient")){
            this.setOwnerClientId(compound.getInt("OwnerClient"));
        }

        if (compound.contains("isHostile")){
            this.setHostile(compound.getBoolean("isHostile"));
        } else {
            this.checkHostility();
        }

        if (compound.contains("isNatural")){
            this.setNatural(compound.getBoolean("isNatural"));
        }

        if (compound.contains("LifeTicks")) {
            this.setLimitedLife(compound.getInt("LifeTicks"));
        }
        if (compound.contains("RevivePos")){
            this.setRevivePos(NbtUtils.readBlockPos(compound.getCompound("RevivePos")));
            if (compound.contains("ReviveDim")){
                this.setReviveDim(compound.getString("ReviveDim"));
            } else {
                if (this instanceof Entity entity) {
                    this.setReviveDim(entity.level.dimension());
                }
            }
        }
    }

    default void saveOwnedData(CompoundTag compound){
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }
        if (this.getOwnerClientId() > -1) {
            compound.putInt("OwnerClient", this.getOwnerClientId());
        }
        if (this.isHostile()){
            compound.putBoolean("isHostile", this.isHostile());
        }
        if (this.isNatural()){
            compound.putBoolean("isNatural", this.isNatural());
        }
        if (this.isLimitedLife()) {
            compound.putInt("LifeTicks", this.getLifespan());
        }
        if (this.getRevivePos() != null){
            compound.put("RevivePos", NbtUtils.writeBlockPos(this.getRevivePos()));
            compound.putString("ReviveDim", this.getReviveDim());
        }
    }
}
