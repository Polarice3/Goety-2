package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.entities.projectiles.BlastFungus;
import com.Polarice3.Goety.common.entities.projectiles.SnapFungus;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WebBlock;
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

    public static void ClimbAnyWall(LivingEntity livingEntity){
        Vec3 movement = livingEntity.getDeltaMovement();
        if (livingEntity instanceof Player player){
            if (!player.getAbilities().flying && player.horizontalCollision){
                movement = new Vec3(movement.x, 0.2D, movement.z);
            }
            player.setDeltaMovement(movement);
        } else {
            if (livingEntity.horizontalCollision){
                movement = new Vec3(movement.x, 0.2D, movement.z);
            }
            livingEntity.setDeltaMovement(movement);
        }
    }

    public static void WebMovement(LivingEntity livingEntity){
        AABB axisalignedbb = livingEntity.getBoundingBox();
        BlockPos blockpos = new BlockPos(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
        BlockPos blockpos1 = new BlockPos(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        if (livingEntity.level.hasChunksAt(blockpos, blockpos1)) {
            for(int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
                for(int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                    for(int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                        blockpos$mutable.set(i, j, k);
                        BlockState blockstate = livingEntity.level.getBlockState(blockpos$mutable);
                        if (blockstate.getBlock() instanceof WebBlock){
                            livingEntity.makeStuckInBlock(blockstate, Vec3.ZERO);
                        }
                    }
                }
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

    public static boolean starAmuletActive(Player player){
        return CuriosFinder.hasCurio(player, ModItems.STAR_AMULET.get()) && MobUtil.healthIsHalved(player);
    }

    public static void releaseAllPois(Villager villager){
        villager.releasePoi(MemoryModuleType.HOME);
        villager.releasePoi(MemoryModuleType.JOB_SITE);
        villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
        villager.releasePoi(MemoryModuleType.MEETING_POINT);
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

    public static void throwSnapFungus(LivingEntity livingEntity, Level level){
        SnapFungus blastFungus = new SnapFungus(livingEntity, level);
        throwFungus(blastFungus, livingEntity, level);
    }

    public static void throwBlastFungus(LivingEntity livingEntity, Level level){
        BlastFungus blastFungus = new BlastFungus(livingEntity, level);
        throwFungus(blastFungus, livingEntity, level);
    }

    public static void throwFungus(Projectile projectile, LivingEntity livingEntity, Level level){
        float f2 = 0.35F;
        if (BlockFinder.emptySquareSpace(level, livingEntity.blockPosition(), 13, true)){
            f2 = 0.75F;
        } else if (BlockFinder.emptySquareSpace(level, livingEntity.blockPosition(), 6, true)){
            f2 = 0.55F;
        }
        projectile.shootFromRotation(livingEntity, -90.0F, 0.0F, 0.0F, f2, 12.0F);
        level.addFreshEntity(projectile);
    }

    public static void shoot(LivingEntity livingEntity, double p_37266_, double p_37267_, double p_37268_, float p_37269_, float p_37270_) {
        Vec3 vec3 = (new Vec3(p_37266_, p_37267_, p_37268_)).normalize().add(livingEntity.getRandom().triangle(0.0D, 0.0172275D * (double)p_37270_), livingEntity.getRandom().triangle(0.0D, 0.0172275D * (double)p_37270_), livingEntity.getRandom().triangle(0.0D, 0.0172275D * (double)p_37270_)).scale((double)p_37269_);
        livingEntity.setDeltaMovement(vec3);
    }

    public static int getPotentialBonusSpawns(Raid.RaiderType p_219829_, RandomSource p_219830_, int p_219831_, DifficultyInstance p_219832_, boolean p_219833_) {
        Difficulty difficulty = p_219832_.getDifficulty();
        boolean flag = difficulty == Difficulty.EASY;
        boolean flag1 = difficulty == Difficulty.NORMAL;
        int i;
        switch (p_219829_) {
            case WITCH -> {
                if (flag || p_219831_ <= 2 || p_219831_ == 4) {
                    return 0;
                }
                i = 1;
            }
            case PILLAGER, VINDICATOR -> {
                if (flag) {
                    i = p_219830_.nextInt(2);
                } else if (flag1) {
                    i = 1;
                } else {
                    i = 2;
                }
            }
            case RAVAGER -> i = !flag && p_219833_ ? 1 : 0;
            default -> {
                return 0;
            }
        }

        return i > 0 ? p_219830_.nextInt(i + 1) : 0;
    }

    public static boolean isFinalWave(Raid raid) {
        return raid.getGroupsSpawned() == raid.getNumGroups(raid.getLevel().getDifficulty());
    }

    public static boolean hasBonusWave(Raid raid) {
        return raid.getBadOmenLevel() > 1;
    }

    public static boolean shouldSpawnBonusGroup(Raid raid) {
        return isFinalWave(raid) && raid.getTotalRaidersAlive() == 0 && hasBonusWave(raid);
    }
}
