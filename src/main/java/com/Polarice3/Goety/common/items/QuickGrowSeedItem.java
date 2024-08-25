package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.AbstractVine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class QuickGrowSeedItem extends Item {
   public boolean isPoison;

   public QuickGrowSeedItem(boolean isPoison) {
      super(new Properties()
              .tab(Goety.TAB));
      this.isPoison = isPoison;
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
         AABB aabb = ModEntityType.QUICK_GROWING_VINE.get().getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
         if (level.noCollision((Entity)null, aabb) && level.getEntities((Entity)null, aabb).isEmpty()) {
            if (level instanceof ServerLevel serverlevel) {
               AbstractVine vine = ModEntityType.QUICK_GROWING_VINE.get().create(serverlevel);
               if (this.isPoison){
                  vine = ModEntityType.POISON_QUILL_VINE.get().create(serverlevel);
               }
               if (vine == null){
                  return InteractionResult.FAIL;
               }
               EntityType<?> entityType = vine.getVariant(level, blockpos);
               if (entityType != null){
                  vine = (AbstractVine) entityType.create(level);
               }
               if (vine == null){
                  return InteractionResult.FAIL;
               }
               vine.setPos(vec3);
               if (p_40510_.getPlayer() != null) {
                  vine.setTrueOwner(p_40510_.getPlayer());
               }
               vine.finalizeSpawn(serverlevel, serverlevel.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
               vine.setPerpetual(true);
               vine.setPersistenceRequired();
               if (p_40510_.getPlayer() != null) {
                  if (p_40510_.getPlayer().isCrouching()) {
                     vine.setProximity(true);
                  }
               }
               serverlevel.addFreshEntityWithPassengers(vine);
               level.playSound((Player)null, vine.getX(), vine.getY(), vine.getZ(), SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
               vine.gameEvent(GameEvent.ENTITY_PLACE, p_40510_.getPlayer());
            }

            itemstack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return InteractionResult.FAIL;
         }
      }
   }
}