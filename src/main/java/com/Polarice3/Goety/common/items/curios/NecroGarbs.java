package com.Polarice3.Goety.common.items.curios;

import com.Polarice3.Goety.compat.iron.IronAttributes;
import com.Polarice3.Goety.compat.iron.IronLoaded;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

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

        @Override
        public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
            if (!worldIn.isClientSide) {
                if (entityIn instanceof LivingEntity livingEntity) {
                    if (ItemConfig.NecroCrownWeakness.get()) {
                        if (!this.isNameless) {
                            if (CuriosFinder.hasCurio(livingEntity, this)) {
                                if (MobUtil.isInSunlightNoRain(livingEntity)) {
                                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
                                }
                            }
                        }
                    }
                }
            }

            super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        }
    }

    public static class NecroCapeItem extends NecroGarbs {
        public NecroCapeItem(boolean isNameless) {
            super(isNameless);
        }

        @Override
        public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
            if (!worldIn.isClientSide) {
                if (entityIn instanceof LivingEntity livingEntity) {
                    if (ItemConfig.NecroCapeHunger.get()) {
                        if (!this.isNameless) {
                            if (CuriosFinder.hasCurio(livingEntity, this)) {
                                if (MobUtil.isInSunlightNoRain(livingEntity)) {
                                    livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2, false, false));
                                }
                            }
                        }
                    }
                }
            }

            super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                            UUID uuid, ItemStack stack) {
            Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
            if (IronLoaded.IRON_SPELLBOOKS.isLoaded()){
                if (MainConfig.RobesIronResist.get()) {
                    map.put(IronAttributes.BLOOD_MAGIC_RESIST, new AttributeModifier(UUID.fromString("491b09e2-380d-49ce-b7e3-7512180c3eb0"), "Robes Iron Spell Resist", 0.25F, AttributeModifier.Operation.ADDITION));
                }
            }
            return map;
        }
    }
}
