package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class CastMagicEvent extends LivingEvent {
    private final ISpell spell;

    public CastMagicEvent(LivingEntity entity, ISpell spell) {
        super(entity);
        this.spell = spell;
    }

    public ISpell getSpell(){
        return this.spell;
    }
}
