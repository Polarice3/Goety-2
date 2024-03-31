package com.Polarice3.Goety.common.advancements;

import com.Polarice3.Goety.Goety;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.KilledTrigger;

public class ModCriteriaTriggers {
    public static final KilledTrigger SERVANT_KILLED_ENTITY = new KilledTrigger(Goety.location("servant_killed_entity"));

    public static void init() {
        CriteriaTriggers.register(SERVANT_KILLED_ENTITY);
    }
}
