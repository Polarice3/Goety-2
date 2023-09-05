package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HauntedArmorStandItem extends Item {
   public HauntedArmorStandItem() {
      super(new Item.Properties()
              .stacksTo(16)
              .tab(Goety.TAB));
   }

   public InteractionResult useOn(UseOnContext p_40510_) {
      Direction direction = p_40510_.getClickedFace();
      if (direction == Direction.DOWN) {
         return InteractionResult.FAIL;
      } else {
         Level level = p_40510_.getLevel();
         BlockPlaceContext blockplacecontext = new BlockPlaceContext(p_40510_);
         BlockPos blockpos = blockplacecontext.getClickedPos();
         ItemStack itemstack = p_40510_.getItemInHand();
         Vec3 vec3 = Vec3.atBottomCenterOf(blockpos);
         AABB aabb = ModEntityType.HAUNTED_ARMOR_STAND.get().getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
         if (level.noCollision((Entity)null, aabb) && level.getEntities((Entity)null, aabb).isEmpty()) {
            if (level instanceof ServerLevel serverlevel) {
               HauntedArmorStand armorStand = ModEntityType.HAUNTED_ARMOR_STAND.get().create(serverlevel, itemstack.getTag(), (Component)null, p_40510_.getPlayer(), blockpos, MobSpawnType.SPAWN_EGG, true, true);
               if (armorStand == null) {
                  return InteractionResult.FAIL;
               }

               float f = (float)Mth.floor((Mth.wrapDegrees(p_40510_.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
               armorStand.moveTo(armorStand.getX(), armorStand.getY(), armorStand.getZ(), f, 0.0F);
               this.randomizePose(armorStand, level.random);
               serverlevel.addFreshEntityWithPassengers(armorStand);
               level.playSound((Player)null, armorStand.getX(), armorStand.getY(), armorStand.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
               armorStand.gameEvent(GameEvent.ENTITY_PLACE, p_40510_.getPlayer());
            }

            itemstack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   private void randomizePose(ArmorStand p_219999_, RandomSource p_220000_) {
      Rotations rotations = p_219999_.getHeadPose();
      float f = p_220000_.nextFloat() * 5.0F;
      float f1 = p_220000_.nextFloat() * 20.0F - 10.0F;
      Rotations rotations1 = new Rotations(rotations.getX() + f, rotations.getY() + f1, rotations.getZ());
      p_219999_.setHeadPose(rotations1);
      rotations = p_219999_.getBodyPose();
      f = p_220000_.nextFloat() * 10.0F - 5.0F;
      rotations1 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
      p_219999_.setBodyPose(rotations1);
   }
}