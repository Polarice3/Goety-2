package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.deco.HauntedPainting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Optional;

public class HauntedPaintingItem extends HangingEntityItem {
    public HauntedPaintingItem() {
        super(ModEntityType.MOD_PAINTING.get(), new Properties().tab(Goety.TAB));
    }

    public InteractionResult useOn(UseOnContext p_41331_) {
        BlockPos blockpos = p_41331_.getClickedPos();
        Direction direction = p_41331_.getClickedFace();
        BlockPos blockpos1 = blockpos.relative(direction);
        Player player = p_41331_.getPlayer();
        ItemStack itemstack = p_41331_.getItemInHand();
        if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
            return InteractionResult.FAIL;
        } else {
            Level level = p_41331_.getLevel();
            HangingEntity hangingentity;
            Optional<HauntedPainting> optional = HauntedPainting.createModded(level, blockpos1, direction);
            if (optional.isEmpty()) {
                return InteractionResult.CONSUME;
            }

            hangingentity = optional.get();

            CompoundTag compoundtag = itemstack.getTag();
            if (compoundtag != null) {
                EntityType.updateCustomEntityTag(level, player, hangingentity, compoundtag);
            }

            if (hangingentity.survives()) {
                if (!level.isClientSide) {
                    hangingentity.playPlacementSound();
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, hangingentity.position());
                    level.addFreshEntity(hangingentity);
                }

                itemstack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.CONSUME;
            }
        }
    }

    protected boolean mayPlace(Player p_41326_, Direction p_41327_, ItemStack p_41328_, BlockPos p_41329_) {
        return !p_41327_.getAxis().isVertical() && p_41326_.mayUseItemAt(p_41329_, p_41327_, p_41328_);
    }
}
