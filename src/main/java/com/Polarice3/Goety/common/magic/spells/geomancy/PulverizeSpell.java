package com.Polarice3.Goety.common.magic.spells.geomancy;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.crafting.ModRecipeSerializer;
import com.Polarice3.Goety.common.crafting.PulverizeRecipe;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.BlockSpells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class PulverizeSpell extends BlockSpells {
    @Override
    public int defaultSoulCost() {
        return SpellConfig.PulverizeCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.PulverizeCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RADIUS.get());
        return list;
    }

    @Override
    public boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        BlockState blockState = worldIn.getBlockState(target);
        PulverizeRecipe pulverizeRecipe = worldIn.getRecipeManager()
                .getAllRecipesFor(ModRecipeSerializer.PULVERIZE_TYPE.get())
                .stream()
                .filter(pulverizeRecipe1 -> pulverizeRecipe1.ingredient.test(new ItemStack(blockState.getBlock())))
                .findFirst().orElse(null);
        return pulverizeRecipe != null;
    }

    @Override
    public void blockResult(ServerLevel worldIn, LivingEntity caster, BlockPos target) {
        int radius = 0;
        if (WandUtil.enchantedFocus(caster)){
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        }
        if (radius > 0) {
            for (BlockPos blockPos : BlockFinder.multiBlockBreak(caster, target, radius, radius, radius)) {
                this.pulverize(worldIn, caster, blockPos);
            }
        } else {
            this.pulverize(worldIn, caster, target);
        }
    }

    public void pulverize(ServerLevel worldIn, LivingEntity caster, BlockPos target){
        BlockState blockState = worldIn.getBlockState(target);
        PulverizeRecipe pulverizeRecipe = worldIn.getRecipeManager()
                .getAllRecipesFor(ModRecipeSerializer.PULVERIZE_TYPE.get())
                .stream()
                .filter(pulverizeRecipe1 -> pulverizeRecipe1.ingredient.test(new ItemStack(blockState.getBlock())))
                .findFirst().orElse(null);
        if (pulverizeRecipe != null){
            worldIn.levelEvent(2001, target, Block.getId(worldIn.getBlockState(target)));
            BlockState pulverized = Blocks.CAVE_AIR.defaultBlockState();
            if (pulverizeRecipe.getBlockResult() != Blocks.CAVE_AIR){
                pulverized = pulverizeRecipe.getBlockResult().withPropertiesOf(blockState);
            }
            if (pulverizeRecipe.getResultItem() != null){
                for (int i = 0; i < pulverizeRecipe.getResultItem().copy().getCount(); ++i){
                    ItemHelper.addItemEntity(worldIn, target, new ItemStack(pulverizeRecipe.getResultItem().getItem()));
                }
            }
            worldIn.setBlockAndUpdate(target, pulverized);
        }
    }
}
