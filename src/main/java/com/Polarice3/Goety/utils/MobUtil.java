package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.BlastFungus;
import com.Polarice3.Goety.common.entities.projectiles.FireTornado;
import com.Polarice3.Goety.common.entities.projectiles.SnapFungus;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MobUtil {
    public static final Predicate<LivingEntity> NO_CREATIVE_OR_SPECTATOR = (p_200824_0_) -> {
        return !(p_200824_0_ instanceof Player) || !p_200824_0_.isSpectator() && !((Player)p_200824_0_).isCreative();
    };

    public static boolean isShifting(LivingEntity entityLiving){
        return entityLiving.isCrouching() || entityLiving.isShiftKeyDown();
    }

    public static boolean validEntity(Entity entity){
        if (entity instanceof Player player){
            return playerValidity(player, false);
        } else {
            return entity.isAttackable();
        }
    }

    public static boolean playerValidity(Player player, boolean isNotLich){
        if (!player.isCreative() && !player.isSpectator()) {
            if (isNotLich) {
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

    public static BlockHitResult rayTrace(Entity entity, double distance, boolean fluids) {
        return (BlockHitResult) entity.pick(distance, 1.0F, fluids);
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

    public static boolean isInWeb(LivingEntity livingEntity){
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
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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

    public static void dropFromLootTable(LivingEntity living, float luck) {
        ResourceLocation resourcelocation = living.getLootTable();
        LootTable loottable = living.level.getServer().getLootTables().get(resourcelocation);
        LootContext.Builder lootcontext$builder = MobUtil.createLootContext(DamageSource.GENERIC, living, luck);
        LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
        loottable.getRandomItems(ctx).forEach(living::spawnAtLocation);
    }

    public static LootContext.Builder createLootContext(DamageSource pDamageSource, LivingEntity livingEntity, float luck) {
        return (new LootContext.Builder((ServerLevel) livingEntity.level)).withRandom(livingEntity.getRandom()).withParameter(LootContextParams.THIS_ENTITY, livingEntity).withParameter(LootContextParams.ORIGIN, livingEntity.position()).withParameter(LootContextParams.DAMAGE_SOURCE, pDamageSource).withOptionalParameter(LootContextParams.KILLER_ENTITY, pDamageSource.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, pDamageSource.getDirectEntity()).withLuck(luck);
    }

    public static LootContext.Builder createLootContext(DamageSource pDamageSource, LivingEntity livingEntity) {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) livingEntity.level)).withRandom(livingEntity.getRandom()).withParameter(LootContextParams.THIS_ENTITY, livingEntity).withParameter(LootContextParams.ORIGIN, livingEntity.position()).withParameter(LootContextParams.DAMAGE_SOURCE, pDamageSource).withOptionalParameter(LootContextParams.KILLER_ENTITY, pDamageSource.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, pDamageSource.getDirectEntity());
        if (livingEntity.getLastHurtByMob() != null && livingEntity.getLastHurtByMob() instanceof Player player) {
            lootcontext$builder = lootcontext$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
        }

        return lootcontext$builder;
    }

    public static void knockBack(LivingEntity attacker, LivingEntity target, double xPower, double yPower, double zPower) {
        Vec3 vec3 = new Vec3(target.getX() - attacker.getX(), target.getY() - attacker.getY(), target.getZ() - attacker.getZ()).normalize();
        double pY0 = Math.max(-vec3.y, yPower);
        Vec3 vec31 = new Vec3(-vec3.x * xPower, pY0, -vec3.z * zPower);
        double resist = attacker.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double resist1 = Math.max(0.0D, 1.0D - resist);
        if (attacker instanceof Player player) {
            if (MobUtil.playerValidity(player, false)) {
                player.hurtMarked = true;
                if (!player.level.isClientSide){
                    player.setOnGround(false);
                }
            }
        }
        attacker.setDeltaMovement(attacker.getDeltaMovement().add(vec31).scale(resist1));
        attacker.hasImpulse = true;
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

    public static void twister(Entity pEntity, double pX, double pY, double pZ){
        pEntity.setDeltaMovement(pX, pY, pZ);
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
    public static List<Entity> getTargets(Level level, LivingEntity pSource, double pRange, double radius) {
        List<Entity> list = new ArrayList<>();
        double vectorY = pSource.getY();
        if (pSource instanceof Player){
            vectorY = pSource.getEyeY();
        }
        Vec3 srcVec = new Vec3(pSource.getX(), vectorY, pSource.getZ());
        Vec3 lookVec = pSource.getViewVector(1.0F);
        Vec3 rangeVec = new Vec3(lookVec.x * pRange, lookVec.y * pRange, lookVec.z * pRange);
        Vec3 end = srcVec.add(rangeVec);
        List<Entity> entities = level.getEntities(pSource, pSource.getBoundingBox().expandTowards(lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange).inflate(radius, radius, radius));
        double hitDist = 0.0D;

        for (Entity hit : entities) {
            if (hit.isPickable() && hit != pSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).test(hit)) {
                float borderSize = hit.getPickRadius();
                AABB collisionBB = hit.getBoundingBox().inflate(borderSize);
                Optional<Vec3> interceptPos = collisionBB.clip(srcVec, end);
                if (collisionBB.contains(srcVec)) {
                    if (0.0D <= hitDist) {
                        list.add(hit);
                        hitDist = 0.0D;
                    }
                } else if (interceptPos.isPresent()) {
                    double possibleDist = srcVec.distanceTo(interceptPos.get());

                    if (possibleDist < hitDist || hitDist == 0.0D) {
                        list.add(hit);
                        hitDist = possibleDist;
                    }
                }
            }
        }
        return list;
    }

    @Nullable
    public static Entity getSingleTarget(Level level, LivingEntity pSource, double pRange, double pRadius) {
        Entity target = null;
        Vec3 srcVec = pSource.getEyePosition();
        Vec3 lookVec = pSource.getViewVector(1.0F);
        double[] lookRange = new double[] {lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange};
        Vec3 destVec = srcVec.add(lookRange[0], lookRange[1], lookRange[2]);
        List<Entity> possibleList = level.getEntities(pSource, pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]).inflate(pRadius, pRadius, pRadius));
        double hitDist = 0;

        for (Entity hit : possibleList) {
            if (hit.isPickable() && hit != pSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).test(hit)) {
                float borderSize = hit.getPickRadius();
                AABB collisionBB = hit.getBoundingBox().inflate(borderSize, borderSize, borderSize);
                Optional<Vec3> interceptPos = collisionBB.clip(srcVec, destVec);

                if (collisionBB.contains(srcVec)) {
                    if (0.0D < hitDist || hitDist == 0.0D) {
                        target = hit;
                        hitDist = 0.0D;
                    }
                } else if (interceptPos.isPresent()) {
                    double possibleDist = srcVec.distanceTo(interceptPos.get());

                    if (possibleDist < hitDist || hitDist == 0.0D) {
                        target = hit;
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

    public static void explosionDamage(Level level, Entity source, DamageSource damageSource, double x, double y, double z, float radius) {
        explosionDamage(level, source, damageSource, x, y, z, radius, 0);
    }

    public static void explosionDamage(Level level, Entity source, DamageSource damageSource, BlockPos blockPos, float radius, float damage){
        explosionDamage(level, source, damageSource, blockPos.getX(), blockPos.getY(), blockPos.getZ(), radius, damage);
    }

    public static void explosionDamage(Level level, Entity source, DamageSource damageSource, double x, double y, double z, float radius, float damage){
        float f2 = radius * 2.0F;
        int k1 = Mth.floor(x - (double)f2 - 1.0D);
        int l1 = Mth.floor(x + (double)f2 + 1.0D);
        int i2 = Mth.floor(y - (double)f2 - 1.0D);
        int i1 = Mth.floor(y + (double)f2 + 1.0D);
        int j2 = Mth.floor(z - (double)f2 - 1.0D);
        int j1 = Mth.floor(z + (double)f2 + 1.0D);
        List<Entity> list = level.getEntities(source, new AABB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        Vec3 vec3 = new Vec3(x, y, z);
        for (Entity entity : list) {
            double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double) f2;
            if (d12 <= 1.0D) {
                double d5 = entity.getX() - x;
                double d7 = entity.getEyeY() - y;
                double d9 = entity.getZ() - z;
                double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                if (d13 != 0.0D) {
                    d5 /= d13;
                    d7 /= d13;
                    d9 /= d13;
                    double d14 = (double) getSeenPercent(vec3, entity);
                    double d10 = (1.0D - d12) * d14;
                    float actualDamage = damage == 0 ? (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D)) : damage;
                    entity.hurt(damageSource, actualDamage);
                    double d11 = d10;
                    if (entity instanceof LivingEntity) {
                        d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity) entity, d10);
                    }
                    if (damageSource.isMagic()){
                        if (entity instanceof FireTornado fireTornado){
                            fireTornado.trueRemove();
                        }
                    }

                    MobUtil.push(entity, d5 * d11, d7 * d11, d9 * d11);
                }
            }
        }
    }

    public static float getSeenPercent(Vec3 vector, Entity target) {
        AABB aabb = target.getBoundingBox();
        double d0 = 1.0D / ((aabb.maxX - aabb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((aabb.maxY - aabb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((aabb.maxZ - aabb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
        if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
            int i = 0;
            int j = 0;

            for(double d5 = 0.0D; d5 <= 1.0D; d5 += d0) {
                for(double d6 = 0.0D; d6 <= 1.0D; d6 += d1) {
                    for(double d7 = 0.0D; d7 <= 1.0D; d7 += d2) {
                        double d8 = Mth.lerp(d5, aabb.minX, aabb.maxX);
                        double d9 = Mth.lerp(d6, aabb.minY, aabb.maxY);
                        double d10 = Mth.lerp(d7, aabb.minZ, aabb.maxZ);
                        Vec3 vec3 = new Vec3(d8 + d3, d9, d10 + d4);
                        if (target.level.clip(new ClipContext(vec3, vector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target)).getType() == HitResult.Type.MISS) {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float)i / (float)j;
        } else {
            return 0.0F;
        }
    }

    public static boolean canPositionBeSeen(Level level, LivingEntity living, double x, double y, double z) {
        HitResult result = level.clip(new ClipContext(new Vec3(living.getX(), living.getY() + (double) living.getEyeHeight(), living.getZ()), new Vec3(x, y, z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, living));
        double dist = result.getLocation().distanceToSqr(x, y, z);
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }

    /**
     * Copy of Vanilla's getEquipmentDropChance. Had to accesstransformer handDropChances and armorDropChances
     */
    public static float getEquipmentDropChance(Mob mob, EquipmentSlot p_21520_) {
        float f;
        switch (p_21520_.getType()) {
            case HAND:
                f = mob.handDropChances[p_21520_.getIndex()];
                break;
            case ARMOR:
                f = mob.armorDropChances[p_21520_.getIndex()];
                break;
            default:
                f = 0.0F;
        }

        return f;
    }

    /**
     * Copy of Vanilla's Mob convertTo to be able to accept Entity class instead of just Mob class.
     */
    @Nullable
    public static Entity convertTo(Entity originalEntity, EntityType<?> convertedType, boolean loot, Player player) {
        if (originalEntity.isRemoved()) {
            return null;
        } else {
            Entity newEntity = convertedType.create(originalEntity.level);
            if (newEntity != null) {
                newEntity.copyPosition(originalEntity);
                if (originalEntity instanceof Mob originalMob && newEntity instanceof Mob newMob) {
                    newMob.setBaby(originalMob.isBaby());
                    newMob.setNoAi(originalMob.isNoAi());
                    if (originalMob.hasCustomName()) {
                        newEntity.setCustomName(originalMob.getCustomName());
                        newEntity.setCustomNameVisible(originalMob.isCustomNameVisible());
                    }

                    if (originalMob.isPersistenceRequired()) {
                        newMob.setPersistenceRequired();
                    }

                    newMob.setInvulnerable(originalMob.isInvulnerable());
                    if (loot) {
                        newMob.setCanPickUpLoot(originalMob.canPickUpLoot());

                        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                            ItemStack itemstack = originalMob.getItemBySlot(equipmentslot);
                            if (!itemstack.isEmpty()) {
                                newMob.setItemSlot(equipmentslot, itemstack.copy());
                                newMob.setDropChance(equipmentslot, getEquipmentDropChance(originalMob, equipmentslot));
                                itemstack.setCount(0);
                            }
                        }
                    }
                    if (player != null){
                        summonTame(newMob, player);
                    }

                    if (originalMob.level instanceof ServerLevel serverLevel) {
                        if (originalMob instanceof Villager villager && newMob instanceof ZombieVillager zombievillager) {
                            zombievillager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), (CompoundTag) null);
                            zombievillager.setVillagerData(villager.getVillagerData());
                            zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE).getValue());
                            zombievillager.setTradeOffers(villager.getOffers().createTag());
                            zombievillager.setVillagerXp(villager.getVillagerXp());
                            if (!originalMob.isSilent()) {
                                serverLevel.levelEvent((Player)null, 1026, originalMob.blockPosition(), 0);
                            }
                        }
                    }

                    originalMob.level.addFreshEntity(newEntity);
                    if (originalMob.isPassenger()) {
                        Entity entity = originalMob.getVehicle();
                        if (entity != null) {
                            originalMob.stopRiding();
                            newEntity.startRiding(entity, true);
                        }
                    }
                }

                originalEntity.discard();
            }
            return newEntity;
        }
    }

    public static void summonTame(Entity entity, Player player){
        if (entity instanceof TamableAnimal tamableAnimal){
            tamableAnimal.tame(player);
        } else if (entity instanceof AbstractHorse horse){
            horse.setTamed(true);
            horse.setOwnerUUID(player.getUUID());
        } else if (entity instanceof Owned summonedEntity) {
            summonedEntity.setPersistenceRequired();
            summonedEntity.setOwnerId(player.getUUID());
            if (summonedEntity instanceof Summoned summoned){
                summoned.setWandering(false);
            }
        }
    }

    public static void explodeCreeper(Creeper creeper) {
        if (!creeper.level.isClientSide) {
            Explosion.BlockInteraction explosion$blockinteraction = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(creeper.level, creeper) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            float f = creeper.isPowered() ? 2.0F : 1.0F;
            creeper.level.explode(creeper, creeper.getX(), creeper.getY(), creeper.getZ(), 3.0F * f, explosion$blockinteraction);
            creeper.discard();
            spawnLingeringCloud(creeper);
        }
    }

    public static void spawnLingeringCloud(Creeper creeper) {
        Collection<MobEffectInstance> collection = creeper.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(creeper.level, creeper.getX(), creeper.getY(), creeper.getZ());
            areaeffectcloud.setRadius(2.5F);
            areaeffectcloud.setRadiusOnUse(-0.5F);
            areaeffectcloud.setWaitTime(10);
            areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
            areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());

            for(MobEffectInstance mobeffectinstance : collection) {
                areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
            }

            creeper.level.addFreshEntity(areaeffectcloud);
        }

    }

    public static boolean hasNegativeEffects(LivingEntity livingEntity){
        return !livingEntity.getActiveEffects().isEmpty() && livingEntity.getActiveEffects().stream().anyMatch((mobEffectInstance2 -> mobEffectInstance2.getEffect().getCategory() == MobEffectCategory.HARMFUL));
    }

    public static boolean hasLongNegativeEffects(LivingEntity livingEntity){
        return !livingEntity.getActiveEffects().isEmpty() && livingEntity.getActiveEffects().stream().anyMatch((mobEffectInstance2 -> mobEffectInstance2.getEffect().getCategory() == MobEffectCategory.HARMFUL && mobEffectInstance2.getDuration() > MathHelper.secondsToTicks(5)));
    }

    public static boolean isMoving(LivingEntity livingEntity){
        return livingEntity.isOnGround() && livingEntity.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F;
    }

    public static boolean hasVisualLineOfSight(LivingEntity looker, Entity target) {
        if (target.level != looker.level) {
            return false;
        } else {
            Vec3 vec3 = new Vec3(looker.getX(), looker.getEyeY(), looker.getZ());
            Vec3 vec31 = new Vec3(target.getX(), target.getEyeY(), target.getZ());
            if (vec31.distanceTo(vec3) > 128.0D) {
                return false;
            } else {
                return looker.level.clip(new ClipContext(vec3, vec31, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, looker)).getType() == HitResult.Type.MISS;
            }
        }
    }

    public static boolean isPushed(LivingEntity livingEntity){
        List<Entity> list = livingEntity.level.getEntities(livingEntity, livingEntity.getBoundingBox(), EntitySelector.pushableBy(livingEntity));
        return !list.isEmpty() && livingEntity.isPushable();
    }

    public static boolean isInSunlight(LivingEntity livingEntity){
        float f = livingEntity.getLightLevelDependentMagicValue();
        BlockPos blockpos = livingEntity.getVehicle() instanceof Boat ? (new BlockPos(livingEntity.getX(), (double) Math.round(livingEntity.getY()), livingEntity.getZ())).above() : new BlockPos(livingEntity.getX(), (double) Math.round(livingEntity.getY()), livingEntity.getZ());
        return f > 0.5F && livingEntity.level.canSeeSky(blockpos);
    }
}
