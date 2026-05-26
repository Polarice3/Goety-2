package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocateRitual extends Ritual {

    public LocateRitual(RitualRecipe recipe) {
        super(recipe);
    }

    @Override
    public boolean isValid(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                           Player castingPlayer, ItemStack activationItem,
                           List<Ingredient> remainingAdditionalIngredients) {
        if (world instanceof ServerLevel serverLevel) {
            if (this.recipe.getStructureTag() == null) {
                return false;
            }
            if (!this.areAdditionalIngredientsFulfilled(world, darkAltarPos, castingPlayer, remainingAdditionalIngredients)) {
                return false;
            }

            if (tileEntity.findStructurePos != null) {
                return true;
            }

            if (tileEntity.structureSearchFailed) {
                castingPlayer.displayClientMessage(Component.translatable("info.goety.ritual.location.fail"), true);
                tileEntity.structureSearchFailed = false;
                return false;
            }

            if (tileEntity.pendingStructureSearch != null) {
                if (!tileEntity.pendingStructureSearch.isDone()) {
                    return true;
                }
                BlockPos result = tileEntity.pendingStructureSearch.getNow(null);
                tileEntity.pendingStructureSearch = null;
                if (result != null) {
                    tileEntity.findStructurePos = result;
                } else {
                    tileEntity.structureSearchFailed = true;
                }
                return true;
            }
            final TagKey<Structure> structureTag = this.recipe.getStructureTag();
            final BlockPos searchCenter = darkAltarPos;

            tileEntity.pendingStructureSearch = CompletableFuture.supplyAsync(() ->
                    serverLevel.findNearestMapStructure(structureTag, searchCenter, 100, true)
            );

            return true;
        }
        return false;
    }

    public boolean identify(Level world, BlockPos darkAltarPos, Player player, ItemStack activationItem) {
        return this.recipe.getStructureTag() != null
                && this.areAdditionalIngredientsFulfilled(world, darkAltarPos, player, this.recipe.getIngredients());
    }

    @Override
    public void interrupt(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity, Player castingPlayer, ItemStack activationItem) {
        super.interrupt(world, darkAltarPos, tileEntity, castingPlayer, activationItem);
        tileEntity.findStructurePos = null;
        if (tileEntity.pendingStructureSearch != null) {
            tileEntity.pendingStructureSearch.cancel(true);
            tileEntity.pendingStructureSearch = null;
        }
        tileEntity.structureSearchFailed = false;
    }

    @Override
    public boolean isPending(DarkAltarBlockEntity tileEntity) {
        return tileEntity.pendingStructureSearch != null
                && !tileEntity.pendingStructureSearch.isDone();
    }

    @Override
    public void finish(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        super.finish(world, blockPos, tileEntity, castingPlayer, activationItem);

        activationItem.shrink(1);
        if (world instanceof ServerLevel serverLevel) {
            BlockPos structure = tileEntity.findStructurePos;
            if (structure != null) {
                ItemStack result = MapItem.create(serverLevel, structure.getX(), structure.getZ(), (byte) 2, true, true);
                MapItem.renderBiomePreviewMap(serverLevel, result);
                MapItemSavedData.addTargetDecoration(result, structure, "+", MapDecoration.Type.TARGET_POINT);
                String string = "filled_map.goety.magic";
                if (this.recipe.getStructureName() != null) {
                    string = this.recipe.getStructureName();
                }
                result.setHoverName(Component.translatable(string));
                IItemHandler handler = tileEntity.itemStackHandler.orElseThrow(RuntimeException::new);
                handler.insertItem(0, result, false);
                result.onCraftedBy(world, castingPlayer, 1);
                tileEntity.findStructurePos = null;
            }
        }

    }
}
