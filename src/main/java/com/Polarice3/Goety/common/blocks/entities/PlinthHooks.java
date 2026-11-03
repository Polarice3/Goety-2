package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.entities.IGolem;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.blocks.PlinthBlock;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.AbstractHauntedArmor;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.mixin.LivingEntityAccessor;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//Backported from Goety 3, made by Vivideru: https://github.com/Vivideru/Goety-3/blob/main/src/main/java/com/Vivideru/Goety/common/blocks/entities/PlinthHooks.java
public final class PlinthHooks {
    private static final String TAG_PLINTH = "Plinth";
    private static final String TAG_POS = "PlinthPos";
    private static final String TAG_DIM = "PlinthDim";
    private static final String TAG_RESTING = "Resting";
    private static final String TAG_WOKE = "WokeAt";
    private static final String TAG_FROZEN = "PlinthFrozen";
    public static final float HEAL_SHARE_PER_SECOND = 0.02F;
    public static final float REPAIR_SHARE_PER_SECOND = 0.02F;
    public static final int RETURN_DELAY = 100;
    public static final double CENTER_TOLERANCE = 0.2D;

    private PlinthHooks() {
    }

    public static boolean canRest(Entity entity) {
        return entity instanceof IGolem || entity instanceof AbstractHauntedArmor;
    }

    public static int footprint(Mob mob) {
        float width = mob.getBbWidth();
        return width <= 1.5F ? 1 : width <= 3.0F ? 3 : 5;
    }

    @Nullable
    public static GlobalPos getPlinth(Mob mob) {
        if (!mob.getPersistentData().contains(TAG_PLINTH)) {
            return null;
        }
        CompoundTag tag = mob.getPersistentData().getCompound(TAG_PLINTH);
        if (!tag.contains(TAG_POS) || !tag.contains(TAG_DIM)) {
            return null;
        }
        ResourceLocation dimension = ResourceLocation.tryParse(tag.getString(TAG_DIM));
        if (dimension == null) {
            return null;
        }
        return GlobalPos.of(ResourceKey.create(Registries.DIMENSION, dimension), BlockPos.of(tag.getLong(TAG_POS)));
    }

    public static void setPlinth(Mob mob, GlobalPos plinth) {
        CompoundTag tag = mob.getPersistentData();
        CompoundTag data;
        if (!tag.contains(TAG_PLINTH)) {
            data = new CompoundTag();
        } else {
            data = tag.getCompound(TAG_PLINTH);
        }
        data.putLong(TAG_POS, plinth.pos().asLong());
        data.putString(TAG_DIM, plinth.dimension().location().toString());
        data.putBoolean(TAG_RESTING, false);
        tag.put(TAG_PLINTH, data);
    }

    public static void clearPlinth(Mob mob) {
        if (mob.getPersistentData().contains(TAG_PLINTH)) {
            mob.getPersistentData().remove(TAG_PLINTH);
        }
    }

    public static boolean isResting(Entity entity) {
        return entity instanceof Mob mob && mob.getPersistentData().contains(TAG_PLINTH) && mob.getPersistentData().getCompound(TAG_PLINTH).getBoolean(TAG_RESTING);
    }

    private static void setResting(Mob mob, boolean resting) {
        CompoundTag tag = mob.getPersistentData();
        CompoundTag data;
        if (!tag.contains(TAG_PLINTH)) {
            data = new CompoundTag();
        } else {
            data = tag.getCompound(TAG_PLINTH);
        }
        data.putBoolean(TAG_RESTING, resting);
        tag.put(TAG_PLINTH, data);
    }

    public static boolean recentlyWoke(Mob mob) {
        CompoundTag data = mob.getPersistentData().getCompound(TAG_PLINTH);
        return data.contains(TAG_WOKE) && mob.level.getGameTime() - data.getLong(TAG_WOKE) < RETURN_DELAY;
    }

    public static boolean isFrozen(Entity entity) {
        if (!(entity instanceof Mob mob) || !canRest(mob) || !mob.isNoAi()) {
            return false;
        }
        return mob.level.isClientSide || isResting(mob);
    }

    public static void freezeClient(Mob mob) {
        LivingEntityAccessor accessor = (LivingEntityAccessor) mob;
        if (accessor.goety$getLerpSteps() > 0) {
            mob.setPos(accessor.goety$getLerpX(), accessor.goety$getLerpY(), accessor.goety$getLerpZ());
            mob.setYRot((float) accessor.goety$getLerpYRot());
            accessor.goety$setLerpSteps(0);
        }
        accessor.goety$setLerpHeadSteps(0);
        mob.xo = mob.getX();
        mob.yo = mob.getY();
        mob.zo = mob.getZ();
        float yRot = mob.getYRot();
        mob.setYRot(yRot);
        mob.yRotO = yRot;
        mob.yBodyRot = yRot;
        mob.yBodyRotO = yRot;
        mob.setYHeadRot(yRot);
        mob.yHeadRotO = yRot;
        mob.setXRot(0.0F);
        mob.xRotO = 0.0F;
        mob.tickCount = 0;
        CompoundTag data = mob.getPersistentData();
        if (!data.getBoolean(TAG_FROZEN)) {
            mob.walkAnimation.setSpeed(0.0F);
            mob.walkAnimation.update(0.0F, 1.0F);
            data.putBoolean(TAG_FROZEN, true);
        }
    }

    public static void thawClient(Mob mob) {
        CompoundTag data = mob.getPersistentData();
        if (data.getBoolean(TAG_FROZEN)) {
            data.remove(TAG_FROZEN);
        }
    }

    public static boolean isOverCenter(Mob mob, PlinthBlockEntity plinth) {
        Vec3 center = plinth.restPosition();
        double dx = center.x - mob.getX();
        double dz = center.z - mob.getZ();
        return dx * dx + dz * dz <= CENTER_TOLERANCE * CENTER_TOLERANCE && Math.abs(center.y - mob.getY()) < 0.5D;
    }

    @Nullable
    public static PlinthBlockEntity plinthAt(Level level, BlockPos pos) {
        return level.isLoaded(pos) && level.getBlockEntity(pos) instanceof PlinthBlockEntity plinth ? plinth : null;
    }

    public static boolean footprintComplete(Level level, BlockPos center, Mob mob) {
        int half = footprint(mob) / 2;
        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                if (!(level.getBlockState(center.offset(dx, 0, dz)).getBlock() instanceof PlinthBlock)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Nullable
    public static BlockPos squareCenter(Level level, BlockPos plate, Mob mob) {
        int half = footprint(mob) / 2;
        BlockPos best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                BlockPos center = plate.offset(dx, 0, dz);
                int distance = dx * dx + dz * dz;
                if (distance < bestDistance && plinthAt(level, center) != null && footprintComplete(level, center, mob)) {
                    best = center;
                    bestDistance = distance;
                }
            }
        }
        return best;
    }

    public static InteractionResult tryBindToPlinth(ItemStack stack, Player player, LivingEntity entity) {
        if (!(entity instanceof Mob mob) || !canRest(mob) || !(mob instanceof IOwned owned) || owned.getTrueOwner() != player) {
            return InteractionResult.PASS;
        }
        GlobalPos globalPos = WaystoneItem.getPosition(stack);
        if (globalPos == null || globalPos.dimension() != player.level.dimension()) {
            return InteractionResult.PASS;
        }
        PlinthBlockEntity plinth = plinthAt(player.level, globalPos.pos());
        if (plinth == null) {
            return InteractionResult.PASS;
        }
        BlockPos center = squareCenter(player.level, globalPos.pos(), mob);
        boolean complete = center != null;
        if (complete) {
            globalPos = GlobalPos.of(globalPos.dimension(), center);
        }
        leaveCurrentPlinth(mob);
        setPlinth(mob, globalPos);
        if (mob instanceof IServant servant) {
            servant.setBoundPos(globalPos.pos());
            servant.setWandering(false);
            servant.setStaying(false);
        }
        player.displayClientMessage(Component.translatable(complete ? "info.goety.plinth.bind" : "info.goety.plinth.too_small", entity.getDisplayName(), footprint(mob)), true);
        player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
        return InteractionResult.SUCCESS;
    }

    private static void leaveCurrentPlinth(Mob mob) {
        GlobalPos current = getPlinth(mob);
        if (current != null && current.dimension() == mob.level.dimension()) {
            PlinthBlockEntity plinth = plinthAt(mob.level, current.pos());
            if (plinth != null && mob.getUUID().equals(plinth.getOccupant())) {
                plinth.setOccupant(null);
            }
        }
        if (isResting(mob)) {
            wake(mob, null);
        }
    }

    public static boolean stopResting(Mob occupant) {
        return occupant instanceof IServant servant && servant.getTrueOwner() != null && (!servant.isGuardingArea() || servant.isCommanded() || servant.isPatrolling());
    }

    public static void tickPlinth(ServerLevel level, PlinthBlockEntity plinth, boolean secondTick) {
        BlockPos pos = plinth.getBlockPos();
        Mob occupant = plinth.getOccupantMob();
        if (plinth.getOccupant() != null && occupant == null) {
            Entity stale = level.getEntity(plinth.getOccupant());
            if (stale == null || !stale.isAlive()) {
                plinth.setOccupant(null);
            }
            return;
        }
        if (occupant != null) {
            GlobalPos assigned = getPlinth(occupant);
            if (assigned == null || !assigned.pos().equals(pos) || assigned.dimension() != level.dimension()) {
                plinth.setOccupant(null);
                return;
            }
            if (isResting(occupant)) {
                face(occupant, plinth);
                boolean working = needsMending(occupant);
                if (secondTick && working) {
                    mend(occupant);
                }
                plinth.setCharged(working);
                LivingEntity enemy = findEnemy(level, plinth, occupant);
                if (stopResting(occupant) || enemy != null) {
                    wake(occupant, enemy);
                }
            } else {
                plinth.setCharged(false);
            }
            return;
        }
        plinth.setCharged(false);
        adopt(level, plinth);
    }

    private static boolean needsMending(Mob mob) {
        if (mob.getHealth() < mob.getMaxHealth()) {
            return true;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = mob.getItemBySlot(slot);
            if (stack.isDamageableItem() && stack.isDamaged()) {
                return true;
            }
        }
        return false;
    }

    private static void mend(Mob mob) {
        if (mob.getHealth() < mob.getMaxHealth()) {
            mob.heal(mob.getMaxHealth() * HEAL_SHARE_PER_SECOND);
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = mob.getItemBySlot(slot);
            if (stack.isDamageableItem() && stack.isDamaged()) {
                int step = Math.max(1, Math.round(stack.getMaxDamage() * REPAIR_SHARE_PER_SECOND));
                stack.setDamageValue(Math.max(0, stack.getDamageValue() - step));
            }
        }
    }

    private static void face(Mob mob, PlinthBlockEntity plinth) {
        BlockState state = plinth.getBlockState();
        if (!state.hasProperty(PlinthBlock.FACING)) {
            return;
        }
        float yRot = state.getValue(PlinthBlock.FACING).toYRot();
        if (mob.getYRot() != yRot || mob.yBodyRot != yRot || mob.getYHeadRot() != yRot) {
            setRotation(mob, yRot);
        }
    }

    private static void setRotation(Mob mob, float yRot) {
        mob.setYRot(yRot);
        mob.yRotO = yRot;
        mob.yBodyRot = yRot;
        mob.yBodyRotO = yRot;
        mob.setYHeadRot(yRot);
        mob.yHeadRotO = yRot;
        mob.setXRot(0.0F);
        mob.xRotO = 0.0F;
    }

    private static void adopt(ServerLevel level, PlinthBlockEntity plinth) {
        BlockPos pos = plinth.getBlockPos();
        if (!level.getEntitiesOfClass(Mob.class, new AABB(pos).deflate(0.1D, 0.0D, 0.1D).expandTowards(0.0D, 3.0D, 0.0D), PlinthHooks::isResting).isEmpty()) {
            return;
        }
        AABB area = new AABB(pos).inflate(0.5D, 0.0D, 0.5D).expandTowards(0.0D, 3.0D, 0.0D);
        for (Mob mob : level.getEntitiesOfClass(Mob.class, area, PlinthHooks::canRest)) {
            if (!mob.isAlive() || mob.getTarget() != null || isResting(mob) || recentlyWoke(mob) || mob instanceof Raider raider && raider.hasActiveRaid()) {
                continue;
            }
            GlobalPos assigned = getPlinth(mob);
            if (assigned != null && !(assigned.pos().equals(pos) && assigned.dimension() == level.dimension())) {
                continue;
            }
            boolean servant = mob instanceof IOwned owned && owned.getTrueOwner() != null;
            if (servant && !(mob instanceof IServant servant1 && servant1.getBoundPos() != null && servant1.getBoundPos().equals(pos))) {
                continue;
            }
            if (!isOverCenter(mob, plinth)) {
                continue;
            }
            if (rest(mob, plinth)) {
                return;
            }
        }
    }

    public static boolean rest(Mob mob, PlinthBlockEntity plinth) {
        Level level = plinth.getLevel();
        if (level == null || !footprintComplete(level, plinth.getBlockPos(), mob)) {
            return false;
        }
        GlobalPos globalPos = GlobalPos.of(level.dimension(), plinth.getBlockPos());
        GlobalPos assigned = getPlinth(mob);
        if (assigned == null || !assigned.equals(globalPos)) {
            setPlinth(mob, globalPos);
        }
        Vec3 center = plinth.restPosition();
        BlockState state = plinth.getBlockState();
        float yRot = state.hasProperty(PlinthBlock.FACING) ? state.getValue(PlinthBlock.FACING).toYRot() : mob.getYRot();
        mob.getNavigation().stop();
        mob.setDeltaMovement(Vec3.ZERO);
        mob.moveTo(center.x, center.y, center.z, yRot, 0.0F);
        setRotation(mob, yRot);
        if (mob instanceof Summoned summoned) {
            summoned.overrideSetTarget(null);
            summoned.setBoundPos(plinth.getBlockPos());
            summoned.setWandering(false);
            summoned.setStaying(false);
        } else {
            mob.setTarget(null);
        }
        mob.setNoAi(true);
        mob.setSilent(true);
        mob.setPersistenceRequired();
        setResting(mob, true);
        plinth.setOccupant(mob.getUUID());
        if (level instanceof ServerLevel serverLevel) {
            ServerParticleUtil.summonUndeadParticles(serverLevel, mob);
        }
        level.playSound(null, plinth.getBlockPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 0.8F, 0.6F);
        return true;
    }

    public static void wake(Mob mob, @Nullable LivingEntity target) {
        if (!isResting(mob)) {
            if (target != null) {
                setTarget(mob, target);
            }
            return;
        }
        mob.setNoAi(false);
        mob.setSilent(false);
        setResting(mob, false);
        CompoundTag data = mob.getPersistentData().getCompound(TAG_PLINTH);
        data.putLong(TAG_WOKE, mob.level.getGameTime());
        mob.getPersistentData().put(TAG_PLINTH, data);
        if (target != null) {
            setTarget(mob, target);
        }
        mob.level.playSound(null, mob.blockPosition(), SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8F, 0.7F);
    }

    private static void setTarget(Mob mob, LivingEntity target) {
        if (mob instanceof Summoned summoned) {
            summoned.overrideSetTarget(target);
        } else {
            mob.setTarget(target);
        }
    }

    @Nullable
    private static LivingEntity findEnemy(ServerLevel level, PlinthBlockEntity plinth, Mob occupant) {
        int radius = plinth.getRadius();
        Vec3 center = plinth.restPosition();
        LivingEntity nearest = null;
        double best = Double.MAX_VALUE;
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, new AABB(plinth.getBlockPos()).inflate(radius), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        for (LivingEntity candidate : candidates) {
            double distance = candidate.distanceToSqr(center);
            if (distance <= radius * (double) radius && distance < best && isEnemy(occupant, candidate, radius)) {
                nearest = candidate;
                best = distance;
            }
        }
        return nearest;
    }

    public static boolean isEnemy(Mob occupant, LivingEntity candidate, double radius) {
        if (candidate == occupant || !candidate.isAlive() || isResting(candidate)) {
            return false;
        }
        return TargetingConditions.forCombat()
                .range(radius)
                .ignoreLineOfSight()
                .selector(target -> MobUtil.isOwnedTargetable(occupant, target))
                .test(occupant, candidate)
                && occupant.hasLineOfSight(candidate);
    }

    public static void onDisturbed(Mob mob) {
        if (isResting(mob)) {
            wake(mob, null);
        }
    }

    public static Direction facingOf(BlockState state) {
        return state.hasProperty(PlinthBlock.FACING) ? state.getValue(PlinthBlock.FACING) : Direction.NORTH;
    }
}
