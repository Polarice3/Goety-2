package com.Polarice3.Goety.api.entities.ally;

import com.Polarice3.Goety.api.entities.IGolem;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface IServant extends IOwned {
    int GUARDING_RANGE = MobsConfig.ServantGuardingRange.get();

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

    default boolean isGuardingArea(){
        if (this instanceof Entity entity) {
            if (entity.level.dimension() != this.getBoundLevel()){
                return false;
            }
        }
        return this.getBoundPos() != null;
    }

    default boolean canGuardArea(){
        return true;
    }

    default BlockPos getBoundPos(){
        return null;
    }

    default Vec3 vec3BoundPos(){
        return Vec3.atBottomCenterOf(this.getBoundPos());
    }

    default void setBoundPos(BlockPos blockPos){
        if (this instanceof Entity entity) {
            this.setBoundDim(entity.level.dimension());
        }
    }

    default ResourceKey<Level> getBoundLevel() {
        ResourceLocation resourcelocation = new ResourceLocation(this.getBoundDim());
        return ResourceKey.create(Registries.DIMENSION, resourcelocation);
    }

    default String getBoundDim(){
        return Level.OVERWORLD.location().toString();
    }

    default void setBoundDim(ResourceKey<Level> resourceKey) {
        this.setBoundDim(resourceKey.location().toString());
    }

    default void setBoundDim(String string) {

    }

    default boolean isWithinGuard(BlockPos p_21445_){
        if (this.getBoundPos() == null) {
            return true;
        } else {
            return this.getBoundPos().distSqr(p_21445_) < Mth.square(GUARDING_RANGE);
        }
    }

    default void setFollowing(){
        this.setBoundPos(null);
        this.setWandering(false);
        this.setStaying(false);
    }

    default boolean isFollowing(){
        return !this.isWandering() && !this.isStaying() && !this.isGuardingArea() && this.canFollow();
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
            if (!this.isWandering() && !this.isStaying() && !this.isGuardingArea() && this.canWander()) {
                this.setBoundPos(null);
                this.setWandering(true);
                this.setStaying(false);
                player.displayClientMessage(Component.translatable("info.goety.servant.wander", living.getDisplayName()), true);
                flag = true;
            } else if (!this.isStaying() && !this.isGuardingArea() && this.canStay()) {
                this.setBoundPos(null);
                this.setWandering(false);
                this.setStaying(true);
                player.displayClientMessage(Component.translatable("info.goety.servant.staying", living.getDisplayName()), true);
                flag = true;
            } else if (!this.isGuardingArea() && this.canGuardArea()) {
                this.setBoundPos(living.blockPosition());
                this.setWandering(false);
                this.setStaying(false);
                player.displayClientMessage(Component.translatable("info.goety.servant.guard", living.getDisplayName()), true);
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

    default boolean canCommandToBlock(Level level, BlockPos blockPos) {
        return !level.getBlockState(blockPos).isSolidRender(level, blockPos);
    }

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

    default void setCommandPosEntityOrder(LivingEntity living){
        this.setCommandPosEntity(living);
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

    //Wanna rename this to not mix up with vanilla lol.
    default boolean isAbleToRide(LivingEntity livingEntity) {
        return this.canRide(livingEntity);
    }

    default boolean canRide(LivingEntity livingEntity){
        if (!(this instanceof PlayerRideable)
                && !(this instanceof IGolem)
                && (livingEntity instanceof PlayerRideable || livingEntity.getType().is(ModTags.EntityTypes.SERVANT_RIDEABLE))
                && livingEntity.getFirstPassenger() == null){
            if (livingEntity instanceof AbstractHorse horse){
                return horse.isTamed();
            } else if (MobUtil.getOwner(livingEntity) != null){
                return MobUtil.getOwner(livingEntity) == this.getTrueOwner();
            }
        }
        if (livingEntity instanceof IServant servant && this instanceof LivingEntity rider){
            return servant.canBeRidden(rider);
        }
        return false;
    }

    default boolean canBeRidden(LivingEntity livingEntity){
        return false;
    }

    default void servantTick(){
        if (this instanceof Mob owned){
            this.stayingMode();
            if (this.getKillChance() > 0){
                this.setKillChance(this.getKillChance() - 1);
            }
            if (MobsConfig.StayingServantChunkLoad.get()) {
                if (owned.isAlive()) {
                    this.chunkLoad();
                }
            }
            this.commandMode();
            if (this.isWandering() || this.isGuardingArea()){
                if (this.isStaying()) {
                    this.setStaying(false);
                }
            }
            if (this.isGuardingArea()){
                if (owned.getTarget() != null){
                    if (owned.getTarget().distanceToSqr(this.vec3BoundPos()) > Mth.square(GUARDING_RANGE * 2)){
                        owned.setTarget(null);
                        if (!this.isCommanded()){
                            owned.getNavigation().moveTo(this.getBoundPos().getX(), this.getBoundPos().getY(), this.getBoundPos().getZ(), 1.0F);
                        }
                    }
                } else if (!this.isCommanded() && owned.distanceToSqr(this.vec3BoundPos()) > Mth.square(GUARDING_RANGE)){
                    owned.getNavigation().moveTo(this.getBoundPos().getX(), this.getBoundPos().getY(), this.getBoundPos().getZ(), 1.0F);
                }
            }
            if (this.getNoHealTime() <= 0){
                this.healServant();
            } else {
                this.setNoHealTime(this.getNoHealTime() - 1);
            }
            this.burnServant(owned);
        }
    }

    default long getTicketTime() {
        return 0;
    }

    default void setTicketTime(long time) {

    }

    default long decreaseTicketTime() {
        long ticket = this.getTicketTime() - 1L;
        this.setTicketTime(ticket);
        return ticket;
    }

    default void chunkLoad() {
        if (this instanceof Mob owned){
            if (owned.level instanceof ServerLevel serverLevel) {
                if (this.isStaying()) {
                    if (this.getTrueOwner() instanceof Player) {
                        int i = SectionPos.blockToSectionCoord(owned.position().x());
                        int j = SectionPos.blockToSectionCoord(owned.position().z());
                        BlockPos blockPos = BlockPos.containing(owned.position());
                        if (this.decreaseTicketTime() <= 0L || i != SectionPos.blockToSectionCoord(blockPos.getX()) || j != SectionPos.blockToSectionCoord(blockPos.getZ())) {
                            serverLevel.getChunkSource().addRegionTicket(ModTicketTypes.SERVANT, owned.chunkPosition(), 2, owned.blockPosition());
                            serverLevel.resetEmptyTime();
                            this.setTicketTime(ModTicketTypes.SERVANT.timeout() - 1L);
                        }
                    }
                } else if (this.getTicketTime() > 0) {
                    this.setTicketTime(0);
                }
            }
        }
    }

    default void healServant(){
        if (this instanceof LivingEntity self) {
            if (this.getTrueOwner() != null) {
                boolean crown = false;
                if (ServantUtil.isFrostHeal(self)) {
                    crown = CuriosFinder.hasFrostCrown(this.getTrueOwner());
                }
                if (ServantUtil.isWildHeal(self)) {
                    crown = CuriosFinder.hasWildCrown(this.getTrueOwner());
                }
                if (ServantUtil.isNetherHeal(self)) {
                    crown = CuriosFinder.hasNetherCrown(this.getTrueOwner());
                }
                if (ServantUtil.isNecroHeal(self)) {
                    crown = CuriosFinder.hasUndeadCrown(this.getTrueOwner());
                }
                if (ServantUtil.isAbyssHeal(self)) {
                    crown = CuriosFinder.hasAbyssCrown(this.getTrueOwner());
                }
                if (ServantUtil.isVoidHeal(self)) {
                    crown = CuriosFinder.hasVoidCrown(this.getTrueOwner());
                }
                if (!crown) {
                    if (this.getLifespan() > 0) {
                        this.setHasLifespan(true);
                    }
                } else {
                    this.setHasLifespan(false);
                }
                if (!self.level.isClientSide) {
                    if (!this.hasLifespan() || this.getLifespan() > 20) {
                        ServantUtil.healServant(this.getTrueOwner(), self);
                    }
                }
            }
        }
    }

    default void burnServant(LivingEntity livingEntity){
        boolean flag = this.servantSunBurn() && this.burnSunTick() && !livingEntity.fireImmune() && MobsConfig.UndeadServantSunlightBurn.get();
        if (flag) {
            ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty()) {
                if (itemstack.isDamageableItem() && MobsConfig.UndeadServantSunlightHelmet.get()) {
                    itemstack.setDamageValue(itemstack.getDamageValue() + livingEntity.getRandom().nextInt(2));
                    if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                        livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD);
                        livingEntity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                flag = false;
            }

            if (flag) {
                livingEntity.setSecondsOnFire(8);
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
                            if (this.isAbleToRide(this.getCommandPosEntity())) {
                                if (owned.startRiding(this.getCommandPosEntity())) {
                                    if (this.getTrueOwner() instanceof Player player){
                                        player.displayClientMessage(Component.translatable("info.goety.servant.dismount"), true);
                                    }
                                }
                            }
                            this.setCommandPosEntity(null);
                        }
                        if (this.isGuardingArea()){
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
            if (compound.contains("boundDim")){
                this.setBoundDim(compound.getString("boundDim"));
            } else {
                if (this instanceof Entity entity) {
                    this.setBoundDim(entity.level.dimension());
                }
            }
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
            compound.putString("boundDim", this.getBoundDim());
        }
        compound.putInt("noHealTime", this.getNoHealTime());
    }
}
