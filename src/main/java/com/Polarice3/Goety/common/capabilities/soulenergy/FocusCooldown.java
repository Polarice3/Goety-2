package com.Polarice3.Goety.common.capabilities.soulenergy;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SFocusCooldownPacket;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.Map;

/**
 * Made custom cooldown mechanic based of vanilla item cooldowns for any future tinkering. And also to prevent an exploit where relogging can reset item cooldown.".
 */
public class FocusCooldown {
    public final Map<Item, CooldownInstance> cooldowns = Maps.newHashMap();

    public boolean isOnCooldown(Item item) {
        return this.getCooldownPercent(item) > 0.0F;
    }

    public float getCooldownPercent(Item item) {
        CooldownInstance cooldownInstance = this.cooldowns.get(item);
        if (cooldownInstance != null) {
            return Mth.clamp((float) cooldownInstance.time / cooldownInstance.totalTime, 0.0F, 1.0F);
        } else {
            return 0.0F;
        }
    }

    public void tick(Player player, Level level) {
        if (!this.cooldowns.isEmpty()) {
            Iterator<Map.Entry<Item, CooldownInstance>> iterator = this.cooldowns.entrySet().iterator();

            while(iterator.hasNext()) {
                Map.Entry<Item, CooldownInstance> entry = iterator.next();
                entry.getValue().decreaseTime();
                if (entry.getValue().time <= 0) {
                    iterator.remove();
                    if (!level.isClientSide) {
                        this.onCooldownEnded(player, entry.getKey());
                    }
                }
            }
        }
    }

    public void addCooldown(Player player, Level level, Item item, int coolDown) {
        this.cooldowns.put(item, new CooldownInstance(coolDown));
        if (!level.isClientSide) {
            this.onCooldownStarted(player, item, coolDown);
        }
    }

    public void removeCooldown(Player player, Level level, Item item) {
        this.cooldowns.remove(item);
        if (!level.isClientSide) {
            this.onCooldownEnded(player, item);
        }
    }

    protected void onCooldownStarted(Player player, Item item, int duration) {
        ModNetwork.sendTo(player, new SFocusCooldownPacket(item, duration));
    }

    protected void onCooldownEnded(Player player, Item item) {
        ModNetwork.sendTo(player, new SFocusCooldownPacket(item, 0));
    }

    public CooldownInstance getInstance(Item item){
        return this.cooldowns.get(item);
    }

    public void setCooldown(Item item, CooldownInstance cooldownInstance){
        this.cooldowns.put(item, cooldownInstance);
    }

    public void save(ListTag listTag) {
        cooldowns.forEach((item, cooldown) -> {
            if (isOnCooldown(item)) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putInt("Item", Item.getId(item));
                compoundTag.putInt("Time", cooldown.time);
                compoundTag.putInt("TotalTime", cooldown.totalTime);
                listTag.add(compoundTag);
            }
        });
    }

    public void load(ListTag listTag){
        if (listTag != null) {
            listTag.forEach(tag -> {
                CompoundTag compoundTag = (CompoundTag) tag;
                Item item = Item.byId(compoundTag.getInt("Item"));
                int startTime = compoundTag.getInt("Time");
                int endTime = compoundTag.getInt("TotalTime");
                cooldowns.put(item, new CooldownInstance(startTime, endTime));
            });
        }
    }

    public static class CooldownInstance {
        int time;
        final int totalTime;

        public CooldownInstance(int time){
            this(time, time);
        }

        public CooldownInstance(int time, int totalTime) {
            this.time = time;
            this.totalTime = totalTime;
        }

        public void decreaseTime(){
            if (this.time > 0){
                --this.time;
            }
        }
    }
}
