package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.init.ModTags;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BrewUtils {
    public static String AOE_ID = "AreaOfEffect";
    public static String LINGERING_ID = "Lingering";
    public static String QUAFF_ID = "Quaff";
    public static String VELOCITY_ID = "Velocity";
    private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);

    public static int getAreaOfEffect(ItemStack p_43576_) {
        CompoundTag compoundtag = p_43576_.getTag();
        if (compoundtag != null && compoundtag.contains(AOE_ID)) {
            return compoundtag.getInt(AOE_ID);
        } else {
            return 0;
        }
    }

    public static void setAreaOfEffect(ItemStack pStack, int aoe){
        pStack.getOrCreateTag().putInt(AOE_ID, aoe);
    }

    public static float getLingering(ItemStack p_43576_) {
        CompoundTag compoundtag = p_43576_.getTag();
        if (compoundtag != null && compoundtag.contains(LINGERING_ID)) {
            return compoundtag.getFloat(LINGERING_ID);
        } else {
            return 0;
        }
    }

    public static void setLingering(ItemStack pStack, float lingering){
        pStack.getOrCreateTag().putFloat(LINGERING_ID, lingering);
    }

    public static int getQuaff(ItemStack itemStack){
        CompoundTag compoundtag = itemStack.getTag();
        if (compoundtag != null && compoundtag.contains(QUAFF_ID)) {
            return compoundtag.getInt(QUAFF_ID);
        } else {
            return 0;
        }
    }

    public static int getQuaffLevel(ItemStack itemStack){
        int quaff = getQuaff(itemStack);
        if (quaff == 4){
            return 1;
        } else if (quaff == 8){
            return 2;
        } else if (quaff >= 16){
            return 3;
        }
        return 0;
    }

    public static void setQuaff(ItemStack pStack, int quaff){
        pStack.getOrCreateTag().putInt(QUAFF_ID, quaff);
    }

    public static float getVelocity(@NotNull ItemStack itemStack){
        CompoundTag compoundtag = itemStack.getTag();
        if (compoundtag != null && compoundtag.contains(VELOCITY_ID)) {
            return compoundtag.getFloat(VELOCITY_ID);
        } else {
            return 0;
        }
    }

    public static int getVelocityLevel(ItemStack itemStack){
        float velocity = getVelocity(itemStack);
        if (velocity == 0.1F){
            return 1;
        } else if (velocity == 0.3F){
            return 2;
        } else if (velocity >= 0.5F){
            return 3;
        }
        return 0;
    }

    public static void setVelocity(ItemStack pStack, float velocity){
        pStack.getOrCreateTag().putFloat(VELOCITY_ID, velocity);
    }

    public static List<BrewEffectInstance> getBrewEffects(ItemStack p_43548_) {
        return getAllEffects(p_43548_.getTag());
    }

    public static List<BrewEffectInstance> getAllEffects(@Nullable CompoundTag p_43567_) {
        List<BrewEffectInstance> list = Lists.newArrayList();
        getCustomEffects(p_43567_, list);
        return list;
    }

    public static List<BrewEffectInstance> getCustomEffects(ItemStack p_43572_) {
        return getCustomEffects(p_43572_.getTag());
    }

    public static List<BrewEffectInstance> getCustomEffects(@Nullable CompoundTag p_43574_) {
        List<BrewEffectInstance> list = Lists.newArrayList();
        getCustomEffects(p_43574_, list);
        return list;
    }

    public static void getCustomEffects(@Nullable CompoundTag p_43569_, List<BrewEffectInstance> p_43570_) {
        if (p_43569_ != null && p_43569_.contains("CustomBrewEffects", 9)) {
            ListTag listtag = p_43569_.getList("CustomBrewEffects", 10);

            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                BrewEffectInstance instance = BrewEffectInstance.load(compoundtag);
                if (instance != null) {
                    p_43570_.add(instance);
                }
            }
        }

    }

    public static ItemStack setCustomEffects(ItemStack stack, Collection<MobEffectInstance> instances1, Collection<BrewEffectInstance> instances) {
        if (!instances1.isEmpty()){
            CompoundTag compoundtag = stack.getOrCreateTag();
            ListTag listtag = compoundtag.getList("CustomPotionEffects", 9);

            for(MobEffectInstance mobeffectinstance : instances1) {
                listtag.add(mobeffectinstance.save(new CompoundTag()));
            }

            compoundtag.put("CustomPotionEffects", listtag);
        }
        if (!instances.isEmpty()) {
            CompoundTag compoundtag = stack.getOrCreateTag();
            ListTag listtag = compoundtag.getList("CustomBrewEffects", 9);

            for (BrewEffectInstance instance : instances) {
                listtag.add(instance.save(new CompoundTag()));
            }

            compoundtag.put("CustomBrewEffects", listtag);
        }
        return stack;
    }

    public static void addBrewTooltip(ItemStack itemStack, List<Component> p_43557_, float p_43558_) {
        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemStack);
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        List<BrewEffectInstance> list2 = getBrewEffects(itemStack);
        if (list.isEmpty() && list2.isEmpty()) {
            p_43557_.add(NO_EFFECT);
        } else {
            if (!list2.isEmpty()) {
                for (BrewEffectInstance brewEffectInstance : list2) {
                    MutableComponent mutablecomponent = brewEffectInstance.getName();
                    BrewEffect mobeffect = brewEffectInstance.getEffect();

                    if (brewEffectInstance.getAmplifier() > 0) {
                        mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + brewEffectInstance.getAmplifier()));
                    }

                    p_43557_.add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
                }
            }
            if (!list.isEmpty()){
                for(MobEffectInstance mobeffectinstance : list) {
                    MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
                    MobEffect mobeffect = mobeffectinstance.getEffect();
                    Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();
                    if (!map.isEmpty()) {
                        for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                            AttributeModifier attributemodifier = entry.getValue();
                            AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffect.getAttributeModifierValue(mobeffectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                            list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                        }
                    }

                    if (mobeffectinstance.getAmplifier() > 0) {
                        mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
                    }

                    if (mobeffectinstance.getDuration() > 20) {
                        mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, p_43558_));
                    }

                    p_43557_.add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
                }
            }
            if (BrewUtils.getAreaOfEffect(itemStack) > 0){
                p_43557_.add(Component.translatable("tooltip.goety.brew.area", Component.translatable("potion.potency." + BrewUtils.getAreaOfEffect(itemStack))).withStyle(ChatFormatting.BLUE));
            }
            if (BrewUtils.getLingering(itemStack) > 0){
                p_43557_.add(Component.translatable("tooltip.goety.brew.lingering", Component.translatable("potion.potency." + (int)BrewUtils.getLingering(itemStack))).withStyle(ChatFormatting.BLUE));
            }
            if (BrewUtils.getQuaffLevel(itemStack) > 0){
                p_43557_.add(Component.translatable("tooltip.goety.brew.quaff", Component.translatable("potion.potency." + BrewUtils.getQuaffLevel(itemStack))).withStyle(ChatFormatting.BLUE));
            }
            if (BrewUtils.getVelocityLevel(itemStack) > 0){
                p_43557_.add(Component.translatable("tooltip.goety.brew.velocity", Component.translatable("potion.potency." + BrewUtils.getVelocityLevel(itemStack))).withStyle(ChatFormatting.BLUE));
            }
            if (!list1.isEmpty()) {
                p_43557_.add(CommonComponents.EMPTY);
                p_43557_.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

                for(Pair<Attribute, AttributeModifier> pair : list1) {
                    AttributeModifier attributemodifier2 = pair.getSecond();
                    double d0 = attributemodifier2.getAmount();
                    double d1;
                    if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                        d1 = attributemodifier2.getAmount();
                    } else {
                        d1 = attributemodifier2.getAmount() * 100.0D;
                    }

                    if (d0 > 0.0D) {
                        p_43557_.add(Component.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                    } else if (d0 < 0.0D) {
                        d1 *= -1.0D;
                        p_43557_.add(Component.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.RED));
                    }
                }
            }
        }
    }

    public static boolean hasBrewEffect(ItemStack itemStack){
        return !getBrewEffects(itemStack).isEmpty();
    }

    public static boolean hasEffect(ItemStack itemStack){
        return !PotionUtils.getCustomEffects(itemStack).isEmpty() || !getBrewEffects(itemStack).isEmpty();
    }

    public static boolean brewableFood(ItemStack itemStack){
        return itemStack.is(ModTags.Items.BREWABLE_FOOD) && !hasEffect(itemStack);
    }

    public static int getColor(ItemStack p_43576_) {
        CompoundTag compoundtag = p_43576_.getTag();
        if (compoundtag != null && compoundtag.contains("CustomPotionColor", 99)) {
            return compoundtag.getInt("CustomPotionColor");
        } else {
            return getColor(PotionUtils.getMobEffects(p_43576_), getBrewEffects(p_43576_));
        }
    }

    public static int getColor(Collection<MobEffectInstance> instance1, Collection<BrewEffectInstance> instance2) {
        if (instance1.isEmpty() && instance2.isEmpty()) {
            return 3694022;
        } else {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            int j = 0;

            for(MobEffectInstance mobeffectinstance : instance1) {
                if (mobeffectinstance.isVisible()) {
                    int k = mobeffectinstance.getEffect().getColor();
                    int l = mobeffectinstance.getAmplifier() + 1;
                    f += (float)(l * (k >> 16 & 255)) / 255.0F;
                    f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
                    f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
                    j += l;
                }
            }

            for(BrewEffectInstance brewEffectInstance : instance2) {
                int k = brewEffectInstance.getEffect().getColor();
                int l = brewEffectInstance.getAmplifier() + 1;
                f += (float)(l * (k >> 16 & 255)) / 255.0F;
                f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
                f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
                j += l;
            }

            if (j == 0) {
                return 0;
            } else {
                f = f / (float)j * 255.0F;
                f1 = f1 / (float)j * 255.0F;
                f2 = f2 / (float)j * 255.0F;
                return (int)f << 16 | (int)f1 << 8 | (int)f2;
            }
        }
    }
}
