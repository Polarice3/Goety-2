package com.Polarice3.Goety.common.items.equipment;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RampagingAxeItem extends AxeItem {
    public RampagingAxeItem() {
        super(ModTiers.SPECIAL, 5.0F, -3.0F, (new Properties()).rarity(Rarity.UNCOMMON));
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if (pState.is(BlockTags.MINEABLE_WITH_AXE)){
            MobEffectInstance effectinstance1 = pEntityLiving.getEffect(GoetyEffects.RAMPAGE.get());
            if (!pEntityLiving.hasEffect(GoetyEffects.RAMPAGE.get())){
                pEntityLiving.addEffect(new MobEffectInstance(GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10)));
            } else if (effectinstance1 != null){
                if (effectinstance1.getAmplifier() < 4 && pLevel.random.nextFloat() <= 0.25F) {
                    EffectsUtil.amplifyEffect(pEntityLiving, GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10));
                } else {
                    EffectsUtil.resetDuration(pEntityLiving, GoetyEffects.RAMPAGE.get(), MathHelper.secondsToTicks(10));
                }
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }
}
