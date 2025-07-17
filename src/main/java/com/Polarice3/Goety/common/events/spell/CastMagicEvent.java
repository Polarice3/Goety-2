package com.Polarice3.Goety.common.events.spell;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * CastMagicEvent is fired when finished using {@link com.Polarice3.Goety.common.items.magic.DarkWand} to cast spell. <br>
 * <br>
 * This event is fired via the {@link GoetyEventFactory#onCastSpell(LivingEntity, ISpell)}.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the spell is not cast.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class CastMagicEvent extends LivingEvent {
    private ISpell spell;

    public CastMagicEvent(LivingEntity entity, ISpell spell) {
        super(entity);
        this.spell = spell;
    }

    public ISpell getSpell(){
        return this.spell;
    }

    public void setSpell(ISpell spell) {
        this.spell = spell;
    }
}
