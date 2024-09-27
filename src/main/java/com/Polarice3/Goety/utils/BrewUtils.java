package com.Polarice3.Goety.utils;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.entities.util.BrewEffectCloud;
import com.Polarice3.Goety.common.entities.util.BrewGas;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SAddBrewParticlesPacket;
import com.Polarice3.Goety.common.network.server.SPlayWorldSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BrewUtils {
    public static String AOE_ID = "AreaOfEffect";
    public static String LINGERING_ID = "Lingering";
    public static String QUAFF_ID = "Quaff";
    public static String VELOCITY_ID = "Velocity";
    public static String AQUATIC_ID = "Aquatic";
    public static String FIRE_PROOF_ID = "FireProof";
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

    /**
     * I can't remember why I made this float and not int, lol.
     */
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
        if (quaff == 8){
            return 1;
        } else if (quaff == 16){
            return 2;
        } else if (quaff >= 24){
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

    public static boolean getAquatic(ItemStack p_43576_) {
        CompoundTag compoundtag = p_43576_.getTag();
        if (compoundtag != null && compoundtag.contains(AQUATIC_ID)) {
            return compoundtag.getBoolean(AQUATIC_ID);
        } else {
            return false;
        }
    }

    public static void setAquatic(ItemStack pStack, boolean aquatic){
        pStack.getOrCreateTag().putBoolean(AQUATIC_ID, aquatic);
    }

    public static boolean getFireProof(ItemStack p_43576_) {
        CompoundTag compoundtag = p_43576_.getTag();
        if (compoundtag != null && compoundtag.contains(FIRE_PROOF_ID)) {
            return compoundtag.getBoolean(FIRE_PROOF_ID);
        } else {
            return false;
        }
    }

    public static void setFireProof(ItemStack pStack, boolean fireProof){
        pStack.getOrCreateTag().putBoolean(FIRE_PROOF_ID, fireProof);
    }

    public static List<BrewEffectInstance> getBrewEffects(ItemStack p_43548_) {
        return getAllEffects(p_43548_.getTag());
    }

    public static List<BrewEffectInstance> getAllEffects(Collection<BrewEffectInstance> p_43563_) {
        List<BrewEffectInstance> list = Lists.newArrayList();
        list.addAll(p_43563_);
        return list;
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

                    if (brewEffectInstance.getDuration() > 20) {
                        mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, formatDuration(brewEffectInstance, p_43558_));
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
                p_43557_.add(Component.translatable("tooltip.goety.brew.area", Component.translatable("potion.potency." + (BrewUtils.getAreaOfEffect(itemStack) - 1))).withStyle(ChatFormatting.BLUE));
            }
            if (BrewUtils.getLingering(itemStack) > 0){
                p_43557_.add(Component.translatable("tooltip.goety.brew.lingering", Component.translatable("potion.potency." + ((int)BrewUtils.getLingering(itemStack) - 1))).withStyle(ChatFormatting.BLUE));
            }
            if (BrewUtils.getQuaffLevel(itemStack) > 0){
                p_43557_.add(Component.translatable("tooltip.goety.brew.quaff", Component.translatable("potion.potency." + (BrewUtils.getQuaffLevel(itemStack) - 1))).withStyle(ChatFormatting.BLUE));
            }
            if (BrewUtils.getVelocityLevel(itemStack) > 0){
                p_43557_.add(Component.translatable("tooltip.goety.brew.velocity", Component.translatable("potion.potency." + (BrewUtils.getVelocityLevel(itemStack) - 1))).withStyle(ChatFormatting.BLUE));
            }
            if (BrewUtils.getAquatic(itemStack)){
                p_43557_.add(Component.translatable("tooltip.goety.brew.aquatic").withStyle(ChatFormatting.BLUE));
            }
            if (BrewUtils.getFireProof(itemStack)){
                p_43557_.add(Component.translatable("tooltip.goety.brew.fireProof").withStyle(ChatFormatting.BLUE));
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

    public static String formatDuration(BrewEffectInstance p_19582_, float p_19583_) {
        int i = Mth.floor((float)p_19582_.getDuration() * p_19583_);
        return StringUtil.formatTickDuration(i);
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

    public static boolean isLingering(ItemStack itemStack) {
        return itemStack.is(ModItems.LINGERING_BREW.get());
    }

    public static boolean isGas(ItemStack itemStack){
        return itemStack.is(ModItems.GAS_BREW.get());
    }

    public static void onHit(LivingEntity livingEntity, ItemStack itemStack, @Nullable Entity target, BlockPos blockPos, Direction direction) {
        if (!livingEntity.level.isClientSide) {
            Potion potion = PotionUtils.getPotion(itemStack);
            List<MobEffectInstance> list = PotionUtils.getMobEffects(itemStack);
            List<BrewEffectInstance> list1 = BrewUtils.getBrewEffects(itemStack);
            boolean flag = potion == Potions.WATER && list.isEmpty();
            if (flag) {
                applyWater(livingEntity, itemStack, target, blockPos);
            } else if (!list.isEmpty() || !list1.isEmpty()) {
                if (isGas(itemStack)){
                    makeBrewGas(livingEntity, itemStack, target, blockPos);
                } else if (isLingering(itemStack)) {
                    makeAreaOfEffectCloud(livingEntity, itemStack, target, blockPos);
                } else {
                    applySplash(livingEntity, itemStack, target, blockPos, list, list1);
                }
            }

            ModNetwork.sendToALL(new SAddBrewParticlesPacket(itemStack, blockPos, potion.hasInstantEffects(), BrewUtils.getColor(itemStack)));
            if (target != null){
                onHitBlock(livingEntity, itemStack, target.blockPosition(), direction);
                onHitEntity(livingEntity, itemStack, target);
            }
        }
    }

    public static void onHitEntity(LivingEntity livingEntity, ItemStack itemStack, Entity target) {
        if (!livingEntity.level.isClientSide) {
            if (!isGas(itemStack)) {
                List<BrewEffectInstance> list = BrewUtils.getBrewEffects(itemStack);
                for (BrewEffectInstance brewEffectInstance : list) {
                    brewEffectInstance.getEffect().applyBlockEffect(livingEntity.level, target.blockPosition(), livingEntity, brewEffectInstance.getDuration(), brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(itemStack));
                }
            }
        }
    }

    public static void onHitBlock(LivingEntity livingEntity, ItemStack itemStack, BlockPos blockPos, Direction direction) {
        if (!livingEntity.level.isClientSide) {
            if (!isGas(itemStack)) {
                Potion potion = PotionUtils.getPotion(itemStack);
                List<BrewEffectInstance> list = BrewUtils.getBrewEffects(itemStack);
                boolean flag = potion == Potions.WATER && list.isEmpty();
                BlockPos blockpos1 = blockPos.relative(direction);
                if (flag) {
                    dowseFire(livingEntity, blockpos1);
                    dowseFire(livingEntity, blockpos1.relative(direction.getOpposite()));

                    for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                        dowseFire(livingEntity, blockpos1.relative(direction1));
                    }
                }
                for (BrewEffectInstance brewEffectInstance : list) {
                    brewEffectInstance.getEffect().applyDirectionalBlockEffect(livingEntity.level, blockPos, direction, livingEntity, brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(itemStack));
                    brewEffectInstance.getEffect().applyBlockEffect(livingEntity.level, blockPos, livingEntity, brewEffectInstance.getDuration(), brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(itemStack));
                }
            }
        }
    }

    public static AABB makeBoundingBox(double p_20385_, double p_20386_, double p_20387_) {
        float f = 0.25F / 2.0F;
        float f1 = 0.25F;
        return new AABB(p_20385_ - (double)f, p_20386_, p_20387_ - (double)f, p_20385_ + (double)f, p_20386_ + (double)f1, p_20387_ + (double)f);
    }

    public static final Predicate<LivingEntity> WATER_SENSITIVE = LivingEntity::isSensitiveToWater;

    public static void applyWater(LivingEntity livingEntity, ItemStack itemStack, @Nullable Entity target, BlockPos blockPos) {
        int area = BrewUtils.getAreaOfEffect(itemStack) + 4;
        int areaSqr = Mth.square(area);
        Vec3 vec3 = Vec3.atCenterOf(blockPos);
        if (target != null){
            vec3 = target.position();
        }
        AABB aabb = makeBoundingBox(vec3.x, vec3.y, vec3.z).inflate(area, area / 2.0D, area);
        List<LivingEntity> list = livingEntity.level.getEntitiesOfClass(LivingEntity.class, aabb, WATER_SENSITIVE);
        if (!list.isEmpty()) {
            for(LivingEntity livingTarget : list) {
                double d0 = vec3.distanceToSqr(livingTarget.position());
                if (d0 < areaSqr && livingTarget.isSensitiveToWater()) {
                    livingTarget.hurt(DamageSource.indirectMagic(livingEntity, livingEntity), 1.0F);
                }
            }
        }

        for(Axolotl axolotl : livingEntity.level.getEntitiesOfClass(Axolotl.class, aabb)) {
            axolotl.rehydrate();
        }

    }

    public static void applySplash(LivingEntity livingEntity, ItemStack itemStack, @Nullable Entity target, BlockPos blockPos, List<MobEffectInstance> mobEffectInstances, List<BrewEffectInstance> brewEffectInstances) {
        int area = BrewUtils.getAreaOfEffect(itemStack) + 4;
        int areaSqr = Mth.square(area);
        Vec3 vec3 = Vec3.atCenterOf(blockPos);
        if (target != null){
            vec3 = target.position();
        }
        AABB aabb = makeBoundingBox(vec3.x, vec3.y, vec3.z).inflate(area, area / 2.0D, area);
        List<LivingEntity> list = livingEntity.level.getEntitiesOfClass(LivingEntity.class, aabb);
        if (!list.isEmpty()) {
            for(LivingEntity livingTarget : list) {
                if (livingTarget.isAffectedByPotions()) {
                    double d0 = vec3.distanceToSqr(livingTarget.position());
                    if (d0 < areaSqr) {
                        double d1 = 1.0D - Math.sqrt(d0) / areaSqr;
                        if (livingTarget == target) {
                            d1 = 1.0D;
                        }

                        if (!mobEffectInstances.isEmpty()) {
                            for (MobEffectInstance mobeffectinstance : mobEffectInstances) {
                                MobEffect mobeffect = mobeffectinstance.getEffect();
                                if (mobeffect.isInstantenous()) {
                                    mobeffect.applyInstantenousEffect(livingEntity, livingEntity, livingTarget, mobeffectinstance.getAmplifier(), d1);
                                } else {
                                    int i = (int) (d1 * (double) mobeffectinstance.getDuration() + 0.5D);
                                    if (i > 20) {
                                        livingTarget.addEffect(new MobEffectInstance(mobeffect, i, mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()), livingEntity);
                                    }
                                }
                            }
                        }
                        if (!brewEffectInstances.isEmpty()) {
                            for (BrewEffectInstance brewEffectInstance : brewEffectInstances) {
                                BrewEffect brewEffect = brewEffectInstance.getEffect();
                                if (brewEffect.isInstantenous()) {
                                    brewEffect.applyInstantenousEffect(livingEntity, livingEntity, livingTarget, brewEffectInstance.getAmplifier(), d1);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static void makeAreaOfEffectCloud(LivingEntity livingEntity, ItemStack itemStack, @Nullable Entity target, BlockPos blockPos) {
        int h = BrewUtils.getAreaOfEffect(itemStack);
        float i = BrewUtils.getLingering(itemStack);
        Vec3 vec3 = Vec3.atCenterOf(blockPos);
        if (target != null){
            vec3 = target.position();
        }
        BrewEffectCloud brewEffectCloud = new BrewEffectCloud(livingEntity.level, vec3.x(), vec3.y(), vec3.z());
        brewEffectCloud.setOwner(livingEntity);

        brewEffectCloud.setRadius(3.0F + h);
        if (i < 0.3) {
            brewEffectCloud.setRadiusOnUse(-(0.5F - Mth.square(i)));
        }
        brewEffectCloud.setWaitTime(10);
        brewEffectCloud.setRadiusPerTick(-brewEffectCloud.getRadius() / (float)brewEffectCloud.getDuration());

        for(MobEffectInstance mobeffectinstance : PotionUtils.getCustomEffects(itemStack)) {
            brewEffectCloud.addEffect(new MobEffectInstance(mobeffectinstance));
        }

        for(BrewEffectInstance brewEffectInstance : BrewUtils.getCustomEffects(itemStack)) {
            if (brewEffectInstance.getEffect().canLinger()) {
                brewEffectCloud.addBrewEffect(new BrewEffectInstance(brewEffectInstance));
            }
        }

        CompoundTag compoundtag = itemStack.getTag();
        if (compoundtag != null && compoundtag.contains("CustomPotionColor", 99)) {
            brewEffectCloud.setFixedColor(compoundtag.getInt("CustomPotionColor"));
        }

        livingEntity.level.addFreshEntity(brewEffectCloud);
    }

    public static void makeBrewGas(LivingEntity livingEntity, ItemStack itemStack, @Nullable Entity target, BlockPos blockPos){
        int h = BrewUtils.getAreaOfEffect(itemStack);
        int i = (int) BrewUtils.getLingering(itemStack);
        if (target != null){
            blockPos = target.blockPosition();
        }
        BrewGas brewGas = new BrewGas(livingEntity.level, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        brewGas.setGas(PotionUtils.getCustomEffects(itemStack), BrewUtils.getCustomEffects(itemStack),
                120 * (i + 1), 3 * (h + 1), livingEntity);

        livingEntity.level.addFreshEntity(brewGas);

        if (!livingEntity.level.isClientSide){
            ModNetwork.sendToALL(new SPlayWorldSoundPacket(blockPos, ModSounds.BREW_GAS.get(), 1.0F, livingEntity.level.random.nextFloat() * 0.1F + 0.9F));
        }
    }

    public static void dowseFire(LivingEntity livingEntity, BlockPos p_150193_) {
        BlockState blockstate = livingEntity.level.getBlockState(p_150193_);
        if (blockstate.is(BlockTags.FIRE)) {
            livingEntity.level.removeBlock(p_150193_, false);
        } else if (AbstractCandleBlock.isLit(blockstate)) {
            AbstractCandleBlock.extinguish((Player)null, blockstate, livingEntity.level, p_150193_);
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            livingEntity.level.levelEvent((Player)null, 1009, p_150193_, 0);
            CampfireBlock.dowse(livingEntity, livingEntity.level, p_150193_, blockstate);
            livingEntity.level.setBlockAndUpdate(p_150193_, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
        }

    }
}
