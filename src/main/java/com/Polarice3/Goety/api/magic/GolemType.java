package com.Polarice3.Goety.api.magic;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.magic.construct.*;
import com.google.common.collect.Maps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Supplier;

public enum GolemType implements net.minecraftforge.common.IExtensibleEnum {
    NONE(null, null),
    WHISPERER(Blocks.MOSS_BLOCK::defaultBlockState, new WhispererMold()),
    LEAPLEAF(ModBlocks.OVERGROWN_ROOTS.get()::defaultBlockState, new LeapleafMold()),
    ICE_GOLEM(Blocks.BLUE_ICE::defaultBlockState, new IceGolemMold()),
    SQUALL_GOLEM(ModBlocks.JADE_BLOCK.get()::defaultBlockState, new SquallGolemMold()),
    REDSTONE_GOLEM(Blocks.REDSTONE_BLOCK::defaultBlockState, new RedstoneGolemMold()),
    GRAVE_GOLEM(ModBlocks.SKULL_PILE.get()::defaultBlockState, new GraveGolemMold()),
    REDSTONE_MONSTROSITY(ModBlocks.REINFORCED_REDSTONE_BLOCK.get()::defaultBlockState, new RedstoneMonstrosityMold());

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
}
