package com.Polarice3.Goety.compat.patchouli;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.compat.ICompatable;
import com.Polarice3.Goety.init.ModTags;
import com.google.common.base.Suppliers;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
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
        PatchouliAPI.get().registerMultiblock(Goety.location("whisperer"), WHISPERER.get());
        PatchouliAPI.get().registerMultiblock(Goety.location("leapleaf"), LEAPLEAF.get());
        PatchouliAPI.get().registerMultiblock(Goety.location("squall_golem"), SQUALL_GOLEM.get());
        PatchouliAPI.get().registerMultiblock(Goety.location("redstone_golem"), REDSTONE_GOLEM.get());
        PatchouliAPI.get().registerMultiblock(Goety.location("redstone_golem_revive"), REDSTONE_GOLEM_REVIVE.get());
        PatchouliAPI.get().registerMultiblock(Goety.location("grave_golem"), GRAVE_GOLEM.get());
    }

    public static ItemStack getBlackBook(){
        return PatchouliAPI.get().getBookStack(Goety.location("black_book"));
    }

    public static ItemStack getWitchesBrew(){
        return PatchouliAPI.get().getBookStack(Goety.location("witches_brew"));
    }

    public static final Supplier<IMultiblock> WHISPERER = Suppliers.memoize(() -> {
        IStateMatcher leaves = PatchouliAPI.get().predicateMatcher(Blocks.JUNGLE_LEAVES,
                state -> state.is(Blocks.JUNGLE_LEAVES));
        IStateMatcher moss = PatchouliAPI.get().predicateMatcher(Blocks.MOSS_BLOCK,
                state -> state.is(Blocks.MOSS_BLOCK));
        IStateMatcher blossom = PatchouliAPI.get().predicateMatcher(ModBlocks.CORPSE_BLOSSOM.get(),
                state -> state.is(ModBlocks.CORPSE_BLOSSOM.get()));
        return PatchouliAPI.get().makeMultiblock(
                new String[][] {
                        {
                                "C"
                        },
                        {
                                "0"
                        },
                        {
                                "H",
                        }
                },
                'H', leaves,
                'C', blossom,
                '0', moss
        );
    });

    public static final Supplier<IMultiblock> LEAPLEAF = Suppliers.memoize(() -> {
        IStateMatcher roots = PatchouliAPI.get().predicateMatcher(ModBlocks.OVERGROWN_ROOTS.get(),
                state -> state.is(ModBlocks.OVERGROWN_ROOTS.get()));
        IStateMatcher leaves = PatchouliAPI.get().predicateMatcher(Blocks.JUNGLE_LEAVES,
                state -> state.is(Blocks.JUNGLE_LEAVES));
        IStateMatcher blossom = PatchouliAPI.get().predicateMatcher(ModBlocks.CORPSE_BLOSSOM.get(),
                state -> state.is(ModBlocks.CORPSE_BLOSSOM.get()));
        return PatchouliAPI.get().makeMultiblock(
                new String[][] {
                        {
                                "_C_"
                        },
                        {
                                "L0L"
                        },
                        {
                                "HHH"
                        }
                },
                'H', roots,
                'L', leaves,
                'C', blossom,
                '0', roots
        );
    });

    public static final Supplier<IMultiblock> SQUALL_GOLEM = Suppliers.memoize(() -> {
        IStateMatcher highrockMold = PatchouliAPI.get().predicateMatcher(ModBlocks.POLISHED_HIGHROCK_BLOCK.get(),
                state -> state.is(ModBlocks.POLISHED_HIGHROCK_BLOCK.get()));
        IStateMatcher bricks = PatchouliAPI.get().predicateMatcher(Blocks.STONE_BRICKS,
                state -> state.is(Blocks.STONE_BRICKS));
        IStateMatcher gold = PatchouliAPI.get().predicateMatcher(ModBlocks.INDENTED_GOLD_BLOCK.get(),
                state -> state.is(ModTags.Blocks.INDENTED_GOLD_BLOCKS));
        IStateMatcher jade = PatchouliAPI.get().predicateMatcher(ModBlocks.JADE_BLOCK.get(),
                state -> state.is(ModBlocks.JADE_BLOCK.get()));
        return PatchouliAPI.get().makeMultiblock(
                new String[][] {
                        {
                                "_________",
                                "___HHH___",
                                "__HSGSH__",
                                "__HGJGH__",
                                "__HSGSH__",
                                "___HHH___",
                                "_________"
                        },
                        {
                                "_________",
                                "_________",
                                "___HHH___",
                                "___H0H___",
                                "___HHH___",
                                "_________",
                                "_________"
                        }
                },
                'H', highrockMold,
                'S', bricks,
                'G', gold,
                'J', jade,
                '0', highrockMold
        );
    });

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

    public static final Supplier<IMultiblock> GRAVE_GOLEM = Suppliers.memoize(() -> {
        IStateMatcher stoneMold = PatchouliAPI.get().predicateMatcher(Blocks.STONE_BRICKS,
                state -> state.getBlock().getDescriptionId().contains("bricks") && !(state.getBlock() instanceof SlabBlock) && !(state.getBlock() instanceof StairBlock) && !(state.getBlock() instanceof WallBlock));
        IStateMatcher darkMetalMold = PatchouliAPI.get().predicateMatcher(ModBlocks.DARK_METAL_BLOCK.get(),
                state -> state.is(ModBlocks.DARK_METAL_BLOCK.get()));
        IStateMatcher coarseDirt = PatchouliAPI.get().predicateMatcher(Blocks.COARSE_DIRT,
                state -> state.is(Blocks.COARSE_DIRT));
        IStateMatcher skullPiles = PatchouliAPI.get().predicateMatcher(ModBlocks.SKULL_PILE.get(),
                state -> state.is(ModBlocks.SKULL_PILE.get()));
        IStateMatcher shadeBody = PatchouliAPI.get().predicateMatcher(ModBlocks.SHADE_STONE_BLOCK.get(),
                state -> state.is(ModTags.Blocks.SHADE_STONE));
        IStateMatcher boneMold = PatchouliAPI.get().predicateMatcher(Blocks.BONE_BLOCK,
                state -> state.is(Blocks.BONE_BLOCK));
        IStateMatcher soulSand = PatchouliAPI.get().predicateMatcher(Blocks.SOUL_SAND,
                state -> state.is(BlockTags.SOUL_FIRE_BASE_BLOCKS));
        return PatchouliAPI.get().makeMultiblock(
                new String[][] {
                        {
                                "_________",
                                "_DBBBBBD_",
                                "_BLLLLLB_",
                                "_BLOPOLB_",
                                "_BLPPPLB_",
                                "_BLOPOLB_",
                                "_BLLLLLB_",
                                "_DBBBBBD_",
                                "_________",
                        },
                        {
                                "_________",
                                "_________",
                                "__CCCCC__",
                                "__CSLSC__",
                                "__CL0LC__",
                                "__CSLSC__",
                                "__CCCCC__",
                                "_________",
                                "_________",
                        }
                },
                'L', shadeBody,
                'B', stoneMold,
                'D', darkMetalMold,
                'P', skullPiles,
                'O', boneMold,
                'C', coarseDirt,
                'S', soulSand,
                '0', shadeBody
        );
    });
}
