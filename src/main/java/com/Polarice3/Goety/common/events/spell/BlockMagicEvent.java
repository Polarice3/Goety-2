package com.Polarice3.Goety.common.events.spell;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

/**
 * CastMagicEvent is fired when right-clicking on a block {@link com.Polarice3.Goety.common.items.magic.DarkWand} with a spell. <br>
 * <br>
 * This event is fired via the {@link GoetyEventFactory#onBlockBasedSpell(LevelAccessor, BlockPos, BlockState, ISpell, Direction, LivingEntity)}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the spell is not cast.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class BlockMagicEvent extends BlockEvent {
    private ISpell spell;
    @Nullable
    private final Direction direction;
    private final LivingEntity caster;

    public BlockMagicEvent(LevelAccessor level, BlockPos pos, BlockState state, ISpell spell, @Nullable Direction direction, LivingEntity caster) {
        super(level, pos, state);
        this.spell = spell;
        this.direction = direction;
        this.caster = caster;
    }

    public ISpell getSpell() {
        return this.spell;
    }

    public void setSpell(ISpell spell) {
        this.spell = spell;
    }

    @Nullable
    public Direction getDirection() {
        return this.direction;
    }

    public LivingEntity getCaster() {
        return this.caster;
    }
}
