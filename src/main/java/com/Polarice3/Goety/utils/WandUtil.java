package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.entities.neutral.AbstractVine;
import com.Polarice3.Goety.common.entities.neutral.TotemicBomb;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.entities.projectiles.IceBouquet;
import com.Polarice3.Goety.common.entities.projectiles.Spike;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WandUtil {

    private static boolean isMatchingItem(ItemStack itemStack) {
        return itemStack.getItem() instanceof IWand;
    }

    public static ItemStack findWandOnHand(LivingEntity playerEntity, InteractionHand hand) {
        ItemStack foundStack = ItemStack.EMPTY;
        if (isMatchingItem(playerEntity.getItemInHand(hand))){
            foundStack = playerEntity.getItemInHand(hand);
        }

        return foundStack;
    }

    public static ItemStack findWand(LivingEntity playerEntity) {
        ItemStack foundStack = ItemStack.EMPTY;
        if (isMatchingItem(playerEntity.getMainHandItem())){
            foundStack = playerEntity.getMainHandItem();
        } else if (isMatchingItem(playerEntity.getOffhandItem())){
            foundStack = playerEntity.getOffhandItem();
        }

        return foundStack;
    }

    public static ItemStack findFocusOnHand(LivingEntity playerEntity, InteractionHand hand){
        ItemStack foundStack = ItemStack.EMPTY;
        if (!findWandOnHand(playerEntity, hand).isEmpty()){
            if (!IWand.getFocus(findWandOnHand(playerEntity, hand)).isEmpty()) {
                foundStack = IWand.getFocus(findWandOnHand(playerEntity, hand));
            }
        }

        return foundStack;
    }

    public static ItemStack findFocus(LivingEntity playerEntity){
        ItemStack foundStack = ItemStack.EMPTY;
        if (!findWand(playerEntity).isEmpty()){
            if (!IWand.getFocus(findWand(playerEntity)).isEmpty()) {
                foundStack = IWand.getFocus(findWand(playerEntity));
            }
        }

        return foundStack;
    }

    public static ItemStack findFocusInInv(@Nullable Player player){
        ItemStack foundStack = ItemStack.EMPTY;
        if (player != null) {
            for (int i = 0; i < player.getInventory().items.size(); ++i) {
                ItemStack inSlot = player.getInventory().getItem(i);
                if (inSlot.getItem() instanceof IFocus) {
                    foundStack = inSlot;
                }
            }
        }
        return foundStack;
    }

    public static ISpell getSpellOnHand(LivingEntity livingEntity, InteractionHand hand){
        if (WandUtil.findFocusOnHand(livingEntity, hand).getItem() instanceof IFocus magicFocus){
            if (magicFocus.getSpell() != null){
                return magicFocus.getSpell();
            }
        }
        return null;
    }

    public static ISpell getSpell(LivingEntity livingEntity){
        if (WandUtil.findFocus(livingEntity).getItem() instanceof IFocus magicFocus){
            if (magicFocus.getSpell() != null){
                return magicFocus.getSpell();
            }
        }
        return null;
    }

    public static boolean hasFocusInInv(@Nullable Player player){
        return !findFocusInInv(player).isEmpty();
    }

    public static boolean enchantedFocus(LivingEntity playerEntity){
        return !findFocus(playerEntity).isEmpty() && findFocus(playerEntity).isEnchanted();
    }

    public static int getLevels(Enchantment enchantment, LivingEntity playerEntity){
        if (enchantedFocus(playerEntity)) {
            return findFocus(playerEntity).getEnchantmentLevel(enchantment);
        } else {
            return 0;
        }
    }

    public static void spawnFangs(LivingEntity livingEntity, double pPosX, double pPosZ, double PPPosY, double pOPosY, float pYRot, int pWarmUp) {
        BlockPos blockpos = new BlockPos(pPosX, pOPosY, pPosZ);
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = livingEntity.level.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(livingEntity.level, blockpos1, Direction.UP)) {
                if (!livingEntity.level.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = livingEntity.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(livingEntity.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(PPPosY) - 1);

        if (flag) {
            Fangs fangEntity = new Fangs(livingEntity.level, pPosX, (double)blockpos.getY() + d0, pPosZ, pYRot, pWarmUp, livingEntity);
            if (livingEntity instanceof Player player){
                if (WandUtil.enchantedFocus(player)){
                    if (WandUtil.getLevels(ModEnchantments.ABSORB.get(), player) != 0){
                        fangEntity.setAbsorbing(true);
                    }
                }
            }
            livingEntity.level.addFreshEntity(fangEntity);
        }

    }

    public static void spawnSpikes(LivingEntity livingEntity, double pPosX, double pPosZ, double PPPosY, double pOPosY, float pYRot, int pWarmUp) {
        BlockPos blockpos = new BlockPos(pPosX, pOPosY, pPosZ);
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = livingEntity.level.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(livingEntity.level, blockpos1, Direction.UP)) {
                if (!livingEntity.level.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = livingEntity.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(livingEntity.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(PPPosY) - 1);

        if (flag) {
            Spike spike = new Spike(livingEntity.level, pPosX, (double)blockpos.getY() + d0, pPosZ, pYRot, pWarmUp, livingEntity);
            if (livingEntity instanceof Player player){
                if (WandUtil.enchantedFocus(player)){
                    float enchantment = 0;
                    int burning = 0;
                    int soulEater = 0;
                    if (WandUtil.enchantedFocus(player)) {
                        enchantment = WandUtil.getLevels(ModEnchantments.POTENCY.get(), player);
                        burning = WandUtil.getLevels(ModEnchantments.BURNING.get(), player);
                        soulEater = WandUtil.getLevels(ModEnchantments.SOUL_EATER.get(), player);
                    }
                    spike.setExtraDamage(enchantment);
                    spike.setBurning(burning);
                    spike.setSoulEater(soulEater);
                }
            }
            livingEntity.level.addFreshEntity(spike);
        }

    }

    public static void spawnIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity){
        spawnIceBouquet(world, pPos, livingEntity, 0.0F, 0);
    }

    public static void spawnIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity, float damage, int duration){
        Vec3 vector3d = Vec3Util.west(pPos);
        Vec3 vector3d1 = Vec3Util.east(pPos);
        IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
        iceBouquet1.setExtraDamage(damage);
        iceBouquet1.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet1);
        IceBouquet iceBouquet2 = new IceBouquet(world, vector3d, livingEntity);
        iceBouquet2.setExtraDamage(damage);
        iceBouquet2.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet2);
        IceBouquet iceBouquet3 = new IceBouquet(world, vector3d1, livingEntity);
        iceBouquet3.setExtraDamage(damage);
        iceBouquet3.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet3);
        IceBouquet iceBouquet4 = new IceBouquet(world, Vec3Util.north(pPos), livingEntity);
        iceBouquet4.setExtraDamage(damage);
        iceBouquet4.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet4);
        IceBouquet iceBouquet5 = new IceBouquet(world, Vec3Util.south(pPos), livingEntity);
        iceBouquet5.setExtraDamage(damage);
        iceBouquet5.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet5);
        IceBouquet iceBouquet6 = new IceBouquet(world, Vec3Util.north(vector3d), livingEntity);
        iceBouquet6.setExtraDamage(damage);
        iceBouquet6.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet6);
        IceBouquet iceBouquet7 = new IceBouquet(world, Vec3Util.south(vector3d), livingEntity);
        iceBouquet7.setExtraDamage(damage);
        iceBouquet7.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet7);
        IceBouquet iceBouquet8 = new IceBouquet(world, Vec3Util.north(vector3d1), livingEntity);
        iceBouquet8.setExtraDamage(damage);
        iceBouquet8.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet8);
        IceBouquet iceBouquet9 = new IceBouquet(world, Vec3Util.south(vector3d1), livingEntity);
        iceBouquet9.setExtraDamage(damage);
        iceBouquet9.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet9);
    }

    public static void spawnCrossIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity){
        spawnCrossIceBouquet(world, pPos, livingEntity, 0.0F, 0);
    }

    public static void spawnCrossIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity, float damage, int duration){
        IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
        iceBouquet1.setExtraDamage(damage);
        iceBouquet1.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet1);
        IceBouquet iceBouquet2 = new IceBouquet(world, Vec3Util.west(pPos), livingEntity);
        iceBouquet2.setExtraDamage(damage);
        iceBouquet2.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet2);
        IceBouquet iceBouquet3 = new IceBouquet(world, Vec3Util.east(pPos), livingEntity);
        iceBouquet3.setExtraDamage(damage);
        iceBouquet3.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet3);
        IceBouquet iceBouquet4 = new IceBouquet(world, Vec3Util.north(pPos), livingEntity);
        iceBouquet4.setExtraDamage(damage);
        iceBouquet4.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet4);
        IceBouquet iceBouquet5 = new IceBouquet(world, Vec3Util.south(pPos), livingEntity);
        iceBouquet5.setExtraDamage(damage);
        iceBouquet5.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet5);
    }

    public static void spawn4x4IceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity){
        spawn4x4IceBouquet(world, pPos, livingEntity, 0.0F, 0);
    }

    public static void spawn4x4IceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity, float damage, int duration){
        Vec3 vector3d = Vec3Util.west(pPos);
        Vec3 vector3d1 = Vec3Util.east(pPos);
        int random = world.random.nextInt(4);
        Vec3 vec31;
        Vec3 vec32;
        Vec3 vec33;
        if (random == 0){
            vec31 = Vec3Util.north(pPos);
            vec32 = Vec3Util.west(pPos);
            vec33 = Vec3Util.north(vector3d);
        } else if (random == 1){
            vec31 = Vec3Util.south(pPos);
            vec32 = Vec3Util.west(pPos);
            vec33 = Vec3Util.south(vector3d);
        } else if (random == 2){
            vec31 = Vec3Util.north(pPos);
            vec32 = Vec3Util.east(pPos);
            vec33 = Vec3Util.north(vector3d1);
        } else {
            vec31 = Vec3Util.south(pPos);
            vec32 = Vec3Util.east(pPos);
            vec33 = Vec3Util.south(vector3d1);
        }

        IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
        iceBouquet1.setExtraDamage(damage);
        iceBouquet1.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet1);
        IceBouquet iceBouquet2 = new IceBouquet(world, vec31, livingEntity);
        iceBouquet2.setExtraDamage(damage);
        iceBouquet2.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet2);
        IceBouquet iceBouquet3 = new IceBouquet(world, vec32, livingEntity);
        iceBouquet3.setExtraDamage(damage);
        iceBouquet3.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet3);
        IceBouquet iceBouquet4 = new IceBouquet(world, vec33, livingEntity);
        iceBouquet4.setExtraDamage(damage);
        iceBouquet4.addLifeSpan(duration);
        world.addFreshEntity(iceBouquet4);
    }

    /**
     * Based off GeomancyHelper class from Dungeons Mobs: <a href="https://github.com/Infamous-Misadventures/Dungeons-Mobs/blob/1.19/src/main/java/com/infamous/dungeons_mobs/utils/GeomancyHelper.java">...</a>
     */
    public static final int[] CONFIG_1_NORTH_ROW = new int[]{2, 3, 4, 5, 6};
    public static final int[] CONFIG_1_WEST_ROW = new int[]{6, 7, 8, 9, 10};
    public static final int[] CONFIG_1_SOUTH_ROW = new int[]{10, 11, 12, 13, 14};
    public static final int[] CONFIG_1_EAST_ROW = new int[]{14, 15, 0, 1, 2};
    public static final int[][] CONFIG_1_ROWS = new int[][]{CONFIG_1_NORTH_ROW, CONFIG_1_WEST_ROW, CONFIG_1_SOUTH_ROW, CONFIG_1_EAST_ROW};

    private static boolean isValueInArray(int[] array, int toCheckValue) {
        for (int element : array) {
            if (element == toCheckValue) {
                return true;
            }
        }
        return false;
    }

    private static double getXShift(int i, double xshift) {
        if (i == 0 || i == 1 || i == 2 || i == 14 || i == 15) {
            xshift = -2.0D;
        }
        if (i >= 6 && i <= 10) {
            xshift = 2.0D;
        }

        if (i == 3 || i == 13) {
            xshift = -1.0D;
        }
        if (i == 5 || i == 11) {
            xshift = 1.0D;
        }
        return xshift;
    }

    private static double getZShift(int i, double zshift) {
        if (i >= 10 && i <= 14) {
            zshift = -2.0D;
        }
        if (i >= 2 && i <= 6) {
            zshift = 2.0D;
        }
        if (i == 9 || i == 15) {
            zshift = -1.0D;
        }
        if (i == 1 || i == 7) {
            zshift = 1.0D;
        }
        return zshift;
    }

    private static BlockPos createCenteredBlockPosOnTarget(Entity targetEntity) {
        return new BlockPos(
                Math.floor(targetEntity.getX()),
                Math.floor(targetEntity.getY()),
                Math.floor(targetEntity.getZ()));
    }

    public static void summonMonolith(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> wallEntityType, double xshift, double zshift, int extra) {
        summonMonolith(casterEntity, targetPos, wallEntityType, xshift, zshift, 0, extra);
    }

    public static void summonMonolith(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> wallEntityType, double xshift, double zshift, int delay, int extra) {
        targetPos = targetPos.offset(xshift, 1, zshift);
        Vec3 vec3 = Vec3.atBottomCenterOf(targetPos);
        Level level = casterEntity.level;
        AbstractMonolith monolith = wallEntityType.create(level);
        if (monolith != null) {
            EntityType<?> entityType = monolith.getVariant(level, targetPos);
            if (entityType != null){
                monolith = (AbstractMonolith) entityType.create(level);
            }
            if (monolith != null) {
                monolith.setTrueOwner(casterEntity);
                monolith.setPos(vec3.x(), vec3.y(), vec3.z());
                if (level instanceof ServerLevel serverLevel) {
                    monolith.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(targetPos), MobSpawnType.MOB_SUMMONED, null, null);
                }
                if (monolith instanceof AbstractVine vine) {
                    vine.setWarmup(delay);
                    monolith.setLifeSpan(3 + extra);
                } else if (monolith instanceof TotemicBomb totemicBomb) {
                    totemicBomb.setExplosionPower(2.0F + (extra / 4.0F));
                } else {
                    monolith.setLifeSpan(6 + extra);
                }
                level.addFreshEntity(monolith);
            }
        }
    }

    public static void summonMonolith(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> wallEntityType, double xshift, double zshift, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonMonolith(casterEntity, targetPos, wallEntityType, xshift, zshift, extra);
    }

    public static void summonTurret(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> wallEntityType, @Nullable Entity target, int duration, int potency) {
        summonTurret(casterEntity, targetPos, wallEntityType, target, 0, duration, potency);
    }

    public static void summonTurret(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> wallEntityType, @Nullable Entity target, int delay, int duration, int potency) {
        targetPos = targetPos.above();
        Vec3 vec3 = Vec3.atBottomCenterOf(targetPos);
        Level level = casterEntity.level;
        AbstractMonolith monolith = wallEntityType.create(level);
        if (monolith != null) {
            EntityType<?> entityType = monolith.getVariant(level, targetPos);
            if (entityType != null){
                monolith = (AbstractMonolith) entityType.create(level);
            }
            if (monolith != null) {
                monolith.setTrueOwner(casterEntity);
                monolith.setPos(vec3.x(), vec3.y(), vec3.z());
                if (level instanceof ServerLevel serverLevel) {
                    monolith.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(targetPos), MobSpawnType.MOB_SUMMONED, null, null);
                }
                if (monolith instanceof AbstractVine vine) {
                    vine.setWarmup(delay);
                }
                if (potency > 0) {
                    monolith.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), potency - 1, false, false));
                }
                if (target instanceof LivingEntity living) {
                    monolith.setTarget(living);
                }
                monolith.setLifeSpan(7 * (duration + 1));
                level.addFreshEntity(monolith);
            }
        }
    }

    public static void summonLesserSquareTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int[] rowToRemove, int extra) {
        for (int constructPositionIndex = 0; constructPositionIndex <= 7; constructPositionIndex++) {
            if (isValueInArray(rowToRemove, constructPositionIndex)) {
                continue;
            }
            double xshift = 0;
            double zshift = 0;
            xshift = getXShift(constructPositionIndex, xshift);
            zshift = getZShift(constructPositionIndex, zshift);
            summonMonolith(casterEntity, targetPos, entityType, xshift, zshift, extra);
        }
    }

    public static void summonSquareTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int[] rowToRemove, int extra) {
        for (int constructPositionIndex = 0; constructPositionIndex <= 15; constructPositionIndex++) {
            if (isValueInArray(rowToRemove, constructPositionIndex)) {
                continue;
            }
            double xshift = 0;
            double zshift = 0;
            xshift = getXShift(constructPositionIndex, xshift);
            zshift = getZShift(constructPositionIndex, zshift);
            summonMonolith(casterEntity, targetPos, entityType, xshift, zshift, extra);
        }
    }

    public static void summonSquareTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int[] rowToRemove, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonSquareTrap(casterEntity, targetPos, entityType, rowToRemove, extra);
    }

    public static void summonMinorSquareTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, Direction direction, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);

        if (direction.getAxis() == Direction.Axis.X){
            summonMonolith(casterEntity, targetPos, entityType, 0, -2, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 0, 2, 0, extra);
            if (casterEntity.getRandom().nextBoolean()) {
                summonMonolith(casterEntity, targetPos, entityType, -1, -2, 2, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, -2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, -1, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, 0, 8, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, 1, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, 2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, -1, 2, 2, extra);
            } else {
                summonMonolith(casterEntity, targetPos, entityType, 1, -2, 2, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, -2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, -1, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 0, 8, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 1, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, 1, 2, 2, extra);
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            summonMonolith(casterEntity, targetPos, entityType, -2, 0, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, 0, 0, extra);
            if (casterEntity.getRandom().nextBoolean()) {
                summonMonolith(casterEntity, targetPos, entityType, -2, -1, 2, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, -2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, -1, -2, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 0, -2, 8, extra);
                summonMonolith(casterEntity, targetPos, entityType, 1, -2, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, -2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, -1, 2, extra);
            } else {
                summonMonolith(casterEntity, targetPos, entityType, -2, 1, 2, extra);
                summonMonolith(casterEntity, targetPos, entityType, -2, 2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, -1, 2, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 0, 2, 8, extra);
                summonMonolith(casterEntity, targetPos, entityType, 1, 2, 6, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 2, 4, extra);
                summonMonolith(casterEntity, targetPos, entityType, 2, 1, 2, extra);
            }
        }

    }

    public static void summonCircleTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, Direction direction, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);

        if (direction.getAxis() == Direction.Axis.X){
            summonMonolith(casterEntity, targetPos, entityType, 1, 2, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, -1, 2, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, -1, 1, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, 0, 2, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, 1, 3, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, -1, 3, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, 0, 2, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, 1, 1, extra);
            summonMonolith(casterEntity, targetPos, entityType, 1, -2, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, -1, -2, 0, extra);
        } else if (direction.getAxis() == Direction.Axis.Z){
            summonMonolith(casterEntity, targetPos, entityType, 2, 1, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 2, -1, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, 1, 2, 1, extra);
            summonMonolith(casterEntity, targetPos, entityType, 0, 2, 2, extra);
            summonMonolith(casterEntity, targetPos, entityType, -1, 2, 3, extra);
            summonMonolith(casterEntity, targetPos, entityType, 1, -2, 3, extra);
            summonMonolith(casterEntity, targetPos, entityType, 0, -2, 2, extra);
            summonMonolith(casterEntity, targetPos, entityType, -1, -2, 1, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, 1, 0, extra);
            summonMonolith(casterEntity, targetPos, entityType, -2, -1, 0, extra);
        }
    }

    public static void summonCubeTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        for (int constructPositionIndex = 0; constructPositionIndex <= 25; constructPositionIndex++) {
            if (constructPositionIndex != 4
                    && constructPositionIndex != 8
                    && constructPositionIndex != 12
                    && constructPositionIndex != 16){
                double xshift = 0;
                double zshift = 0;
                xshift = getXShift(constructPositionIndex, xshift);
                zshift = getZShift(constructPositionIndex, zshift);
                if (xshift != 0 && zshift != 0) {
                    summonMonolith(casterEntity, targetPos, entityType, xshift, zshift, constructPositionIndex, extra);
                }
            }
        }
    }

    public static void summonCubeTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonCubeTrap(casterEntity, targetPos, entityType, extra);
    }

    public static void summonHallTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        Direction direction = Direction.fromYRot(casterEntity.getYHeadRot());
        if (direction.getAxis() == Direction.Axis.X){
            for (int length = -2; length <= 2; length++) {
                summonMonolith(casterEntity, targetPos, entityType, 2, length, length + 2, extra);
            }
            for (int length = -2; length <= 2; length++) {
                summonMonolith(casterEntity, targetPos, entityType, -2, length, length + 2, extra);
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            for (int length = -2; length <= 2; length++) {
                summonMonolith(casterEntity, targetPos, entityType, length, 2, length + 2, extra);
            }
            for (int length = -2; length <= 2; length++) {
                summonMonolith(casterEntity, targetPos, entityType, length, -2, length + 2, extra);
            }
        }
    }

    public static void summonHallTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonHallTrap(casterEntity, targetPos, entityType, extra);
    }

    public static void summonSurroundTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonMonolith(casterEntity, targetPos, entityType, 1, 0, 0, extra);
        summonMonolith(casterEntity, targetPos, entityType, -1, 0, 1, extra);
        summonMonolith(casterEntity, targetPos, entityType, 1, 1, 2, extra);
        summonMonolith(casterEntity, targetPos, entityType, -1, 1, 3, extra);
        summonMonolith(casterEntity, targetPos, entityType, 1, -1, 4, extra);
        summonMonolith(casterEntity, targetPos, entityType, -1, -1, 5, extra);
        summonMonolith(casterEntity, targetPos, entityType, 0, 1, 6, extra);
        summonMonolith(casterEntity, targetPos, entityType, 0, -1, 7, extra);
    }

    public static void summonSurroundTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonSurroundTrap(casterEntity, targetPos, entityType, extra);
    }

    public static void summonWallTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonWallTrap(casterEntity, targetPos, entityType, 5, extra);
    }

    public static void summonWallTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int amount, int extra) {
        Direction direction = Direction.fromYRot(casterEntity.getYHeadRot());
        if (direction.getAxis() == Direction.Axis.X){
            for (int length = -amount; length < amount; length++) {
                summonMonolith(casterEntity, targetPos, entityType, 0, length, extra);
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            for (int length = -amount; length < amount; length++) {
                summonMonolith(casterEntity, targetPos, entityType, length, 0, extra);
            }
        }
    }

    public static void summonWallTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonWallTrap(casterEntity, targetEntity, entityType, 5, extra);
    }

    public static void summonWallTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int amount, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        Direction direction = Direction.fromYRot(targetEntity.getYHeadRot());
        if (targetEntity instanceof Mob){
            direction = targetEntity.getMotionDirection();
        }
        if (direction.getAxis() == Direction.Axis.X){
            if (casterEntity.getRandom().nextBoolean()) {
                for (int length = -amount; length < amount; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, -2, length, extra);
                }
            } else {
                for (int length = -amount; length < amount; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, 2, length, extra);
                }
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            if (casterEntity.getRandom().nextBoolean()) {
                for (int length = -amount; length < amount; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, length, -2, extra);
                }
            } else {
                for (int length = -amount; length < amount; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, length, 2, extra);
                }
            }
        }
    }

    public static void summonRandomPillarsTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        for (int length = 0; length < 25; length++) {
            summonMonolith(casterEntity, targetPos, entityType, -8 + casterEntity.getRandom().nextInt(16), -8 + casterEntity.getRandom().nextInt(16), extra);
        }
    }

    public static void summonRandomPillarsTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonRandomPillarsTrap(casterEntity, targetEntity, entityType, 12, extra);
    }

    public static void summonRandomPillarsTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int amount, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        for (int length = 0; length < amount; length++) {
            summonMonolith(casterEntity, targetPos, entityType, -4 + casterEntity.getRandom().nextInt(8), -4 + casterEntity.getRandom().nextInt(8), extra);
        }
    }

    public static void summonQuadOffensiveTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        summonMonolith(casterEntity, targetPos, entityType, -2, 0, extra);
        summonMonolith(casterEntity, targetPos, entityType, 2, 0, extra);
        summonMonolith(casterEntity, targetPos, entityType, 0, -2, extra);
        summonMonolith(casterEntity, targetPos, entityType, 0, 2, extra);
    }

    public static void summonQuadOffensiveTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonQuadOffensiveTrap(casterEntity, targetPos, entityType, extra);
    }
}
