package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WildRobeItem extends SingleStackItem {

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            if (CuriosFinder.hasWildRobe(livingEntity)){
                if (!livingEntity.level.isClientSide) {
                    if (livingEntity.hasEffect(MobEffects.POISON)){
                        livingEntity.removeEffect(MobEffects.POISON);
                    }
                    if (livingEntity.hasEffect(GoetyEffects.ACID_VENOM.get())){
                        livingEntity.removeEffect(GoetyEffects.ACID_VENOM.get());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void EffectApplyEvents(MobEffectEvent.Applicable event){
        LivingEntity livingEntity = event.getEntity();
        if (event.getEffectInstance().getEffect() == MobEffects.POISON
        || event.getEffectInstance().getEffect() == GoetyEffects.ACID_VENOM.get()) {
            if (CuriosFinder.hasWildRobe(livingEntity)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        if (IronLoaded.IRON_SPELLBOOKS.isLoaded()){
            if (MainConfig.RobesIronResist.get()) {
                map.put(IronAttributes.NATURE_MAGIC_RESIST, new AttributeModifier(UUID.fromString("73cd408f-b6b5-470c-ba69-560cb85f41ad"), "Robes Iron Spell Resist", 0.5F, AttributeModifier.Operation.ADDITION));
            }
        }
        return map;
    }
}
