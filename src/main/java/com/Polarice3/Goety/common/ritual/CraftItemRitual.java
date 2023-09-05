package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class CraftItemRitual extends Ritual{

    public CraftItemRitual(RitualRecipe recipe) {
        super(recipe);
    }

    @Override
    public void finish(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        super.finish(world, blockPos, tileEntity, castingPlayer, activationItem);

        activationItem.shrink(1);

        for(int i = 0; i < 20; ++i) {
            double d0 = (double)blockPos.getX() + world.random.nextDouble();
            double d1 = (double)blockPos.getY() + world.random.nextDouble();
            double d2 = (double)blockPos.getZ() + world.random.nextDouble();
            world.addParticle(ParticleTypes.POOF, d0, d1, d2, 0, 0, 0);
        }

        ItemStack result = this.recipe.getResultItem().copy();
        result.onCraftedBy(world, castingPlayer, 1);
        IItemHandler handler = tileEntity.itemStackHandler.orElseThrow(RuntimeException::new);
        handler.insertItem(0, result, false);
    }
}
