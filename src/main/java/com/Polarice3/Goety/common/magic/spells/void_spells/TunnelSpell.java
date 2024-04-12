package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.HoleBlockEntity;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.BlockSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TunnelSpell extends BlockSpell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.TunnelCost.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.TunnelCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        BlockState blockState = worldIn.getBlockState(target);
        return !blockState.hasBlockEntity() && !blockState.is(ModTags.Blocks.TUNNEL_BLACKLIST) && blockState.getDestroySpeed(worldIn, target) != -1.0F;
    }

    @Override
    public void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        BlockHitResult blockHitResult = MobUtil.rayTrace(caster, 8, false);
        Direction direction = blockHitResult.getDirection();
        BlockPos blockPos = new BlockPos(blockHitResult.getBlockPos());
        int totalDistance = SpellConfig.TunnelDefaultDistance.get();
        int extraLife = 0;
        if (WandUtil.enchantedFocus(caster)) {
            totalDistance += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
            extraLife += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        for (int distance = 0; distance < totalDistance; ++distance) {
            BlockState blockState = worldIn.getBlockState(blockPos);
            if (blockState.is(ModTags.Blocks.TUNNEL_BLACKLIST) || blockState.getBlock() == ModBlocks.HOLE.get() || blockState.isAir()) {
                break;
            }
            if (blockState.getDestroySpeed(worldIn, blockPos) == -1.0F) {
                break;
            }
            blockPos = blockPos.relative(direction.getOpposite());
        }
        createHole(worldIn, blockHitResult.getBlockPos(), direction, (byte)Math.round((float)(totalDistance + 1)), SpellConfig.TunnelDefaultLifespan.get() + (extraLife * 20));
        this.playSound(worldIn, caster, ModSounds.CAST_SPELL.get());
    }

    public static boolean createHole(Level world, BlockPos blockPos, Direction direction, int count, int lifespan) {
        BlockState blockState = world.getBlockState(blockPos);
        if (!world.isClientSide && world.getBlockEntity(blockPos) == null
                && !blockState.is(ModTags.Blocks.TUNNEL_BLACKLIST)
                && blockState.getBlock() != ModBlocks.HOLE.get()
                && (blockState.isAir() || !BlockFinder.canBeReplaced(world, blockPos))
                && blockState.getDestroySpeed(world, blockPos) != -1.0F) {
            if (world.setBlockAndUpdate(blockPos, ModBlocks.HOLE.get().defaultBlockState())) {
                HoleBlockEntity newHole = (HoleBlockEntity)world.getBlockEntity(blockPos);
                if (newHole != null) {
                    newHole.setStats(blockState, lifespan, count, direction);
                    return true;
                }
            }
        }
        return false;
    }
}
