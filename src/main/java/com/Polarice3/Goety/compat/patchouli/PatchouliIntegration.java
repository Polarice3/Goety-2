package com.Polarice3.Goety.compat.patchouli;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.compat.ICompatable;
import com.google.common.base.Suppliers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.function.Supplier;

public class PatchouliIntegration implements ICompatable {
    public void setup(FMLCommonSetupEvent event) {
        PatchouliAPI.get().registerMultiblock(Goety.location("redstone_golem"), REDSTONE_GOLEM.get());
        PatchouliAPI.get().registerMultiblock(Goety.location("redstone_golem_revive"), REDSTONE_GOLEM_REVIVE.get());
    }

    public static final Supplier<IMultiblock> REDSTONE_GOLEM = Suppliers.memoize(() -> {
        IStateMatcher diamondMold = PatchouliAPI.get().predicateMatcher(Blocks.DIAMOND_BLOCK,
                state -> state.is(Tags.Blocks.STORAGE_BLOCKS_DIAMOND));
        IStateMatcher redstoneCore = PatchouliAPI.get().predicateMatcher(Blocks.REDSTONE_BLOCK,
                state -> state.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE));
        IStateMatcher stoneMold = PatchouliAPI.get().predicateMatcher(Blocks.STONE_BRICKS,
                state -> state.getBlock().getDescriptionId().contains("bricks") && !(state.getBlock() instanceof SlabBlock) && !(state.getBlock() instanceof StairBlock) && !(state.getBlock() instanceof WallBlock));
        IStateMatcher magmaBlock = PatchouliAPI.get().predicateMatcher(Blocks.MAGMA_BLOCK,
                state -> state.is(Blocks.MAGMA_BLOCK));
        return PatchouliAPI.get().makeMultiblock(
                new String[][] {
                        {
                                "_________",
                                "___SDS___",
                                "__DLLLD__",
                                "_SLLRLLS_",
                                "_DLRRRLD_",
                                "_SLLRLLS_",
                                "__DLLLD__",
                                "___SDS___",
                                "_________",
                        },
                        {
                                "_________",
                                "_________",
                                "___SDS___",
                                "__SMMMS__",
                                "__DM0MD__",
                                "__SMMMS__",
                                "___SDS___",
                                "_________",
                                "_________",
                        }
                },
                'L', Blocks.LAVA,
                'D', diamondMold,
                'R', redstoneCore,
                'S', stoneMold,
                'M', magmaBlock,
                '0', magmaBlock
        );
    });

    public static final Supplier<IMultiblock> REDSTONE_GOLEM_REVIVE = Suppliers.memoize(() -> {
        IStateMatcher diamond = PatchouliAPI.get().predicateMatcher(Blocks.DIAMOND_BLOCK,
                state -> state.is(Tags.Blocks.STORAGE_BLOCKS_DIAMOND));
        IStateMatcher redstoneCore = PatchouliAPI.get().predicateMatcher(Blocks.REDSTONE_BLOCK,
                state -> state.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE));
        IStateMatcher magmaBlock = PatchouliAPI.get().predicateMatcher(Blocks.MAGMA_BLOCK,
                state -> state.is(Blocks.MAGMA_BLOCK));
        return PatchouliAPI.get().makeMultiblock(
                new String[][] {
                        {
                                "__H__"
                        },
                        {
                                "MRRRM"
                        },
                        {
                                "MMRMM"
                        },
                        {
                                "M_0_M"
                        }
                },
                'H', ModBlocks.REDSTONE_GOLEM_SKULL_BLOCK.get(),
                'R', redstoneCore,
                'M', magmaBlock,
                '0', diamond
        );
    });
}
