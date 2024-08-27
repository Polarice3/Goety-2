package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BlastFungusItem extends Item {
    public BlastFungusItem() {
        super(new Properties().tab(Goety.TAB));
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int random = Mth.nextInt(level.random, Math.min(6, itemstack.getCount()), Math.min(10, itemstack.getCount()));
        int used = random;
        for (int i = 0; i < random; ++i) {
            MobUtil.throwBlastFungus(player, level);
            if (CuriosFinder.hasWarlockRobe(player)){
                if (level.random.nextFloat() <= 0.1F){
                    used -= 1;
                }
            }
        }
        if (!player.isSilent()) {
            level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ModSounds.BLAST_FUNGUS_THROW.get(), player.getSoundSource(), 2.0F, 0.8F + level.random.nextFloat() * 0.4F);
        }
        itemstack.shrink(used);
        player.getCooldowns().addCooldown(ModItems.BLAST_FUNGUS.get(), MathHelper.secondsToTicks(random));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

}
