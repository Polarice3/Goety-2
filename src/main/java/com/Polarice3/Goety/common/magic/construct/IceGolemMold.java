package com.Polarice3.Goety.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.golem.IceGolem;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class IceGolemMold implements IMold {
    private static final List<BlockPos> ICE_LOCATIONS = ImmutableList.of(
            new BlockPos(0, -1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0)
    );
    private static final List<BlockPos> ICE_LOCATIONS_Z = ImmutableList.of(
            new BlockPos(0, -1, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    );
    private static final BlockPos BLUE_ICE = new BlockPos(0, 0, 0);
    private static final BlockPos SNOW = new BlockPos(0, 1, 0);

    private static List<BlockPos> checkIceX(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : ICE_LOCATIONS){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.PACKED_ICE)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkIceZ(Level level, BlockPos blockPos){
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : ICE_LOCATIONS_Z){
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.PACKED_ICE)){
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    public static boolean canSpawn(Level level, BlockPos blockPos){
        if (checkIceX(level, blockPos).isEmpty()){
            for (BlockPos blockPos1 : ICE_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Blocks.PACKED_ICE)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            return level.getBlockState(blockPos.offset(SNOW)).is(Blocks.SNOW_BLOCK) && level.getBlockState(blockPos.offset(BLUE_ICE)).is(Blocks.BLUE_ICE);
        } else if (checkIceZ(level, blockPos).isEmpty()) {
            for (BlockPos blockPos1 : ICE_LOCATIONS_Z) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Blocks.PACKED_ICE)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            return level.getBlockState(blockPos.offset(SNOW)).is(Blocks.SNOW_BLOCK) && level.getBlockState(blockPos.offset(BLUE_ICE)).is(Blocks.BLUE_ICE);
        } else {
            return false;
        }
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            if (level.getBlockState(blockPos).is(Blocks.BLUE_ICE)) {
                if (canSpawn(level, blockPos)) {
                    IceGolem iceGolem = ModEntityType.ICE_GOLEM.get().create(level);
                    if (iceGolem != null) {
                        iceGolem.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(iceGolem.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        iceGolem.setTrueOwner(player);
                        iceGolem.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.below().getY() + 0.05D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                        if (level.addFreshEntity(iceGolem)) {
                            removeBlocks(level, blockPos);
                            stack.shrink(1);
                            if (player instanceof ServerPlayer serverPlayer) {
                                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, iceGolem);
                            }
                            return true;
                        }
                    }
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.block.fail"), true);
                }
            }
        }
        return false;
    }

    public static void removeBlocks(Level level, BlockPos blockPos){
        if (!level.isClientSide) {
            BlockPos blockPos1 = blockPos.offset(BLUE_ICE);
            if (level.getBlockState(blockPos1).is(Blocks.BLUE_ICE)) {
                level.levelEvent(2001, blockPos1, Block.getId(level.getBlockState(blockPos1)));
                level.setBlockAndUpdate(blockPos1, Blocks.AIR.defaultBlockState());
            }
            BlockPos blockPos2 = blockPos.offset(SNOW);
            if (level.getBlockState(blockPos2).is(Blocks.SNOW_BLOCK)) {
                level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
            }
        }
    }
}
