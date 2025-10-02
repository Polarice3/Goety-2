package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class IceAxeItem extends DiggerItem {
    public IceAxeItem(Tier tier) {
        super(6.0F, -3.1F, tier, BlockTags.ICE, new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41427_) {
        Level level = p_41427_.getLevel();
        BlockPos blockpos = p_41427_.getClickedPos();
        Player player = p_41427_.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        if (player != null) {
            ItemStack itemStack = p_41427_.getItemInHand();
            if (blockstate.isSolidRender(level, blockpos)) {
                ItemHelper.hurtAndBreak(itemStack, 1, player);
                double yDelta = 0.52D;
                player.setDeltaMovement(player.getDeltaMovement().x(), yDelta, player.getDeltaMovement().z());
                player.resetFallDistance();
            }
        }
        return super.useOn(p_41427_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ChatFormatting main = ChatFormatting.DARK_PURPLE;
        ChatFormatting secondary = ChatFormatting.BLUE;
        tooltip.add(Component.translatable("info.goety.ice_axe").withStyle(main));
        tooltip.add(Component.translatable("info.goety.ice_axe.push").withStyle(secondary));
    }
}
