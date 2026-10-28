package com.Polarice3.Goety.api.entities;

import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.ally.illager.CrusherServant;
import com.Polarice3.Goety.common.entities.ally.illager.PikerServant;
import com.Polarice3.Goety.common.entities.ally.illager.RoyalGuardServant;
import com.Polarice3.Goety.common.entities.ally.undead.ReaperServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.common.entities.hostile.Reaper;
import com.Polarice3.Goety.common.entities.hostile.illagers.Crusher;
import com.Polarice3.Goety.common.entities.hostile.illagers.Piker;
import com.Polarice3.Goety.common.items.equipment.DarkScytheItem;
import com.Polarice3.Goety.common.items.equipment.FangedDaggerItem;
import com.Polarice3.Goety.common.items.equipment.HammerItem;
import com.Polarice3.Goety.common.items.equipment.SickleItem;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;

public interface IPoise {

    default int getMaxPoise() {
        return 9999;
    }

    default int getPoiseRecover() {
        return 0;
    }

    default int getPoise() {
        return 0;
    }

    default void setPoise(int poise) {
    }

    default int getPoiseCoolLength() {
        return 0;
    }

    default int getPoiseCool() {
        return 0;
    }

    default void setPoiseCool(int cool) {
    }

    default boolean shouldStagger() {
        return this.getPoise() >= this.getMaxPoise();
    }

    default void addPoise(DamageSource pSource, float pAmount) {
        this.addPoise(pSource, pAmount, 0.0F);
    }

    default void addPoise(DamageSource pSource, float pAmount, float overwhelm) {
        if (this.getPoise() < this.getMaxPoise()) {
            Entity source = pSource.getEntity();
            Entity direct = pSource.getDirectEntity();
            int increase = 0;
            if (source != null) {
                if (direct != null) {
                    if (direct instanceof LivingEntity livingDirect) {
                        ItemStack itemStack = livingDirect.getMainHandItem();
                        if (ModDamageSource.physicalAttacks(pSource)) {
                            if (itemStack.is(ModTags.Items.PIKES) || livingDirect instanceof Piker || livingDirect instanceof PikerServant) {
                                increase += 28;
                            } else if (itemStack.is(ModTags.Items.MACES) || livingDirect instanceof RoyalGuardServant || livingDirect instanceof BlackguardServant) {
                                increase += 32;
                            } else if (itemStack.is(ModTags.Items.GLAIVES) || livingDirect instanceof VanguardServant) {
                                increase += 25;
                            } else if (itemStack.is(ModTags.Items.CLAWS) || livingDirect instanceof BlackWolf || livingDirect instanceof BlackBeast) {
                                increase += 13;
                            } else if (itemStack.is(ModTags.Items.HAMMERS) || itemStack.getItem() instanceof HammerItem || livingDirect instanceof Crusher || livingDirect instanceof CrusherServant) {
                                increase += 34;
                            } else if (itemStack.is(ModTags.Items.SCYTHES) || itemStack.getItem() instanceof DarkScytheItem || livingDirect instanceof Reaper || livingDirect instanceof ReaperServant) {
                                increase += 20;
                            } else if (itemStack.is(ModTags.Items.DAGGERS) || itemStack.getItem() instanceof SickleItem || itemStack.getItem() instanceof FangedDaggerItem) {
                                increase += 13;
                            } else if (itemStack.is(ItemTags.SHOVELS) || itemStack.getItem() instanceof ShovelItem) {
                                increase += 30;
                            } else if (itemStack.is(ItemTags.PICKAXES) || itemStack.getItem() instanceof PickaxeItem) {
                                increase += 30;
                            } else if (itemStack.is(ItemTags.SWORDS) || itemStack.getItem() instanceof SwordItem) {
                                increase += 25;
                            } else if (itemStack.is(ItemTags.AXES) || itemStack.getItem() instanceof AxeItem) {
                                increase += 28;
                            } else {
                                increase += 10;
                            }
                        } else {
                            increase += 10;
                        }
                    } else if (direct instanceof AbstractArrow arrow) {
                        if (arrow.shotFromCrossbow()) {
                            increase += 35;
                        } else {
                            increase += 23;
                        }
                    } else {
                        increase += 10;
                    }
                }
                if (overwhelm > 0.0F && pAmount >= overwhelm) {
                    increase = 90;
                }
                if (increase > 0) {
                    this.setPoise(this.getPoise() + increase);
                    this.setPoiseCool(this.getPoiseCoolLength());
                }
            }
        }
    }

    default void poiseTick() {
        if (this instanceof Entity entity) {
            if (this.getPoiseCool() > 0) {
                this.setPoiseCool(this.getPoiseCool() - 1);
            } else if (this.getPoise() > 0) {
                if (entity.tickCount % 20 == 0) {
                    this.setPoise(Math.max(0, this.getPoise() - this.getPoiseRecover()));
                }
            }
        }
    }

    default void readPoiseData(CompoundTag pCompound){
        if (pCompound.contains("Poise")) {
            this.setPoise(pCompound.getInt("Poise"));
        }
        if (pCompound.contains("PoiseCool")) {
            this.setPoiseCool(pCompound.getInt("PoiseCool"));
        }
    }

    default void savePoiseData(CompoundTag pCompound){
        pCompound.putInt("Poise", this.getPoise());
        pCompound.putInt("PoiseCool", this.getPoiseCool());
    }
}
