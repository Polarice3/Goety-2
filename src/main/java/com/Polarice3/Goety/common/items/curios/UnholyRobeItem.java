package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UnholyRobeItem extends SingleStackItem{

    public UnholyRobeItem() {
        super(new Properties().fireResistant().stacksTo(1));
    }

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            if (CuriosFinder.hasCurio(livingEntity, item -> item.getItem() instanceof UnholyRobeItem)){
                if (livingEntity.isOnFire()){
                    livingEntity.setRemainingFireTicks(0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        if (CuriosFinder.hasCurio(victim, item -> item.getItem() instanceof UnholyRobeItem)){
            float resistance = 1.0F - (ItemConfig.NetherRobeResistance.get() / 100.0F);
            if (ModDamageSource.hellfireAttacks(event.getSource())){
                resistance = Math.max(0.75F, resistance);
                event.setAmount(event.getAmount() * resistance);
            }
        }
    }

    @Override
    public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        if (IronLoaded.IRON_SPELLBOOKS.isLoaded()){
            if (MainConfig.RobesIronResist.get()) {
                map.put(IronAttributes.FIRE_MAGIC_RESIST, new AttributeModifier(UUID.fromString("bd88096f-189e-4b29-adc2-e65255f73a9f"), "Robes Iron Fire Spell Resist", 1.0F, AttributeModifier.Operation.ADDITION));
                map.put(IronAttributes.NATURE_MAGIC_RESIST, new AttributeModifier(UUID.fromString("3987801f-f418-475d-b662-4544eb267e88"), "Robes Iron Nature Spell Resist", 0.5F, AttributeModifier.Operation.ADDITION));
                map.put(IronAttributes.BLOOD_MAGIC_RESIST, new AttributeModifier(UUID.fromString("f81a0876-dcb9-4eee-b382-e5bc6170a54f"), "Robes Iron Blood Spell Resist", 0.75F, AttributeModifier.Operation.ADDITION));
                map.put(IronAttributes.HOLY_MAGIC_RESIST, new AttributeModifier(UUID.fromString("d29ed6a0-f38d-4c77-89b2-850743ec96b8"), "Robes Iron Holy Spell Resist", -0.25F, AttributeModifier.Operation.ADDITION));
            }
        }
        return map;
    }
}
