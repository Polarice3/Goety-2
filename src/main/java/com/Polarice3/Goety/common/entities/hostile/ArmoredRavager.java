package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.entities.neutral.IRavager;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.RavagerArmorItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.UUID;

public class ArmoredRavager extends Ravager implements IRavager {
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("d404309f-25d3-4837-8828-e2b7b0ea79fd");
    
    public ArmoredRavager(EntityType<? extends Ravager> p_33325_, Level p_33326_) {
        super(p_33325_, p_33326_);
        this.xpReward = 40;
    }

    public void addAdditionalSaveData(CompoundTag p_33353_) {
        super.addAdditionalSaveData(p_33353_);
        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.CHEST);
        if(!itemStack.isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            itemStack.save(compoundTag);
            p_33353_.put("ArmorItem", compoundTag);
        }
    }

    public void readAdditionalSaveData(CompoundTag p_33344_) {
        super.readAdditionalSaveData(p_33344_);
        CompoundTag armorItem = p_33344_.getCompound("ArmorItem");
        if(!armorItem.isEmpty()) {
            this.setArmorEquipment(ItemStack.of(armorItem));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getArmor().isEmpty()){
            this.convertTo(EntityType.RAVAGER, false);
        }
    }

    public ItemStack getArmor() {
        return this.getItemBySlot(EquipmentSlot.CHEST);
    }

    public void setArmor(ItemStack p_30733_) {
        this.setItemSlot(EquipmentSlot.CHEST, p_30733_);
        this.setDropChance(EquipmentSlot.CHEST, 0.0F);
    }

    public void setArmorEquipment(ItemStack armor) {
        if (!this.level.isClientSide) {
            this.setItemSlot(EquipmentSlot.CHEST, armor);
            this.setDropChance(EquipmentSlot.CHEST, 0.0F);
            this.updateArmor();
        }
    }

    public void updateArmor(){
        AttributeInstance attribute = this.getAttribute(Attributes.ARMOR);
        if (attribute != null) {
            attribute.removeModifier(ARMOR_MODIFIER_UUID);
            if (this.isArmor(this.getArmor())) {
                int i = ((RavagerArmorItem) this.getArmor().getItem()).getProtection();
                if (i != 0) {
                    attribute.addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Ravager armor bonus", (double) i, AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }

    public boolean isArmor(ItemStack p_30731_) {
        return p_30731_.getItem() instanceof RavagerArmorItem;
    }

    public int getAttackTick() {
        return super.getAttackTick();
    }

    public int getStunnedTick() {
        return super.getStunnedTick();
    }

    public int getRoarTick() {
        return super.getRoarTick();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        int i = pLevel.getLevel().random.nextInt(2);
        float f = pLevel.getLevel().getDifficulty() == Difficulty.HARD ? 0.75F : 0.45F;
        if (pLevel.getLevel().random.nextFloat() < f) {
            ++i;
        }

        if (pLevel.getLevel().random.nextFloat() < f) {
            ++i;
        }

        if (pLevel.getLevel().random.nextFloat() < f) {
            ++i;
        }

        Item item = ArmoredRavager.getEquipmentForSlot(i);
        if (item != null) {
            this.setArmorEquipment(new ItemStack(item));
        }
        return pSpawnData;
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return EntityType.RAVAGER.getDefaultLootTable();
    }

    @Nullable
    public static Item getEquipmentForSlot(int p_21414_) {
        if (p_21414_ < 3) {
            return ModItems.IRON_RAVAGER_ARMOR.get();
        } else if (p_21414_ == 3) {
            return ModItems.GOLD_RAVAGER_ARMOR.get();
        } else if (p_21414_ == 4) {
            return ModItems.DIAMOND_RAVAGER_ARMOR.get();
        }
        return null;
    }
}
