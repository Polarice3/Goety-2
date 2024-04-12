package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.common.magic.BlockSpell;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;

public class StateSpell extends BlockSpell {

    @Override
    public int defaultSoulCost() {
        return 0;
    }

    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public int defaultSpellCooldown() {
        return 0;
    }

    @Override
    public boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        BlockState blockState = worldIn.getBlockState(target);
        for (Direction direction : Direction.values()){
            if (!blockState.canSurvive(worldIn, target.relative(direction))){
                return false;
            }
        }
        return true;
    }

    @Override
    public void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        if (caster instanceof Player player) {
            BlockState blockState = worldIn.getBlockState(target);
            Block block = blockState.getBlock();
            StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
            Collection<Property<?>> collection = statedefinition.getProperties();
            ResourceLocation resourceLocation = ForgeRegistries.BLOCKS.getKey(block);
            if (resourceLocation != null) {
                String s = resourceLocation.toString();
                CompoundTag compoundtag = WandUtil.findFocus(player).getOrCreateTagElement("BlockProperty");
                String s1 = compoundtag.getString(s);
                Property<?> property = statedefinition.getProperty(s1);
                if (property == null || !canChange(property)) {
                    property = collection.iterator().next();
                }

                if (canChange(property)) {
                    BlockState blockstate = cycleState(blockState, property, player.isSecondaryUseActive());
                    worldIn.setBlock(target, blockstate, 18);
                }
            }
        }
    }

    public static boolean canChange(Property<?> property){
        return property instanceof DirectionProperty || property == BlockStateProperties.HORIZONTAL_AXIS
                || property == BlockStateProperties.AXIS || property == BlockStateProperties.ROTATION_16
                || property == BlockStateProperties.SLAB_TYPE || property == BlockStateProperties.NORTH_WALL
                || property == BlockStateProperties.SOUTH_WALL || property == BlockStateProperties.EAST_WALL
                || property == BlockStateProperties.WEST_WALL;
    }

    private static <T extends Comparable<T>> BlockState cycleState(BlockState blockState, Property<T> property, boolean crouching) {
        return blockState.setValue(property, getRelative(property.getPossibleValues(), blockState.getValue(property), crouching));
    }

    private static <T> T getRelative(Iterable<T> iterable, @Nullable T p_40975_, boolean crouching) {
        return (T)(crouching ? Util.findPreviousInIterable(iterable, p_40975_) : Util.findNextInIterable(iterable, p_40975_));
    }

    private static void message(LivingEntity livingEntity, Component component) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(component, true);
        }
    }

    private static <T extends Comparable<T>> String getNameHelper(BlockState blockState, Property<T> property) {
        return property.getName(blockState.getValue(property));
    }
}
