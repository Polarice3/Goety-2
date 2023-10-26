package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.AbstractMonolith;
import com.Polarice3.Goety.common.entities.neutral.TotemicBomb;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.entities.projectiles.IceBouquet;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
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
        return itemStack.getItem() instanceof DarkWand;
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

    public static ItemStack findFocus(LivingEntity playerEntity){
        ItemStack foundStack = ItemStack.EMPTY;
        if (!findWand(playerEntity).isEmpty()){
            if (!DarkWand.getFocus(findWand(playerEntity)).isEmpty()) {
                foundStack = DarkWand.getFocus(findWand(playerEntity));
            }
        }

        return foundStack;
    }

    public static ItemStack findFocusInInv(@Nullable Player player){
        ItemStack foundStack = ItemStack.EMPTY;
        if (player != null) {
            for (int i = 0; i < player.getInventory().items.size(); ++i) {
                ItemStack inSlot = player.getInventory().getItem(i);
                if (inSlot.getItem() instanceof MagicFocus) {
                    foundStack = inSlot;
                }
            }
        }
        return foundStack;
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
        BlockPos blockpos = BlockPos.containing(pPosX, pOPosY, pPosZ);
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

    public static void spawnIceBouquet(Level world, BlockPos pPos, LivingEntity livingEntity){
        BlockPos blockPos2 = pPos.west();
        BlockPos blockPos3 = pPos.east();
        IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
        world.addFreshEntity(iceBouquet1);
        IceBouquet iceBouquet2 = new IceBouquet(world, blockPos2, livingEntity);
        world.addFreshEntity(iceBouquet2);
        IceBouquet iceBouquet3 = new IceBouquet(world, blockPos3, livingEntity);
        world.addFreshEntity(iceBouquet3);
        IceBouquet iceBouquet4 = new IceBouquet(world, pPos.north(), livingEntity);
        world.addFreshEntity(iceBouquet4);
        IceBouquet iceBouquet5 = new IceBouquet(world, pPos.south(), livingEntity);
        world.addFreshEntity(iceBouquet5);
        IceBouquet iceBouquet6 = new IceBouquet(world, blockPos2.north(), livingEntity);
        world.addFreshEntity(iceBouquet6);
        IceBouquet iceBouquet7 = new IceBouquet(world, blockPos2.south(), livingEntity);
        world.addFreshEntity(iceBouquet7);
        IceBouquet iceBouquet8 = new IceBouquet(world, blockPos3.north(), livingEntity);
        world.addFreshEntity(iceBouquet8);
        IceBouquet iceBouquet9 = new IceBouquet(world, blockPos3.south(), livingEntity);
        world.addFreshEntity(iceBouquet9);
    }

    public static void spawnIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity){
        Vec3 vector3d = Vec3Util.west(pPos);
        Vec3 vector3d1 = Vec3Util.east(pPos);
        IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
        world.addFreshEntity(iceBouquet1);
        IceBouquet iceBouquet2 = new IceBouquet(world, vector3d, livingEntity);
        world.addFreshEntity(iceBouquet2);
        IceBouquet iceBouquet3 = new IceBouquet(world, vector3d1, livingEntity);
        world.addFreshEntity(iceBouquet3);
        IceBouquet iceBouquet4 = new IceBouquet(world, Vec3Util.north(pPos), livingEntity);
        world.addFreshEntity(iceBouquet4);
        IceBouquet iceBouquet5 = new IceBouquet(world, Vec3Util.south(pPos), livingEntity);
        world.addFreshEntity(iceBouquet5);
        IceBouquet iceBouquet6 = new IceBouquet(world, Vec3Util.north(vector3d), livingEntity);
        world.addFreshEntity(iceBouquet6);
        IceBouquet iceBouquet7 = new IceBouquet(world, Vec3Util.south(vector3d), livingEntity);
        world.addFreshEntity(iceBouquet7);
        IceBouquet iceBouquet8 = new IceBouquet(world, Vec3Util.north(vector3d1), livingEntity);
        world.addFreshEntity(iceBouquet8);
        IceBouquet iceBouquet9 = new IceBouquet(world, Vec3Util.south(vector3d1), livingEntity);
        world.addFreshEntity(iceBouquet9);
    }

    public static void spawnCrossIceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity){
        IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
        world.addFreshEntity(iceBouquet1);
        IceBouquet iceBouquet2 = new IceBouquet(world, Vec3Util.west(pPos), livingEntity);
        world.addFreshEntity(iceBouquet2);
        IceBouquet iceBouquet3 = new IceBouquet(world, Vec3Util.east(pPos), livingEntity);
        world.addFreshEntity(iceBouquet3);
        IceBouquet iceBouquet4 = new IceBouquet(world, Vec3Util.north(pPos), livingEntity);
        world.addFreshEntity(iceBouquet4);
        IceBouquet iceBouquet5 = new IceBouquet(world, Vec3Util.south(pPos), livingEntity);
        world.addFreshEntity(iceBouquet5);
    }

    public static void spawn4x4IceBouquet(Level world, Vec3 pPos, LivingEntity livingEntity){
        Vec3 vector3d = Vec3Util.west(pPos);
        Vec3 vector3d1 = Vec3Util.east(pPos);
        int random = world.random.nextInt(4);
        if (random == 0) {
            IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
            world.addFreshEntity(iceBouquet1);
            IceBouquet iceBouquet2 = new IceBouquet(world, Vec3Util.north(pPos), livingEntity);
            world.addFreshEntity(iceBouquet2);
            IceBouquet iceBouquet3 = new IceBouquet(world, Vec3Util.west(pPos), livingEntity);
            world.addFreshEntity(iceBouquet3);
            IceBouquet iceBouquet4 = new IceBouquet(world, Vec3Util.north(vector3d), livingEntity);
            world.addFreshEntity(iceBouquet4);
        } else if (random == 1){
            IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
            world.addFreshEntity(iceBouquet1);
            IceBouquet iceBouquet2 = new IceBouquet(world, Vec3Util.south(pPos), livingEntity);
            world.addFreshEntity(iceBouquet2);
            IceBouquet iceBouquet3 = new IceBouquet(world, Vec3Util.west(pPos), livingEntity);
            world.addFreshEntity(iceBouquet3);
            IceBouquet iceBouquet4 = new IceBouquet(world, Vec3Util.south(vector3d), livingEntity);
            world.addFreshEntity(iceBouquet4);
        } else if (random == 2){
            IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
            world.addFreshEntity(iceBouquet1);
            IceBouquet iceBouquet2 = new IceBouquet(world, Vec3Util.north(pPos), livingEntity);
            world.addFreshEntity(iceBouquet2);
            IceBouquet iceBouquet3 = new IceBouquet(world, Vec3Util.east(pPos), livingEntity);
            world.addFreshEntity(iceBouquet3);
            IceBouquet iceBouquet4 = new IceBouquet(world, Vec3Util.north(vector3d1), livingEntity);
            world.addFreshEntity(iceBouquet4);
        } else {
            IceBouquet iceBouquet1 = new IceBouquet(world, pPos, livingEntity);
            world.addFreshEntity(iceBouquet1);
            IceBouquet iceBouquet2 = new IceBouquet(world, Vec3Util.south(pPos), livingEntity);
            world.addFreshEntity(iceBouquet2);
            IceBouquet iceBouquet3 = new IceBouquet(world, Vec3Util.east(pPos), livingEntity);
            world.addFreshEntity(iceBouquet3);
            IceBouquet iceBouquet4 = new IceBouquet(world, Vec3Util.south(vector3d1), livingEntity);
            world.addFreshEntity(iceBouquet4);
        }
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
        return BlockPos.containing(
                Math.floor(targetEntity.getX()),
                Math.floor(targetEntity.getY()),
                Math.floor(targetEntity.getZ()));
    }

    public static void summonMonolith(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> wallEntityType, double xshift, double zshift, int extra) {
        targetPos = targetPos.offset((int) xshift, 1, (int) zshift);
        Level level = casterEntity.level;
        AbstractMonolith monolith = wallEntityType.create(level);
        if (monolith != null) {
            monolith.setTrueOwner(casterEntity);
            monolith.setPos(targetPos.getX(), targetPos.getY(), targetPos.getZ());
            if (level instanceof ServerLevel serverLevel) {
                monolith.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(targetPos), MobSpawnType.MOB_SUMMONED, null, null);
            }
            if (monolith instanceof TotemicBomb totemicBomb) {
                totemicBomb.setExplosionPower(2.0F + (extra / 4.0F));
            } else {
                monolith.setLifeSpan(6 + extra);
            }
            level.addFreshEntity(monolith);
        }
    }

    public static void summonMonolith(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> wallEntityType, double xshift, double zshift, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        summonMonolith(casterEntity, targetPos, wallEntityType, xshift, zshift, extra);
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

    public static void summonWallTrap(LivingEntity casterEntity, BlockPos targetPos, EntityType<? extends AbstractMonolith> entityType, int extra) {
        Direction direction = Direction.fromYRot(casterEntity.getYHeadRot());
        if (direction.getAxis() == Direction.Axis.X){
            for (int length = -5; length < 5; length++) {
                summonMonolith(casterEntity, targetPos, entityType, 0, length, extra);
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            for (int length = -5; length < 5; length++) {
                summonMonolith(casterEntity, targetPos, entityType, length, 0, extra);
            }
        }
    }

    public static void summonWallTrap(LivingEntity casterEntity, Entity targetEntity, EntityType<? extends AbstractMonolith> entityType, int extra) {
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        Direction direction = Direction.fromYRot(targetEntity.getYHeadRot());
        if (direction.getAxis() == Direction.Axis.X){
            if (casterEntity.getRandom().nextBoolean()) {
                for (int length = -5; length < 5; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, -2, length, extra);
                }
            } else {
                for (int length = -5; length < 5; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, 2, length, extra);
                }
            }
        } else if (direction.getAxis() == Direction.Axis.Z){
            if (casterEntity.getRandom().nextBoolean()) {
                for (int length = -5; length < 5; length++) {
                    summonMonolith(casterEntity, targetPos, entityType, length, -2, extra);
                }
            } else {
                for (int length = -5; length < 5; length++) {
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
        BlockPos targetPos = createCenteredBlockPosOnTarget(targetEntity);
        for (int length = 0; length < 12; length++) {
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
