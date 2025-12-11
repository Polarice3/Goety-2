package com.Polarice3.Goety.common.events.spell;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * ChangeSoulEnergyEvent is fired when player gains or loss Soul Energy. <br>
 * <br>
 * This event is fired via both {@link GoetyEventFactory#onSoulEnergyGain(Player, int)} & {@link GoetyEventFactory#onSoulEnergyLoss(Player, int)}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the amount is set to 0.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ChangeSoulEnergyEvent extends PlayerEvent {
    private int soulChange;

    public ChangeSoulEnergyEvent(Player entity, int soulChange) {
        super(entity);
        this.soulChange = soulChange;
    }

    public int getSoulChange() {
        return this.soulChange;
    }

    public void setSoulChange(int soulChange) {
        this.soulChange = soulChange;
    }

    @Cancelable
    public static class Gain extends ChangeSoulEnergyEvent {
        public Gain(Player e, int soulChange){
            super(e, soulChange);
        }
    }

    @Cancelable
    public static class Loss extends ChangeSoulEnergyEvent {
        public Loss(Player e, int soulChange){
            super(e, soulChange);
        }
    }
}
