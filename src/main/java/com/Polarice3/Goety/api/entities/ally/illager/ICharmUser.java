package com.Polarice3.Goety.api.entities.ally.illager;

import com.Polarice3.Goety.api.items.magic.IMobCharm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public interface ICharmUser {

    default void charmTick() {
        if (this instanceof Mob mob) {
            if (!this.getCharm().isEmpty()) {
                if (this.getCharm().getItem() instanceof IMobCharm charm) {
                    if (mob.level instanceof ServerLevel serverLevel) {
                        charm.charmTick(this.getCharm());
                        if (charm.mobShouldUse(serverLevel, mob, this.getCharm())) {
                            charm.mobUse(serverLevel, mob, this.getCharm());
                        }
                    }
                }
            }
        }
    }

    default ItemStack getCharm() {
        return ItemStack.EMPTY;
    }

    default void setCharm(ItemStack itemStack) {

    }

    default void saveCharmData(CompoundTag compound){
        if (!this.getCharm().isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            this.getCharm().save(compoundTag);
            compound.put("CharmItem", compoundTag);
        }
    }

    default void readCharmData(CompoundTag compound) {
        if (compound.contains("CharmItem")) {
            CompoundTag charm = compound.getCompound("CharmItem");
            if (!charm.isEmpty()) {
                this.setCharm(ItemStack.of(charm));
            }
        }
    }
}
