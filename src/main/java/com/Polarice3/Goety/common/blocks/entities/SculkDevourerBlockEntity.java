package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.common.blocks.SculkDevourerBlock;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;

public class SculkDevourerBlockEntity extends OwnedBlockEntity implements GameEventListener {
    private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);

    public SculkDevourerBlockEntity(BlockPos p_222774_, BlockState p_222775_) {
        super(ModBlockEntities.SCULK_DEVOURER.get(), p_222774_, p_222775_);
    }

    public boolean handleEventsImmediately() {
        return true;
    }

    public PositionSource getListenerSource() {
        return this.blockPosSource;
    }

    public int getListenerRadius() {
        return 8;
    }

    public boolean handleGameEvent(ServerLevel p_222777_, GameEvent.Message p_222778_) {
        if (!this.isRemoved()) {
            GameEvent.Context gameevent$context = p_222778_.context();
            if (p_222778_.gameEvent() == GameEvent.ENTITY_DIE) {
                Entity $$4 = gameevent$context.sourceEntity();
                if ($$4 instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity) $$4;
                    if (!livingentity.wasExperienceConsumed() && this.getPlayer() != null && SEHelper.getSoulsContainer(this.getPlayer())) {
                        int i = livingentity.getExperienceReward();
                        if (livingentity.shouldDropExperience() && i > 0) {
                            SEHelper.increaseSouls(this.getPlayer(), i);
                        }

                        livingentity.skipDropExperience();
                        SculkDevourerBlock.bloom(p_222777_, this.worldPosition, this.getBlockState(), p_222777_.getRandom());
                    }

                    return true;
                }
            }

        }
        return false;
    }
}
