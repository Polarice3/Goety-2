package com.Polarice3.Goety.common.entities.hostile;

import com.Polarice3.Goety.common.entities.neutral.IRavager;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.RavagerArmorItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
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

public class ArmoredRavager extends Ravager implements ContainerListener, IRavager {
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("d404309f-25d3-4837-8828-e2b7b0ea79fd");
    protected SimpleContainer inventory;
    
    public ArmoredRavager(EntityType<? extends Ravager> p_33325_, Level p_33326_) {
        super(p_33325_, p_33326_);
        this.createInventory();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getArmor().isEmpty()){
            this.convertTo(EntityType.RAVAGER, false);
        }
    }

    public ItemStack getArmorSlot(){
        return this.inventory.getItem(0);
    }

    public void setArmorSlot(ItemStack itemStack){
        this.inventory.setItem(0, itemStack);
    }

    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(1);
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            ItemStack itemstack = simplecontainer.getItem(0);
            if (!itemstack.isEmpty()) {
                this.inventory.setItem(0, itemstack.copy());
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
    }

    protected void updateContainerEquipment() {
        if (!this.level.isClientSide) {
            this.setArmorEquipment(this.getArmorSlot());
            this.setDropChance(EquipmentSlot.CHEST, 0.0F);
        }
    }

    public void containerChanged(Container p_30696_) {
        ItemStack itemstack = this.getArmor();
        this.updateContainerEquipment();
        ItemStack itemstack1 = this.getArmor();
        if (this.tickCount > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
            this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
        }

    }

    public ItemStack getArmor() {
        return this.getItemBySlot(EquipmentSlot.CHEST);
    }

    private void setArmor(ItemStack p_30733_) {
        this.setItemSlot(EquipmentSlot.CHEST, p_30733_);
        this.setDropChance(EquipmentSlot.CHEST, 0.0F);
    }

    private void setArmorEquipment(ItemStack p_30735_) {
        this.setArmor(p_30735_);
        if (!this.level.isClientSide) {
            AttributeInstance attribute = this.getAttribute(Attributes.ARMOR);
            if (attribute != null) {
                attribute.removeModifier(ARMOR_MODIFIER_UUID);
                if (this.isArmor(p_30735_)) {
                    int i = ((RavagerArmorItem) p_30735_.getItem()).getProtection();
                    if (i != 0) {
                        attribute.addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Ravager armor bonus", (double) i, AttributeModifier.Operation.ADDITION));
                    }
                }
            }
        }

    }

    public boolean isArmor(ItemStack p_30731_) {
        return p_30731_.getItem() instanceof RavagerArmorItem;
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
            this.setArmorSlot(new ItemStack(item));
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

    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (this.isAlive() && capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && itemHandler != null)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (itemHandler != null) {
            net.minecraftforge.common.util.LazyOptional<?> oldHandler = itemHandler;
            itemHandler = null;
            oldHandler.invalidate();
        }
    }
}
