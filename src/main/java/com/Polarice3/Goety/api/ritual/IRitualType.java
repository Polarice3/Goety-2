package com.Polarice3.Goety.api.ritual;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.RitualBlockEntity;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface IRitualType {

    String getName();

    default ItemStack getJeiIcon(){
        return new ItemStack(Items.OBSIDIAN);
    }

    @Deprecated
    default boolean getRequirement(RitualBlockEntity pTileEntity, BlockPos pPos, Level pLevel){
        return this.getRequirement(pTileEntity, null, pPos, pLevel);
    }

    default boolean getRequirement(RitualBlockEntity pTileEntity, @Nullable Player pPlayer, BlockPos pPos, Level pLevel){
        return false;
    }

    default void onPerformRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                                 Player castingPlayer, ItemStack activationItem) {
    }

    //For current addons that have added their own custom Rituals without overridden onFinishRitual override.
    default void sendFinishRay(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                               Player castingPlayer, ItemStack activationItem) {
        if (world instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(0x8FE6DF);
            Vec3 vec3 = darkAltarPos.getCenter();
            ServerParticleUtil.sendStretchedGodRay(serverLevel, vec3.x, vec3.y, vec3.z, colorUtil);
        }
    }

    default void onFinishRitual(Level world, BlockPos darkAltarPos, DarkAltarBlockEntity tileEntity,
                                Player castingPlayer, ItemStack activationItem){
    }
}
