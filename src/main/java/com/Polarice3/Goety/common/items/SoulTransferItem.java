package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.blocks.CursedCageBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SoulTransferItem extends Item {

    public SoulTransferItem() {
        super(new Properties().tab(Goety.TAB).stacksTo(1));
    }

    public boolean isFoil(ItemStack pStack) {
        return pStack.hasTag();
    }

    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        ItemStack stack = pContext.getItemInHand();
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof SoulTransferItem) {
                BlockPos blockpos = pContext.getClickedPos();
                BlockEntity tileEntity = world.getBlockEntity(blockpos);
                if (tileEntity instanceof ArcaBlockEntity){
                    ArcaBlockEntity arcaTile = (ArcaBlockEntity) tileEntity;
                    if (pContext.getPlayer() == arcaTile.getPlayer()) {
                        CompoundTag nbt = new CompoundTag();
                        stack.setTag(nbt);
                        nbt.putUUID("owner", (arcaTile.getPlayer().getUUID()));
                        nbt.putString("owner_name", arcaTile.getPlayer().getDisplayName().getString());
                        pContext.getPlayer().playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                        world.playLocalSound(blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1.0F, 0.45F, false);
                        return InteractionResult.sidedSuccess(world.isClientSide);
                    }
                }
                BlockState blockstate = world.getBlockState(blockpos);
                if (blockstate.is(ModBlocks.CURSED_CAGE_BLOCK.get()) && !blockstate.getValue(CursedCageBlock.POWERED)) {
                    ItemStack itemstack = pContext.getItemInHand();
                    if (!world.isClientSide) {
                        ((CursedCageBlock) ModBlocks.CURSED_CAGE_BLOCK.get()).setItem(world, blockpos, blockstate, itemstack);
                        world.levelEvent(null, 1010, blockpos, Item.getId(this));
                        itemstack.shrink(1);
                    }

                    return InteractionResult.sidedSuccess(world.isClientSide);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag()) {
            tooltip.add(Component.translatable("tooltip." + Goety.MOD_ID + ".arcaPlayer").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + stack.getTag().getString("owner_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
        } else {
            tooltip.add(Component.translatable("tooltip." + Goety.MOD_ID + ".arca_not_bound").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
        }
    }

}
