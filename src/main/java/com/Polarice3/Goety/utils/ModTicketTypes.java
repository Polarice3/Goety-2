package com.Polarice3.Goety.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.TicketType;

public class ModTicketTypes {
    public static final TicketType<BlockPos> SERVANT = TicketType.create("goety:servant", Vec3i::compareTo, 40);
    public static final TicketType<BlockPos> BLOCK = TicketType.create("goety:block", Vec3i::compareTo, 20);
}
