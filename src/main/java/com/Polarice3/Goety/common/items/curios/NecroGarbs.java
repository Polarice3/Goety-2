package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NecroGarbs extends SingleStackItem {
    public boolean isNameless;

    public NecroGarbs(boolean isNameless){
        super();
        this.isNameless = isNameless;
    }

    public static class NecroCrownItem extends NecroGarbs {
        public NecroCrownItem() {
            super(false);
        }
    }

    public static class NecroCapeItem extends NecroGarbs {
        public NecroCapeItem(boolean isNameless) {
            super(isNameless);
        }
    }

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            if (!LichdomHelper.isLich(livingEntity)) {
                if (MobUtil.isInSunlighNoRain(livingEntity)) {
                    if (ItemConfig.NecroCrownWeakness.get()) {
                        if (CuriosFinder.hasCurio(livingEntity, item -> item.getItem() instanceof NecroCrownItem crownItem && !crownItem.isNameless)) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
                        }
                    }
                    if (ItemConfig.NecroCapeHunger.get()) {
                        if (CuriosFinder.hasCurio(livingEntity, item -> item.getItem() instanceof NecroCapeItem capeItem && !capeItem.isNameless)) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2, false, false));
                        }
                    }
                }
            }
        }
    }
}
