package com.Polarice3.Goety.common.magic.spells.void_spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
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

public class EnderChestSpell extends Spell {
    private static final Component CONTAINER_TITLE = Component.translatable("container.enderchest");

    @Override
    public int defaultSoulCost() {
        return SpellConfig.EnderChestCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.EnderChestDuration.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.ENDER_CHEST_OPEN;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.EnderChestCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        if (caster instanceof Player player){
            PlayerEnderChestContainer playerenderchestcontainer = player.getEnderChestInventory();
            player.openMenu(new SimpleMenuProvider((p_53124_, p_53125_, p_53126_) -> {
                return ChestMenu.threeRows(p_53124_, p_53125_, playerenderchestcontainer);
            }, CONTAINER_TITLE));
            this.playSound(worldIn, caster, 2.0F, 1.0F);
        }
    }
}
