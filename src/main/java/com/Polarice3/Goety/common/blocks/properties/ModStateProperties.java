package com.Polarice3.Goety.common.blocks.properties;

import com.Polarice3.Goety.common.blocks.entities.void_spawner.VoidSpawnerState;
import com.Polarice3.Goety.common.blocks.entities.void_vault.VoidVaultState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModStateProperties {
    public static final IntegerProperty LEVEL_TOWER = IntegerProperty.create("level", 0, 3);
    public static final IntegerProperty LEVEL_BREW = IntegerProperty.create("level", 0, 4);
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 5);
    public static final IntegerProperty PLUSHIE_TYPE = IntegerProperty.create("plushie_type", 0, 64);
    public static final BooleanProperty FAILED = BooleanProperty.create("failed");
    public static final BooleanProperty GENERATED = BooleanProperty.create("generated");
    public static final BooleanProperty VOID = BooleanProperty.create("void");
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public static final EnumProperty<VoidSpawnerState> VOID_SPAWNER_STATE = EnumProperty.create("void_spawner_state", VoidSpawnerState.class);
    public static final EnumProperty<VoidVaultState> VOID_VAULT_STATE = EnumProperty.create("void_vault_state", VoidVaultState.class);

}
