package com.Polarice3.Goety.common.entities.neutral;

import com.Polarice3.Goety.AttributesConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Customizable Attribute codes based from @AlexModGuy
 */
public interface ICustomAttributes {
    Map<EntityType<? extends LivingEntity>, AttributeSupplier> ATTRIBUTE_MODIFIER_MAP = new HashMap<>();

    static <T extends LivingEntity & ICustomAttributes> void applyAttributesForEntity(EntityType<? extends LivingEntity> type, T entity) {
        entity.attributes = new AttributeMap(getAttributesForEntity(type, entity));
        entity.setHealth(entity.getMaxHealth());
    }

    static<T extends ICustomAttributes> AttributeSupplier getAttributesForEntity(EntityType<? extends LivingEntity> type, T entity) {
        AttributeSupplier originalAttributes = DefaultAttributes.getSupplier(type);
        if (!AttributesConfig.OverrideAttributes.get()){
            return originalAttributes;
        }
        if (ATTRIBUTE_MODIFIER_MAP.containsKey(type)) {
            return ATTRIBUTE_MODIFIER_MAP.get(type);
        }
        AttributeSupplier.Builder originalMutable = new AttributeSupplier.Builder(originalAttributes);
        if (entity.getConfiguredAttributes() != null) {
            originalMutable.combine(entity.getConfiguredAttributes());
        }
        AttributeSupplier newAttributeMap = originalMutable.build();
        ATTRIBUTE_MODIFIER_MAP.put(type, newAttributeMap);
        return newAttributeMap;
    }

    AttributeSupplier.Builder getConfiguredAttributes();
}
