package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.common.magic.BlockSpells;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public class PulverizeSpell extends BlockSpells {
    @Override
    public int SoulCost() {
        return 0;
    }

    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public int SpellCooldown() {
        return 0;
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
                || blockState.is(Blocks.GRAVEL);
    }

    @Override
    public void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        BlockState blockState = worldIn.getBlockState(target);
        if (blockState.is(Blocks.DEEPSLATE)){
            worldIn.levelEvent(2001, target, Block.getId(worldIn.getBlockState(target)));
            worldIn.setBlockAndUpdate(target, Blocks.COBBLED_DEEPSLATE.defaultBlockState());
        } else if (blockState.is(Blocks.COBBLED_DEEPSLATE) || blockState.is(Blocks.STONE)){
            worldIn.levelEvent(2001, target, Block.getId(worldIn.getBlockState(target)));
            worldIn.setBlockAndUpdate(target, Blocks.COBBLESTONE.defaultBlockState());
        } else if (blockState.is(Tags.Blocks.COBBLESTONE) || blockState.is(Blocks.BLACKSTONE)){
            worldIn.levelEvent(2001, target, Block.getId(worldIn.getBlockState(target)));
            worldIn.setBlockAndUpdate(target, Blocks.GRAVEL.defaultBlockState());
        } else if (blockState.is(Blocks.GRAVEL)){
            worldIn.levelEvent(2001, target, Block.getId(worldIn.getBlockState(target)));
            worldIn.setBlockAndUpdate(target, Blocks.SAND.defaultBlockState());
        }
    }
}
