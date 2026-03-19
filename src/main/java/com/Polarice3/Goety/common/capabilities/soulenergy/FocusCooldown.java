package com.Polarice3.Goety.common.capabilities.soulenergy;

import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SFocusCooldownPacket;
import com.Polarice3.Goety.common.network.server.SFocusSpecificCooldownPacket;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;
import java.util.Map;

/**
 * Made custom cooldown mechanic based of vanilla item cooldowns for any future tinkering. And also to prevent an exploit where relogging can reset item cooldown.".
 */
public class FocusCooldown {
    public final Map<Item, CooldownInstance> cooldowns = Maps.newHashMap();
    public final Map<String, CooldownInstance> cooldownsSpecific = Maps.newHashMap();

    public static String keyOf(ItemStack stack) {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (location != null) {
            String base = location.toString();
            return stack.getTag() != null ? base + stack.getTag() : base;
        }
        return "minecraft:air";
    }

    public boolean isOnSpecificCooldown(ItemStack itemStack) {
        return this.getSpecificCooldownPercent(itemStack) > 0.0F;
    }

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

    public float getSpecificCooldownPercent(ItemStack itemStack) {
        CooldownInstance cooldownInstance = this.cooldownsSpecific.get(keyOf(itemStack));
        if (cooldownInstance != null) {
            return Mth.clamp((float) cooldownInstance.time / cooldownInstance.totalTime, 0.0F, 1.0F);
        }
        return 0.0F;
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
        if (!this.cooldownsSpecific.isEmpty()) {
            Iterator<Map.Entry<String, CooldownInstance>> iterator = this.cooldownsSpecific.entrySet().iterator();

            while(iterator.hasNext()) {
                Map.Entry<String, CooldownInstance> entry = iterator.next();
                entry.getValue().decreaseTime();
                if (entry.getValue().time <= 0) {
                    iterator.remove();
                    if (!level.isClientSide) {
                        this.onSpecificCooldownEnded(player, entry.getKey());
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

    public void addSpecificCooldown(Player player, Level level, ItemStack itemStack, int coolDown) {
        this.cooldownsSpecific.put(keyOf(itemStack), new CooldownInstance(coolDown));
        if (!level.isClientSide) {
            this.onSpecificCooldownStarted(player, itemStack, coolDown);
        }
    }

    public void removeCooldown(Player player, Level level, Item item) {
        this.cooldowns.remove(item);
        if (!level.isClientSide) {
            this.onCooldownEnded(player, item);
        }
    }

    public void removeSpecificCooldown(Player player, Level level, ItemStack itemStack) {
        this.cooldownsSpecific.remove(keyOf(itemStack));
        if (!level.isClientSide) {
            this.onSpecificCooldownEnded(player, itemStack);
        }
    }

    public void removeSpecificCooldown(Player player, Level level, String string) {
        this.cooldownsSpecific.remove(string);
        if (!level.isClientSide) {
            this.onSpecificCooldownEnded(player, string);
        }
    }

    protected void onCooldownStarted(Player player, Item item, int duration) {
        ModNetwork.sendTo(player, new SFocusCooldownPacket(item, duration));
    }

    protected void onSpecificCooldownStarted(Player player, ItemStack itemStack, int duration) {
        ModNetwork.sendTo(player, new SFocusSpecificCooldownPacket(itemStack, duration));
    }

    protected void onCooldownEnded(Player player, Item item) {
        ModNetwork.sendTo(player, new SFocusCooldownPacket(item, 0));
    }

    protected void onSpecificCooldownEnded(Player player, String key) {
        ModNetwork.sendTo(player, new SFocusSpecificCooldownPacket(key, 0));
    }

    protected void onSpecificCooldownEnded(Player player, ItemStack itemStack) {
        onSpecificCooldownEnded(player, keyOf(itemStack));
    }

    public CooldownInstance getInstance(Item item){
        return this.cooldowns.get(item);
    }

    public void setCooldown(Item item, CooldownInstance cooldownInstance){
        this.cooldowns.put(item, cooldownInstance);
    }

    public Map<Item, CooldownInstance> getCooldowns(){
        return this.cooldowns;
    }

    public Map<String, CooldownInstance> getSpecificCooldowns(){
        return this.cooldownsSpecific;
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

    public void saveSpecifics(ListTag listTag) {
        cooldownsSpecific.forEach((key, cooldown) -> {
            if (cooldown.time > 0) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putString("Key", key);
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

    public void loadSpecifics(ListTag listTag){
        if (listTag != null) {
            listTag.forEach(tag -> {
                CompoundTag compoundTag = (CompoundTag) tag;
                String key = compoundTag.getString("Key");
                int startTime = compoundTag.getInt("Time");
                int endTime = compoundTag.getInt("TotalTime");
                cooldownsSpecific.put(key, new CooldownInstance(startTime, endTime));
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
