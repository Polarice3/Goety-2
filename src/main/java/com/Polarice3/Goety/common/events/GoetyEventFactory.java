package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.api.magic.ISpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

public class GoetyEventFactory {

    public static ISpell onCastSpell(LivingEntity livingEntity, ISpell spell){
        CastMagicEvent event = new CastMagicEvent(livingEntity, spell);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getSpell();
    }
}
