package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.items.handler.SoulUsingItemHandler;
import com.Polarice3.Goety.config.MainConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import java.util.Map;

public class CraftItemRitual extends Ritual{

    public CraftItemRitual(RitualRecipe recipe) {
        super(recipe);
    }

    @Override
    public void finish(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        super.finish(world, blockPos, tileEntity, castingPlayer, activationItem);

        for(int i = 0; i < 20; ++i) {
            double d0 = (double)blockPos.getX() + world.random.nextDouble();
            double d1 = (double)blockPos.getY() + world.random.nextDouble();
            double d2 = (double)blockPos.getZ() + world.random.nextDouble();
            world.addParticle(ParticleTypes.POOF, d0, d1, d2, 0, 0, 0);
        }

        ItemStack result = this.recipe.getResultItem().copy();
        if (activationItem.getItem() instanceof IWand && result.getItem() instanceof IWand){
            SoulUsingItemHandler initWand = SoulUsingItemHandler.get(activationItem);
            SoulUsingItemHandler resultWand = SoulUsingItemHandler.get(result);

            resultWand.insertItem(initWand.getStackInSlot(0));
        }
        if (MainConfig.RitualCraftEnchant.get()) {
            if (activationItem.isEnchanted()) {
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(activationItem);
                for (Enchantment enchantment : enchants.keySet()) {
                    if (enchantment != null && enchantment.canEnchant(result)) {
                        enchants.put(enchantment, enchants.get(enchantment));
                        EnchantmentHelper.setEnchantments(enchants, result);
                    }
                }
            }
        }
        if (MainConfig.RitualCraftDamage.get()) {
            if (result.isDamageableItem()) {
                float percent = 1.0F - (float) (activationItem.getMaxDamage() - activationItem.getDamageValue()) / activationItem.getMaxDamage();
                int damage = (int) (result.getMaxDamage() * percent);
                result.setDamageValue(damage);
            }
        }
        activationItem.shrink(1);
        result.onCraftedBy(world, castingPlayer, 1);
        IItemHandler handler = tileEntity.itemStackHandler.orElseThrow(RuntimeException::new);
        handler.insertItem(0, result, false);
    }
}
