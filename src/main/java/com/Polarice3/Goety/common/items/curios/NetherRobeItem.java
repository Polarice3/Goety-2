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
public class NetherRobeItem extends SingleStackItem{

    public NetherRobeItem() {
        super(new Properties().fireResistant().stacksTo(1));
    }

    @SubscribeEvent
    public static void LivingEffects(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null){
            if (CuriosFinder.hasCurio(livingEntity, item -> item.getItem() instanceof NetherRobeItem)){
                if (livingEntity.isOnFire()){
                    livingEntity.setRemainingFireTicks(livingEntity.getRemainingFireTicks() - 2);
                }
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event){
        LivingEntity victim = event.getEntity();
        if (CuriosFinder.hasCurio(victim, item -> item.getItem() instanceof NetherRobeItem)){
            float resistance = 1.0F - (ItemConfig.NetherRobeResistance.get() / 100.0F);
            if (resistance <= 0.0F){
                event.setCanceled(true);
            } else {
                if (event.getSource().is(DamageTypeTags.IS_FIRE) || ModDamageSource.isMagicFire(event.getSource())){
                    event.setAmount(event.getAmount() * resistance);
                }
                if (ModDamageSource.hellfireAttacks(event.getSource())){
                    resistance = Math.max(0.75F, resistance);
                    event.setAmount(event.getAmount() * resistance);
                }
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
                map.put(IronAttributes.FIRE_MAGIC_RESIST, new AttributeModifier(UUID.fromString("c4bc990e-a8b4-4e6e-8055-e8ba88d90e55"), "Robes Iron Spell Resist", 0.5F, AttributeModifier.Operation.ADDITION));
            }
        }
        return map;
    }
}
