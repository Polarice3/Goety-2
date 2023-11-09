package com.Polarice3.Goety.common.magic.spells.utility;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.magic.InstantCastSpells;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;

public class EnderChestSpell extends InstantCastSpells {
    private static final Component CONTAINER_TITLE = Component.translatable("container.enderchest");

    @Override
    public int SoulCost() {
        return SpellConfig.EnderChestCost.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.ENDER_CHEST_OPEN;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity entityLiving, ItemStack staff) {
        if (entityLiving instanceof Player player){
            PlayerEnderChestContainer playerenderchestcontainer = player.getEnderChestInventory();
            player.openMenu(new SimpleMenuProvider((p_53124_, p_53125_, p_53126_) -> {
                return ChestMenu.threeRows(p_53124_, p_53125_, playerenderchestcontainer);
            }, CONTAINER_TITLE));
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), CastingSound(), this.getSoundSource(), 2.0F, 1.0F);
        }
    }
}
