package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.api.magic.ITouchSpell;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.BlockSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;

public class IgniteSpell extends BlockSpell implements ITouchSpell {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.IgniteCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.FIRECHARGE_USE;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.IgniteCoolDown.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.BURNING.get());
        return list;
    }

    public InteractionResult interact(ServerLevel worldIn, LivingEntity caster){
        double d0 = caster.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        double entityReach = caster.getAttributeValue(ForgeMod.ENTITY_REACH.get());
        return Items.FIRE_CHARGE.useOn(new UseOnContext(worldIn, null, caster.getUsedItemHand(), new ItemStack(Items.FIRE_CHARGE), MobUtil.rayTrace(caster, Math.max(d0, entityReach), false)));
    }

    @Override
    public boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target, Direction direction) {
        return interact(worldIn, caster) != InteractionResult.FAIL;
    }

    @Override
    public void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target, Direction direction) {
    }

    @Override
    public void touchResult(ServerLevel worldIn, LivingEntity caster, LivingEntity target) {
        worldIn.playSound(null, target, SoundEvents.FIRECHARGE_USE, this.getSoundSource(), 1.0F, 1.0F);
        target.setSecondsOnFire(SpellConfig.IgniteFireSeconds.get() + WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster));
    }
}
