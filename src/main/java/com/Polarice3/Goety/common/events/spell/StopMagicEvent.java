package com.Polarice3.Goety.common.events.spell;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * CastingMagicEvent is fired when stopped using {@link com.Polarice3.Goety.common.items.magic.DarkWand} to cast a spell. <br>
 * <br>
 * This event is fired via the {@link GoetyEventFactory#onStopSpell(LivingEntity, ItemStack, ISpell, int, int)}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the spell is not cast and no effects that happen when stopped in the middle of casting will occur.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class StopMagicEvent extends LivingEvent {
    private final ISpell spell;
    private final ItemStack useItem;
    private final int castTime;
    private final int timeRemaining;

    public StopMagicEvent(LivingEntity entity, ItemStack useItem, ISpell spell, int castTime, int timeRemaining) {
        super(entity);
        this.useItem = useItem;
        this.spell = spell;
        this.castTime = castTime;
        this.timeRemaining = timeRemaining;
    }

    public ISpell getSpell(){
        return this.spell;
    }

    public ItemStack getUseItem() {
        return this.useItem;
    }

    public int castingTime() {
        return this.castTime;
    }

    public int getTimeRemaining() {
        return this.timeRemaining;
    }
}
