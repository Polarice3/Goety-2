package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.projectiles.BerserkFungus;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BerserkFungusItem extends Item {
   public BerserkFungusItem() {
      super(new Properties().tab(Goety.TAB));
   }

   public InteractionResultHolder<ItemStack> use(Level p_41128_, Player p_41129_, InteractionHand p_41130_) {
      ItemStack itemstack = p_41129_.getItemInHand(p_41130_);
      p_41128_.playSound((Player)null, p_41129_.getX(), p_41129_.getY(), p_41129_.getZ(), ModSounds.BLAST_FUNGUS_THROW.get(), SoundSource.PLAYERS, 0.5F, 0.4F / (p_41128_.getRandom().nextFloat() * 0.4F + 0.8F));
      if (!p_41128_.isClientSide) {
         BerserkFungus berserkFungus = new BerserkFungus(p_41129_, p_41128_);
         berserkFungus.shootFromRotation(p_41129_, p_41129_.getXRot(), p_41129_.getYRot(), -20.0F, 0.75F, 8.0F);
         p_41128_.addFreshEntity(berserkFungus);
      }
      if (!p_41129_.getAbilities().instabuild) {
         if (CuriosFinder.hasWarlockRobe(p_41129_)){
            if (p_41128_.random.nextFloat() > 0.1F){
               itemstack.shrink(1);
            }
         } else {
            itemstack.shrink(1);
         }
      }

      return InteractionResultHolder.sidedSuccess(itemstack, p_41128_.isClientSide());
   }
}