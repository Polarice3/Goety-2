package com.Polarice3.Goety.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MobUtil {
    public static final Predicate<LivingEntity> NO_CREATIVE_OR_SPECTATOR = (p_200824_0_) -> {
        return !(p_200824_0_ instanceof Player) || !p_200824_0_.isSpectator() && !((Player)p_200824_0_).isCreative();
    };

    public static boolean validEntity(Entity entity){
        if (entity instanceof Player player){
            return playerValidity(player, false);
        } else {
            return entity.isAttackable();
        }
    }

    public static boolean playerValidity(Player player, boolean lich){
        if (!player.isCreative() && !player.isSpectator()) {
            if (lich) {
                return !LichdomHelper.isLich(player);
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean undeadAndLich(LivingEntity pLivingEntity){
        if (pLivingEntity.isInvertedHealAndHarm() || pLivingEntity.getMobType() == MobType.UNDEAD) {
            return true;
        } else {
            if (pLivingEntity instanceof Player player){
                return LichdomHelper.isLich(player);
            } else {
                return false;
            }
        }
    }

    public static LootContext.Builder createLootContext(DamageSource pDamageSource, LivingEntity livingEntity) {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) livingEntity.level)).withRandom(livingEntity.getRandom()).withParameter(LootContextParams.THIS_ENTITY, livingEntity).withParameter(LootContextParams.ORIGIN, livingEntity.position()).withParameter(LootContextParams.DAMAGE_SOURCE, pDamageSource).withOptionalParameter(LootContextParams.KILLER_ENTITY, pDamageSource.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, pDamageSource.getDirectEntity());
        if (livingEntity.getLastHurtByMob() != null && livingEntity.getLastHurtByMob() instanceof Player player) {
            lootcontext$builder = lootcontext$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
        }

        return lootcontext$builder;
    }

    public static void push(Entity pEntity, double pX, double pY, double pZ) {
        if (pEntity instanceof Player player) {
            if (MobUtil.playerValidity(player, false)) {
                player.hurtMarked = true;
                if (!player.level.isClientSide){
                    player.setOnGround(false);
                }
            }
        }
        pEntity.setDeltaMovement(pEntity.getDeltaMovement().add(pX, pY, pZ));
        pEntity.hasImpulse = true;
    }

    public static int getSummonLifespan(Level world){
        return 20 * (30 + world.random.nextInt(90));
    }

    public static class MinionMoveControl extends MoveControl {
        public MinionMoveControl(Mob mob) {
            super(mob);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
                double d0 = vector3d.length();
                if (d0 < this.mob.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5D));
                } else {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d0)));
                    if (this.mob.getTarget() == null) {
                        Vec3 vec31 = this.mob.getDeltaMovement();
                        this.mob.setYRot(-((float) Mth.atan2(vec31.x, vec31.z)) * (180F / (float)Math.PI));
                        this.mob.yBodyRot = this.mob.getYRot();
                    } else {
                        double d2 = this.mob.getTarget().getX() - this.mob.getX();
                        double d1 = this.mob.getTarget().getZ() - this.mob.getZ();
                        this.mob.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                    }
                    this.mob.yBodyRot = this.mob.getYRot();
                }

            }
        }
    }

    public static class WraithMoveController extends MoveControl {
        public WraithMoveController(Mob mob) {
            super(mob);
        }

        public void tick() {
            if (this.mob.isNoGravity()) {
                if (this.operation == MoveControl.Operation.MOVE_TO) {
                    Vec3 vector3d = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
                    double d0 = vector3d.length();
                    if (d0 < this.mob.getBoundingBox().getSize()) {
                        this.operation = MoveControl.Operation.WAIT;
                        this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5D));
                    } else {
                        this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d0)));
                        if (this.mob.getTarget() == null) {
                            Vec3 vector3d1 = this.mob.getDeltaMovement();
                            this.mob.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                        } else {
                            double d2 = this.mob.getTarget().getX() - this.mob.getX();
                            double d1 = this.mob.getTarget().getZ() - this.mob.getZ();
                            this.mob.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                        }
                        this.mob.yBodyRot = this.mob.getYRot();
                    }

                }
            } else {
                super.tick();
            }
        }
    }

    public static boolean isInRain(Entity pEntity){
        BlockPos blockpos = pEntity.blockPosition();
        return pEntity.level.isRainingAt(blockpos) || pEntity.level.isRainingAt(new BlockPos((double)blockpos.getX(), pEntity.getBoundingBox().maxY, (double)blockpos.getZ()));
    }

    public static boolean healthIsHalved(LivingEntity livingEntity){
        return livingEntity.getHealth() <= livingEntity.getMaxHealth()/2;
    }

    /**
     * Target Codes based of codes from @TeamTwilight
     */
    public static List<Entity> getTargets(LivingEntity pSource, double pRange) {
        List<Entity> list = new ArrayList<>();
        double vectorY = pSource.getY();
        if (pSource instanceof Player){
            vectorY = pSource.getEyeY();
        }
        Vec3 source = new Vec3(pSource.getX(), vectorY, pSource.getZ());
        Vec3 lookVec = pSource.getViewVector(1.0F);
        Vec3 rangeVec = new Vec3(lookVec.x * pRange, lookVec.y * pRange, lookVec.z * pRange);
        Vec3 end = source.add(rangeVec);
        float size = 3.0F;
        List<Entity> entities = pSource.level.getEntities(pSource, pSource.getBoundingBox().expandTowards(rangeVec).inflate(size));
        double hitDist = 0.0D;

        for (Entity entity : entities) {
            if (entity.isPickable() && entity != pSource) {
                float borderSize = entity.getPickRadius();
                AABB collisionBB = entity.getBoundingBox().inflate(borderSize);
                Optional<Vec3> interceptPos = collisionBB.clip(source, end);

                if (collisionBB.contains(source)) {
                    if (0.0D <= hitDist) {
                        list.add(entity);
                        hitDist = 0.0D;
                    }
                } else if (interceptPos.isPresent()) {
                    double possibleDist = source.distanceTo(interceptPos.get());

                    if (possibleDist < hitDist || hitDist == 0.0D) {
                        list.add(entity);
                        hitDist = possibleDist;
                    }
                }
            }
        }
        return list;
    }

    @Nullable
    public static Entity getSingleTarget(Level level, LivingEntity living) {
        Entity target = null;
        double range = 15.0D;
        Vec3 srcVec = living.getEyePosition();
        Vec3 lookVec = living.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x() * range, lookVec.y() * range, lookVec.z() * range);
        float radius = 1.0F;
        List<Entity> possibleList = level.getEntities(living, living.getBoundingBox().expandTowards(lookVec.x() * range, lookVec.y() * range, lookVec.z() * range).inflate(radius, radius, radius));
        double hitDist = 0;

        for (Entity possibleEntity : possibleList) {
            if (possibleEntity.isPickable()) {
                float borderSize = possibleEntity.getPickRadius();
                AABB collisionBB = possibleEntity.getBoundingBox().inflate(borderSize, borderSize, borderSize);
                Optional<Vec3> interceptPos = collisionBB.clip(srcVec, destVec);
                if (collisionBB.contains(srcVec)) {
                    if (0.0D < hitDist || hitDist == 0.0D) {
                        target = possibleEntity;
                        hitDist = 0.0D;
                    }
                } else if (interceptPos.isPresent()) {
                    double possibleDist = srcVec.distanceTo(interceptPos.get());

                    if (possibleDist < hitDist || hitDist == 0.0D) {
                        target = possibleEntity;
                        hitDist = possibleDist;
                    }
                }
            }
        }
        return target;
    }

    /**
     * Code based of @BobMowzies Sunstrike positioning.
     */
    public static void moveDownToGround(Entity entity) {
        HitResult rayTrace = rayTrace(entity);
        if (rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) rayTrace;
            if (hitResult.getDirection() == Direction.UP) {
                BlockState hitBlock = entity.level.getBlockState(hitResult.getBlockPos());
                if (hitBlock.getBlock() instanceof SlabBlock && hitBlock.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
                    entity.setPos(entity.getX(), hitResult.getBlockPos().getY() + 1.0625F - 0.5f, entity.getZ());
                } else {
                    entity.setPos(entity.getX(), hitResult.getBlockPos().getY() + 1.0625F, entity.getZ());
                }
                if (entity.level instanceof ServerLevel) {
                    ((ServerLevel) entity.level).getChunkSource().broadcastAndSend(entity, new ClientboundTeleportEntityPacket(entity));
                }
            }
        }
    }

    private static HitResult rayTrace(Entity entity) {
        Vec3 startPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        Vec3 endPos = new Vec3(entity.getX(), entity.level.getMinBuildHeight(), entity.getZ());
        return entity.level.clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
    }
}
