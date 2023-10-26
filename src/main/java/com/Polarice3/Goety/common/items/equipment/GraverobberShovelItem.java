package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;

public class GraverobberShovelItem extends ShovelItem {
    public GraverobberShovelItem() {
        super(ModTiers.SPECIAL, 1.5F, -3.0F, (new Properties()).rarity(Rarity.UNCOMMON));
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.is(BlockTags.MINEABLE_WITH_SHOVEL)){
            if (!MobUtil.isShifting(pEntityLiving)) {
                for (BlockPos blockPos : multiBlockBreak(pEntityLiving, pPos)) {
                    BlockState blockstate = pLevel.getBlockState(blockPos);
                    if (blockstate.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                        if (pLevel.destroyBlock(blockPos, true, pEntityLiving)) {
                            if (blockstate.getDestroySpeed(pLevel, blockPos) != 0) {
                                pStack.hurtAndBreak(1, pEntityLiving, (p_220044_0_)
                                        -> p_220044_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                            }
                        }
                    }
                }
            }
            if (pLevel.getServer() != null) {
                double d0 = (double) (pLevel.random.nextFloat() * 0.5F) + 0.25D;
                double d1 = (double) (pLevel.random.nextFloat() * 0.5F) + 0.25D;
                double d2 = (double) (pLevel.random.nextFloat() * 0.5F) + 0.25D;
                LootTable loottable = pLevel.getServer().getLootData().getLootTable(EntityType.SKELETON.getDefaultLootTable());
                if ((pState.is(BlockTags.DIRT) || pState.is(BlockTags.SAND))){
                    if (pLevel.random.nextFloat() <= 0.1F){
                        if (pLevel.random.nextBoolean()){
                            loottable = pLevel.getServer().getLootData().getLootTable(EntityType.ZOMBIE.getDefaultLootTable());
                        }
                        LootParams.Builder lootcontext$builder = MobUtil.createLootContext(pLevel.damageSources().generic(), pEntityLiving);
                        LootParams ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach((loot) -> {
                            ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX() + d0, pPos.getY() + d1, pPos.getZ() + d2, loot);
                            if (pState.is(BlockTags.SAND)) {
                                if (loot.getFoodProperties(pEntityLiving) != null && Objects.requireNonNull(loot.getFoodProperties(pEntityLiving)).getEffects().isEmpty()) {
                                    itemEntity.setItem(new ItemStack(Items.BONE));
                                }
                            }
                            pLevel.addFreshEntity(itemEntity);
                        });
                    }
                } else if (pState.is(BlockTags.WITHER_SUMMON_BASE_BLOCKS)){
                    if (pLevel.random.nextFloat() <= 0.1F){
                        LootParams.Builder lootcontext$builder = MobUtil.createLootContext(pLevel.damageSources().generic(), pEntityLiving);
                        LootParams ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
                        loottable.getRandomItems(ctx).forEach((loot) -> {
                            ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX() + d0, pPos.getY() + d1, pPos.getZ() + d2, loot);
                            pLevel.addFreshEntity(itemEntity);
                        });
                    }
                }
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    public static Iterable<BlockPos> multiBlockBreak(LivingEntity livingEntity, BlockPos blockPos){
        BlockHitResult blockHitResult = MobUtil.rayTrace(livingEntity, 10, false);
        Direction direction = blockHitResult.getDirection();
        Direction direction1 = livingEntity.getDirection();
        boolean xRot = direction1.getStepX() == 0;
        boolean zRot = direction1.getStepZ() == 0;
        boolean hasY = direction.getStepY() == 0;
        Vec3i start = new Vec3i(!xRot && !hasY && zRot && direction1.equals(Direction.WEST) ? -1 : 0, 0, xRot && !hasY && !zRot && direction1.equals(Direction.NORTH) ? -1 : 0);
        Vec3i end = new Vec3i(!xRot && !hasY && zRot && direction1.equals(Direction.EAST) ? 1 : 0, hasY ? 1 : 0, xRot && !hasY && !zRot && direction1.equals(Direction.SOUTH) ? 1 : 0);
        return BlockPos.betweenClosed(
                blockPos.offset(start),
                blockPos.offset(end));
    }
}
