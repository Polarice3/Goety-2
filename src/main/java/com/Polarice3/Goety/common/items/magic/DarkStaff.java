package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.magic.SpellType;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DarkStaff extends DarkWand {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public DarkStaff(Properties properties, double damage, double attackSpeed, SpellType spellType){
        super(properties, spellType);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", damage - 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public DarkStaff(Properties properties, double damage, SpellType spellType) {
        this(properties, damage, -2.4D, spellType);
    }

    public DarkStaff(double damage, double attackSpeed, SpellType spellType) {
        this(wandProperties(), damage, attackSpeed, spellType);
    }

    public DarkStaff(double damage, SpellType spellType) {
        this(damage, -2.4D, spellType);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack) {
        if (pEquipmentSlot == EquipmentSlot.MAINHAND){
            return this.defaultModifiers;
        }
        return super.getAttributeModifiers(pEquipmentSlot, stack);
    }

    @Override
    public float getWandVisualHeight(Level level, LivingEntity entity, ItemStack stack) {
        return 0.8F;
    }
}
