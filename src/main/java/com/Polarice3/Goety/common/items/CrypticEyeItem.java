package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.EyeItemEntity;
import com.Polarice3.Goety.common.world.structures.ModStructureTags;
import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class CrypticEyeItem extends Item {
   public CrypticEyeItem() {
      super(new Properties().tab(Goety.TAB).stacksTo(1).durability(16));
   }

   public InteractionResultHolder<ItemStack> use(Level p_41184_, Player p_41185_, InteractionHand p_41186_) {
      ItemStack itemstack = p_41185_.getItemInHand(p_41186_);
      p_41185_.startUsingItem(p_41186_);
      if (p_41184_ instanceof ServerLevel serverlevel) {
          BlockPos blockpos = serverlevel.findNearestMapStructure(ModStructureTags.CRYPT, p_41185_.blockPosition(), 100, false);
         if (blockpos != null) {
            EyeItemEntity eyeItem = new EyeItemEntity(ModEntityType.CRYPTIC_EYE.get(), p_41184_, p_41185_.getX(), p_41185_.getY(0.5D), p_41185_.getZ());
            eyeItem.setItem(itemstack);
            eyeItem.signalTo(blockpos);
            eyeItem.setSurviveAfterDeath(false);
            p_41184_.gameEvent(GameEvent.PROJECTILE_SHOOT, eyeItem.position(), GameEvent.Context.of(p_41185_));
            p_41184_.addFreshEntity(eyeItem);
            ItemHelper.hurtAndBreak(itemstack, 1, p_41185_);
            p_41184_.playSound((Player)null, p_41185_.getX(), p_41185_.getY(), p_41185_.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (p_41184_.getRandom().nextFloat() * 0.4F + 0.8F));
            p_41184_.levelEvent((Player)null, 1003, p_41185_.blockPosition(), 0);
            p_41185_.awardStat(Stats.ITEM_USED.get(this));
            p_41185_.swing(p_41186_, true);
            return InteractionResultHolder.success(itemstack);
         }
      }

      return InteractionResultHolder.consume(itemstack);
   }
}