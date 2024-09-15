package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.entities.hostile.cultists.Warlock;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.BlastFungus;
import com.Polarice3.Goety.common.entities.projectiles.SnapFungus;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static final Predicate<Entity> LIVING_OR_PART = (entity) -> {
        return entity.isAlive() && (entity instanceof LivingEntity || entity instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity);
    };

    public static boolean isShifting(Entity entity){
        return entity.isCrouching() || entity.isShiftKeyDown();
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

    public static boolean areAllies(@Nullable Entity entity, @Nullable Entity entity1){
        if (entity != null && entity1 != null) {
            return entity.isAlliedTo(entity1) || entity1.isAlliedTo(entity) || entity == entity1
                    || (entity instanceof Player player && entity1 instanceof LivingEntity living
                        && (SEHelper.getAllyEntities(player).contains(living)
                        || SEHelper.getAllyEntityTypes(player).contains(living.getType())))
                    || (entity1 instanceof Player player1 && entity instanceof LivingEntity living1
                        && (SEHelper.getAllyEntities(player1).contains(living1)
                        || SEHelper.getAllyEntityTypes(player1).contains(living1.getType())));
        } else {
            return false;
        }
    }

    public static boolean areNotFullAllies(@Nullable Entity entity, @Nullable Entity entity1){
        if (entity != null && entity1 != null) {
            return !entity.isAlliedTo(entity1) || !entity1.isAlliedTo(entity)
                    || (entity instanceof Player player && entity1 instanceof LivingEntity living
                    && (!SEHelper.getAllyEntities(player).contains(living) && !SEHelper.getAllyEntityTypes(player).contains(living.getType())))
                    || (entity1 instanceof Player player1 && entity instanceof LivingEntity living1
                    && (!SEHelper.getAllyEntities(player1).contains(living1) && !SEHelper.getAllyEntityTypes(player1).contains(living1.getType())));
        } else {
            return true;
        }
    }

    public static boolean sameDimension(@Nullable Entity entity1, @Nullable Entity entity2){
        if (entity1 == null || entity2 == null){
            return false;
        }
        return entity1.level.dimension() == entity2.level.dimension();
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

    public static List<BlockState> surroundingBlocks(LivingEntity livingEntity, Predicate<BlockState> blockPredicate){
        List<BlockState> blockStates = new ArrayList<>();
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
                        if (blockPredicate.test(blockstate)){
                            blockStates.add(blockstate);
                        }
                    }
                }
            }
        }
        return blockStates;
    }

    public static boolean isInBlock(LivingEntity livingEntity, Predicate<BlockState> blockPredicate){
        return !surroundingBlocks(livingEntity, blockPredicate).isEmpty();
    }

    public static boolean isInWeb(LivingEntity livingEntity){
        return isInBlock(livingEntity, blockState -> blockState.getBlock() instanceof WebBlock);
    }

    public static void WebMovement(LivingEntity livingEntity){
        for (BlockState blockState : surroundingBlocks(livingEntity, blockState -> blockState.getBlock() instanceof WebBlock)){
            livingEntity.makeStuckInBlock(blockState, Vec3.ZERO);
        }
    }

    public static void PowderedSnowMovement(LivingEntity livingEntity){
        for (BlockState blockState : surroundingBlocks(livingEntity, blockState -> blockState.getBlock() instanceof PowderSnowBlock)){
            livingEntity.makeStuckInBlock(blockState, Vec3.ZERO);
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

    public static void knockBack(LivingEntity knocked, Entity knocker, double xPower, double yPower, double zPower) {
        Vec3 vec3 = new Vec3(knocker.getX() - knocked.getX(), knocker.getY() - knocked.getY(), knocker.getZ() - knocked.getZ()).normalize();
        double pY0 = Math.max(-vec3.y, yPower);
        Vec3 vec31 = new Vec3(-vec3.x * xPower, pY0, -vec3.z * zPower);
        double resist = knocked.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double resist1 = Math.max(0.0D, 1.0D - resist);
        if (knocked instanceof Player player) {
            if (MobUtil.playerValidity(player, false)) {
                player.hurtMarked = true;
                if (!player.level.isClientSide){
                    player.setOnGround(false);
                }
            }
        }
        knocked.setDeltaMovement(knocked.getDeltaMovement().add(vec31).scale(resist1));
        knocked.hasImpulse = true;
    }

    public static void forcefulKnockBack(LivingEntity knocked, double strength, double ratioX, double ratioZ, double reduction) {
        net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(knocked, (float) strength, ratioX, ratioZ);
        if(event.isCanceled()) return;
        strength = event.getStrength();
        ratioX = event.getRatioX();
        ratioZ = event.getRatioZ();
        strength *= 1.0D - (knocked.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) * reduction);
        if (!(strength <= 0.0D)) {
            knocked.hasImpulse = true;
            Vec3 vec3 = knocked.getDeltaMovement();
            Vec3 vec31 = (new Vec3(ratioX, 0.0D, ratioZ)).normalize().scale(strength);
            knocked.setDeltaMovement(vec3.x / 2.0D - vec31.x, knocked.isOnGround() ? Math.min(0.4D, vec3.y / 2.0D + strength) : vec3.y, vec3.z / 2.0D - vec31.z);
        }
    }

    public static void pull(Entity pEntity, double pX, double pY, double pZ){
        push(pEntity, -pX, -pY, -pZ);
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
        double resist = 0.0D;
        if (pEntity instanceof LivingEntity living) {
            resist = living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        }
        double resist1 = Math.max(0.0D, 1.0D - resist);
        pEntity.setDeltaMovement(pEntity.getDeltaMovement().add(pX, pY, pZ).scale(resist1));
        pEntity.hasImpulse = true;
    }

    public static void forcePush(Entity pEntity, double pX, double pY, double pZ) {
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

    public static List<EntityType<?>> getEntityTypesConfig(List<? extends String> config){
        List<EntityType<?>> list = new ArrayList<>();
        if (!config.isEmpty()){
            for (String id : config){
                EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
                if (entityType != null){
                    list.add(entityType);
                }
            }
        }
        return list;
    }

    public static boolean hasEntityTypesConfig(List<? extends String> config, EntityType<?> entityType){
        return !getEntityTypesConfig(config).isEmpty() && getEntityTypesConfig(config).contains(entityType);
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

    public static boolean starAmuletActive(LivingEntity livingEntity){
        return CuriosFinder.hasCurio(livingEntity, ModItems.STAR_AMULET.get()) && MobUtil.healthIsHalved(livingEntity);
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
    public static List<Entity> getTargets(Level level, LivingEntity pSource, double pRange, double pRadius) {
        return getTargets(level, pSource, pRange, pRadius, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(LIVING_OR_PART).and(entity -> !MobUtil.areAllies(entity, pSource)));
    }

    public static List<Entity> getTargets(Level level, LivingEntity pSource, double pRange, double pRadius, Predicate<? super Entity> predicate) {
        List<Entity> list = new ArrayList<>();
        Vec3 srcVec = pSource.getEyePosition();
        Vec3 lookVec = pSource.getViewVector(1.0F);
        double[] lookRange = new double[] {lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange};
        Vec3 destVec = srcVec.add(lookRange[0], lookRange[1], lookRange[2]);
        List<Entity> possibleList = level.getEntities(pSource, pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]).inflate(pRadius, pRadius, pRadius),
                predicate);
        double hitDist = 0.0D;

        for (Entity hit : possibleList) {
            if (hit.isPickable() && pSource.hasLineOfSight(hit) && hit != pSource) {
                float maxSize = pSource instanceof Mob ? 2.0F : 0.8F;
                float borderSize = Math.max(maxSize, hit.getPickRadius());
                AABB collisionBB = hit.getBoundingBox().inflate(borderSize);
                Optional<Vec3> interceptPos = collisionBB.clip(srcVec, destVec);
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
    public static Entity getSingleTarget(Level pLevel, LivingEntity pSource, double pRange, double pRadius) {
        return getSingleTarget(pLevel, pSource, pRange, pRadius, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(LIVING_OR_PART).and(entity -> !MobUtil.areAllies(entity, pSource) && entity.isPickable()));
    }

    @Nullable
    public static Entity getSingleTarget(Level pLevel, LivingEntity pSource, double pRange, double pRadius, Predicate<? super Entity> predicate) {
        Entity target = null;
        Vec3 srcVec = pSource.getEyePosition();
        Vec3 lookVec = pSource.getViewVector(1.0F);
        double[] lookRange = new double[] {lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange};
        Vec3 destVec = srcVec.add(lookRange[0], lookRange[1], lookRange[2]);
        List<Entity> possibleList = pLevel.getEntities(pSource, pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]).inflate(pRadius, pRadius, pRadius),
                predicate);
        double hitDist = 0.0D;

        for (Entity hit : possibleList) {
            if (pSource.hasLineOfSight(hit) && hit != pSource) {
                float borderSize = Math.max(0.8F, hit.getPickRadius());
                AABB collisionBB = hit.getBoundingBox().inflate(borderSize, borderSize, borderSize);
                Optional<Vec3> interceptPos = collisionBB.clip(srcVec, destVec);

                if (collisionBB.contains(srcVec)) {
                    if (0.0D <= hitDist) {
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

    @Nullable
    public static Entity getNearbyTarget(Level pLevel, LivingEntity pSource, double pRange, double pRadius) {
        Entity target = null;
        if (getSingleTarget(pLevel, pSource, pRange, pRadius) != null){
            target = getSingleTarget(pLevel, pSource, pRange, pRadius);
        } else {
            Vec3 lookVec = pSource.getViewVector(1.0F);
            double[] lookRange = new double[] {lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange};
            List<Entity> possibleList = pLevel.getEntities(pSource, pSource.getBoundingBox().move(lookVec.x / 2, 0, lookVec.z / 2).expandTowards(lookRange[0], lookRange[1], lookRange[2]).inflate(pRadius, pRadius, pRadius),
                    EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(LIVING_OR_PART).and(entity -> !MobUtil.areAllies(entity, pSource)));

            for (Entity hit : possibleList) {
                if (hit.isPickable() && pSource.hasLineOfSight(hit)) {
                    target = hit;
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

    public static void moveUpFromGround(Entity entity, int distance) {
        HitResult rayTrace = rayTraceToAir(entity, distance);
        if (rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) rayTrace;
            if (hitResult.getDirection() == Direction.DOWN) {
                entity.setPos(entity.getX(), hitResult.getBlockPos().getY() - 1.0625F, entity.getZ());
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

    private static HitResult rayTraceToAir(Entity entity, int distance) {
        Vec3 startPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        Vec3 endPos = new Vec3(entity.getX(), entity.getY() + distance, entity.getZ());
        return entity.level.clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
    }

    public static void throwSnapFungus(LivingEntity livingEntity, Level level){
        SnapFungus blastFungus = new SnapFungus(livingEntity, level);
        throwFungus(blastFungus, livingEntity);
    }

    public static void throwBlastFungus(LivingEntity livingEntity, Level level){
        BlastFungus blastFungus = new BlastFungus(livingEntity, level);
        throwFungus(blastFungus, livingEntity);
    }

    public static void throwFungus(Projectile projectile, LivingEntity livingEntity){
        shootUp(projectile, livingEntity);
    }

    public static float ceilingVelocity(Entity entity){
        return ceilingVelocity(entity, 0.75F);
    }

    public static float ceilingVelocity(Entity entity, float initialV){
        float f2 = 0.35F;
        if (BlockFinder.emptySquareSpace(entity.level, entity.blockPosition(), 13, true)){
            f2 = initialV;
        } else if (BlockFinder.emptySquareSpace(entity.level, entity.blockPosition(), 6, true)){
            f2 = 0.55F;
        }
        return f2;
    }

    public static void shootUp(Projectile projectile, Entity entity){
        shootUp(projectile, entity, ceilingVelocity(entity));
    }

    public static void shootUp(Projectile projectile, Entity entity, float velocity){
        projectile.shootFromRotation(entity, -90.0F, 0.0F, 0.0F, velocity, 12.0F);
        entity.level.addFreshEntity(projectile);
    }

    public static void shootUp(Projectile projectile, float velocity){
        shootFromRotation(projectile, -90.0F, 0.0F, 0.0F, velocity, 12.0F);
        projectile.level.addFreshEntity(projectile);
    }

    public static void shoot(Entity entity, double p_37266_, double p_37267_, double p_37268_, float p_37269_, float p_37270_) {
        Vec3 vec3 = (new Vec3(p_37266_, p_37267_, p_37268_)).normalize().add(entity.level.random.triangle(0.0D, 0.0172275D * (double)p_37270_), entity.level.random.triangle(0.0D, 0.0172275D * (double)p_37270_), entity.level.random.triangle(0.0D, 0.0172275D * (double)p_37270_)).scale((double)p_37269_);
        entity.setDeltaMovement(vec3);
    }

    public static void shootFromRotation(Projectile projectile, float p_37253_, float p_37254_, float p_37255_, float p_37256_, float p_37257_) {
        float f = -Mth.sin(p_37254_ * ((float)Math.PI / 180F)) * Mth.cos(p_37253_ * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((p_37253_ + p_37255_) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(p_37254_ * ((float)Math.PI / 180F)) * Mth.cos(p_37253_ * ((float)Math.PI / 180F));
        projectile.shoot((double)f, (double)f1, (double)f2, p_37256_, p_37257_);
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

    public static void explosionDamage(Level level, Entity source, DamageSource damageSource, BlockPos blockPos, float radius, float damage){
        explosionDamage(level, source, damageSource, blockPos.getX(), blockPos.getY(), blockPos.getZ(), radius, damage);
    }

    public static void explosionDamage(Level level, Entity source, DamageSource damageSource, double x, double y, double z, float radius, float damage){
        new SpellExplosion(level, source, damageSource, x, y, z, radius, damage);
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
        return convertTo(originalEntity, convertedType, loot, false, player);
    }

    @Nullable
    public static Entity convertTo(Entity originalEntity, EntityType<?> convertedType, boolean loot, boolean newEquip, Player player) {
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
                        } else if (newEquip && newMob instanceof IOwned owned){
                            owned.convertNewEquipment(originalEntity);
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
        } else if (entity instanceof IOwned summonedEntity && entity instanceof Mob mob) {
            mob.setPersistenceRequired();
            summonedEntity.setTrueOwner(player);
            if (summonedEntity instanceof IServant summoned){
                summoned.setWandering(false);
            }
            if (summonedEntity instanceof Summoned summoned){
                summoned.spawnUpgraded();
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

    public static boolean isInBrightLight(LivingEntity livingEntity){
        float f = livingEntity.getLightLevelDependentMagicValue();
        return f >= 0.5F;
    }

    public static boolean isInSunlight(LivingEntity livingEntity){
        if (livingEntity.level.isDay() && !livingEntity.level.isClientSide) {
            float f = livingEntity.getLightLevelDependentMagicValue();
            BlockPos blockpos = new BlockPos(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ());
            boolean flag = livingEntity.isInWaterRainOrBubble() || livingEntity.isInPowderSnow || livingEntity.wasInPowderSnow;
            return f > 0.5F && livingEntity.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && livingEntity.level.canSeeSky(blockpos);
        }

        return false;
    }

    public static boolean noSunlight(LivingEntity livingEntity){
        return !isInSunlight(livingEntity) || livingEntity.level.isRaining();
    }

    /**
     * Mind Bending, lol.
     */
    public static boolean ownerStack(IOwned owned0, IOwned owned1){
        LivingEntity masterOwner0 = owned0.getMasterOwner();
        LivingEntity masterOwner1 = owned1.getMasterOwner();
        LivingEntity trueOwner0 = owned0.getTrueOwner();
        LivingEntity trueOwner1 = owned1.getTrueOwner();
        if (trueOwner0 != null && trueOwner1 != null){
            if (masterOwner0 != null && masterOwner1 != null){
                return masterOwner0 == masterOwner1;
            } else if (masterOwner0 != null){
                return masterOwner0 == trueOwner1;
            } else if (masterOwner1 != null){
                return masterOwner1 == trueOwner0;
            } else {
                return trueOwner0 == trueOwner1;
            }
        }
        return false;
    }

    public static boolean isSpellCasting(LivingEntity livingEntity){
        return livingEntity.isUsingItem() && livingEntity.getUseItem().getItem() instanceof IWand && !WandUtil.findFocus(livingEntity).isEmpty();
    }

    public static void instaLook(Mob mob, Vec3 vec3){
        mob.getLookControl().setLookAt(vec3.x, vec3.y, vec3.z, 200.0F, mob.getMaxHeadXRot());
        double d2 = vec3.x - mob.getX();
        double d1 = vec3.z - mob.getZ();
        mob.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
        mob.yBodyRot = mob.getYRot();
    }

    public static void instaLook(Mob looker, Entity target){
        looker.getLookControl().setLookAt(target, 200.0F, looker.getMaxHeadXRot());
        double d2 = target.getX() - looker.getX();
        double d1 = target.getZ() - looker.getZ();
        looker.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
        looker.yBodyRot = looker.getYRot();
    }

    public static void rotateTo(Mob looker, LivingEntity target){
        rotateTo(looker, target, 90.0F);
    }

    public static void rotateTo(Mob looker, LivingEntity target, float speed){
        double d2 = target.getX() - looker.getX();
        double d1 = target.getZ() - looker.getZ();
        float rotate = -((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI);
        looker.setYRot(rotLerp(looker.getYRot(), rotate, speed));
        looker.yBodyRot = looker.getYRot();
        looker.yHeadRotO = looker.getYRot();
        looker.yHeadRot = looker.getYRot();
    }

    public static float rotLerp(float start, float end, float speed) {
        float f = Mth.wrapDegrees(end - start);
        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return start + f;
    }

    public static ItemStack createFirework(int explosions, DyeColor[] dyeColor) {
        ItemStack firework = new ItemStack(Items.FIREWORK_ROCKET);
        ItemStack star = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag starTag = star.getOrCreateTagElement("Explosion");
        starTag.putInt("Type", FireworkRocketItem.Shape.BURST.getId());
        CompoundTag fireworkTag = firework.getOrCreateTagElement("Fireworks");
        ListTag explosionTags = new ListTag();
        CompoundTag starExplosionTag = star.getTagElement("Explosion");
        if (starExplosionTag != null) {
            List<Integer> colorList = Lists.newArrayList();
            for (DyeColor color : dyeColor) {
                int pinkFireworkColor = color.getFireworkColor();
                colorList.add(pinkFireworkColor);
            }
            starExplosionTag.putIntArray("Colors", colorList);
            starExplosionTag.putIntArray("FadeColors", colorList);
            for (int i = 0; i < explosions; i++) {
                explosionTags.add(starExplosionTag);
            }
        }
        if (!explosionTags.isEmpty()) {
            fireworkTag.put("Explosions", explosionTags);
        }
        return firework;
    }

    public static void hurtUsedShield(LivingEntity living, float p_36383_) {
        if (living.getUseItem().canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK)) {
            if (!living.level.isClientSide && living instanceof ServerPlayer player) {
                player.awardStat(Stats.ITEM_USED.get(player.getUseItem().getItem()));
            }

            if (p_36383_ >= 3.0F) {
                int i = 1 + Mth.floor(p_36383_);
                InteractionHand interactionhand = living.getUsedItemHand();
                living.getUseItem().hurtAndBreak(i, living, (p_219739_) -> {
                    p_219739_.broadcastBreakEvent(interactionhand);
                    if (living instanceof Player player) {
                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, living.getUseItem(), interactionhand);
                    }
                });
                if (living.getUseItem().isEmpty()) {
                    if (interactionhand == InteractionHand.MAIN_HAND) {
                        living.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        living.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                    living.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + living.level.random.nextFloat() * 0.4F);
                }
            }

        }
    }

    public static void disableShield(LivingEntity livingEntity, int ticks) {
        if (livingEntity instanceof Player player && player.isBlocking()) {
            player.getCooldowns().addCooldown(player.getUseItem().getItem(), ticks);
            player.stopUsingItem();
            player.level.broadcastEntityEvent(player, (byte)30);
        }
    }

    public static boolean canAttack(LivingEntity attacker, LivingEntity target){
        return !attacker.isAlliedTo(target) && !target.isAlliedTo(attacker) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target) && attacker.canAttack(target);
    }

    public static boolean mobCanAttack(Mob attacker, LivingEntity target){
        if (attacker.getTarget() == target){
            return EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target) && attacker.canAttack(target);
        }
        return canAttack(attacker, target);
    }

    public static boolean ownedCanAttack(Owned attacker, LivingEntity target){
        if (attacker.getTrueOwner() != null){
            LivingEntity owner = attacker.getTrueOwner();
            if (owner instanceof Mob mob){
                return mobCanAttack(mob, target);
            } else {
                return canAttack(owner, target);
            }
        }
        return mobCanAttack(attacker, target);
    }

    public static void sweepAttack(LivingEntity attacker, Entity target, DamageSource damageSource, float damage){
        sweepAttack(attacker, target, damageSource, 1.0D, damage);
    }

    public static void sweepAttack(LivingEntity attacker, Entity target, DamageSource damageSource, double radius, float damage){
        for(LivingEntity livingentity : attacker.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(radius, 0.25D, radius))) {
            if (livingentity != attacker && livingentity != target && !attacker.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker()) && attacker.canAttack(livingentity)) {
                livingentity.knockback((double)0.4F, (double)Mth.sin(attacker.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(attacker.getYRot() * ((float)Math.PI / 180F))));
                livingentity.hurt(damageSource, damage);
            }
        }
    }

    /**
     * Code based of @BobMowzies sweep codes. From Here
     */
    public static List<LivingEntity> getAttackableLivingEntitiesNearby(LivingEntity source, double distanceX, double distanceY, double distanceZ, double radius) {
        return getLivingEntitiesNearby(source, distanceX, distanceY, distanceZ, radius).stream().filter(target -> canAttack(source, target)).toList();
    }

    public static List<LivingEntity> getLivingEntitiesNearby(Entity source, double distanceX, double distanceY, double distanceZ, double radius) {
        return getEntitiesNearby(source, LivingEntity.class, distanceX, distanceY, distanceZ, radius);
    }

    public static <T extends Entity> List<T> getEntitiesNearby(Entity source, Class<T> entityClass, double dX, double dY, double dZ, double radius) {
        return source.level.getEntitiesOfClass(entityClass, source.getBoundingBox().inflate(dX, dY, dZ), target -> target != source && source.distanceTo(target) <= radius + target.getBbWidth() / 2.0F && target.getY() <= (source.getY() + dY));
    }
    /**
     * To Here
    */

    public static WeightedRandomList<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel p_220444_, StructureManager p_220445_, ChunkGenerator p_220446_, MobCategory p_220447_, BlockPos p_220448_, @Nullable Holder<Biome> p_220449_) {
        return net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(p_220444_, p_220447_, p_220448_, NaturalSpawner.isInNetherFortressBounds(p_220448_, p_220444_, p_220447_, p_220445_) ? p_220445_.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).getOrThrow(BuiltinStructures.FORTRESS).spawnOverrides().get(MobCategory.MONSTER).spawns() : p_220446_.getMobsAt(p_220449_ != null ? p_220449_ : p_220444_.getBiome(p_220448_), p_220445_, p_220447_, p_220448_));
    }

    public static Vec3 calculateViewVector(float p_20172_, float p_20173_) {
        float f = p_20172_ * ((float)Math.PI / 180F);
        float f1 = -p_20173_ * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }

    public static Vec3 getHorizontalLeftLookAngle(Entity entity) {
        return MobUtil.calculateViewVector(0, entity.getYRot() - 90);
    }

    public static Vec3 getHorizontalRightLookAngle(Entity entity) {
        return MobUtil.calculateViewVector(0, entity.getYRot() + 90);
    }

    public static Vec3 getHorizontalLookAngle(Entity entity) {
        return MobUtil.calculateViewVector(0, entity.getYRot());
    }

    public static void setRot(Entity entity, float p_19916_, float p_19917_) {
        entity.setYRot(p_19916_ % 360.0F);
        entity.setXRot(p_19917_ % 360.0F);
    }

    /**
     * Based on Dweller code by Gargin: <a href="https://github.com/maow-tty/cave-dweller-decompiled/blob/master/src/main/java/com/gargin/cavenoise/entity/custom/DwellerStareGoal.java#L118">...</a>
     */
    public static boolean isPlayerLookingTowards(Player player, float fov, Mob mob) {
        boolean yawPlayerLookingTowards = false;
        float yFovMod = 0.65F;
        float fovMod = (35.0F / fov - 1.0F) * 0.4F + 1.0F;
        fov *= fovMod;
        Vec3 a = player.position();
        Vec3 b = mob.position();
        Vec2 dist = new Vec2((float)b.x - (float)a.x, (float)b.z - (float)a.z);
        dist = dist.normalized();
        double newAngle = Math.toDegrees(Math.atan2(dist.x, dist.y));
        float lookX = (float)player.getViewVector(1.0F).x;
        float lookZ = (float)player.getViewVector(1.0F).z;
        double newLookAngle = Math.toDegrees(Math.atan2(lookX, lookZ));
        double newNewAngle = loopAngle(newAngle - newLookAngle) + (double)fov;
        newNewAngle = loopAngle(newNewAngle);
        if (newNewAngle > 0.0 && newNewAngle < (double)(fov * 2.0F)) {
            yawPlayerLookingTowards = true;
        }

        boolean pitchPlayerLookingTowards = false;
        boolean shouldOnlyUsePitch = false;
        float yFov = fov * yFovMod;
        Vec2 yDist = new Vec2((float)Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.z - a.z) * (b.z - a.z)), (float)(b.y - a.y));
        yDist = yDist.normalized();
        double yAngle = Math.toDegrees(Math.atan2(yDist.x, yDist.y));
        float lookY = (float)player.getViewVector(1.0F).y;
        Vec2 lookDist = new Vec2((float)Math.sqrt(lookX * lookX + lookZ * lookZ), lookY);
        lookDist = lookDist.normalized();
        double yLookAngle = Math.toDegrees(Math.atan2(lookDist.x, lookDist.y));
        double newYAngle = loopAngle(yAngle - yLookAngle) + (double)yFov;
        newYAngle = loopAngle(newYAngle);
        if (newYAngle > 0.0 && newYAngle < (double)(yFov * 2.0F)) {
            pitchPlayerLookingTowards = true;
        }

        if (!(yLookAngle < (double)(180.0F - yFov)) || !(yLookAngle > (double)yFov)) {
            shouldOnlyUsePitch = true;
        }

        return (yawPlayerLookingTowards || shouldOnlyUsePitch) && pitchPlayerLookingTowards;
    }

    public static double loopAngle(double angle) {
        if (angle > 360.0) {
            return angle - 360.0;
        } else {
            return angle < 0.0 ? angle + 360.0 : angle;
        }
    }

    public static void setBaseAttributes(AttributeInstance attribute, double value){
        if (attribute != null){
            attribute.setBaseValue(value);
        }
    }

    public static double getAttributeValue(LivingEntity livingEntity, Attribute attribute){
        return getAttributeValue(livingEntity, attribute, 0.0D);
    }

    public static double getAttributeValue(LivingEntity livingEntity, Attribute attribute, double nullCheck){
        if (livingEntity.getAttribute(attribute) != null){
            return livingEntity.getAttributeValue(attribute);
        } else {
            return nullCheck;
        }
    }

    public void circleEntity(Mob mob, Entity target, float radius, float speed, boolean direction, int circleFrame, float offset, float moveSpeedMultiplier) {
        int directionInt = direction ? 1 : -1;
        double t = (double)(directionInt * circleFrame) * 0.5 * (double)speed / (double)radius + (double)offset;
        Vec3 movePos = target.position().add((double)radius * Math.cos(t), 0.0, (double)radius * Math.sin(t));
        mob.getNavigation().moveTo(movePos.x, movePos.y, movePos.z, (double)(speed * moveSpeedMultiplier));
    }

    //Enderman Teleport
    public static boolean teleport(LivingEntity livingEntity) {
        return teleport(livingEntity, 0);
    }

    public static boolean teleport(LivingEntity livingEntity, int level) {
        return teleport(livingEntity, 64, level);
    }

    public static boolean teleport(LivingEntity livingEntity, int initialDistance, int level) {
        int distance = initialDistance + (level * 2);
        if (!livingEntity.level.isClientSide() && livingEntity.isAlive()) {
            double d0 = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5D) * distance;
            double d1 = livingEntity.getY() + (double)(livingEntity.getRandom().nextInt(distance) - (distance / 2));
            double d2 = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5D) * distance;
            return teleport(livingEntity, d0, d1, d2);
        } else {
            return false;
        }
    }

    public static boolean teleportTowards(LivingEntity livingEntity, Entity target) {
        Vec3 vec3 = new Vec3(livingEntity.getX() - target.getX(), livingEntity.getY(0.5D) - target.getEyeY(), livingEntity.getZ() - target.getZ());
        vec3 = vec3.normalize();
        double d0 = 16.0D;
        double d1 = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5D) * 8.0D - vec3.x * d0;
        double d2 = livingEntity.getY() + (double)(livingEntity.getRandom().nextInt(16) - 8) - vec3.y * d0;
        double d3 = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5D) * 8.0D - vec3.z * d0;
        return teleport(livingEntity, d1, d2, d3);
    }

    public static boolean teleport(LivingEntity livingEntity, double x, double y, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while(blockpos$mutableblockpos.getY() > livingEntity.level.getMinBuildHeight() && !livingEntity.level.getBlockState(blockpos$mutableblockpos).getMaterial().blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = livingEntity.level.getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.getMaterial().blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(livingEntity, x, y, z);
            if (event.isCanceled()) return false;
            Vec3 vec3 = livingEntity.position();
            boolean flag2 = livingEntity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2) {
                livingEntity.level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(livingEntity));
                if (!livingEntity.isSilent()) {
                    livingEntity.level.playSound((Player)null, livingEntity.xo, livingEntity.yo, livingEntity.zo, SoundEvents.ENDERMAN_TELEPORT, livingEntity.getSoundSource(), 1.0F, 1.0F);
                    livingEntity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    public static boolean randomWaterTeleport(LivingEntity livingEntity, double p_20985_, double p_20986_, double p_20987_, boolean p_20988_) {
        double d0 = livingEntity.getX();
        double d1 = livingEntity.getY();
        double d2 = livingEntity.getZ();
        double d3 = p_20986_;
        boolean flag = false;
        BlockPos blockpos = new BlockPos(p_20985_, p_20986_, p_20987_);
        Level level = livingEntity.level;
        if (level.isLoaded(blockpos)) {
            boolean flag1 = false;

            while(!flag1 && blockpos.getY() > level.getMinBuildHeight()) {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = level.getBlockState(blockpos1);
                if (blockstate.getMaterial().blocksMotion()) {
                    flag1 = true;
                } else if (blockstate.getFluidState().is(FluidTags.WATER)){
                    --d3;
                    flag1 = true;
                } else {
                    --d3;
                    blockpos = blockpos1;
                }
            }

            if (flag1) {
                livingEntity.teleportTo(p_20985_, d3, p_20987_);
                if (level.noCollision(livingEntity)) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            livingEntity.teleportTo(d0, d1, d2);
            return false;
        } else {
            if (p_20988_) {
                level.broadcastEntityEvent(livingEntity, (byte)46);
            }

            if (livingEntity instanceof PathfinderMob) {
                ((PathfinderMob)livingEntity).getNavigation().stop();
            }

            return true;
        }
    }

    public static Predicate<LivingEntity> ownedPredicate(Entity entity){
        return target -> isOwnedTargetable(entity, target);
    }

    public static boolean isOwnedTargetable(Entity attacker, LivingEntity target){
        LivingEntity owner = null;
        if (attacker instanceof IOwned owned){
            owner = owned.getTrueOwner();
        } else if (attacker instanceof OwnableEntity ownable && ownable.getOwner() instanceof LivingEntity livingEntity){
            owner = livingEntity;
        }
        if (owner instanceof Enemy
                || (owner instanceof IOwned owned && owned.isHostile())
                || attacker instanceof Enemy
                || (attacker instanceof IOwned ownedAttacker && ownedAttacker.isHostile())){
            return target instanceof Player player && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player);
        } else {
            return (target instanceof Enemy
                    && !((target.getMobType() == MobType.UNDEAD || target.getType().is(ModTags.EntityTypes.LICH_NEUTRAL)) && LichdomHelper.isLich(owner) && MainConfig.LichUndeadFriends.get())
                    && !(owner != null && ((CuriosFinder.hasNecroSet(owner) && CuriosFinder.validNecroUndead(target)) || (CuriosFinder.neutralNamelessSet(owner) && CuriosFinder.validNamelessUndead(target))) && !MobsConfig.NecroRobeUndead.get())
                    && !(MobUtil.isWitchType(target) && owner != null && CuriosFinder.isWitchFriendly(owner) && !MobsConfig.VariousRobeWitch.get())
                    && !(CuriosFinder.validFrostMob(target) && owner != null && CuriosFinder.neutralFrostSet(owner))
                    && !(CuriosFinder.validWildMob(target) && owner != null && CuriosFinder.neutralWildSet(owner))
                    && !(CuriosFinder.validNetherMob(target) && owner != null && CuriosFinder.neutralNetherSet(owner))
                    && !(target instanceof Creeper && target.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && MobsConfig.MinionsAttackCreepers.get())
                    && !(target instanceof NeutralMob neutralMob && ((owner != null && neutralMob.getTarget() != owner) || ((NeutralMob) target).getTarget() != attacker))
                    && !(target instanceof IOwned owned && (owner != null && owned.getTrueOwner() == owner))
                    || (target instanceof IOwned owned
                    && !(attacker instanceof IOwned ownedAttacker && ownedAttacker.isHostile())
                    && owned.isHostile())
                    || (owner instanceof Player player
                    && ((!SEHelper.getGrudgeEntities(player).isEmpty() && SEHelper.getGrudgeEntities(player).contains(target))
                    || (!SEHelper.getGrudgeEntityTypes(player).isEmpty() && SEHelper.getGrudgeEntityTypes(player).contains(target.getType())))));
        }
    }

    public static boolean isWitchType(Entity target){
        return target instanceof Witch || target instanceof Warlock || target instanceof Crone || target.getType().is(ModTags.EntityTypes.WITCH_SET_NEUTRAL);
    }

    public static void createWitherRose(LivingEntity target, @Nullable LivingEntity killer) {
        createBlockUponDeath(target, killer, Blocks.WITHER_ROSE);
    }

    public static void createBlockUponDeath(LivingEntity target, @Nullable LivingEntity killer, Block block) {
        if (!target.level.isClientSide) {
            boolean flag = false;
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(target.level, killer)) {
                BlockPos blockpos = target.blockPosition();
                BlockState blockstate = block.defaultBlockState();
                if (target.level.isEmptyBlock(blockpos) && blockstate.canSurvive(target.level, blockpos)) {
                    target.level.setBlock(blockpos, blockstate, 3);
                    flag = true;
                }
            }

            if (!flag) {
                ItemEntity itementity = new ItemEntity(target.level, target.getX(), target.getY(), target.getZ(), new ItemStack(block));
                target.level.addFreshEntity(itementity);
            }

        }
    }

    public static boolean isDirectlyLooking(LivingEntity looker, LivingEntity looked) {
        Vec3 vec3 = looker.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(looked.getX() - looker.getX(), looked.getEyeY() - looker.getEyeY(), looked.getZ() - looker.getZ());
        double d0 = vec31.length();
        vec31 = vec31.normalize();
        double d1 = vec3.dot(vec31);
        return d1 > 1.0D - 0.025D / d0 && looker.hasLineOfSight(looked);
    }

    /**
     * Based of @Crimson_Steve codes.
     */
    public static AABB makeAttackRange(double x, double y, double z, double sizeX, double sizeY, double sizeZ) {
        return new AABB(x - (sizeX / 2.0D), y - (sizeY / 2.0D), z - (sizeZ / 2.0D), x + (sizeX / 2.0D), y + (sizeY / 2.0D), z + (sizeZ / 2.0D));
    }

    public static class SizeInfo {
        public final float defaultWidth;
        public final float defaultHeight;
        public final float eyeHeight;
        public final float maxStep;
        public final boolean isDefault;

        public SizeInfo(LivingEntity entity) {
            CompoundTag persistentData = entity.getPersistentData();
            this.defaultWidth = persistentData.getFloat("initWidth");
            this.defaultHeight = persistentData.getFloat("initHeight");
            this.maxStep = entity.getStepHeight();
            this.eyeHeight = entity.getEyeHeight();
            this.isDefault = true;
        }
    }
}
