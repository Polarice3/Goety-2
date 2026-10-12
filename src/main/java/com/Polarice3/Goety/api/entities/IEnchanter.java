package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface IEnchanter extends IAnimated {
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String ENCHANT = "enchant";
    public static String NOVELTY = "novelty";

    default List<UUID> getEnchantList() {
        return new ArrayList<>();
    }

    default void setEnchantList(List<UUID> list) {
    }

    default int getEnchantCool() {
        return 0;
    }

    default void setEnchantCool(int cool) {
    }

    default int getEnchantTime() {
        return 0;
    }

    default void setEnchantTime(int time) {
    }

    default int enchantTimeLimit() {
        return MathHelper.secondsToTicks(AttributesConfig.EnchanterServantEnchantTime.get());
    }

    default void enchanterTick() {
        if (this instanceof Mob mob) {
            if (mob.level instanceof ServerLevel serverLevel) {
                if (this.getEnchantCool() > 0) {
                    this.setEnchantCool(this.getEnchantCool() - 1);
                }
                if (mob.tickCount > 20) {
                    if (!this.getEnchantList().isEmpty()) {
                        this.getEnchantList().removeIf(uuid -> {
                            Entity entity = serverLevel.getEntity(uuid);
                            if (!(entity instanceof LivingEntity livingEntity)) {
                                return true;
                            }
                            if (mob.isDeadOrDying()) {
                                return true;
                            }
                            if (this.enchantTimeLimit() > -1) {
                                if (this.getEnchantTime() >= this.enchantTimeLimit()) {
                                    this.setEnchantCool(200);
                                    return true;
                                }
                            }
                            if (livingEntity.isDeadOrDying()) {
                                return true;
                            }
                            return livingEntity.distanceTo(mob) > 20.0F;
                        });
                        this.getEnchantList().forEach(uuid -> {
                            Entity entity = serverLevel.getEntity(uuid);
                            if (entity instanceof LivingEntity livingEntity) {
                                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.ENCHANTED.get(), 30), mob);
                            }
                        });
                        this.setEnchantTime(this.getEnchantTime() + 1);
                    } else if (this.getEnchantTime() > 0) {
                        this.setEnchantTime(0);
                    }
                }
            }
        }
    }

    default void setShowArms(boolean showArms) {
    }

    default boolean showArms() {
        return !this.isCurrentAnimation(IEnchanter.IDLE);
    }

    default void readEnchanterData(CompoundTag pCompound) {
        if (pCompound.contains("EnchantCool")){
            this.setEnchantCool(pCompound.getInt("EnchantCool"));
        }
        if (pCompound.contains("EnchantTime")){
            this.setEnchantTime(pCompound.getInt("EnchantTime"));
        }
        if (pCompound.contains("EnchantTargets")){
            ListTag listTag = pCompound.getList("EnchantTargets", 8);
            List<UUID> list = new ArrayList<>();
            for(int i = 0; i < listTag.size(); ++i) {
                list.add(UUID.fromString(listTag.getString(i)));
            }
            this.setEnchantList(list);
        }
    }

    default void addEnchanterData(CompoundTag pCompound) {
        pCompound.putInt("EnchantCool", this.getEnchantCool());
        pCompound.putInt("EnchantTime", this.getEnchantTime());
        if (!this.getEnchantList().isEmpty()) {
            ListTag listTag = new ListTag();
            for (UUID uuid : this.getEnchantList()) {
                listTag.add(StringTag.valueOf(uuid.toString()));
            }
            pCompound.put("EnchantTargets", listTag);
        }
    }
}
