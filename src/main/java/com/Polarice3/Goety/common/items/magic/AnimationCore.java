package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.magic.GolemType;
import com.Polarice3.Goety.common.magic.construct.SpawnFromBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AnimationCore extends Item {
    public AnimationCore() {
        super(new Properties().tab(Goety.TAB));
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (GolemType.getGolemList().containsKey(blockState)){
            if (GolemType.getGolemList().get(blockState).spawnServant(ctx.getPlayer(), stack, ctx.getLevel(), ctx.getClickedPos())){
                return InteractionResult.sidedSuccess(ctx.getLevel().isClientSide);
            }
        }
        return SpawnFromBlock.spawnServant(ctx.getPlayer(), stack, ctx.getLevel(), ctx.getClickedPos()) ?
                InteractionResult.sidedSuccess(ctx.getLevel().isClientSide)
                : InteractionResult.FAIL;
    }
}
