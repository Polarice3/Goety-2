package com.Polarice3.Goety.api.entities.ally;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.golem.AbstractGolemServant;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface IServant extends IOwned {
    int PATROL_RANGE = MobsConfig.ServantPatrolRange.get();

    boolean isWandering();

    void setWandering(boolean wandering);

    boolean isStaying();

    void setStaying(boolean staying);

    default boolean canWander(){
        return true;
    }

    default boolean canStay(){
        return true;
    }

    default boolean isPatrolling(){
        return this.getBoundPos() != null;
    }

    default boolean canPatrol(){
        return true;
    }

    default BlockPos getBoundPos(){
        return null;
    }

    default Vec3 vec3BoundPos(){
        return Vec3.atBottomCenterOf(this.getBoundPos());
    }

    default void setBoundPos(BlockPos blockPos){
    }

    default void setFollowing(){
        this.setBoundPos(null);
        this.setWandering(false);
        this.setStaying(false);
    }

    default boolean isFollowing(){
        return !this.isWandering() && !this.isStaying() && !this.isPatrolling() && this.canFollow();
    }

    default boolean canFollow() {
        return true;
    }

    default void spawnUpgraded(){
        if (this instanceof Mob mob) {
            if (mob.getMobType() == MobType.UNDEAD) {
                this.setUpgraded(CuriosFinder.hasUndeadCape(this.getTrueOwner()));
            } else if (mob.getMobType() == ModMobType.NATURAL || mob.getMobType() == MobType.ARTHROPOD) {
                this.setUpgraded(CuriosFinder.hasWildRobe(this.getTrueOwner()));
            } else if (mob.getMobType() == ModMobType.FROST) {
                this.setUpgraded(CuriosFinder.hasFrostRobes(this.getTrueOwner()));
            } else if (mob.getMobType() == MobType.WATER) {
                this.setUpgraded(CuriosFinder.hasAbyssRobes(this.getTrueOwner()));
            }
        }
    }

    boolean canUpdateMove();

    default void updateMoveMode(Player player){
        if (this instanceof LivingEntity living) {
            boolean flag = false;
            if (!this.isWandering() && !this.isStaying() && !this.isPatrolling() && this.canWander()) {
                this.setBoundPos(null);
                this.setWandering(true);
                this.setStaying(false);
                player.displayClientMessage(Component.translatable("info.goety.servant.wander", living.getDisplayName()), true);
                flag = true;
            } else if (!this.isStaying() && !this.isPatrolling() && this.canStay()) {
                this.setBoundPos(null);
                this.setWandering(false);
                this.setStaying(true);
                player.displayClientMessage(Component.translatable("info.goety.servant.staying", living.getDisplayName()), true);
                flag = true;
            } else if (!this.isPatrolling() && this.canPatrol()) {
                this.setBoundPos(living.blockPosition());
                this.setWandering(false);
                this.setStaying(false);
                player.displayClientMessage(Component.translatable("info.goety.servant.patrol", living.getDisplayName()), true);
                flag = true;
            } else if (this.canFollow()) {
                this.setBoundPos(null);
                this.setWandering(false);
                this.setStaying(false);
                player.displayClientMessage(Component.translatable("info.goety.servant.follow", living.getDisplayName()), true);
                flag = true;
            }
            if (flag){
                living.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0f, 1.0f);
            }
        }
    }

    default boolean canBeCommanded(){
        return true;
    }

    boolean isCommanded();

    default void setCommandPos(BlockPos blockPos) {
        this.setCommandPos(blockPos, true);
    }

    default BlockPos getCommandPos(){
        return null;
    }

    default void setCommandPos(BlockPos blockPos, boolean removeEntity) {
        if (removeEntity) {
            this.setCommandPosEntity(null);
        }
        this.setCommandPos(blockPos);
        this.setCommandTick(MathHelper.secondsToTicks(10));
    }

    void setCommandPosEntity(LivingEntity living);

    @Nullable
    default LivingEntity getCommandPosEntity(){
        return null;
    }

    default int getCommandTick(){
        return 0;
    }

    default void setCommandTick(int time){
    }

    default int getNoHealTime(){
        return 0;
    }

    default void setNoHealTime(int time){
    }

    default int getKillChance(){
        return 0;
    }

    default void setKillChance(int time){
    }

    void tryKill(Player player);

    default boolean isUpgraded() {
        return false;
    }

    default void setUpgraded(boolean upgraded){

    }

    @Deprecated
    default boolean isSunSensitive2() {
        return this.servantSunBurn();
    }

    default boolean servantSunBurn() {
        return false;
    }

    default boolean burnSunTick() {
        if (this instanceof LivingEntity living) {
            if (living.level.isDay() && !living.level.isClientSide) {
                float f = living.getLightLevelDependentMagicValue();
                BlockPos blockpos = BlockPos.containing(living.getX(), living.getEyeY(), living.getZ());
                boolean flag = living.isInWaterRainOrBubble() || living.isInPowderSnow || living.wasInPowderSnow;
                return f > 0.5F && living.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && living.level.canSeeSky(blockpos);
            }
        }

        return false;
    }

    default boolean canRide(LivingEntity livingEntity){
        if (!(this instanceof PlayerRideable)
                && !(this instanceof AbstractGolemServant)
                && livingEntity instanceof PlayerRideable
                && livingEntity.getFirstPassenger() == null){
            if (livingEntity instanceof AbstractHorse horse){
                return horse.isTamed();
            } else if (livingEntity instanceof OwnableEntity ownable && this.getTrueOwner() != null){
                return ownable.getOwner() == this.getTrueOwner();
            } else if (livingEntity instanceof IOwned owned && this.getTrueOwner() != null){
                return owned.getTrueOwner() == this.getTrueOwner();
            }
        }
        return false;
    }

    default void servantTick(){
        if (this instanceof Mob owned){
            this.stayingMode();
            if (this.getKillChance() > 0){
                this.setKillChance(this.getKillChance() - 1);
            }
            if (MobsConfig.StayingServantChunkLoad.get()) {
                if (owned.level instanceof ServerLevel serverLevel) {
                    if (this.isStaying()) {
                        if (this.getTrueOwner() instanceof Player player) {
                            if (player.tickCount % 10 == 0) {
                                for (int i = -1; i <= 1; i++) {
                                    for (int j = -1; j <= 1; j++) {
                                        ChunkPos pos = new ChunkPos(owned.blockPosition().offset(i * 16, 0, j * 16));
                                        serverLevel.setChunkForced(pos.x, pos.z, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.commandMode();
            if (this.isWandering() || this.isPatrolling()){
                if (this.isStaying()) {
                    this.setStaying(false);
                }
            }
            if (this.isPatrolling()){
                if (owned.getTarget() != null){
                    if (owned.getTarget().distanceToSqr(this.vec3BoundPos()) > Mth.square(PATROL_RANGE * 2)){
                        owned.setTarget(null);
                        if (!this.isCommanded()){
                            owned.getNavigation().moveTo(this.getBoundPos().getX(), this.getBoundPos().getY(), this.getBoundPos().getZ(), 1.0F);
                        }
                    }
                } else if (!this.isCommanded() && owned.distanceToSqr(this.vec3BoundPos()) > Mth.square(PATROL_RANGE)){
                    owned.getNavigation().moveTo(this.getBoundPos().getX(), this.getBoundPos().getY(), this.getBoundPos().getZ(), 1.0F);
                }
            }
            if (this.getNoHealTime() <= 0){
                this.healServant(owned);
            } else {
                this.setNoHealTime(this.getNoHealTime() - 1);
            }
            boolean flag = this.servantSunBurn() && this.burnSunTick() && !owned.fireImmune() && MobsConfig.UndeadServantSunlightBurn.get();
            if (flag) {
                ItemStack itemstack = owned.getItemBySlot(EquipmentSlot.HEAD);
                if (!itemstack.isEmpty()) {
                    if (itemstack.isDamageableItem() && MobsConfig.UndeadServantSunlightHelmet.get()) {
                        itemstack.setDamageValue(itemstack.getDamageValue() + owned.getRandom().nextInt(2));
                        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            owned.broadcastBreakEvent(EquipmentSlot.HEAD);
                            owned.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    owned.setSecondsOnFire(8);
                }
            }
        }
    }

    default void healServant(LivingEntity livingEntity){
        if (this.getTrueOwner() != null){
            boolean crown = false;
            if (livingEntity.getMobType() == ModMobType.FROST){
                crown = CuriosFinder.hasFrostCrown(this.getTrueOwner());
            }
            if (livingEntity.getMobType() == ModMobType.NATURAL || livingEntity.getMobType() == MobType.ARTHROPOD){
                crown = CuriosFinder.hasWildCrown(this.getTrueOwner());
            }
            if (livingEntity.getMobType() == ModMobType.NETHER){
                crown = CuriosFinder.hasNetherCrown(this.getTrueOwner());
            }
            if (livingEntity.getMobType() == MobType.UNDEAD){
                crown = CuriosFinder.hasUndeadCrown(this.getTrueOwner());
            }
            if (livingEntity.getMobType() == MobType.WATER){
                crown = CuriosFinder.hasAbyssCrown(this.getTrueOwner());
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
                            if (livingEntity.getMobType() == MobType.WATER && MobsConfig.WaterMinionHeal.get()){
                                curio = CuriosFinder.hasAbyssRobes(owner);
                                soulCost = MobsConfig.WaterMinionHealCost.get();
                                healRate = MobsConfig.WaterMinionHealTime.get();
                                healAmount = MobsConfig.WaterMinionHealAmount.get().floatValue();
                            }
                            if ((livingEntity.getMobType() == ModMobType.NATURAL || livingEntity.getMobType() == MobType.ARTHROPOD) && MobsConfig.NaturalMinionHeal.get()){
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

    default double getCommandSpeed(){
        return 1.25D;
    }

    default void commandMode(){
        if (this instanceof Mob owned){
            if (this.isCommanded()){
                if (owned.getNavigation().isStableDestination(this.getCommandPos()) || this.getCommandPosEntity() != null){
                    this.setCommandTick(this.getCommandTick() - 1);
                    if (this.getCommandPosEntity() != null){
                        owned.getNavigation().moveTo(this.getCommandPosEntity(), this.getCommandSpeed());
                    } else {
                        owned.getNavigation().moveTo(this.getCommandPos().getX() + 0.5D, this.getCommandPos().getY(), this.getCommandPos().getZ() + 0.5D, this.getCommandSpeed());
                    }

                    if (owned.getNavigation().isStuck() || this.getCommandTick() <= 0){
                        this.setCommandPosEntity(null);
                        this.setCommandPos(null);
                    } else if (this.getCommandPos().closerToCenterThan(
                            owned.getControlledVehicle() != null ? owned.getControlledVehicle().position() : owned.position(),
                            owned.getControlledVehicle() != null ? owned.getControlledVehicle().getBbWidth() + 1.0D : owned.getBbWidth() + 1.0D)){
                        if (this.getCommandPosEntity() != null &&
                                owned.getBoundingBox().inflate(1.25D).intersects(this.getCommandPosEntity().getBoundingBox())){
                            if (this.canRide(this.getCommandPosEntity())) {
                                if (owned.startRiding(this.getCommandPosEntity())) {
                                    if (this.getTrueOwner() instanceof Player player){
                                        player.displayClientMessage(Component.translatable("info.goety.servant.dismount"), true);
                                    }
                                }
                            }
                            this.setCommandPosEntity(null);
                        }
                        if (this.isPatrolling()){
                            this.setBoundPos(this.getCommandPos());
                        }
                        owned.moveTo(this.getCommandPos(), owned.getYRot(), owned.getXRot());
                        this.setCommandPos(null);
                    }
                } else {
                    this.setCommandPos(null);
                }
            }
        }
    }

    default void stayingMode(){
        if (this instanceof Mob owned) {
            AttributeInstance modifiableattributeinstance = owned.getAttribute(Attributes.MOVEMENT_SPEED);
            if (modifiableattributeinstance != null) {
                if (this.isStaying()) {
                    if (owned.getNavigation().getPath() != null) {
                        owned.getNavigation().stop();
                    }
                    if (owned.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                        modifiableattributeinstance.removeModifier(SPEED_MODIFIER);
                        modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER);
                    }
                    this.stayingPosition();
                    if (this.isWandering()) {
                        this.setWandering(false);
                    }
                } else {
                    if (modifiableattributeinstance.hasModifier(SPEED_MODIFIER)) {
                        modifiableattributeinstance.removeModifier(SPEED_MODIFIER);
                    }
                }
            }
        }
    }

    default void stayingPosition(){
        if (this instanceof Mob owned) {
            if (owned.getTarget() != null) {
                owned.getLookControl().setLookAt(owned.getTarget(), owned.getMaxHeadYRot(), owned.getMaxHeadXRot());
                double d2 = owned.getTarget().getX() - owned.getX();
                double d1 = owned.getTarget().getZ() - owned.getZ();
                owned.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                owned.yBodyRot = owned.getYRot();
            }
        }
    }

    default void readServantData(CompoundTag compound){
        if (compound.contains("Upgraded")) {
            this.setUpgraded(compound.getBoolean("Upgraded"));
        }
        if (compound.contains("wandering")) {
            this.setWandering(compound.getBoolean("wandering"));
        }
        if (compound.contains("staying")) {
            this.setStaying(compound.getBoolean("staying"));
        }
        if (compound.contains("commandPos")){
            this.setCommandPos(NbtUtils.readBlockPos(compound.getCompound("commandPos")));
        }
        if (compound.contains("commandPosEntity")){
            if (EntityFinder.getLivingEntityByUuiD(compound.getUUID("commandPosEntity")) != null) {
                this.setCommandPosEntity(EntityFinder.getLivingEntityByUuiD(compound.getUUID("commandPosEntity")));
            }
        }
        if (compound.contains("boundPos")){
            this.setBoundPos(NbtUtils.readBlockPos(compound.getCompound("boundPos")));
        }
        if (compound.contains("noHealTime")){
            this.setNoHealTime(compound.getInt("noHealTime"));
        }
    }

    default void saveServantData(CompoundTag compound){
        compound.putBoolean("Upgraded", this.isUpgraded());
        compound.putBoolean("wandering", this.isWandering());
        compound.putBoolean("staying", this.isStaying());
        if (this.getCommandPos() != null){
            compound.put("commandPos", NbtUtils.writeBlockPos(this.getCommandPos()));
        }
        if (this.getCommandPosEntity() != null) {
            compound.putUUID("commandPosEntity", this.getCommandPosEntity().getUUID());
        }
        compound.putInt("commandTick", this.getCommandTick());
        if (this.getBoundPos() != null){
            compound.put("boundPos", NbtUtils.writeBlockPos(this.getBoundPos()));
        }
        compound.putInt("noHealTime", this.getNoHealTime());
    }
}
