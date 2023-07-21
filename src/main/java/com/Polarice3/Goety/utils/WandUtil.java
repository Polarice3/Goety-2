package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.Fangs;
import com.Polarice3.Goety.common.entities.projectiles.IceBouquet;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

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
}
