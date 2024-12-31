package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.config.ItemConfig;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class DarkStaff extends DarkWand {
    protected static final UUID OFFHAND_ATTACK_DAMAGE_UUID = UUID.fromString("6dc7952d-11a6-4bf4-954b-b527b35787c6");
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    public final Multimap<Attribute, AttributeModifier> offhandModifier;

    public DarkStaff(Properties properties, double damage, double attackSpeed, SpellType spellType){
        super(properties, spellType);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", damage - 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder2 = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(OFFHAND_ATTACK_DAMAGE_UUID, "Dark Staff Proficiency", 0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        this.offhandModifier = builder2.build();
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
        } else if (pEquipmentSlot == EquipmentSlot.OFFHAND && ItemConfig.StaffOffhandBuff.get()){
            return this.offhandModifier;
        }
        return super.getAttributeModifiers(pEquipmentSlot, stack);
    }
}
