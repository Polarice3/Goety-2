package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.common.magic.BlockSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class RotationSpell extends BlockSpell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.RotationCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.TOCK.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.RotationCoolDown.get();
    }

    @Override
    public boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        BlockHitResult blockHitResult = MobUtil.rayTrace(caster, 8, false);
        Direction direction0 = blockHitResult.getDirection();
        return blockCanRotate(worldIn, target, direction0);
    }

    @Override
    public void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        worldIn.playSound(null, target, ModSounds.TOCK.get(), this.getSoundSource(), 1.0F, 1.0F);
    }

    /**
     * Based on @Lorthrazer's codes: <a href="https://github.com/Lothrazar/FLib/blob/trunk/1.20/src/main/java/com/lothrazar/library/util/BlockUtil.java#L30">...</a>
     */
    public static boolean blockCanRotate(Level world, BlockPos pos, Direction side) {
        BlockState oldState = world.getBlockState(pos);
        BlockState newState = null;
        if (oldState.is(BlockTags.SLABS)) {
            final String key = "type";
            final String valueDupe = "double";
            for (Property<?> prop : oldState.getProperties()) {
                if (prop.getName().equals(key)) {
                    newState = oldState.cycle(prop);
                    if (newState.getValue(prop).toString().equals(valueDupe)) {
                        newState = newState.cycle(prop);
                    }
                }
            }
        } else if (oldState.hasProperty(RotatedPillarBlock.AXIS)) {
            Direction.Axis current = oldState.getValue(RotatedPillarBlock.AXIS);
            switch (current) {
                case X:
                    newState = oldState.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
                    break;
                case Y:
                    newState = oldState.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Z);
                    break;
                case Z:
                    newState = oldState.setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
                    break;
                default:
                    break;
            }
        } else if (oldState.hasProperty(BlockStateProperties.ROTATION_16)){
            newState = oldState.setValue(BlockStateProperties.ROTATION_16, (oldState.getValue(BlockStateProperties.ROTATION_16) + 1) & 15);
        } else {
            switch (side) {
                case DOWN, UP:
                    newState = oldState.rotate(world, pos, Rotation.CLOCKWISE_180);
                    break;
                case EAST, SOUTH:
                    newState = oldState.rotate(world, pos, Rotation.CLOCKWISE_90);
                    break;
                case NORTH, WEST:
                    newState = oldState.rotate(world, pos, Rotation.COUNTERCLOCKWISE_90);
                    break;
                default:
                    break;
            }
        }
        if (oldState.hasProperty(BedBlock.PART) || oldState.hasProperty(ChestBlock.TYPE)){
            newState = null;
        }
        boolean rotated = false;
        if (newState != null && newState.canSurvive(world, pos)) {
            rotated = world.setBlockAndUpdate(pos, newState);
        }
        return rotated;
    }
}
