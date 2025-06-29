package com.Polarice3.Goety.common.events.spell;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * CastingMagicEvent is fired when started to use {@link com.Polarice3.Goety.common.items.magic.DarkWand} with a spell. <br>
 * <br>
 * This event is fired via the {@link GoetyEventFactory#onStartSpell(LivingEntity, ItemStack, ISpell)}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the spell is not cast.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class StartMagicEvent extends LivingEvent {
    private ISpell spell;
    private final ItemStack useItem;

    public StartMagicEvent(LivingEntity entity, ItemStack useItem, ISpell spell) {
        super(entity);
        this.useItem = useItem;
        this.spell = spell;
    }

    public ISpell getSpell(){
        return this.spell;
    }

    public void setSpell(ISpell spell) {
        this.spell = spell;
    }

    public ItemStack getUseItem() {
        return this.useItem;
    }
}
