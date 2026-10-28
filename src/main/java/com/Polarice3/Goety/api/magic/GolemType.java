package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.magic.construct.*;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public enum GolemType implements net.minecraftforge.common.IExtensibleEnum {
    NONE(null, null),
    WHISPERER(Blocks.MOSS_BLOCK::defaultBlockState, new WhispererMold()),
    LEAPLEAF(ModBlocks.OVERGROWN_ROOTS.get()::defaultBlockState, new LeapleafMold()),
    FLAME_CONSTRUCT(Blocks.MAGMA_BLOCK::defaultBlockState, new FlameConstructMold()),
    ICE_GOLEM(Blocks.BLUE_ICE::defaultBlockState, new IceGolemMold()),
    SQUALL_GOLEM(ModBlocks.JADE_BLOCK.get()::defaultBlockState, new SquallGolemMold()),
    REDSTONE_GOLEM(Blocks.REDSTONE_BLOCK::defaultBlockState, new RedstoneGolemMold()),
    GRAVE_GOLEM(ModBlocks.SKULL_PILE.get()::defaultBlockState, new GraveGolemMold()),
    REDSTONE_MONSTROSITY(ModBlocks.MONSTROUS_CORE.get()::defaultBlockState, new RedstoneMonstrosityMold()),
    GRAVE_MONSTROSITY(ModBlocks.MONSTROUS_CORE.get()::defaultBlockState, new GraveMonstrosityMold());

    private final Supplier<BlockState> blockState;
    private final IMold mold;

    GolemType(Supplier<BlockState> blockState, IMold mold){
        this.blockState = blockState;
        this.mold = mold;
    }

    public static GolemType create(String name, Supplier<BlockState> blockState, IMold mold){
        throw new IllegalStateException("Enum not extended");
    }

    public BlockState getBlockState() {
        return this.blockState.get();
    }

    public IMold getMold(){
        return this.mold;
    }

    public static Map<BlockState, IMold> getGolemList(){
        Map<BlockState, IMold> list = Maps.newHashMap();
        for (GolemType golemType : GolemType.values()){
            if (golemType.blockState != null) {
                list.put(golemType.getBlockState(), golemType.getMold());
            }
        }
        return list;
    }

    public static List<IMold> getMolds(BlockState blockState) {
        List<IMold> molds = new ArrayList<>();
        for (GolemType golemType : GolemType.values()) {
            if (golemType.blockState != null && golemType.getBlockState().equals(blockState)) {
                molds.add(golemType.getMold());
            }
        }
        return molds;
    }

    public static boolean hasMold(BlockState blockState) {
        return !getMolds(blockState).isEmpty();
    }

    public static boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos pos) {
        List<IMold> molds = getMolds(level.getBlockState(pos));
        if (molds.isEmpty()) {
            return false;
        }
        for (IMold mold : molds) {
            if (mold.isStructureValid(level, pos)) {
                return mold.spawnServant(player, stack, level, pos);
            }
        }
        return molds.get(0).spawnServant(player, stack, level, pos);
    }
}
