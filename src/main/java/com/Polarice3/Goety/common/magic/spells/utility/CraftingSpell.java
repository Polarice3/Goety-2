package com.Polarice3.Goety.common.magic.spells.utility;

import com.Polarice3.Goety.client.inventory.container.CraftingFocusMenu;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.config.SpellConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class CraftingSpell extends Spell {
    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

    @Override
    public int defaultSoulCost() {
        return SpellConfig.CraftingCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.CraftingDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.CraftingCoolDown.get();
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        if (entityLiving instanceof Player player){
            player.openMenu(new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) -> {
                return new CraftingFocusMenu(p_52229_, p_52230_, ContainerLevelAccess.create(worldIn, entityLiving.blockPosition()));
            }, CONTAINER_TITLE));
        }
    }
}
