package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Goety.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FangedDaggerItem extends TieredItem implements Vanishable {
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public FangedDaggerItem(Tier tier) {
        super(tier, (new Properties().tab(Goety.TAB).rarity(Rarity.UNCOMMON)));
        this.attackDamage = tier.getAttackDamageBonus();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -1.6F, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public FangedDaggerItem() {
        this(ModTiers.SPECIAL);
    }

    public float getDamage() {
        return this.attackDamage;
    }

    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
        return !p_43294_.isCreative();
    }

    public boolean hurtEnemy(ItemStack p_43278_, LivingEntity p_43279_, LivingEntity p_43280_) {
        p_43278_.hurtAndBreak(1, p_43280_, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    public boolean mineBlock(ItemStack p_43282_, Level p_43283_, BlockState p_43284_, BlockPos p_43285_, LivingEntity p_43286_) {
        if (p_43284_.getDestroySpeed(p_43283_, p_43285_) != 0.0F) {
            p_43282_.hurtAndBreak(2, p_43286_, (p_43276_) -> {
                p_43276_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return (enchantment.category == EnchantmentCategory.WEAPON
                || enchantment.category == EnchantmentCategory.BREAKABLE
                || enchantment.category == EnchantmentCategory.VANISHABLE
                || enchantment == Enchantments.MOB_LOOTING)
                && !(enchantment instanceof SweepingEdgeEnchantment);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot p_41639_, ItemStack stack) {
        return p_41639_ == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(p_41639_, stack);
    }

    @SubscribeEvent
    public static void FangedHurt(LivingHurtEvent event){
        LivingEntity livingEntity = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (ModDamageSource.physicalAttacks(event.getSource())){
            if (attacker instanceof LivingEntity livingAttacker){
                MobEffect effect = MobEffects.POISON;
                if (CuriosFinder.hasWildRobe(livingAttacker)){
                    effect = GoetyEffects.ACID_VENOM.get();
                }
                if (livingAttacker.getMainHandItem().getItem() == ModItems.FANGED_DAGGER.get()){
                    if (livingAttacker.hasEffect(GoetyEffects.VENOMOUS_HANDS.get())){
                        EffectsUtil.increaseDuration(livingEntity, effect, 600);
                    } else {
                        livingEntity.addEffect(new MobEffectInstance(effect, 200));
                    }
                }
            }
        }
    }
}
