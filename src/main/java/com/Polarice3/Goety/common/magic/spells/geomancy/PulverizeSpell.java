package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.magic.BlockSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public class PulverizeSpell extends BlockSpells {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.PulverizeCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.PulverizeCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        BlockState blockState = worldIn.getBlockState(target);
        return blockState.is(Blocks.DEEPSLATE) || blockState.is(Blocks.COBBLED_DEEPSLATE)
                || blockState.is(Blocks.STONE) || blockState.is(Tags.Blocks.COBBLESTONE)
                || blockState.is(Blocks.BLACKSTONE)

                || blockState.is(Blocks.DEEPSLATE_BRICKS) || blockState.is(Blocks.DEEPSLATE_TILES)
                || blockState.is(Blocks.POLISHED_DEEPSLATE)

                || blockState.is(Blocks.DEEPSLATE_BRICK_SLAB) || blockState.is(Blocks.DEEPSLATE_TILE_SLAB)
                || blockState.is(Blocks.POLISHED_DEEPSLATE_SLAB)

                || blockState.is(Blocks.DEEPSLATE_BRICK_STAIRS) || blockState.is(Blocks.DEEPSLATE_TILE_STAIRS)
                || blockState.is(Blocks.POLISHED_DEEPSLATE_STAIRS)

                || blockState.is(Blocks.DEEPSLATE_BRICK_WALL) || blockState.is(Blocks.DEEPSLATE_TILE_WALL)
                || blockState.is(Blocks.POLISHED_DEEPSLATE_WALL)

                || blockState.is(Blocks.NETHER_BRICKS) || blockState.is(Blocks.STONE_BRICKS)
                || blockState.is(Blocks.END_STONE_BRICKS)

                || blockState.is(Blocks.CHISELED_NETHER_BRICKS)

                || blockState.is(Blocks.STONE_STAIRS) || blockState.is(Blocks.STONE_SLAB)

                || blockState.is(Blocks.STONE_BRICK_SLAB) || blockState.is(Blocks.STONE_BRICK_STAIRS)
                || blockState.is(Blocks.STONE_BRICK_WALL)

                || blockState.is(Blocks.SMOOTH_STONE) || blockState.is(Blocks.SMOOTH_STONE_SLAB)

                || blockState.is(Blocks.MOSSY_STONE_BRICKS) || blockState.is(Blocks.MOSSY_STONE_BRICK_SLAB)
                || blockState.is(Blocks.MOSSY_STONE_BRICK_STAIRS) || blockState.is(Blocks.MOSSY_STONE_BRICK_WALL)

                || blockState.is(Blocks.CRACKED_DEEPSLATE_BRICKS) || blockState.is(Blocks.CRACKED_DEEPSLATE_TILES)
                || blockState.is(Blocks.CRACKED_STONE_BRICKS) || blockState.is(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)

                || blockState.is(Blocks.POLISHED_BLACKSTONE) || blockState.is(Blocks.POLISHED_BLACKSTONE_SLAB)
                || blockState.is(Blocks.POLISHED_BLACKSTONE_STAIRS) || blockState.is(Blocks.POLISHED_BLACKSTONE_WALL)

                || blockState.is(Blocks.POLISHED_BLACKSTONE_BRICKS) || blockState.is(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB)
                || blockState.is(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS) || blockState.is(Blocks.POLISHED_BLACKSTONE_BRICK_WALL)

                || blockState.is(Blocks.CHISELED_DEEPSLATE) || blockState.is(Blocks.CHISELED_STONE_BRICKS)
                || blockState.is(Blocks.CHISELED_POLISHED_BLACKSTONE)

                || blockState.is(Blocks.SANDSTONE) || blockState.is(Blocks.RED_SANDSTONE)

                || blockState.is(Blocks.SMOOTH_SANDSTONE) || blockState.is(Blocks.SMOOTH_RED_SANDSTONE)
                || blockState.is(Blocks.SMOOTH_SANDSTONE_SLAB) || blockState.is(Blocks.SMOOTH_RED_SANDSTONE_SLAB)
                || blockState.is(Blocks.SMOOTH_SANDSTONE_STAIRS) || blockState.is(Blocks.SMOOTH_RED_SANDSTONE_STAIRS)

                || blockState.is(Blocks.CUT_SANDSTONE) || blockState.is(Blocks.CUT_RED_SANDSTONE)
                || blockState.is(Blocks.CUT_SANDSTONE_SLAB) || blockState.is(Blocks.CUT_RED_SANDSTONE_SLAB)

                || blockState.is(Blocks.POLISHED_ANDESITE) || blockState.is(Blocks.POLISHED_DIORITE)
                || blockState.is(Blocks.POLISHED_GRANITE)

                || blockState.is(Blocks.POLISHED_ANDESITE_SLAB) || blockState.is(Blocks.POLISHED_DIORITE_SLAB)
                || blockState.is(Blocks.POLISHED_GRANITE_SLAB)

                || blockState.is(Blocks.POLISHED_ANDESITE_STAIRS) || blockState.is(Blocks.POLISHED_DIORITE_STAIRS)
                || blockState.is(Blocks.POLISHED_GRANITE_STAIRS)

                || blockState.is(Blocks.ANDESITE) || blockState.is(Blocks.DIORITE)
                || blockState.is(Blocks.GRANITE)

                || blockState.is(Blocks.CHISELED_QUARTZ_BLOCK) || blockState.is(Blocks.QUARTZ_BRICKS)
                || blockState.is(Blocks.QUARTZ_PILLAR) || blockState.is(Blocks.SMOOTH_QUARTZ)

                || blockState.is(Blocks.SMOOTH_QUARTZ_SLAB) || blockState.is(Blocks.SMOOTH_QUARTZ_STAIRS)

                || blockState.is(Blocks.PRISMARINE_BRICKS) || blockState.is(Blocks.DARK_PRISMARINE)
                || blockState.is(Blocks.PRISMARINE)

                || blockState.is(Blocks.AMETHYST_BLOCK) || blockState.is(Blocks.BRICKS)
                || blockState.is(Blocks.MUD_BRICKS)

                || blockState.is(Blocks.CLAY) || blockState.is(Blocks.DRIPSTONE_BLOCK)
                || blockState.is(Blocks.BONE_BLOCK)

                || blockState.is(Blocks.GRAVEL)

                || blockState.is(Blocks.CRACKED_NETHER_BRICKS);
    }

    @Override
    public void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        BlockState blockState = worldIn.getBlockState(target);
        if (rightBlock(worldIn, caster, target)){
            worldIn.levelEvent(2001, target, Block.getId(worldIn.getBlockState(target)));
            BlockState pulverized = Blocks.CAVE_AIR.defaultBlockState();
            if (blockState.is(Blocks.DEEPSLATE)
                    || blockState.is(Blocks.POLISHED_DEEPSLATE)
                    || blockState.is(Blocks.CRACKED_DEEPSLATE_BRICKS)
                    || blockState.is(Blocks.CRACKED_DEEPSLATE_TILES)
                    || blockState.is(Blocks.CHISELED_DEEPSLATE)){
                pulverized = Blocks.COBBLED_DEEPSLATE.defaultBlockState();
            } else if (blockState.is(Blocks.COBBLED_DEEPSLATE)
                    || blockState.is(Blocks.STONE)
                    || blockState.is(Blocks.SMOOTH_STONE)
                    || blockState.is(Blocks.CRACKED_STONE_BRICKS)
                    || blockState.is(Blocks.CHISELED_STONE_BRICKS)){
                pulverized = Blocks.COBBLESTONE.defaultBlockState();
            } else if (blockState.is(Tags.Blocks.COBBLESTONE) || blockState.is(Blocks.BLACKSTONE)){
                pulverized = Blocks.GRAVEL.defaultBlockState();
            } else if (blockState.is(Blocks.GRAVEL) || blockState.is(Blocks.SANDSTONE)){
                pulverized = Blocks.SAND.defaultBlockState();
            } else if (blockState.is(Blocks.RED_SANDSTONE)){
                pulverized = Blocks.RED_SAND.defaultBlockState();
            } else if (blockState.is(Blocks.CHISELED_NETHER_BRICKS)){
                pulverized = Blocks.NETHER_BRICKS.defaultBlockState();
            } else if (blockState.is(Blocks.MUD_BRICKS)){
                pulverized = Blocks.PACKED_MUD.defaultBlockState();
            } else if (blockState.is(Blocks.DEEPSLATE_BRICKS)){
                pulverized = Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState();
            } else if (blockState.is(Blocks.DEEPSLATE_TILES)){
                pulverized = Blocks.CRACKED_DEEPSLATE_TILES.defaultBlockState();
            } else if (blockState.is(Blocks.NETHER_BRICKS)){
                pulverized = Blocks.CRACKED_NETHER_BRICKS.defaultBlockState();
            } else if (blockState.is(Blocks.STONE_BRICKS)){
                pulverized = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
            } else if (blockState.is(Blocks.POLISHED_BLACKSTONE_BRICKS)){
                pulverized = Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
            } else if (blockState.is(Blocks.STONE_STAIRS) || blockState.is(Blocks.STONE_BRICK_STAIRS)){
                pulverized = Blocks.COBBLESTONE_STAIRS.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.STONE_SLAB) || blockState.is(Blocks.STONE_BRICK_SLAB)
                    || blockState.is(Blocks.SMOOTH_STONE_SLAB)){
                pulverized = Blocks.COBBLESTONE_SLAB.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.COBBLED_DEEPSLATE_WALL) || blockState.is(Blocks.STONE_BRICK_WALL)){
                pulverized = Blocks.COBBLESTONE_WALL.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.MOSSY_STONE_BRICKS)){
                pulverized = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            } else if (blockState.is(Blocks.MOSSY_STONE_BRICK_STAIRS)){
                pulverized = Blocks.MOSSY_COBBLESTONE_STAIRS.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.MOSSY_STONE_BRICK_SLAB)){
                pulverized = Blocks.MOSSY_COBBLESTONE_SLAB.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.MOSSY_STONE_BRICK_WALL)){
                pulverized = Blocks.MOSSY_COBBLESTONE_WALL.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.DEEPSLATE_BRICK_SLAB) || blockState.is(Blocks.DEEPSLATE_TILE_SLAB)
                    || blockState.is(Blocks.POLISHED_DEEPSLATE_SLAB)){
                pulverized = Blocks.COBBLED_DEEPSLATE_SLAB.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.DEEPSLATE_BRICK_STAIRS) || blockState.is(Blocks.DEEPSLATE_TILE_STAIRS)
                    || blockState.is(Blocks.POLISHED_DEEPSLATE_STAIRS)){
                pulverized = Blocks.COBBLED_DEEPSLATE_STAIRS.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.DEEPSLATE_BRICK_WALL) || blockState.is(Blocks.DEEPSLATE_TILE_WALL)
                    || blockState.is(Blocks.POLISHED_DEEPSLATE_WALL)){
                pulverized = Blocks.COBBLED_DEEPSLATE_WALL.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS) || blockState.is(Blocks.POLISHED_BLACKSTONE)
                    || blockState.is(Blocks.CHISELED_POLISHED_BLACKSTONE)){
                pulverized = Blocks.BLACKSTONE.defaultBlockState();
            } else if (blockState.is(Blocks.POLISHED_BLACKSTONE_SLAB) || blockState.is(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB)){
                pulverized = Blocks.BLACKSTONE_SLAB.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.POLISHED_BLACKSTONE_STAIRS) || blockState.is(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS)){
                pulverized = Blocks.BLACKSTONE_STAIRS.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.POLISHED_BLACKSTONE_WALL) || blockState.is(Blocks.POLISHED_BLACKSTONE_BRICK_WALL)){
                pulverized = Blocks.BLACKSTONE_WALL.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.SMOOTH_SANDSTONE) || blockState.is(Blocks.CUT_SANDSTONE)){
                pulverized = Blocks.SANDSTONE.defaultBlockState();
            } else if (blockState.is(Blocks.SMOOTH_RED_SANDSTONE) || blockState.is(Blocks.CUT_RED_SANDSTONE)){
                pulverized = Blocks.RED_SANDSTONE.defaultBlockState();
            } else if (blockState.is(Blocks.SMOOTH_SANDSTONE_SLAB) || blockState.is(Blocks.CUT_SANDSTONE_SLAB)){
                pulverized = Blocks.SANDSTONE_SLAB.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.SMOOTH_RED_SANDSTONE_SLAB) || blockState.is(Blocks.CUT_RED_SANDSTONE_SLAB)){
                pulverized = Blocks.RED_SANDSTONE_SLAB.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.SMOOTH_SANDSTONE_STAIRS)){
                pulverized = Blocks.SANDSTONE_STAIRS.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.SMOOTH_RED_SANDSTONE_STAIRS)){
                pulverized = Blocks.RED_SANDSTONE_STAIRS.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.END_STONE_BRICKS)){
                pulverized = Blocks.END_STONE.defaultBlockState();
            } else if (blockState.is(Blocks.POLISHED_GRANITE)){
                pulverized = Blocks.GRANITE.defaultBlockState();
            } else if (blockState.is(Blocks.POLISHED_DIORITE)){
                pulverized = Blocks.DIORITE.defaultBlockState();
            } else if (blockState.is(Blocks.POLISHED_ANDESITE)){
                pulverized = Blocks.ANDESITE.defaultBlockState();
            } else if (blockState.is(Blocks.POLISHED_GRANITE_SLAB)){
                pulverized = Blocks.GRANITE_SLAB.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.POLISHED_DIORITE_SLAB)){
                pulverized = Blocks.DIORITE_SLAB.withPropertiesOf(blockState);;
            } else if (blockState.is(Blocks.POLISHED_ANDESITE_SLAB)){
                pulverized = Blocks.ANDESITE_SLAB.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.POLISHED_GRANITE_STAIRS)){
                pulverized = Blocks.GRANITE_STAIRS.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.POLISHED_DIORITE_STAIRS)){
                pulverized = Blocks.DIORITE_STAIRS.withPropertiesOf(blockState);;
            } else if (blockState.is(Blocks.POLISHED_ANDESITE_STAIRS)){
                pulverized = Blocks.ANDESITE_STAIRS.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.SMOOTH_QUARTZ) || blockState.is(Blocks.CHISELED_QUARTZ_BLOCK)
                    || blockState.is(Blocks.QUARTZ_BRICKS) || blockState.is(Blocks.QUARTZ_PILLAR)){
                pulverized = Blocks.QUARTZ_BLOCK.defaultBlockState();
            } else if (blockState.is(Blocks.SMOOTH_QUARTZ_STAIRS)){
                pulverized = Blocks.QUARTZ_STAIRS.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.SMOOTH_QUARTZ_SLAB)){
                pulverized = Blocks.QUARTZ_SLAB.withPropertiesOf(blockState);
            } else if (blockState.is(Blocks.ANDESITE)){
                pulverized = Blocks.DIORITE.defaultBlockState();
            } else if (blockState.is(Blocks.GRANITE)){
                ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.QUARTZ));
                pulverized = Blocks.DIORITE.defaultBlockState();
            } else if (blockState.is(Blocks.DIORITE)){
                ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.QUARTZ));
                pulverized = Blocks.COBBLESTONE.defaultBlockState();
            } else if (blockState.is(Blocks.CRACKED_NETHER_BRICKS)){
                for (int i = 0; i < 4; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.NETHER_BRICK));
                }
            } else if (blockState.is(Blocks.PRISMARINE_BRICKS)){
                pulverized = Blocks.PRISMARINE.defaultBlockState();
                for (int i = 0; i < 5; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.PRISMARINE_SHARD));
                }
            } else if (blockState.is(Blocks.DARK_PRISMARINE)){
                pulverized = Blocks.PRISMARINE.defaultBlockState();
                for (int i = 0; i < 4; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.PRISMARINE_SHARD));
                }
                ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.BLACK_DYE));
            } else if (blockState.is(Blocks.PRISMARINE)){
                for (int i = 0; i < 4; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.PRISMARINE_SHARD));
                }
            } else if (blockState.is(Blocks.AMETHYST_BLOCK)){
                for (int i = 0; i < 4; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.AMETHYST_SHARD));
                }
            } else if (blockState.is(Blocks.QUARTZ_BLOCK)){
                for (int i = 0; i < 4; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.QUARTZ));
                }
            } else if (blockState.is(Blocks.BRICKS)){
                for (int i = 0; i < 4; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.BRICK));
                }
            } else if (blockState.is(Blocks.DRIPSTONE_BLOCK)){
                for (int i = 0; i < 4; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.POINTED_DRIPSTONE));
                }
            } else if (blockState.is(Blocks.CLAY)){
                for (int i = 0; i < 4; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.CLAY_BALL));
                }
            } else if (blockState.is(Blocks.BONE_BLOCK)){
                for (int i = 0; i < 9; ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(Items.BONE_MEAL));
                }
            }
            worldIn.setBlockAndUpdate(target, pulverized);
        }
    }
}
