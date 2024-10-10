package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NecroGarbs extends SingleStackItem {
    public boolean isNameless;

    public NecroGarbs(boolean isNameless){
        super();
        this.isNameless = isNameless;
    }

    public static class NecroCrownItem extends NecroGarbs {
        public NecroCrownItem(boolean isNameless) {
            super(isNameless);
        }

        public NecroCrownItem() {
            super(false);
        }
    }

    public static class NecroCapeItem extends NecroGarbs {
        public NecroCapeItem(boolean isNameless) {
            super(isNameless);
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                            UUID uuid, ItemStack stack) {
            Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
            if (IronLoaded.IRON_SPELLBOOKS.isLoaded()){
                if (MainConfig.RobesIronResist.get()) {
                    map.put(IronAttributes.BLOOD_MAGIC_RESIST, new AttributeModifier(UUID.fromString("491b09e2-380d-49ce-b7e3-7512180c3eb0"), "Robes Iron Spell Resist", 0.5F, AttributeModifier.Operation.ADDITION));
                }
            }
            return map;
        }
    }

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            if (!LichdomHelper.isLich(livingEntity)) {
                if (MobUtil.isInSunlightNoRain(livingEntity)) {
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
