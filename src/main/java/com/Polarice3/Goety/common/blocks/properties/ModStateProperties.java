package com.Polarice3.Goety.common.blocks.properties;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModStateProperties {
    public static final IntegerProperty LEVEL_BREW = IntegerProperty.create("level", 0, 4);
    public static final BooleanProperty FAILED = BooleanProperty.create("failed");
    public static final BooleanProperty GENERATED = BooleanProperty.create("generated");
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
}
