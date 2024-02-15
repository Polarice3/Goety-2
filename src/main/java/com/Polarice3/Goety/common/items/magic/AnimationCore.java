package com.Polarice3.Goety.common.items.magic;

import com.Polarice3.Goety.common.magic.construct.GraveGolemMold;
import com.Polarice3.Goety.common.magic.construct.RedstoneGolemMold;
import com.Polarice3.Goety.common.magic.construct.SpawnFromBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class AnimationCore extends Item {
    public AnimationCore() {
        super(new Properties());
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        return RedstoneGolemMold.spawnGolem(ctx.getPlayer(), stack, ctx.getLevel(), ctx.getClickedPos())
                ? InteractionResult.sidedSuccess(ctx.getLevel().isClientSide)
                : GraveGolemMold.spawnGolem(ctx.getPlayer(), stack, ctx.getLevel(), ctx.getClickedPos()) ?
                InteractionResult.sidedSuccess(ctx.getLevel().isClientSide)
                : SpawnFromBlock.spawnServant(ctx.getPlayer(), stack, ctx.getLevel(), ctx.getClickedPos()) ?
                InteractionResult.sidedSuccess(ctx.getLevel().isClientSide)
                : InteractionResult.FAIL;
    }

}
