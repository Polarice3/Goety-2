package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.EndLanternEmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EndLanternBlockEntity extends BlockEntity {
    private int age = -1;

    public EndLanternBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.END_LANTERN.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, EndLanternBlockEntity blockEntity) {
        if (blockEntity.age < 0) {
            blockEntity.age = level.getRandom().nextInt(EndLanternEmitter.MAIN_INTERVAL);
        }
        if (state.getBlock() instanceof EndLanternEmitter block) {
            for (EndLanternEmitter.Emitter emitter : block.getEmitters(state)) {
                Vec3 offset = emitter.offset();
                EndLanternEmitter.tick(level, emitter, pos.getX() + offset.x, pos.getY() + offset.y, pos.getZ() + offset.z, blockEntity.age, level.getRandom());
            }
        }
        blockEntity.age++;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.worldPosition, this.worldPosition.offset(1, 3, 1));
    }
}
