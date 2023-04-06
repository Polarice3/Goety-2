package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.IOwned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DarkScytheItem extends TieredItem implements Vanishable {
    private static float initialDamage = MainConfig.ScytheBaseDamage.get().floatValue();
    private final Multimap<Attribute, AttributeModifier> scytheAttributes;

    public DarkScytheItem(Tier itemTier) {
        super(itemTier, new Properties().durability(itemTier.getUses()).tab(Goety.TAB));
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        initialDamage = MainConfig.ScytheBaseDamage.get().floatValue() + itemTier.getAttackDamageBonus();
        double attackSpeed = 4.0D - MainConfig.ScytheAttackSpeed.get();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", initialDamage - 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -attackSpeed, AttributeModifier.Operation.ADDITION));
        this.scytheAttributes = builder.build();
    }

    public DarkScytheItem(){
        this(Tiers.IRON);
    }

    public static float getInitialDamage() {
        return initialDamage;
    }

    public boolean getMineBlocks(BlockState pState){
        return pState.is(BlockTags.MINEABLE_WITH_HOE) || BlockFinder.isScytheBreak(pState);
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return pState.is(BlockTags.MINEABLE_WITH_HOE) ? 8.0F : 1.0F;
    }

    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, (p_220045_0_) ->
                p_220045_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        if (pAttacker instanceof Player player){
            this.attackMobs(pStack, pTarget, player);
        }
        return true;
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(this.getMineBlocks(pState) ? 1 : 2, pEntityLiving, (p_220044_0_) ->
                    p_220044_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        if (this.getMineBlocks(pState)){
            pLevel.playSound((Player) null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.SCYTHE_HIT.get(), pEntityLiving.getSoundSource(), 1.0F, 1.0F);
            for (int i = -2; i <= 2; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    for (int k = -2; k <= 2; ++k) {
                        BlockPos blockpos1 = pPos.offset(i, j, k);
                        BlockState blockstate = pLevel.getBlockState(blockpos1);
                        if (this.getMineBlocks(blockstate)){
                            if (pLevel.destroyBlock(blockpos1, true, pEntityLiving)){
                                if (blockstate.getDestroySpeed(pLevel, blockpos1) != 0) {
                                    pStack.hurtAndBreak(1, pEntityLiving, (p_220044_0_)
                                            -> p_220044_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public void attackMobs(ItemStack pStack, LivingEntity pTarget, Player pPlayer){
        int enchantment = pStack.getEnchantmentLevel(ModEnchantments.SOUL_EATER.get());
        int soulEater = Mth.clamp(enchantment + 1, 1, 10);
        SEHelper.increaseSouls(pPlayer, MainConfig.DarkScytheSouls.get() * soulEater);

        float f = (float)pPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = EnchantmentHelper.getDamageBonus(pPlayer.getMainHandItem(), pTarget.getMobType());
        float f2 = pPlayer.getAttackStrengthScale(0.5F);
        f = f * (0.2F + f2 * f2 * 0.8F);
        f1 = f1 * f2;
        f = f + f1;

        if (f > 0.5F || f1 > 0.5F) {
            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(pPlayer) * f;
            int j = EnchantmentHelper.getFireAspect(pPlayer);
            double area = 1.0D;
            if (f2 > 0.9F) {
                area = 2.0D;
            }
            for (LivingEntity livingentity : pPlayer.level.getEntitiesOfClass(LivingEntity.class, pTarget.getBoundingBox().inflate(area, 0.25D, area))) {
                if (livingentity != pPlayer && livingentity != pTarget && !pPlayer.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker()) && pPlayer.distanceToSqr(livingentity) < 16.0D && livingentity != pPlayer.getVehicle()) {
                    livingentity.knockback(0.4F, (double) Mth.sin(pPlayer.getYRot() * ((float) Math.PI / 180F)), (double) (-Mth.cos(pPlayer.getYRot() * ((float) Math.PI / 180F))));
                    if (livingentity.hurt(DamageSource.playerAttack(pPlayer), f3)) {
                        if (j > 0) {
                            livingentity.setSecondsOnFire(j * 4);
                        }
                        pStack.hurtAndBreak(1, pPlayer, (p_220045_0_) ->
                                p_220045_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                        if (livingentity instanceof IOwned){
                            if (((IOwned) livingentity).getTrueOwner() != pPlayer){
                                SEHelper.increaseSouls(pPlayer, MainConfig.DarkScytheSouls.get() * soulEater);
                            }
                        } else {
                            SEHelper.increaseSouls(pPlayer, MainConfig.DarkScytheSouls.get() * soulEater);
                        }
                        EnchantmentHelper.doPostHurtEffects(livingentity, pPlayer);
                        EnchantmentHelper.doPostDamageEffects(pPlayer, livingentity);
                    }
                }
            }
        }

        pPlayer.level.playSound((Player) null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.SCYTHE_SWING.get(), pPlayer.getSoundSource(), 1.0F, 1.0F);
        pPlayer.sweepAttack();
    }

    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(BlockTags.MINEABLE_WITH_HOE);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return (enchantment.category == EnchantmentCategory.VANISHABLE
                || enchantment.category == EnchantmentCategory.WEAPON
                || enchantment.category == EnchantmentCategory.BREAKABLE
                || enchantment == Enchantments.MOB_LOOTING
                || enchantment == Enchantments.BLOCK_FORTUNE);
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.scytheAttributes : super.getDefaultAttributeModifiers(equipmentSlot);
    }
}
